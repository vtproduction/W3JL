package com.midsummer.w3jl.entity

/**
 * Created by nienb on 14-Aug-18.
 */
 class Lce<T> (
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val data: T? =null
)