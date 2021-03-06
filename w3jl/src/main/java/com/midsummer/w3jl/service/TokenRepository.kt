package com.midsummer.w3jl.service

import com.midsummer.w3jl.entity.W3JLTokenInfo
import io.reactivex.Single
import org.web3j.abi.datatypes.Function
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by NienLe on 16-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface TokenRepository {

    fun setTokenInfo(tokenInfo: W3JLTokenInfo)
    fun getBalance(address : String) : Single<BigDecimal>
    fun getBalance(address : String, tokenInfo: W3JLTokenInfo) : Single<BigDecimal>
    fun transferToken(
        from : String,
        to: String,
        privateKey : String,
        amount: BigInteger,
        gasPrice: BigInteger,
        gasLimit: BigInteger) : Single<String>
    /*fun callSmartContractFunction(
            function: Function, contractAddress: String, address: String): String*/
}