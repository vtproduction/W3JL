package com.midsummer.w3jl.service

import com.midsummer.w3jl.entity.W3JLBip39Wallet
import com.midsummer.w3jl.entity.W3JLCredential
import com.midsummer.w3jl.entity.W3JLWallet
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONStringer
import org.web3j.crypto.Bip39Wallet
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.util.concurrent.ExecutionException

/**
 * Created by NienLe on 13,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface W3JLRepository {
    @Throws(IOException::class)
    fun createBip39WalletWithPassword(password: String): W3JLBip39Wallet

    fun createBip39WalletWithMnemonic(mnemonics: String) : W3JLBip39Wallet

    fun createBip39WalletWithPasswordAndMnemonic(password: String, mnemonics: String) : W3JLBip39Wallet

    fun createMnemonics(entropy: ByteArray): String

    fun createMnemonics(): String

    fun mnemonicsToPrivateKey(mnemonics: String, password: String?): String

    fun mnemonicsToPublicKey(mnemonics: String, password: String): String

    fun mnemonicsToKeyPair(mnemonics: String, password: String?): ECKeyPair

    fun walletToKeyPair(wallet: Bip39Wallet, password: String): ECKeyPair
    
    @Throws(IOException::class)
    fun loadCredential(password: String, file: File): W3JLCredential?

    fun loadCredentialFromPrivateKey(privateKey: String): Credentials?

    fun getAddressFromPrivateKey(privateKey: String): String?

    @Throws(InterruptedException::class, ExecutionException::class, IOException::class, CipherException::class)
    fun transfer(password: String, walletFile: File, from: String, to: String, amount: BigInteger): String?

    @Throws(InterruptedException::class, ExecutionException::class)
    fun transfer(privateKey: String, from: String, to: String, amount: BigInteger): String?


    fun createWalletFromMnemonic(mnemonics: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromPrivateKey(privateKey: String, password: String?) : W3JLWallet
    fun createWalletFromJsonString(jsonString: String, password: String?) : W3JLWallet

}