package com.midsummer.w3jl.service

import com.midsummer.w3jl.entity.W3JLWallet
import io.reactivex.Single

/**
 * Created by NienLe on 13,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface WalletRepository {
    fun createMnemonics(entropy: ByteArray): String
    fun createMnemonics(): String
    /*fun mnemonicsToPrivateKey(mnemonics: String, password: String?): String
    fun mnemonicsToPublicKey(mnemonics: String, password: String): String
    fun mnemonicsToKeyPair(mnemonics: String, password: String?): ECKeyPair
    @Throws(IOException::class)
    fun loadCredential(password: String, file: File): W3JLCredential?
    fun loadCredentialFromPrivateKey(privateKey: String): W3JLCredential?
    fun getAddressFromPrivateKey(privateKey: String): String?*/

    //fun createHDWalletFromMnemonic(mnemonics: String) : Single<W3JLWallet>
    fun createWalletFromMnemonic(mnemonics: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromPrivateKey(privateKey: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromJsonString(jsonString: String, password: String) : Single<W3JLWallet>



}