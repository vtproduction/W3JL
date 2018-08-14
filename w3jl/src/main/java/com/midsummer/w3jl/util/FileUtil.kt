package com.midsummer.w3jl.util

import android.content.Context
import java.io.*


/**
 * Created by NienLe on 14,August,2018
 * Midsummer.
 * Ping me at nienbkict@gmail.com
 * Happy coding ^_^
 */
object FileUtil{
     fun readJsonFile( file : File) : String{
        var ret = ""

        try {
            val inputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val stringBuilder = StringBuilder()
            var receiveString =  bufferedReader.readLine()
            while (receiveString != null) {
                stringBuilder.append(receiveString)
                receiveString =  bufferedReader.readLine()
            }

            inputStream!!.close()
            ret = stringBuilder.toString()

        } catch (e: FileNotFoundException) {
            return e.localizedMessage
        } catch (e: IOException) {
            return e.localizedMessage
        }
        return ret
    }
}