package com.midsummer.w3jl.service

import android.content.Context
import com.midsummer.w3jl.entity.W3JLTokenInfo
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
    private lateinit var tokenInfo: W3JLTokenInfo


    fun withContext(context: Context) : W3JLFactory {
        this.context = context
        keyStoreFile = File(context.applicationInfo.dataDir, keyStoreFilePath)
        return this
    }

    fun withNetworkProvider(networkProvider : String) : W3JLFactory {
        this.networkProvider = networkProvider
        return this
    }

    fun withTokenInfo(tokenInfo: W3JLTokenInfo) : W3JLFactory {
        this.tokenInfo = tokenInfo
        return this
    }


    fun buildW3JL() : WalletRepository {
        return WalletService(keyStoreFile)
    }

    fun buildW3JLEth() : EthRepository {
        val web3j = Web3jFactory.build(if (networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )
        return EthService(web3j)
    }


    fun buildW3JLToken() : TokenService {
        val web3j = Web3jFactory.build(if (networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )
        return TokenService(context, web3j, tokenInfo)
    }
}