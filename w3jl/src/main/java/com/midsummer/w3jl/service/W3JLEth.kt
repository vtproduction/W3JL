package com.midsummer.w3jl.service

import com.fasterxml.jackson.module.kotlin.readValue
import com.midsummer.w3jl.entity.W3JLWallet
import com.midsummer.w3jl.util.BalanceUtil
import io.reactivex.Single
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.tx.ManagedTransaction.GAS_PRICE
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import java.util.*

/**
 * Created by NienLe on 15,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLEth(var web3j: Web3j) : W3JLEthRepository{

    private val GAS_PRICE = BigInteger.valueOf(20_000_000_000L)
    private val GAS_LIMIT = BigInteger.valueOf(4300000)
    override fun getAccountBalance(address: String): Single<BigInteger> {
        return Single.create{ emitter ->
            try {
                if(WalletUtils.isValidAddress(address)){
                    emitter.onError(Exception("Invalid Address"))
                }else{
                    val ethGetBalance : EthGetBalance = web3j
                            .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                            .sendAsync()
                            .get()
                    emitter.onSuccess(ethGetBalance.balance)
                }
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    override fun getAccountBalance(address: String, number: Int): Single<String> {
        return Single.create{ emitter ->
            try {
                if(WalletUtils.isValidAddress(address)){
                    emitter.onError(Exception("Invalid Address"))
                }else{
                    val ethGetBalance : EthGetBalance = web3j
                            .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                            .sendAsync()
                            .get()
                    emitter.onSuccess(BalanceUtil.weiToEth(ethGetBalance.balance, number))
                }
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    override fun getAccountTransactionCount(address: String): Single<BigInteger> {
        return Single.create{ emitter ->
            try {
                if(WalletUtils.isValidAddress(address)){
                    emitter.onError(Exception("Invalid Address"))
                }else{
                    val ethGetTransactionCount : EthGetTransactionCount = web3j
                            .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                            .sendAsync()
                            .get()
                    emitter.onSuccess(ethGetTransactionCount.transactionCount)
                }
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    override fun transfer(password: String, walletFile: File, from: String, to: String, amount: BigInteger): Single<String> {
        return Single.create{ emitter ->
            try {
                val credentials = WalletUtils.loadCredentials(password, walletFile)
                if (credentials == null){
                    emitter.onError(Exception("Invalid credential!"))
                    return@create
                }
                val ethGetTransactionCount = web3j.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).sendAsync().get()
                val nonce = ethGetTransactionCount.transactionCount
                //println(nonce)
                val rawTransaction = RawTransaction.createEtherTransaction(
                        nonce, GAS_PRICE, GAS_LIMIT, to, amount)
                val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
                val hexValue = Numeric.toHexString(signedMessage)
                val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
                emitter.onSuccess(ethSendTransaction.transactionHash)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    override fun transfer(privateKey: String, from: String, to: String, amount: BigInteger): Single<String> {
        return Single.create{ emitter ->
            try {
                val credentials = Credentials.create(privateKey)
                if (credentials == null){
                    emitter.onError(Exception("Invalid credential!"))
                    return@create
                }

                val ethGetTransactionCount = web3j.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).sendAsync().get()

                val nonce = ethGetTransactionCount.transactionCount
                println(nonce)

                val rawTransaction = RawTransaction.createEtherTransaction(
                        nonce, GAS_PRICE, GAS_LIMIT, to, amount)
                val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
                val hexValue = Numeric.toHexString(signedMessage)

                val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
                emitter.onSuccess(ethSendTransaction.transactionHash)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }
}