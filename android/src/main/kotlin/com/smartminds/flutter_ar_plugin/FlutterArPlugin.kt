package com.smartminds.flutter_ar_plugin

import android.app.Activity
import android.content.Intent
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class FlutterArPlugin: MethodCallHandler {
  companion object {
    private var current_activity: Activity? = null
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_ar_plugin")
      current_activity = registrar.activity()
      channel.setMethodCallHandler(FlutterArPlugin())
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "startARActivity") {
      startArActivity()
    } else {
      result.notImplemented()
    }
  }

  fun startArActivity() {
    val intent = Intent(current_activity, ArActivity::class.java)
    // start your next activity
    current_activity!!.startActivity(intent)
  }
}