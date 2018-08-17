package com.midsummer.w3jl.service


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.midsummer.w3jl.entity.W3JLWallet
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import io.reactivex.Single
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.crypto.*


import java.io.File
import java.security.SecureRandom
import java.util.*

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class WalletService(var filePath: File) : WalletRepository{

    //Offset number use to convert private and public key to string from ECKeyPair
    private val RADIX = 16

    //Use to create Json data
    private val PARAM_N = 8192
    private val PARAM_P = 1
    private val objectMapper = jacksonObjectMapper()

    //HDPath use to generate wallet from mnemonic
    private val ETH_TYPE = "m/44'/60'/0'/0/0"


    /**
     * Create random mnemonic string from entropy
     * @param entropy: The byte array to use as 'seed' to create mnemonic
     * @return mnemonic string
     */
    override fun createMnemonics(entropy: ByteArray): String {
        val sb = StringBuilder()
        SecureRandom().nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
                .createMnemonic(
                        entropy
                ) {
                    sb.append(it)
                }
        return sb.toString()
    }

    /**
     * Create 12 words mnemonic
     * @return mnemonic string
     */
    override fun createMnemonics(): String {
        return createMnemonics(ByteArray(Words.TWELVE.byteLength()))
    }


    /**
     * Create Wallet that follow Bip44 standard
     * @param mnemonics: 12 words string
     * @return wallet object
     */
    private fun createHDWalletFromMnemonic(mnemonics: String): Single<W3JLWallet> {
        return Single.create { emitter  ->
            try {
                val pathArray = ETH_TYPE.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val passphrase = ""
                val list = mnemonics.split(" ")
                val creationTimeSeconds = System.currentTimeMillis() / 1000
                val ds = DeterministicSeed(list, null, passphrase, creationTimeSeconds)
                val seedBytes = ds.seedBytes
                var dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes)
                for (i in 1 until pathArray.size) {
                    val childNumber: ChildNumber
                    childNumber = if (pathArray[i].endsWith("'")) {
                        val number = Integer.parseInt(pathArray[i].substring(0,
                                pathArray[i].length - 1))
                        ChildNumber(number, true)
                    } else {
                        val number = Integer.parseInt(pathArray[i])
                        ChildNumber(number, false)
                    }
                    dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber)
                }
                val keyPair = ECKeyPair.create(dkKey.privKeyBytes)
                val c = Credentials.create(keyPair.privateKey.toString(RADIX))
                val wallet = W3JLWallet()
                wallet.mnemonic = mnemonics
                wallet.source = W3JLWallet.Source.MNEMONIC
                wallet.address = c.address
                wallet.privateKey = keyPair.privateKey.toString(RADIX)
                wallet.publicKey = keyPair.publicKey.toString(RADIX)
                wallet.jsonSource = ""
                wallet.createAt = Calendar.getInstance().timeInMillis
                emitter.onSuccess(wallet)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }


    /**
     * create HDWallet with password (to encrypt json data)
     * @see createHDWalletFromMnemonic
     * @see setWalletJsonInfo
     * @param mnemonics: 12 words string
     * @param password: use to create encrypt json, omit if equal null
     */
    @Throws(Exception::class)
    override fun createWalletFromMnemonic(mnemonics: String, password: String?): Single<W3JLWallet> {
        return createHDWalletFromMnemonic(mnemonics)
                .flatMap { wallet : W3JLWallet ->
                    setWalletJsonInfo(wallet, password)
                }
    }

    /**
     * create HDWallet with private key and encrypt the json data if password is presented
     * @param privateKey: plain private key
     * @param password: nullable password, use to encrypt json data
     */
    override fun createWalletFromPrivateKey(privateKey: String, password: String?): Single<W3JLWallet> {
        return Single.create{ emitter ->
            try {
                val wallet = W3JLWallet()
                if (!WalletUtils.isValidPrivateKey(privateKey)){
                    emitter.onError(Exception("Invalid private key"))
                    return@create
                }
                wallet.source = W3JLWallet.Source.PRIVATE_KEY
                val c = Credentials.create(privateKey)
                wallet.jsonSource = if (password != null){
                    val tmpWallet = Wallet.create(password, c.ecKeyPair,PARAM_N,PARAM_P)
                    objectMapper.writeValueAsString(tmpWallet)
                }else{
                    ""
                }
                wallet.address = c.address
                wallet.privateKey = privateKey
                wallet.createAt = Calendar.getInstance().timeInMillis
                emitter.onSuccess(wallet)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }

    }

    /**
     * Restore wallet from json data
     * @param jsonString: string contain json data
     * @param password: password to decrypt json data, can not be null
     * @return fully decrypted wallet
     */
    override fun createWalletFromJsonString(jsonString: String, password: String): Single<W3JLWallet> {
        return Single.create{ emitter ->
            try {
                val walletFile : WalletFile = objectMapper.readValue(jsonString)
                val c = Credentials.create(Wallet.decrypt(password, walletFile))
                val wallet = W3JLWallet()
                wallet.source = W3JLWallet.Source.JSON
                wallet.address = c.address
                wallet.privateKey = c.ecKeyPair.privateKey.toString(RADIX)
                wallet.jsonSource = jsonString
                wallet.createAt = Calendar.getInstance().timeInMillis
                emitter.onSuccess(wallet)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }

    }

    /**
     * create json data for the wallet
     * @param wallet: wallet object
     * @param password: the password use to encrypt json data
     * @return wallet with encrypted json
     */
    private fun setWalletJsonInfo(wallet: W3JLWallet, password: String?) : Single<W3JLWallet> {
        return Single.create{emitter ->
            if (password == null){
                wallet.jsonSource = ""
                emitter.onSuccess(wallet)
            }else{
                val keyPair = Credentials.create(wallet.privateKey).ecKeyPair
                val tmpWallet = Wallet.create(password, keyPair,PARAM_N,PARAM_P)
                wallet.jsonSource = objectMapper.writeValueAsString(tmpWallet)
                emitter.onSuccess(wallet)
            }
        }
    }

    /*override fun mnemonicsToPrivateKey(mnemonics: String, password: String?): String {
        return mnemonicsToKeyPair(mnemonics, password).privateKey.toString(RADIX)
    }

    override fun mnemonicsToPublicKey(mnemonics: String, password: String): String {
        return mnemonicsToKeyPair(mnemonics, password).publicKey.toString(RADIX)
    }

    override fun mnemonicsToKeyPair(mnemonics: String, password: String?): ECKeyPair {
        val seeds = MnemonicUtils.generateSeed(mnemonics, password)
        return ECKeyPair.create(Hash.sha256(seeds))
    }



    @Throws(IOException::class)
    override fun loadCredential(password: String, file: File): W3JLCredential? {
        val c = WalletUtils.loadCredentials(password, file)
        return W3JLCredential(c.address, c.ecKeyPair.publicKey.toString(RADIX), c.ecKeyPair.privateKey.toString(RADIX))
    }

    override fun loadCredentialFromPrivateKey(privateKey: String): W3JLCredential? {
        return if (!WalletUtils.isValidPrivateKey(privateKey)) {
            null
        } else {
            val c = Credentials.create(privateKey)
            W3JLCredential(c.address, c.ecKeyPair.publicKey.toString(RADIX), c.ecKeyPair.privateKey.toString(RADIX))
        }
    }

    override fun getAddressFromPrivateKey(privateKey: String): String {
        val credentials = loadCredentialFromPrivateKey(privateKey) ?: return ""
        return credentials.address
    }*/
}


