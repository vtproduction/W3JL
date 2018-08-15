package com.midsummer.w3jl.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.midsummer.w3jl.entity.W3JLBip39Wallet
import com.midsummer.w3jl.entity.W3JLCredential
import com.midsummer.w3jl.entity.W3JLWallet
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import io.reactivex.Single
import org.web3j.crypto.*


import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.util.*

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLWallet(var filePath: File) : W3JLWalletRepository{
    private val RADIX = 16
    private val PARAM_N = 8192
    private val PARAM_P = 1
    private val objectMapper = jacksonObjectMapper()


    override fun createBip39WalletWithPassword(password: String): W3JLBip39Wallet {
        return createBip39WalletWithPasswordAndMnemonic(password, createMnemonics())
    }

    override fun createBip39WalletWithMnemonic(mnemonics: String): W3JLBip39Wallet {
        return createBip39WalletWithPasswordAndMnemonic("", mnemonics)
    }

    override fun createBip39WalletWithPasswordAndMnemonic(password: String, mnemonics: String): W3JLBip39Wallet {
        val seed = MnemonicUtils.generateSeed(mnemonics, password)
        val keyPair = ECKeyPair.create(Hash.sha256(seed))
        val walletFile = WalletUtils.generateWalletFile(password, keyPair, filePath, false)
        return W3JLBip39Wallet(walletFile, mnemonics, keyPair.privateKey.toString(RADIX), keyPair.publicKey.toString(RADIX),
                getAddressFromPrivateKey(keyPair.privateKey.toString(RADIX)))
    }

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

    override fun createMnemonics(): String {
        return createMnemonics(ByteArray(Words.TWELVE.byteLength()))
    }

    override fun mnemonicsToPrivateKey(mnemonics: String, password: String?): String {
        return mnemonicsToKeyPair(mnemonics, password).privateKey.toString(RADIX)
    }

    override fun mnemonicsToPublicKey(mnemonics: String, password: String): String {
        return mnemonicsToKeyPair(mnemonics, password).publicKey.toString(RADIX)
    }

    override fun mnemonicsToKeyPair(mnemonics: String, password: String?): ECKeyPair {
        val seeds = MnemonicUtils.generateSeed(mnemonics, password)
        return ECKeyPair.create(Hash.sha256(seeds))
    }

    override fun walletToKeyPair(wallet: Bip39Wallet, password: String): ECKeyPair {
        return mnemonicsToKeyPair(wallet.mnemonic, password)
    }

    @Throws(IOException::class)
    override fun loadCredential(password: String, file: File): W3JLCredential? {
        val c = WalletUtils.loadCredentials(password, file)
        return W3JLCredential(c.address, c.ecKeyPair.publicKey.toString(RADIX), c.ecKeyPair.privateKey.toString(RADIX))
    }

    override fun loadCredentialFromPrivateKey(privateKey: String): Credentials? {
        return if (!WalletUtils.isValidPrivateKey(privateKey)) {
            null
        } else Credentials.create(privateKey)
    }

    override fun getAddressFromPrivateKey(privateKey: String): String {
        val credentials = loadCredentialFromPrivateKey(privateKey) ?: return ""
        return credentials.address
    }



    @Throws(Exception::class)
    override fun createWalletFromMnemonic(mnemonics: String, password: String?): Single<W3JLWallet> {
        return Single.create { emitter  ->
            try {
                val seed = MnemonicUtils.generateSeed(mnemonics, password)
                val keyPair = ECKeyPair.create(Hash.sha256(seed))
                val tmpWallet = Wallet.create(password, keyPair,PARAM_N,PARAM_P)
                val wallet = W3JLWallet()
                wallet.mnemonic = mnemonics
                wallet.source = W3JLWallet.Source.MNEMONIC
                wallet.address = "0x${tmpWallet.address}"
                wallet.privateKey = keyPair.privateKey.toString(RADIX)
                wallet.jsonSource = objectMapper.writeValueAsString(tmpWallet)
                wallet.createAt = Calendar.getInstance().timeInMillis
                emitter.onSuccess(wallet)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

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
                val tmpWallet = Wallet.create(password, c.ecKeyPair,PARAM_N,PARAM_P)
                wallet.address = c.address
                wallet.privateKey = privateKey
                wallet.jsonSource = objectMapper.writeValueAsString(tmpWallet)
                wallet.createAt = Calendar.getInstance().timeInMillis
                emitter.onSuccess(wallet)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }

    }

    override fun createWalletFromJsonString(jsonString: String, password: String?): Single<W3JLWallet> {
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
}