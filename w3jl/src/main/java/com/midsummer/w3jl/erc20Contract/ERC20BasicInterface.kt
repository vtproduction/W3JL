package com.midsummer.w3jl.erc20Contract

import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt

import java.math.BigInteger
import rx.Observable

/**
 * Created by NienLe on 15,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
interface ERC20BasicInterface<T> {

    fun totalSupply(): RemoteCall<BigInteger>

    fun balanceOf(who: String): RemoteCall<BigInteger>

    fun transfer(to: String, value: BigInteger): RemoteCall<TransactionReceipt>

    fun getTransferEvents(transactionReceipt: TransactionReceipt): List<T>

    fun transferEventObservable(startBlock: DefaultBlockParameter,
                                endBlock: DefaultBlockParameter): Observable<T>

}