package com.midsummer.w3jl.test

import org.web3j.crypto.ContractUtils
import org.web3j.ens.Contracts
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService

/**
 * Created by NienLe on 15,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class TestContract {
    lateinit var web3j: Web3j

    init {
        web3j = Web3jFactory.build(HttpService("https://mainnet.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06"))
    }


    fun testContract() {
        val contractAddress = "0x8b353021189375591723e7384262f45709a3c3dc"
        val address = "0x6e7312d1028b70771bb9cdd9837442230a9349ca"

    }
}