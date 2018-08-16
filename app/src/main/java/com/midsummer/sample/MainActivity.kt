package com.midsummer.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.midsummer.w3jl.entity.W3JLTokenInfo
import com.midsummer.w3jl.erc20Contract.TokenRepository
import com.midsummer.w3jl.hdWallet.EthMnemonic
import com.midsummer.w3jl.service.W3JLFactory
import com.midsummer.w3jl.test.TestKethe
import com.midsummer.w3jl.util.BalanceUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fileDir = File(applicationInfo.dataDir, "keystore")
        if (!fileDir.exists())
            fileDir.mkdirs()
        val password = "123456654321"
        //val walletFile = "UTC--2018-08-10T16-34-52.129--22a3140b08c8929af4fd2a80d068f9c82204e943.json"
        val addressFrom = "0x22a3140b08c8929af4fd2a80d068f9c82204e943"
        val addressTo = "0x95b52bcd93D4A87a7E98975F2245a57789a2D34d"
        val mnemonic = "drama print suspect patrol seek estate thrive since remind march letter era"
        val privKey = "9b3f6d65f494ade10eada5e9aa82a5426045373d0b06a6b617c7c066bab16a77"


        val w3JL = W3JLFactory()
                .withContext(this)
                .buildW3JL()

        /*val kethe = TestKethe()
        Log.d(TAG, "kethe key: ${kethe.createPrivateKey(mnemonic)}")*/

        val list : MutableList<String> = mutableListOf()
        list.add("drama")
        list.add("print")
        list.add("suspect")
        list.add("patrol")
        list.add("seek")
        list.add("estate")
        list.add("thrive")
        list.add("since")
        list.add("remind")
        list.add("march")
        list.add("letter")
        list.add("era")
        val ethMnemonic = EthMnemonic()
        Log.d(TAG, "Wallet: ${ethMnemonic.mNemonicToPrivateKey(mnemonic)}")

        compositeDisposable.add(w3JL.createWalletFromMnemonic(mnemonic, "999")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(5, TimeUnit.SECONDS)
                .subscribe({
                    Log.d(TAG,"wallet: $it")
                },{
                    Log.d(TAG,"wallet Error: ${Log.getStackTraceString(it)}")
                }))

        val w3JLEth = W3JLFactory()
                .withContext(this)
                .withNetworkProvider("https://ropsten.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06")
                .buildW3JLEth()

        compositeDisposable.add(w3JLEth.getAccountBalance(addressFrom, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(5, TimeUnit.SECONDS)
                .subscribe({
                    Log.d(TAG,"balance: $it")
                },{
                    Log.d(TAG,"balance Error: ${it.localizedMessage}")
                }))


        val tokenInfo = W3JLTokenInfo()
        tokenInfo.address = "0x86a85ce4CC3FA15a4C24Ae720990d998A65B4917"
        tokenInfo.name = "Venus"
        tokenInfo.symbol = "VNS"
        tokenInfo.decimals = 18

        val tokenOwnerAddress =  "0x95b52bcd93D4A87a7E98975F2245a57789a2D34d"
        val tokenOwnerPrivateKey = "5DF2D1B7F71A6B2FF6F6B5B72EC8FFDF49E0DD4EF65390E8923AC0F0FC1F1371"
        val tokenTo = "0xB7983Ce7796d47616CE896b2d006145BC856Dc0E"
        val tokenRepository = W3JLFactory()
                .withContext(this)
                .withNetworkProvider("https://ropsten.infura.io/v3/95fa3a86534344ee9d1bf00e2b0d6d06")
                .buildW3JLToken()

        val balanceDecimal = tokenRepository.getBalance(tokenOwnerAddress, tokenInfo)
        Log.d(TAG,"getTokenBalace ${BalanceUtil.weiToEth(balanceDecimal!!, 3)}")



        /*val txId = tokenRepository.createTokenTransfer(tokenOwnerAddress, tokenTo,  tokenOwnerPrivateKey,
                tokenInfo.address,BalanceUtil.baseToSubunit(111, 18))
        Log.d(TAG, "txId: $txId")*/
    }

    public override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
