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
class W3JLTokenInfo() : Parcelable {
    var address: String = ""
    var name: String = ""
    var symbol: String = ""
    var decimals: Int = 18

    constructor(parcel: Parcel) : this() {
        address = parcel.readString()
        name = parcel.readString()
        symbol = parcel.readString()
        decimals = parcel.readInt()
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