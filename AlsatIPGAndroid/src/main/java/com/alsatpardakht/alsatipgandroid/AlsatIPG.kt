package com.alsatpardakht.alsatipgandroid

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alsatpardakht.alsatipgandroid.util.asFlow
import com.alsatpardakht.alsatipgandroid.util.getDecodedQueryValueByKey
import com.alsatpardakht.alsatipgcore.core.util.Resource
import com.alsatpardakht.alsatipgcore.data.remote.IPGServiceImpl
import com.alsatpardakht.alsatipgcore.domain.model.TashimModel
import com.alsatpardakht.alsatipgcore.domain.model.PaymentType
import com.alsatpardakht.alsatipgcore.data.remote.util.URLConstant.GO_ROUTE
import com.alsatpardakht.alsatipgcore.data.remote.util.appendQuery
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AlsatIPG private constructor(private val httpLogging: Boolean) {

    private val httpClient = HttpClient(CIO) {
        if (httpLogging) install(Logging) {
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

    @ExperimentalCoroutinesApi
    val paymentSignStatusAsFlow = paymentSignStatus.asFlow()

    private val _paymentValidationStatus: MutableLiveData<PaymentValidationResult> =
        MutableLiveData()
    val paymentValidationStatus: LiveData<PaymentValidationResult> = _paymentValidationStatus

    @ExperimentalCoroutinesApi
    val paymentValidationStatusAsFlow = paymentValidationStatus.asFlow()

    companion object {
        @Volatile
        private var instance: AlsatIPG? = null

        @JvmStatic
        @Synchronized
        fun getInstance(httpLogging: Boolean = false): AlsatIPG {
            return instance ?: AlsatIPG(httpLogging).also {
                instance = it
            }
        }
    }

    private fun sign(
        Amount: Long,
        Api: String,
        RedirectAddress: String,
        InvoiceNumber: String = "",
        Type: PaymentType,
        Tashim: List<TashimModel>
    ): LiveData<PaymentSignResult> {
        CoroutineScope(Dispatchers.Default).launch {
            paymentSignUseCase.execute(
                Amount = Amount,
                Api = Api,
                InvoiceNumber = InvoiceNumber,
                RedirectAddress = RedirectAddress,
                Type = Type,
                Tashim = Tashim
            ).collect {
                when (it) {
                    is Resource.Success -> _paymentSignStatus.postValue(
                        PaymentSignResult(
                            isSuccessful = true,
                            url = GO_ROUTE.appendQuery("Token", it.data.Token)
                        )
                    )
                    is Resource.Loading -> _paymentSignStatus.postValue(PaymentSignResult(isLoading = true))
                    is Resource.Error -> _paymentSignStatus.postValue(PaymentSignResult(error = it.error))
                }
            }
        }
        return paymentSignStatus
    }

    fun signMostaghim(
        Api: String,
        Amount: Long,
        InvoiceNumber: String,
        RedirectAddress: String
    ) = sign(
        Api = Api,
        Amount = Amount,
        InvoiceNumber = InvoiceNumber,
        RedirectAddress = RedirectAddress,
        Type = PaymentType.Mostaghim,
        Tashim = emptyList()
    )

    fun signVaset(
        Api: String,
        Amount: Long,
        RedirectAddress: String,
        Tashim: List<TashimModel>,
        InvoiceNumber: String = "",
    ) = sign(
        Api = Api,
        Amount = Amount,
        InvoiceNumber = InvoiceNumber,
        RedirectAddress = RedirectAddress,
        Type = PaymentType.Vaset,
        Tashim = Tashim
    )

    private fun validation(
        Api: String,
        tref: String,
        iD: String,
        iN: String,
        Type: PaymentType,
        PayId: String
    ): LiveData<PaymentValidationResult> {
        CoroutineScope(Dispatchers.Default).launch {
            paymentValidationUseCase.execute(
                tref = tref,
                iD = iD,
                iN = iN,
                Api = Api,
                Type = Type
            ).collect {
                when (it) {
                    is Resource.Success -> _paymentValidationStatus.postValue(
                        PaymentValidationResult(
                            isSuccessful = true,
                            data = it.data.copy(PayId = PayId)
                        )
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

    private fun validation(
        Api: String,
        data: Uri,
        Type: PaymentType
    ) = validation(
        Api = Api,
        tref = data.getDecodedQueryValueByKey("tref"),
        iN = data.getDecodedQueryValueByKey("iN"),
        iD = data.getDecodedQueryValueByKey("iD"),
        Type = Type,
        PayId = data.getDecodedQueryValueByKey("PayId")
    )


    fun validationMostaghim(Api: String, data: Uri) = validation(
        Api = Api,
        data = data,
        Type = PaymentType.Mostaghim
    )

    fun validationMostaghim(
        Api: String, tref: String, iN: String, iD: String
    ) = validation(
        Api = Api,
        tref = tref,
        iN = iN,
        iD = iD,
        Type = PaymentType.Mostaghim,
        PayId = ""
    )

    fun validationMostaghim(
        Api: String, tref: String, iN: String, iD: String, PayId: String
    ) = validation(
        Api = Api,
        tref = tref,
        iN = iN,
        iD = iD,
        Type = PaymentType.Mostaghim,
        PayId = PayId
    )

    fun validationVaset(Api: String, data: Uri) = validation(
        Api = Api,
        data = data,
        Type = PaymentType.Vaset
    )

    fun validationVaset(
        Api: String, tref: String, iN: String, iD: String
    ) = validation(
        Api = Api,
        tref = tref,
        iN = iN,
        iD = iD,
        Type = PaymentType.Vaset,
        PayId = ""
    )

    fun validationVaset(
        Api: String, tref: String, iN: String, iD: String, PayId: String
    ) = validation(
        Api = Api,
        tref = tref,
        iN = iN,
        iD = iD,
        Type = PaymentType.Vaset,
        PayId = PayId
    )
}
