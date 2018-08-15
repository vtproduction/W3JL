package com.midsummer.w3jl.entity

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLWallet {
    init {

    }

    enum class Source{
        PRIVATE_KEY,
        MNEMONIC,
        JSON,
        NONE
    }

    var source : Source = Source.NONE
    var address : String = ""
    var mnemonic : String = ""
    var privateKey : String = ""
    var jsonSource : String = ""
    var createAt : Long = 0



    override fun toString(): String {
        return "W3JLWallet(source=$source, address='$address', mnemonic='$mnemonic', privateKey='$privateKey', jsonSource='$jsonSource', createAt=$createAt)"
    }


}