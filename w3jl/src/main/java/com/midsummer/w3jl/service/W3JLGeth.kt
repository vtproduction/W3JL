package com.midsummer.w3jl.service

import android.content.Context
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.P
import com.fasterxml.jackson.databind.ObjectMapper
import org.ethereum.geth.*
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.protocol.Web3j
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset

/**
 * Created by NienLe on 16-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLGeth(web3j: Web3j, context: Context) : W3JLGethRepository{
    var keyStore: KeyStore
    private val N = 1 shl 9
    private val P = 1
    init {
        keyStore = KeyStore(File(context.filesDir, "store").absolutePath, Geth.LightScryptN, Geth.LightScryptP)
    }

    override fun signTransaction(signer: String,
                                 signerPrivateKey: String,
                                 toAddress: String,
                                 amount: BigInteger,
                                 gasPrice: BigInteger,
                                 gasLimit: BigInteger,
                                 nonce: Long,
                                 data: ByteArray,
                                 chainId: Long) :ByteArray {
        val value = BigInt(0)
        value.setString(amount.toString(), 10)

        val gasPriceBI = BigInt(0)
        gasPriceBI.setString(gasPrice.toString(), 10)

        val gasLimitBI = BigInt(0)
        gasLimitBI.setString(gasLimit.toString(), 10)

        val tx = Transaction(
                nonce,
                Address(toAddress),
                value,
                gasLimitBI,
                gasPriceBI,
                data)

        val chain = BigInt(chainId) // Chain identifier of the main net
        val gethAccount = importPrivateKey(signerPrivateKey,"123")
        keyStore.unlock(gethAccount,"123")
        val signed = keyStore.signTx(gethAccount, tx, chain)
        keyStore.lock(gethAccount.address)
        return signed.encodeRLP()
    }

    override fun createAccount(password: String): Account {
        return keyStore.newAccount(password)
    }

    override fun importKeystore(store: String, password: String, newPassword: String): Account {
        return keyStore
                .importKey(store.toByteArray(Charset.forName("UTF-8")), password, newPassword)
    }

    override fun importPrivateKey(privateKey: String, newPassword: String): Account {
        val key = BigInteger(privateKey, 16)
        val keypair = ECKeyPair.create(key)

        val walletFile = Wallet.create(newPassword, keypair, N, P)
        val t = ObjectMapper().writeValueAsString(walletFile)
        return importKeystore(t, newPassword, newPassword)
    }

    override fun exportAccount(wallet: String, password: String, newPassword: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAccount(address: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasAccount(address: String): Boolean {
        return keyStore.hasAddress(Address(address))
    }

    override fun fetchAccounts(): Array<String?> {
        val accounts = keyStore.accounts
        val len = accounts.size().toInt()
        val result = arrayOfNulls<String>(len)

        for (i in 0 until len) {
            val gethAccount = accounts.get(i.toLong())
            result[i] =gethAccount.address.hex.toLowerCase()
        }
        return result
    }
}