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

/**
 * The builder class to generate services
 */
class W3JLFactory {

    private var context : Context? = null
    private var keyStoreFilePath : String = "keystore"
    private var networkProvider : String = ""
    private var keyStoreFile: File? = null
    private var tokenInfo: W3JLTokenInfo? = null


    fun withContext(context: Context) : W3JLFactory {
        this.context = context
        keyStoreFile = File(context.applicationInfo.dataDir, keyStoreFilePath)
        return this
    }

    fun withKeyStoreFile(keyStoreFile: File) : W3JLFactory {
        this.keyStoreFile = keyStoreFile
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

    /**
     * Create instance of WalletService
     * Require the keyStoreFile is not null
     * Call one of these functions first, or call both
     * @see withContext
     * @see withKeyStoreFile
     * @return instance of WalletRepository
     */
    fun buildW3JLWallet() : WalletRepository {
        check(keyStoreFile != null) {"Please init context | keyStoreFile first"}
        return WalletService(keyStoreFile!!)
    }

    /**
     * Create instance of EthService
     * @return instance of EthRepository
     */
    fun buildW3JLEth() : EthRepository {
        val web3j = Web3jFactory.build(if (networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )
        return EthService(web3j)
    }

    /**
     * Create instance of TokenService
     * Require the context and tokenInfo are not null
     * Call these functions first
     * @see withContext
     * @see withTokenInfo
     * @return instance of WalletRepository
     */
    fun buildW3JLToken() : TokenService {
        check(context != null) {"Please init context first, using withContext(context: Context)"}
        check(tokenInfo != null) {"Please init TokenInfo first, using withTokenInfo(tokenInfo: W3JLTokenInfo)"}
        val web3j = Web3jFactory.build(if (networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )
        return TokenService(context!!, web3j, tokenInfo)
    }
}