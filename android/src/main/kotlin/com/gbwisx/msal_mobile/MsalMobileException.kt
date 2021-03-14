package com.gbwisx.msal_mobile

internal class MsalMobileException(val errorCode: String, message: String?) : Exception(message)