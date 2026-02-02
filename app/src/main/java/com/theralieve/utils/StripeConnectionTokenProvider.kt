//package com.theralieve.utils
//
//import com.theralieve.domain.usecase.GetCardReaderTokenUseCase
//import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
//import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
//import com.stripe.stripeterminal.external.models.ConnectionTokenException
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//class StripeConnectionTokenProvider @Inject constructor(
//    private val getTokenUseCase: GetCardReaderTokenUseCase
//) : ConnectionTokenProvider {
//
//    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val token = getTokenUseCase()
//                callback.onSuccess(token.getOrNull()?:"")
//            } catch (e: Exception) {
//                callback.onFailure(ConnectionTokenException(e.toString()))
//            }
//        }
//    }
//}
