package com.gbwisx.msal_mobile

import com.microsoft.identity.client.exception.MsalException

interface AuthenticatorInitCallback {
    fun onSuccess()
    fun onError(exception: MsalException)
}