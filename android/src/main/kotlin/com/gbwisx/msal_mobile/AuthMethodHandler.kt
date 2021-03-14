package com.gbwisx.msal_mobile

import android.app.Activity
import com.gbwisx.msal_mobile.Payloads.GetAccountResultPayload
import com.gbwisx.msal_mobile.Payloads.MsalMobileResultPayload
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.SilentAuthenticationCallback
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.util.*

class AuthMethodHandler : MethodCallHandler {
    private var mActivity: Activity? = null
    private var mAuth: Authenticator? = null
    fun setActivity(activity: Activity) {
        mActivity = activity
    }

    private fun error(result: MethodChannel.Result, exception: Exception) {
        // even though this is an error, success is returned because a success response allows for more detail to be sent back
        result.success(Results.MsalMobileResult.error(exception))
    }

    private fun error(result: MethodChannel.Result, errorCode: String, message: String) {
        val exception = MsalMobileException(errorCode, message)
        result.success(Results.MsalMobileResult.error(exception))
    }

    private fun uiRequiredError(result: MethodChannel.Result, exception: MsalUiRequiredException) {
        // even though this is an error, success is returned because a success response allows for more detail to be sent back
        result.success(Results.MsalMobileResult.uiRequiredError(exception))
    }

    private fun success(result: MethodChannel.Result, payload: MsalMobileResultPayload) {
        result.success(Results.MsalMobileResult.success(payload))
    }

    private fun success(result: MethodChannel.Result) {
        result.success(Results.MsalMobileResult.success(true))
    }

    private fun handleInit(result: MethodChannel.Result, configFilePath: String?) {
        val activity = mActivity
        if (activity == null) {
            error(result, "no_activity", "No Android activity was found to bind MSAL to.")
            return
        }
        mAuth = Authenticator()
        mAuth?.init(activity, configFilePath.orEmpty(), object : AuthenticatorInitCallback {
            override fun onSuccess() {
                success(result)
            }

            override fun onError(exception: MsalException) {
                error(result, exception)
            }
        })
    }

    private fun handleGetAccount(result: MethodChannel.Result) {
        mAuth?.getAccount(object : CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {

                success(result, GetAccountResultPayload(activeAccount))

            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                success(result, GetAccountResultPayload(currentAccount))
            }

            override fun onError(exception: MsalException) {
                error(result, exception)
            }
        })
    }

    private fun handleSignIn(result: MethodChannel.Result, loginHint: String?, scopes: Array<String?>) {
        mAuth?.signIn(scopes, loginHint, object : AuthenticationCallback {
            override fun onCancel() {
                success(result, Payloads.AuthenticationResultPayload.cancelled())
            }

            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                success(result, Payloads.AuthenticationResultPayload.success(authenticationResult))
            }

            override fun onError(exception: MsalException) {
                error(result, exception)
            }
        })
    }

    private fun handleSignOut(result: MethodChannel.Result) {

        mAuth?.signOut(object : SignOutCallback {
            override fun onSignOut() {
                success(result)
            }

            override fun onError(exception: MsalException) {
                error(result, exception)
            }
        })
    }

    private fun handleAcquireToken(result: MethodChannel.Result, scopes: Array<String?>) {
        mAuth?.acquireToken(scopes, object : AuthenticationCallback {
            override fun onCancel() {
                success(result, Payloads.AuthenticationResultPayload.cancelled())
            }

            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                success(result, Payloads.AuthenticationResultPayload.success(authenticationResult))
            }

            override fun onError(exception: MsalException) {
                error(result, exception)
            }
        })
    }

    private fun handleAcquireTokenSilent(result: MethodChannel.Result, scopes: Array<String?>, authority: String) {
        mAuth?.acquireTokenSilent(scopes, authority, object : SilentAuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                success(result, Payloads.AuthenticationResultPayload.success(authenticationResult))
            }

            override fun onError(exception: MsalException) {
                if (exception is MsalUiRequiredException) {
                    uiRequiredError(result, exception)
                } else {
                    error(result, exception)
                }
            }
        })
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        try {
            when (call.method) {
                "init" -> {
                    val configFilePath = call.argument<String>("configFilePath")
                    handleInit(result, configFilePath)
                }
                "getAccount" -> {
                    handleGetAccount(result)
                }
                "signIn" -> {
                    val loginHint = call.argument<String>("loginHint")
                    val scopesList = call.argument<ArrayList<String>>("scopes")
                    val scopes:Array<String?> = scopesList?.toTypedArray() ?: arrayOf()
                    handleSignIn(result, loginHint, scopes)
                }
                "signOut" -> {
                    handleSignOut(result)
                }
                "acquireToken" -> {
                    val scopesList = call.argument<ArrayList<String>>("scopes")
                    val scopes:Array<String?> = scopesList?.toTypedArray() ?: arrayOf()
                    handleAcquireToken(result, scopes)
                }
                "acquireTokenSilent" -> {
                    val scopesList = call.argument<ArrayList<String>>("scopes")
                    val scopes:Array<String?> = scopesList?.toTypedArray() ?: arrayOf()
                    val authority = call.argument<String>("authority")
                    handleAcquireTokenSilent(result, scopes, authority!!)
                }
                else -> {
                    result.notImplemented()
                }
            }
        } catch (exception: Exception) {
            error(result, exception)
        }
    }
}