package com.midsummer.w3jl.entity

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
/**
 * Wallet object that will be use in most case of W3JL module
 */
class W3JLWallet {
    init {

    }

    /**
     * The Source enum will determine which case the wallet is created or imported
     * - PRIVATE_KEY: Generate wallet using the plain private key. The wallet will contain private key, public key, address and the encrypted json data
     * - MNEMONIC: Generate wallet using seed words. The HDPath used is m/44'/60'/0'/0/0. The wallet will contain fully information
     * - JSON: Use to recover/import wallet from the encrypted json data, along with password. The wallet will contain fully information, except mnemonic
     * NOTE:
     *      - Since we can create wallet using mnemonic, but the opposite way is impossible, then only wallet with source = MNEMONIC has the mnemonic field
     *      - The jsonSource can only be generated when user provide 'password' that used to encrypt the wallet data, so in case we just want to create the
     *      wallet without any willing to backup/export to json, just create wallet with NULL password, and the jsonSource will be left blank
     *      - The createAt is useless now, no need to consider
     */
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
    var publicKey : String = ""
    var jsonSource : String = ""
    var createAt : Long = 0


    override fun toString(): String {
        return "WalletService(source=$source, address='$address', mnemonic='$mnemonic', privateKey='$privateKey', publicKey='$publicKey', jsonSource='$jsonSource', createAt=$createAt)"
    }


}