package com.midsummer.w3jl.entity

import java.security.PrivateKey

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLBip39Wallet (var filename: String, var mnemonic: String, var privateKey: String, var publicKey: String, var address: String) {

    override fun toString(): String {
        return "W3JLBip39Wallet(filename='$filename', mnemonic='$mnemonic', privateKey='$privateKey', publicKey='$publicKey', address='$address')"
    }
}