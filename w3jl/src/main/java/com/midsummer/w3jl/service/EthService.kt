package com.midsummer.w3jl.service

import com.midsummer.w3jl.util.BalanceUtil
import io.reactivex.Single
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by NienLe on 15,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */


class EthService(var web3j: Web3j) : EthRepository{

    //default gasPrice
    private val GAS_PRICE = BigInteger.valueOf(20_000_000_000L)

    //default gasLimit
    private val GAS_LIMIT = BigInteger.valueOf(4300000)

    /**
     * get the Ether balance of given account
     * @param address plain text of address
     * @return raw number of account balance
     */
    override fun getAccountBalance(address: String): Single<BigInteger> {
        return Single.create{ emitter ->
            try {
                if(WalletUtils.isValidAddress(address)){
                    emitter.onError(Exception("Invalid Address"))
                    return@create
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

    /**
     * get the Ether balance of given account, return human-family format
     * @param address plain text of address
     * @param number number of chars that the result will return
     * @return formatted value of account balance
     */
    override fun getAccountBalance(address: String, number: Int): Single<String> {
        return Single.create{ emitter ->
            try {
                if(WalletUtils.isValidAddress(address)){
                    emitter.onError(Exception("Invalid Address"))
                    return@create
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

    /**
     * get total numbers of transaction that made by the given account
     * @param address plain text of address
     * @return number of transactions
     */
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

    /**
     * Perform send Eth transaction
     * @param privateKey: Wallet private key
     * @param to: Address of receiver
     * @param amount: number of ETH to be sent
     * @param gasPrice: value of transaction gas price
     * @param gasLimit: value of transaction gas limit
     * @return the transaction Id
     */
    override fun transfer(privateKey: String,  to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger): Single<String> {
        return Single.create{ emitter ->
            try {
                val credentials = Credentials.create(privateKey)
                if (credentials == null){
                    emitter.onError(Exception("Invalid credential!"))
                    return@create
                }
                val from = credentials.address
                val ethGetTransactionCount = web3j.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).sendAsync().get()

                val nonce = ethGetTransactionCount.transactionCount
                println(nonce)

                val rawTransaction = RawTransaction.createEtherTransaction(
                        nonce, gasPrice, gasLimit, to, amount)
                val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
                val hexValue = Numeric.toHexString(signedMessage)

                val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
                emitter.onSuccess(ethSendTransaction.transactionHash)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    /**
     * Perform send Eth transaction
     * @param password: Wallet encrypted password
     * @param walletFile: The file that contain encrypted json data of wallet
     * @param to: Address of receiver
     * @param amount: number of ETH to be sent
     * @param gasPrice: value of transaction gas price
     * @param gasLimit: value of transaction gas limit
     * @return the transaction Id
     */
    override fun transfer(password: String, walletFile: File,  to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger): Single<String> {
        return Single.create{ emitter ->
            try {
                val credentials = WalletUtils.loadCredentials(password, walletFile)
                if (credentials == null){
                    emitter.onError(Exception("Invalid credential!"))
                    return@create
                }
                val from = credentials.address
                val ethGetTransactionCount = web3j.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).sendAsync().get()

                val nonce = ethGetTransactionCount.transactionCount
                println(nonce)

                val rawTransaction = RawTransaction.createEtherTransaction(
                        nonce, gasPrice, gasLimit, to, amount)
                val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
                val hexValue = Numeric.toHexString(signedMessage)

                val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
                emitter.onSuccess(ethSendTransaction.transactionHash)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    /**
     * Perform transfer with default gasPrice and gasLimit
     */
    override fun transfer(password: String, walletFile: File, to: String, amount: BigInteger): Single<String> {
        return transfer(password, walletFile, to, amount, GAS_PRICE, GAS_LIMIT)
    }

    /**
     * Perform transfer with default gasPrice and gasLimit
     */
    override fun transfer(privateKey: String, to: String, amount: BigInteger): Single<String> {
        return transfer(privateKey, to, amount, GAS_PRICE, GAS_LIMIT)
    }
}