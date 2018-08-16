package com.midsummer.w3jl.util

import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
object BalanceUtil{
    private val weiInEth = "1000000000000000000"

    fun weiToEth(wei: BigInteger): BigDecimal {
        return Convert.fromWei(BigDecimal(wei), Convert.Unit.ETHER)
    }

    @Throws(Exception::class)
    fun weiToEth(wei: BigInteger, sigFig: Int): String {
        val eth = weiToEth(wei)
        val scale = sigFig - eth.precision() + eth.scale()
        val eth_scaled = eth.setScale(scale, RoundingMode.HALF_UP)
        return eth_scaled.toString()
    }

    fun weiToEth(wei: BigDecimal, sigFig: Int): String {
        val eth = Convert.fromWei(wei, Convert.Unit.ETHER)
        val scale = sigFig - eth.precision() + eth.scale()
        val eth_scaled = eth.setScale(scale, RoundingMode.HALF_UP)
        return eth_scaled.toString()
    }

    fun ethToUsd(priceUsd: String, ethBalance: String): String {
        var usd = BigDecimal(ethBalance).multiply(BigDecimal(priceUsd))
        usd = usd.setScale(2, RoundingMode.CEILING)
        return usd.toString()
    }

    @Throws(Exception::class)
    fun EthToWei(eth: String): String {
        val wei = BigDecimal(eth).multiply(BigDecimal(weiInEth))
        return wei.toBigInteger().toString()
    }

    fun weiToGweiBI(wei: BigInteger): BigDecimal {
        return Convert.fromWei(BigDecimal(wei), Convert.Unit.GWEI)
    }

    fun weiToGwei(wei: BigInteger): String {
        return Convert.fromWei(BigDecimal(wei), Convert.Unit.GWEI).toPlainString()
    }

    fun gweiToWei(gwei: BigDecimal): BigInteger {

        return Convert.toWei(gwei, Convert.Unit.GWEI).toBigInteger()
    }

    /**
     * Base - taken to mean default unit for a currency e.g. ETH, DOLLARS
     * Subunit - taken to mean subdivision of base e.g. WEI, CENTS
     *
     * @param baseAmountStr - decimal amonut in base unit of a given currency
     * @param decimals - decimal places used to convert to subunits
     * @return amount in subunits
     */
    fun baseToSubunit(baseAmountStr: String, decimals: Int): BigInteger {
        assert(decimals >= 0)
        val baseAmount = BigDecimal(baseAmountStr)
        val subunitAmount = baseAmount.multiply(BigDecimal.valueOf(10).pow(decimals))
        try {
            return subunitAmount.toBigIntegerExact()
        } catch (ex: ArithmeticException) {
            assert(false)
            return subunitAmount.toBigInteger()
        }

    }

    /**
     * @param subunitAmount - amouunt in subunits
     * @param decimals - decimal places used to convert subunits to base
     * @return amount in base units
     */
    fun subunitToBase(subunitAmount: BigInteger, decimals: Int): BigDecimal {
        assert(decimals >= 0)
        return BigDecimal(subunitAmount).divide(BigDecimal.valueOf(10).pow(decimals))
    }
}