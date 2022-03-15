package com.alsatpardakht.alsatipgandroid.util

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.alsatpardakht.alsatipgcore.core.util.decodeQueryParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

fun <T> Flow<T?>.onlyNotNull(): Flow<T> = flow {
    this@onlyNotNull.collect {
        if (it != null) emit(it)
    }
}

@ExperimentalCoroutinesApi
fun <T> LiveData<T>.asFlow() = callbackFlow {
    val observer = Observer<T> { value -> trySend(value) }
    observeForever(observer)
    awaitClose {
        removeObserver(observer)
    }
}.flowOn(Dispatchers.Main.immediate).onlyNotNull()

fun Uri.getDecodedQueryValueByKey(inputKey: String): String {
    for (key in this.queryParameterNames) {
        if (key == inputKey) {
            return this.getQueryParameter(key)?.decodeQueryParameter() ?: ""
        }
    }
    return ""
}