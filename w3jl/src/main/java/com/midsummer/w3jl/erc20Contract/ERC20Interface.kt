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

interface ERC20Interface<R, T> : ERC20BasicInterface<T> {

    fun allowance(owner: String, spender: String): RemoteCall<BigInteger>

    fun approve(spender: String, value: BigInteger): RemoteCall<TransactionReceipt>

    fun transferFrom(from: String, to: String, value: BigInteger): RemoteCall<TransactionReceipt>

    fun getApprovalEvents(transactionReceipt: TransactionReceipt): List<R>

    fun approvalEventObservable(startBlock: DefaultBlockParameter,
                                endBlock: DefaultBlockParameter): Observable<R>

}