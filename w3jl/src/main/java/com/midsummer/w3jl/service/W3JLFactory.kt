package com.midsummer.w3jl.service

import android.content.Context
import com.midsummer.w3jl.erc20Contract.TokenRepository
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import java.io.File

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLFactory {

    private lateinit var context : Context
    private var keyStoreFilePath : String = "keystore"
    private var networkProvider : String = ""
    private lateinit var keyStoreFile: File


    fun withContext(context: Context) : W3JLFactory {
        this.context = context
        keyStoreFile = File(context.applicationInfo.dataDir, keyStoreFilePath)
        return this
    }

    fun withNetworkProvider(networkProvider : String) : W3JLFactory {
        this.networkProvider = networkProvider
        return this
    }


    fun buildW3JL() : W3JLWalletRepository {
        return W3JLWallet(keyStoreFile)
    }

    fun buildW3JLEth() : W3JLEthRepository {
        val web3j = Web3jFactory.build(if (networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )
        return W3JLEth(web3j)
    }


    fun buildW3JLToken() : W3JLToken {
        var web3j = Web3jFactory.build(if (networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )
        return W3JLToken(web3j, context)
    }
}