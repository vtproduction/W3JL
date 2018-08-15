package com.midsummer.w3jl.entity

import android.os.Parcel
import android.os.Parcelable
import org.web3j.utils.Files.readString

/**
 * Created by NienLe on 15-Aug-18,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
class W3JLTokenInfo : Parcelable {
    val address: String
    val name: String
    val symbol: String
    val decimals: Int

    constructor(address: String, name: String, symbol: String, decimals: Int) {
        this.address = address
        this.name = name
        this.symbol = symbol
        this.decimals = decimals
    }

    private constructor(p: Parcel) {
        address = p.readString()
        name = p.readString()
        symbol = p.readString()
        decimals = p.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(address)
        dest.writeString(name)
        dest.writeString(symbol)
        dest.writeInt(decimals)
    }

    companion object CREATOR : Parcelable.Creator<W3JLTokenInfo> {
        override fun createFromParcel(parcel: Parcel): W3JLTokenInfo {
            return W3JLTokenInfo(parcel)
        }

        override fun newArray(size: Int): Array<W3JLTokenInfo?> {
            return arrayOfNulls(size)
        }
    }


}