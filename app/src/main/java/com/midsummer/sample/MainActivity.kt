package com.midsummer.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.midsummer.w3jl.service.W3JLFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    val TAG = "MAINNNN"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*val w3jL = W3JL()
        w3jL.initWeb3j("https://ropsten.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06")*/
        val fileDir = File(applicationInfo.dataDir, "keystore")
        if (!fileDir.exists())
            fileDir.mkdirs()
        val password = "123456654321"
        val walletFile = "UTC--2018-08-10T16-34-52.129--22a3140b08c8929af4fd2a80d068f9c82204e943.json"
        val addressFrom = "0x22a3140b08c8929af4fd2a80d068f9c82204e943"
        val addressTo = "0x95b52bcd93D4A87a7E98975F2245a57789a2D34d"

        /*val novaTest = TestNova()
        val mnemonics = novaTest.generateMnemonic()

        val w3JL = W3JLFactory()
                .withContext(this)
                .withNetworkProvider("https://ropsten.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06")
                .build()
        val wallet = w3JL.createBip39Wallet(password,fileDir)
        Log.d(TAG, wallet.toString())*/

        val w3JL = W3JLFactory()
                .withContext(this)
                .withNetworkProvider("https://ropsten.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06")
                .build()
        /*val w = w3JL.createBip39WalletWithPasswordAndMnemonic(password,"cause round witness insect capable what school fire bread truly auto enable")
        Log.d(TAG, w.toString())
        val walletFile2 = File(fileDir, w.filename)
        val str = FileUtil.readJsonFile(walletFile2)
        val c = w3JL.loadCredential(password, walletFile2)
        Log.d(TAG,c.toString())
        Log.d(TAG,str)*/


        val mnemonic = "cause round witness insect capable what school fire bread truly auto enable"
        compositeDisposable.add(w3JL.createWalletFromMnemonic(mnemonic, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(5, TimeUnit.SECONDS)
                .subscribe({
                    Log.d(TAG,"wallet: ${it.toString()}")
                },{
                    Log.d(TAG,"error: ${it.localizedMessage}")
                }))

        //Log.d(TAG, w1.toString())

        /*val privateKey = "ee0ba891fd4ec42e967dcd26306a7352228e6dc71d1211a95ab3dc1c208b61bd"
        val w1 = w3JL.createWalletFromPrivateKey(privateKey, "")
        Log.d(TAG, w1.toString())*/

        /*val json = "{\"address\":\"b9bb5366c14b1d06d0d123ea42ba19355a8bcacf\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"e63ad82dace00aef4c2077fb0d924a52\"},\"ciphertext\":\"4ade04173fc2f56af9f5a5d80f02258bff377bb918577b60f90c5500541eaad6\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"f9bfcf7f623551ef3ce01322af42d8b59df4298a8e25a0b85ac48815885dd351\"},\"mac\":\"3ecf4ca1378968daada1be1a6d226f64942eaf153e39f89960f71761b3c23099\"},\"id\":\"68775750-30f4-44e7-b120-a82e4e3ac54e\",\"version\":3}"
        val w1 = w3JL.createWalletFromJsonString(json, "")

        Log.d(TAG, w1.toString())*/
    }

    public override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
