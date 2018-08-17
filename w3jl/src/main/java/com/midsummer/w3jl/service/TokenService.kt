package com.midsummer.w3jl.service

import android.content.Context
import com.midsummer.w3jl.entity.W3JLTokenInfo
import io.reactivex.Single
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created by NienLe on 17-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */


class TokenService(var context: Context, var web3j: Web3j, var mTokenInfo : W3JLTokenInfo?) : TokenRepository {

    /**
     * Set token info. The token info will be used in this class
     */
    override fun setTokenInfo(tokenInfo: W3JLTokenInfo) {
        this.mTokenInfo = tokenInfo
    }

    /**
     * get current token balance of given address
     * @see getBalance
     */
    override fun getBalance(address : String): Single<BigDecimal> {
        return getBalance(address, mTokenInfo!!)
    }


    /**
     * get current token balance of given address
     * @param address: plain address
     * @param tokenInfo: the token info, contain token contract address
     * @return big number present token balance
     */
    override fun getBalance(address : String, tokenInfo: W3JLTokenInfo): Single<BigDecimal> {
        return Single.create{ emitter ->
            val function = balanceOf(address)
            val responseValue = callSmartContractFunction(function, tokenInfo.address, address)
            val response = FunctionReturnDecoder.decode(
                    responseValue, function.outputParameters)
            if (response.size == 1) {
                emitter.onSuccess(BigDecimal((response[0] as Uint256).value))
            } else {
                emitter.onError(Exception("Unknown"))
            }
        }
    }

    /**
     * perform transfer token from one address to another address.
     * @see GethRepository.signTransaction
     * @param from: The sender address
     * @param to: The receiver address
     * @param privateKey: private key of signer, will be use sign the message
     * @param amount: number of token to transfer
     * @param gasPrice: transaction gas price
     * @param gasLimit: transaction gas limit
     * @return transaction Id
     */
    override fun transferToken(from: String, to: String, privateKey: String, amount: BigInteger,
                                     gasPrice: BigInteger,
                                     gasLimit: BigInteger): Single<String> {
        return Single.create { emitter ->
            try {
                val data = createTokenTransferData(to, amount)
                val nonce = web3j
                        .ethGetTransactionCount(from, DefaultBlockParameterName.LATEST)
                        .sendAsync().get().transactionCount
                val signedMessage = GethService(context)
                        .signTransaction(from, privateKey, mTokenInfo!!.address, BigInteger.valueOf(0), gasPrice, gasLimit, nonce.toLong(), data, 3)
                emitter.onSuccess(web3j
                        .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                        .sendAsync().get().transactionHash)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    /**
     * Call the smart contract specific function. This should be public soon
     * todo: modify 'function' param to use outside this class/module
     * @param function: the smart contract function, contain identifier and params
     * @param contractAddress: the address of contract that execute the function
     * @param address: the address that call the function
     * @return transaction id
     */
    private fun callSmartContractFunction(function: Function, contractAddress: String, address: String): String {
        val encodedFunction = FunctionEncoder.encode(function)
        val response = web3j.ethCall(
                Transaction.createEthCallTransaction(address, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get()
        return response.value
    }


    /**
     * create Function that point to smart contract 'balanceOf' function
     * @param owner: address of wallet that you want to get balance
     * @return Function object
     */
    private fun balanceOf(owner: String): Function {
        return Function(
                "balanceOf",
                listOf(Address(owner)),
                listOf(object : TypeReference<Uint256>() {

                }))
    }

    /**
     * create and encode the Function that point to smart contract 'transfer' function.
     * @param to: address of receiver
     * @param tokenAmount: amount of token to be sent
     * @return byteArray of Function object, ready to be signed
     */
    private fun createTokenTransferData(to: String, tokenAmount: BigInteger): ByteArray {
        val params = Arrays.asList(Address(to), Uint256(tokenAmount))
        val returnTypes = Arrays.asList<TypeReference<*>>(object : TypeReference<Bool>() {

        })
        val function = Function("transfer", params, returnTypes)
        val encodedFunction = FunctionEncoder.encode(function)
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
    }
}