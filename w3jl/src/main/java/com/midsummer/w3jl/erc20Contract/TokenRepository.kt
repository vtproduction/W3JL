package com.midsummer.w3jl.erc20Contract

import com.midsummer.w3jl.entity.W3JLTokenInfo
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.ethereum.geth.Account
import org.ethereum.geth.BigInt
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Wallet
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created by NienLe on 15,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class TokenRepository(var web3j: Web3j) {

    @Throws(Exception::class)
    fun getBalance(address: String, tokenInfo: W3JLTokenInfo): BigDecimal? {
        val function = balanceOf(address)
        val responseValue = callSmartContractFunction(function, tokenInfo.address, address)

        val response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters())
        return if (response.size == 1) {
            BigDecimal((response[0] as Uint256).value)
        } else {
            null
        }
    }


    private fun balanceOf(owner: String): Function {
        return Function(
                "balanceOf",
                listOf(Address(owner)),
                listOf<TypeReference<*>>(object : TypeReference<Uint256>() {

                }))
    }

    @Throws(Exception::class)
    private fun callSmartContractFunction(
            function: org.web3j.abi.datatypes.Function, contractAddress: String, address: String): String {
        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3j.ethCall(
                Transaction.createEthCallTransaction(address, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get()
        return response.getValue()
    }


    fun createTokenTransferData(to: String, tokenAmount: BigInteger): ByteArray {
        val params = Arrays.asList(Address(to), Uint256(tokenAmount))

        val returnTypes = Arrays.asList<TypeReference<*>>(object : TypeReference<Bool>() {

        })

        val function = Function("transfer", params, returnTypes)
        val encodedFunction = FunctionEncoder.encode(function)
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
    }

    fun createTokenTransaction(from: String, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, password: String) : String {
        val count =
                web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST)
                        .sendAsync().get().transactionCount
        val data = createTokenTransferData(to, amount)
        return "stub!"
    }


    fun signTransaction(signer: Wallet, signerPassword: String, toAddress: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, nonce: Long, data: ByteArray, chainId: Long): ByteArray {
        val value = BigInt(0)
        value.setString(amount.toString(), 10)

        val gasPriceBI = BigInt(0)
        gasPriceBI.setString(gasPrice.toString(), 10)

        val gasLimitBI = BigInt(0)
        gasLimitBI.setString(gasLimit.toString(), 10)

        val tx = org.ethereum.geth.Transaction(
                nonce,
                org.ethereum.geth.Address(toAddress),
                value,
                gasLimitBI,
                gasPriceBI,
                data)

        val chain = BigInt(chainId) // Chain identifier of the main net
        val gethAccount = Account()

       /* keyStore.unlock(gethAccount, signerPassword)
        val signed = keyStore.signTx(gethAccount, tx, chain)
        keyStore.lock(gethAccount.getAddress())

        return signed.encodeRLP()*/
        return byteArrayOf(10)
    }
}