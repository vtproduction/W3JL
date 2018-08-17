package com.midsummer.w3jl.entity



/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */


/**
 * Model object that hold basic information about a wallet
 */
class W3JLCredential(var address: String, var publicKey: String, var privateKey: String){

    override fun toString(): String {
        return "W3JLCredential(address='$address', publicKey='$publicKey', privateKey='$privateKey')"
    }
}