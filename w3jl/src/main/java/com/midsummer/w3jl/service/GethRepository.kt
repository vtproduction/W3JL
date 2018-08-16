package com.midsummer.w3jl.service

import io.reactivex.Single
import org.ethereum.geth.Account
import org.web3j.crypto.Wallet
import java.math.BigInteger

/**
 * Created by NienLe on 16-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface GethRepository {

    fun createAccount(password: String) : Account
    fun importKeystore(store: String, password: String, newPassword: String): Account
    fun importPrivateKey(privateKey: String, newPassword: String): Account
    fun exportAccount(wallet: String, password: String, newPassword: String): String
    fun deleteAccount(address: String, password:  String)
    fun signTransaction(signer: String, signerPrivateKey: String,
                        toAddress: String, amount: BigInteger,
                        gasPrice: BigInteger, gasLimit: BigInteger,
                        nonce: Long, data: ByteArray, chainId: Long) :ByteArray

    fun hasAccount(address: String): Boolean
    fun fetchAccounts(): Array<String?>
}