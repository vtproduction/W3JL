package com.midsummer.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.midsummer.w3jl.test.TestNova
import com.midsummer.w3jl.test.W3JL

class MainActivity : AppCompatActivity() {

    val TAG = "MAINNNN"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val w3jL = W3JL()
        w3jL.initWeb3j("https://ropsten.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06")

        val password = "123456"
        val walletFile = "UTC--2018-08-10T16-34-52.129--22a3140b08c8929af4fd2a80d068f9c82204e943.json"
        val addressFrom = "0x22a3140b08c8929af4fd2a80d068f9c82204e943"
        val addressTo = "0x95b52bcd93D4A87a7E98975F2245a57789a2D34d"

        val novaTest = TestNova()
        val mnemonics = novaTest.generateMnemonic()

        Log.d(TAG,mnemonics)
        Log.d(TAG,w3jL.getBalance("0xB89d25B8378d8E8b2CB11E6e1bF80fBf33386f45"))
        //Log.d(TAG,w3jL.mnemonic)
        Log.d(TAG,w3jL.MnemonicToWallet(mnemonics,"123456"))
    }
}
