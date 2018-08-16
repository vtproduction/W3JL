package com.midsummer.w3jl.test

import android.util.Log

/**
 * Created by NienLe on 16,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class TestKethe {



    fun createPrivateKey(mnemonic: String) : String {
        try {
            /*val mnemonicWords = dirtyPhraseToMnemonicWords(mnemonic)
            return mnemonicWords.toKey("m/44'/60'/0'/0/0").keyPair.privateKey.toString(16)*/

            return ""
        } catch (e: Exception) {
            Log.d("DMMM",Log.getStackTraceString(e))
            return ""
        }
    }
}