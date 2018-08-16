package com.midsummer.w3jl.service

import com.midsummer.w3jl.entity.W3JLTokenInfo
import io.reactivex.Single
import org.web3j.abi.datatypes.Function
import java.math.BigInteger

/**
 * Created by NienLe on 16-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface TokenRepository {

    fun getBalance(address : String) : Single<BigInteger>
    fun getBalance(address : String, tokenInfo: W3JLTokenInfo) : Single<BigInteger>
    fun createTokenTransfer(from : String,
                            to: String,
                            privateKey : String,
                            amount: BigInteger) : Single<String>
    fun callSmartContractFunction(
            function: Function, contractAddress: String, address: String): Single<String>
}