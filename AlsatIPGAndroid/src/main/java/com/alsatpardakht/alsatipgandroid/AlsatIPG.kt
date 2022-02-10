package com.alsatpardakht.alsatipgandroid

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alsatpardakht.alsatipgcore.core.util.Resource
import com.alsatpardakht.alsatipgcore.data.remote.IPGServiceImpl
import com.alsatpardakht.alsatipgcore.data.remote.util.URLConstant.GO_ROUTE
import com.alsatpardakht.alsatipgcore.data.remote.util.appendQuery
import com.alsatpardakht.alsatipgcore.data.remote.model.PaymentSignRequest
import com.alsatpardakht.alsatipgcore.data.remote.model.PaymentValidationRequest
import com.alsatpardakht.alsatipgcore.data.repository.IPGRepositoryImpl
import com.alsatpardakht.alsatipgcore.domain.model.PaymentSignResult
import com.alsatpardakht.alsatipgcore.domain.model.PaymentValidationResult
import com.alsatpardakht.alsatipgcore.domain.use_case.PaymentSignUseCase
import com.alsatpardakht.alsatipgcore.domain.use_case.PaymentValidationUseCase
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlsatIPG private constructor() {

    private val httpClient = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    private val iPGService = IPGServiceImpl(httpClient)
    private val iPGRepository = IPGRepositoryImpl(iPGService)
    private val paymentSignUseCase = PaymentSignUseCase(iPGRepository)
    private val paymentValidationUseCase = PaymentValidationUseCase(iPGRepository)

    private val _paymentSignStatus: MutableLiveData<PaymentSignResult> = MutableLiveData()
    val paymentSignStatus: LiveData<PaymentSignResult> = _paymentSignStatus

    private val _paymentValidationStatus: MutableLiveData<PaymentValidationResult> =
        MutableLiveData()
    val paymentValidationStatus: LiveData<PaymentValidationResult> = _paymentValidationStatus

    companion object {
        @Volatile
        private var instance: AlsatIPG? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): AlsatIPG {
            return instance ?: AlsatIPG().also {
                instance = it
            }
        }
    }

    fun sign(paymentSignRequest: PaymentSignRequest): LiveData<PaymentSignResult> {
        CoroutineScope(Dispatchers.Main).launch {
            paymentSignUseCase.execute(paymentSignRequest).collect {
                when (it) {
                    is Resource.Success -> _paymentSignStatus.postValue(
                        PaymentSignResult(
                            isSuccessful = true,
                            url = GO_ROUTE.appendQuery("Token", it.data.Token ?: "")
                        )
                    )
                    is Resource.Loading -> _paymentSignStatus.postValue(PaymentSignResult(isLoading = true))
                    is Resource.Error -> _paymentSignStatus.postValue(PaymentSignResult(error = it.error))
                }
            }
        }
        return paymentSignStatus
    }

    fun validation(Api: String, data: Uri): LiveData<PaymentValidationResult> {
        val paymentValidationRequest = PaymentValidationRequest(Api)
        for (key in data.queryParameterNames) {
            val value = data.getQueryParameter(key)
            when (key) {
                "tref" -> paymentValidationRequest.tref = value
                "iD" -> paymentValidationRequest.iD = value
                "iN" -> paymentValidationRequest.iN = value
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            paymentValidationUseCase.execute(paymentValidationRequest).collect {
                when (it) {
                    is Resource.Success -> _paymentValidationStatus.postValue(
                        PaymentValidationResult(isSuccessful = true, data = it.data)
                    )
                    is Resource.Loading -> _paymentValidationStatus.postValue(
                        PaymentValidationResult(isLoading = true)
                    )
                    is Resource.Error -> _paymentValidationStatus.postValue(
                        PaymentValidationResult(error = it.error)
                    )
                }
            }
        }
        return paymentValidationStatus
    }
}
