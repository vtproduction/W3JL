package com.midsummer.w3jl.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by NienLe on 15,August,2018
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

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        return 0
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