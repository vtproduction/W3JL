package com.midsummer.w3jl.test;

import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by NienLe on 13,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
public class W3JL {



    private Web3j web3j;


    public void initWeb3j(String provider){
        web3j = Web3jFactory.build(new HttpService(provider));
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public String getBalance(String address) throws ExecutionException, InterruptedException {
        EthGetBalance ethGetBalance = web3j
                .ethGetBalance("0xB89d25B8378d8E8b2CB11E6e1bF80fBf33386f45", DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
        BigInteger wei = ethGetBalance.getBalance();
        return weiToEth(wei,5);

    }

    public String getMnemonic(){
        byte[] b = new byte[16];
        new Random().nextBytes(b);
        return MnemonicUtils.generateMnemonic(b);
    }


    private static String weiInEth  = "1000000000000000000";

    public static BigDecimal weiToEth(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }

    public static String weiToEth(BigInteger wei, int sigFig) {
        BigDecimal eth = weiToEth(wei);
        int scale = sigFig - eth.precision() + eth.scale();
        BigDecimal eth_scaled = eth.setScale(scale, RoundingMode.HALF_UP);
        return eth_scaled.toString();
    }

    public String MnemonicToWallet(String mnemonics, String password){
        byte[] seeds = MnemonicUtils.generateSeed(mnemonics, password);
        ECKeyPair ecKeyPair = ECKeyPair.create(Hash.sha256(seeds));
        String prvateKey =ecKeyPair.getPrivateKey().toString(16);
        Credentials credentials = Credentials.create(prvateKey);
        Log.d("MAINNNN","private key: " + prvateKey);
        String address = credentials.getAddress();
        return address;
    }


    public String privateKeyToMnemonic(String privateKey){

        return "";
    }
}
