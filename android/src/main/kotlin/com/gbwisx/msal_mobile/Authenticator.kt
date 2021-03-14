package com.gbwisx.msal_mobile

import android.app.Activity
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SilentAuthenticationCallback
import com.microsoft.identity.client.exception.MsalException
import java.io.File

internal class Authenticator {
    private var mClient: ISingleAccountPublicClientApplication? = null
    private var mActivity: Activity? = null
    fun init(activity: Activity, configFilePath: String, callback: AuthenticatorInitCallback) {
        mActivity = activity
        val configFile = File(configFilePath)
        PublicClientApplication.createSingleAccountPublicClientApplication(activity.applicationContext, configFile, object : ISingleAccountApplicationCreatedListener {
            override fun onCreated(application: ISingleAccountPublicClientApplication) {
                mClient = application
                callback.onSuccess()
            }

            override fun onError(exception: MsalException) {
                callback.onError(exception)
            }
        })
    }

    fun getAccount(callback: CurrentAccountCallback) {
        mClient?.getCurrentAccountAsync(callback)
    }

    fun signIn(scopes: Array<String?>, loginHint: String?, callback: AuthenticationCallback) {
        mActivity?.let {
            mClient?.signIn(it, loginHint, scopes, callback)

        }
    }

    fun signOut(callback: SignOutCallback) {
        mClient?.signOut(callback)
    }

    fun acquireToken(scopes: Array<String?>, callback: AuthenticationCallback) {
        mActivity?.let {

            mClient?.acquireToken(it, scopes, callback)
        }
    }

    fun acquireTokenSilent(scopes: Array<String?>, authority: String, callback: SilentAuthenticationCallback) {
        mClient?.acquireTokenSilentAsync(scopes, authority, callback)
    }
}