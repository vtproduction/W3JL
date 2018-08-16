package com.midsummer.w3jl.service

import android.content.Context
import com.midsummer.w3jl.entity.W3JLTokenInfo
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
 * Created by NienLe on 15-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLToken(var web3j: Web3j, var context : Context) {

    @Throws(Exception::class)
    fun getBalance(wallet: String, tokenInfo: W3JLTokenInfo): BigDecimal? {
        val function = balanceOf(wallet)
        val responseValue = callSmartContractFunction(function, tokenInfo.address, wallet)
        val response = FunctionReturnDecoder.decode(
                responseValue, function.outputParameters)
        return if (response.size == 1) {
            BigDecimal((response[0] as Uint256).value)
        } else {
            null
        }
    }

    fun balanceOf(owner: String): Function {
        return Function(
                "balanceOf",
                listOf(Address(owner)),
                listOf(object : TypeReference<Uint256>() {

                }))
    }

    @Throws(Exception::class)
    fun callSmartContractFunction(
            function: Function, contractAddress: String, wallet: String): String {
        val encodedFunction = FunctionEncoder.encode(function)
        val response = web3j.ethCall(
                Transaction.createEthCallTransaction(wallet, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get()
        return response.value
    }


    fun createTokenTransferData(to: String, tokenAmount: BigInteger): ByteArray {
        val params = Arrays.asList(Address(to), Uint256(tokenAmount))
        val returnTypes = Arrays.asList<TypeReference<*>>(object : TypeReference<Bool>() {

        })
        val function = Function("transfer", params, returnTypes)
        val encodedFunction = FunctionEncoder.encode(function)
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
    }

    private val GAS_PRICE = BigInteger.valueOf(20_000_000_000L)
    private val GAS_LIMIT = BigInteger.valueOf(4300000)
    fun balanceOf(owner: String): Function {
        return Function(
                "balanceOf",
                listOf(Address(owner)),
                listOf(object : TypeReference<Uint256>() {

                }))
    }
    fun createTokenTransfer(from : String,
                            to: String,
                            privateKey : String,
                            contractAddress :  String,
                            amount: BigInteger) : String{
        val data = createTokenTransferData(to, amount)
        val nonce = web3j
                .ethGetTransactionCount(from, DefaultBlockParameterName.LATEST)
                .sendAsync().get().transactionCount
        val signedMessage = GethService(web3j, context)
                .signTransaction(from, privateKey, contractAddress, BigInteger.valueOf(0), GAS_PRICE, GAS_LIMIT, nonce.toLong(), data, 3)
        return web3j
                .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                .sendAsync().get().transactionHash


    }
}