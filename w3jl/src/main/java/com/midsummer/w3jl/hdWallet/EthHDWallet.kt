package com.midsummer.w3jl.hdWallet

/**
 * Created by NienLe on 16,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class EthHDWallet(
        var privateKey: String,
        var publicKey: String,
        var mnemonic: List<String>,
        var mnemonicPath: String,
        var Address: String,
        var keystore: String){

    override fun toString(): String {
        return "EthHDWallet(privateKey='$privateKey', publicKey='$publicKey', mnemonic=$mnemonic, mnemonicPath='$mnemonicPath', Address='$Address', keystore='$keystore')"
    }
}