package com.midsummer.w3jl.service

import io.reactivex.Single
import org.web3j.crypto.CipherException
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.util.concurrent.ExecutionException

/**
 * Created by NienLe on 15,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface W3JLEthRepository {

    fun getAccountBalance(address: String) : Single<BigInteger>

    fun getAccountBalance(address: String, number : Int) : Single<String>

    fun getAccountTransactionCount(address: String) : Single<BigInteger>

    @Throws(InterruptedException::class, ExecutionException::class, IOException::class, CipherException::class)
    fun transfer(password: String, walletFile: File, from: String, to: String, amount: BigInteger): Single<String>

    @Throws(InterruptedException::class, ExecutionException::class)
    fun transfer(privateKey: String, from: String, to: String, amount: BigInteger): Single<String>
}