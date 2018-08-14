package com.midsummer.w3jl.test

import io.github.novacrypto.bip39.wordlists.English
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import java.security.SecureRandom


/**
 * Created by nienb on 14-Aug-18.
 */
class TestNova {


    fun generateMnemonic() : String {
        val sb = StringBuilder()
        val entropy = ByteArray(Words.TWELVE.byteLength())
        SecureRandom().nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
                .createMnemonic(
                        entropy
                ) {
                    sb.append(it)
                }


        return sb.toString()
    }
}