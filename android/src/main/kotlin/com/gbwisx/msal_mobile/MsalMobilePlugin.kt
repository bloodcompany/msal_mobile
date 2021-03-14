package com.gbwisx.msal_mobile

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

/** MsalMobilePlugin  */
class MsalMobilePlugin : FlutterPlugin, ActivityAware {
    private var mMethodHandler: AuthMethodHandler? = null
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        if (mMethodHandler != null) {
            mMethodHandler?.setActivity(binding.activity)
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onDetachedFromActivity() {}
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}
    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {}
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.gbwisx.msal_mobile")
        mMethodHandler = AuthMethodHandler()
        channel.setMethodCallHandler(mMethodHandler)
    }
}