package com.midsummer.w3jl.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import com.midsummer.w3jl.entity.W3JLBip39Wallet
import com.midsummer.w3jl.entity.W3JLCredential
import com.midsummer.w3jl.entity.W3JLWallet
import com.midsummer.w3jl.util.FileUtil
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JL(var web3j: Web3j, var filePath: File) : W3JLRepository{
    private val RADIX = 16
    private val GAS_PRICE = BigInteger.valueOf(20_000_000_000L)
    private val GAS_LIMIT = BigInteger.valueOf(4300000)
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

    override fun transfer(password: String, walletFile: File, from: String, to: String, amount: BigInteger): String? {
        val credentials = WalletUtils.loadCredentials(password, walletFile) ?: return null

        val ethGetTransactionCount = web3j.ethGetTransactionCount(
                from, DefaultBlockParameterName.LATEST).sendAsync().get()

        val nonce = ethGetTransactionCount.transactionCount
        println(nonce)

        val rawTransaction = RawTransaction.createEtherTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, to, amount)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)

        val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
        return ethSendTransaction.transactionHash
    }

    override fun transfer(privateKey: String, from: String, to: String, amount: BigInteger): String? {
        val credentials = loadCredentialFromPrivateKey(privateKey) ?: return null

        val ethGetTransactionCount = web3j.ethGetTransactionCount(
                from, DefaultBlockParameterName.LATEST).sendAsync().get()

        val nonce = ethGetTransactionCount.transactionCount
        println(nonce)

        val rawTransaction = RawTransaction.createEtherTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, to, amount)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)

        val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
        return ethSendTransaction.transactionHash
    }


    override fun createWalletFromMnemonic(mnemonics: String, password: String?): W3JLWallet {
        val seed = MnemonicUtils.generateSeed(mnemonics, password)
        val keyPair = ECKeyPair.create(Hash.sha256(seed))
        val tmpWallet = Wallet.createLight(password, keyPair)
        val wallet = W3JLWallet()
        wallet.mnemonic = mnemonics
        wallet.source = W3JLWallet.Source.MNEMONIC
        wallet.address = "0x${tmpWallet.address}"
        wallet.privateKey = keyPair.privateKey.toString(RADIX)
        wallet.jsonSource = Gson().toJson(tmpWallet)
        wallet.createAt = Calendar.getInstance().timeInMillis
        return wallet
    }

    override fun createWalletFromPrivateKey(privateKey: String, password: String?): W3JLWallet {
        val wallet = W3JLWallet()
        if (!WalletUtils.isValidPrivateKey(privateKey)){
            return wallet
        }
        wallet.source = W3JLWallet.Source.PRIVATE_KEY
        val c = Credentials.create(privateKey)
        val tmpWallet = Wallet.createLight(password, c.ecKeyPair)
        wallet.address = c.address
        wallet.privateKey = privateKey
        wallet.jsonSource = objectMapper.writeValueAsString(tmpWallet)
        wallet.createAt = Calendar.getInstance().timeInMillis
        return wallet
    }

    override fun createWalletFromJsonString(jsonString: String, password: String?): W3JLWallet {
        val walletFile : WalletFile = objectMapper.readValue(jsonString)
        val c = Credentials.create(Wallet.decrypt(password, walletFile))
        val wallet = W3JLWallet()
        wallet.source = W3JLWallet.Source.JSON
        wallet.address = c.address
        wallet.privateKey = c.ecKeyPair.privateKey.toString(RADIX)
        wallet.jsonSource = jsonString
        wallet.createAt = Calendar.getInstance().timeInMillis
        return wallet
    }
}