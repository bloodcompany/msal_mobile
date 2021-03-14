package com.gbwisx.msal_mobile

import com.google.gson.Gson
import com.microsoft.identity.client.exception.MsalException

class Results {
    internal class MsalMobileResult {
        private var isSuccess: Boolean
        private var exception: ExceptionDetail? = null
        private var innerException: ExceptionDetail? = null
        private var payload: Any? = null
        private var isUiRequired = false

        private constructor(ex: Exception) {
            isSuccess = false
            exception = ExceptionDetail(ex)
            if (ex.cause != null) {
                innerException = ExceptionDetail(ex.cause!!)
            }
        }

        private constructor(successPayload: Any) {
            isSuccess = true
            payload = successPayload
        }

        private fun toJson(): String {
            val gson = Gson()
            return gson.toJson(this)
        }

        companion object {
            fun success(successPayload: Any): String {
                val result = MsalMobileResult(successPayload)
                return result.toJson()
            }

            fun error(ex: Exception): String {
                val result = MsalMobileResult(ex)
                return result.toJson()
            }

            fun uiRequiredError(ex: Exception): String {
                val result = MsalMobileResult(ex)
                result.isUiRequired = true
                return result.toJson()
            }
        }
    }

    internal class ExceptionDetail {
        private var message: String? = null
        private var errorCode: String? = null

        constructor(throwable: Throwable) {
            init(throwable)
        }

        constructor(exception: Exception) {
            init(exception)
        }

        private fun init(throwable: Throwable) {
            message = throwable.message
            if (throwable is MsalException) {
                errorCode = throwable.errorCode
            } else if (throwable is MsalMobileException) {
                errorCode = throwable.errorCode
            }
        }
    }
}