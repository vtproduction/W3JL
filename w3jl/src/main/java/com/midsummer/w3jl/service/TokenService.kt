package com.midsummer.w3jl.service

import com.midsummer.w3jl.entity.W3JLTokenInfo
import io.reactivex.Single
import org.web3j.abi.datatypes.Function
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import java.math.BigInteger

/**
 * Created by NienLe on 17-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class TokenService(var tokenInfo : W3JLTokenInfo) : TokenRepository {

    override fun getBalance(address : String): Single<BigInteger> {
        return Single.create{ emitter ->
            try {

            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }

    override fun getBalance(address : String, tokenInfo: W3JLTokenInfo): Single<BigInteger> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createTokenTransfer(from: String, to: String, privateKey: String, amount: BigInteger): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun callSmartContractFunction(function: Function, contractAddress: String, address: String): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}