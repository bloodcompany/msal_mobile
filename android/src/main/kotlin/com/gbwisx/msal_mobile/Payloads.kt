package com.gbwisx.msal_mobile

import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import java.text.SimpleDateFormat

class Payloads {
    internal interface MsalMobileResultPayload
    internal class GetAccountResultPayload(currentMsalAccount: IAccount?) : MsalMobileResultPayload {
        private var currentAccount: Account? = null
        private val accountLoaded = true

        init {
            if (currentMsalAccount != null) {
                currentAccount = Account(currentMsalAccount.tenantId, currentMsalAccount.claims, currentMsalAccount.authority, currentMsalAccount.id, currentMsalAccount.username)
            }
        }
    }

    internal class Account(private val tenantId: String, private val claims: Map<String, *>?, private val authority: String, private val id: String, private val username: String)
    internal class AuthenticationResultPayload : MsalMobileResultPayload {
        private var cancelled: Boolean
        private var success: Boolean
        private var accessToken: String?
        private var tenantId: String? = null
        private var scope: Array<String> = arrayOf()
        private var expiresOn: String? = null

        private constructor(authSuccessful: Boolean, authCancelled: Boolean, authAccessToken: String?) {
            success = authSuccessful
            cancelled = authCancelled
            accessToken = authAccessToken
        }

        private constructor(result: IAuthenticationResult) {
            success = true
            cancelled = false
            accessToken = result.accessToken
            tenantId = result.tenantId
            scope = result.scope
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            expiresOn = formatter.format(result.expiresOn)
        }

        companion object {
            fun success(result: IAuthenticationResult): AuthenticationResultPayload {
                return AuthenticationResultPayload(result)
            }

            fun cancelled(): AuthenticationResultPayload {
                return AuthenticationResultPayload(authSuccessful = false, authCancelled = true, authAccessToken = null)
            }
        }
    }
}