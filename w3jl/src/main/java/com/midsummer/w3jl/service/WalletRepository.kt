package com.midsummer.w3jl.service

import com.midsummer.w3jl.entity.W3JLBip39Wallet
import com.midsummer.w3jl.entity.W3JLCredential
import com.midsummer.w3jl.entity.W3JLWallet
import io.reactivex.Single
import org.web3j.crypto.Bip39Wallet
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.io.File
import java.io.IOException

/**
 * Created by NienLe on 13,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface WalletRepository {

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


    fun createHDWalletFromMnemonic(mnemonics: String) : Single<W3JLWallet>
    fun createWalletFromMnemonic(mnemonics: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromPrivateKey(privateKey: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromJsonString(jsonString: String, password: String?) : Single<W3JLWallet>


}