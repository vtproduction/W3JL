package com.midsummer.w3jl.hdWallet

import com.fasterxml.jackson.databind.ObjectMapper
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.wallet.DeterministicSeed
import org.spongycastle.cms.RecipientId.password
import org.web3j.crypto.WalletFile
import java.security.SecureRandom
import com.fasterxml.jackson.core.JsonProcessingException
import kotlinx.io.IOException
import org.web3j.protocol.ObjectMapperFactory
import org.web3j.crypto.Wallet.createLight
import org.bitcoinj.crypto.MnemonicException
import org.bitcoinj.crypto.MnemonicCode
import org.web3j.crypto.CipherException
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Wallet
import org.web3j.utils.Numeric
import javax.crypto.Cipher


/**
 * Created by NienLe on 16,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */

class EthMnemonic{

    private val ETH_TYPE = "m/44'/60'/0'/0/0"

    fun mnemonicToPrivateKey(mnemonic: String) : String {
        val pathArray = ETH_TYPE.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val passphrase = ""
        val list = mnemonic.split(" ")
        val creationTimeSeconds = System.currentTimeMillis() / 1000
        val ds = DeterministicSeed(list, null, passphrase, creationTimeSeconds)
        val seedBytes = ds.seedBytes
        val mnemonic = ds.mnemonicCode
        val mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(mnemonic)
        val mnemonicKeyPair = ECKeyPair.create(mnemonicSeedBytes)
        var dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes)
        for (i in 1 until pathArray.size) {
            val childNumber: ChildNumber
            if (pathArray[i].endsWith("'")) {
                val number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length - 1))
                childNumber = ChildNumber(number, true)
            } else {
                val number = Integer.parseInt(pathArray[i])
                childNumber = ChildNumber(number, false)
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber)
        }
        val keyPair = ECKeyPair.create(dkKey.privKeyBytes)
        return "privateKey: ${keyPair.privateKey.toString(16)} , publicKey: ${keyPair.publicKey.toString(16)}"
    }

    fun importMnemonic(mnemonic : String, password: String): EthHDWallet? {
        val pathArray = ETH_TYPE.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (pathArray.size <= 1) {
            //内容不对
            return null
        }

        val passphrase = ""
        val list = mnemonic.split(" ")
        val creationTimeSeconds = System.currentTimeMillis() / 1000
        val ds = DeterministicSeed(list, null, passphrase, creationTimeSeconds)
        return createEthWallet(ds, pathArray, password)
    }

    private fun createEthWallet(ds: DeterministicSeed, pathArray: Array<String>, password: String): EthHDWallet? {
        //根私钥
        val seedBytes = ds.seedBytes
        System.out.println("根私钥 " + seedBytes!!.contentToString())
        //助记词
        val mnemonic = ds.mnemonicCode
        System.out.println("助记词 " + mnemonic!!.toTypedArray().contentToString())

        try {
            //助记词种子
            val mnemonicSeedBytes = MnemonicCode.INSTANCE.toEntropy(mnemonic)
            System.out.println("助记词种子 " + mnemonicSeedBytes.contentToString())
            val mnemonicKeyPair = ECKeyPair.create(mnemonicSeedBytes)
            val walletFile = Wallet.createLight(password, mnemonicKeyPair)
            val objectMapper = ObjectMapperFactory.getObjectMapper()
            //存这个keystore 用完后删除
            val jsonStr = objectMapper.writeValueAsString(walletFile)
            println("mnemonic keystore $jsonStr")
            //验证
            val checkWalletFile = objectMapper.readValue(jsonStr, WalletFile::class.java)
            val ecKeyPair = Wallet.decrypt(password, checkWalletFile)
            val checkMnemonicSeedBytes = Numeric.hexStringToByteArray(ecKeyPair.getPrivateKey().toString(16))
            System.out.println("验证助记词种子 " + checkMnemonicSeedBytes.contentToString())
            val checkMnemonic = MnemonicCode.INSTANCE.toMnemonic(checkMnemonicSeedBytes)
            System.out.println("验证助记词 " + checkMnemonic.toTypedArray().contentToString())

        } catch (e: MnemonicException.MnemonicLengthException) {
            e.printStackTrace()
        } catch (e: MnemonicException.MnemonicWordException) {
            e.printStackTrace()
        } catch (e: MnemonicException.MnemonicChecksumException) {
            e.printStackTrace()
        } catch (e: CipherException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (seedBytes == null)
            return null
        var dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes)
        for (i in 1 until pathArray.size) {
            val childNumber: ChildNumber
            if (pathArray[i].endsWith("'")) {
                val number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length - 1))
                childNumber = ChildNumber(number, true)
            } else {
                val number = Integer.parseInt(pathArray[i])
                childNumber = ChildNumber(number, false)
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber)
        }
        System.out.println("path " + dkKey.pathAsString)

        val keyPair = ECKeyPair.create(dkKey.privKeyBytes)
        System.out.println("eth privateKey " + keyPair.getPrivateKey().toString(16))
        System.out.println("eth publicKey " + keyPair.getPublicKey().toString(16))

        var ethHDWallet: EthHDWallet? = null
        try {
            val walletFile = Wallet.createLight(password, keyPair)
            println("eth address " + "0x" + walletFile.getAddress())
            val objectMapper = ObjectMapperFactory.getObjectMapper()
            //存
            val jsonStr = objectMapper.writeValueAsString(walletFile)
            println("eth keystore $jsonStr")

            ethHDWallet = EthHDWallet(keyPair.getPrivateKey().toString(16),
                    keyPair.getPublicKey().toString(16),
                    mnemonic, dkKey.pathAsString,
                    "0x" + walletFile.getAddress(), jsonStr)
        } catch (e: CipherException) {
            e.printStackTrace()
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }

        return ethHDWallet
    }
}