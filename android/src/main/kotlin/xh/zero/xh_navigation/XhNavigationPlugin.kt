package xh.zero.xh_navigation

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import xh.zero.xh_navigation.MapNavigationUtil.Companion.NAVIGATION_AMAP
import xh.zero.xh_navigation.MapNavigationUtil.Companion.NAVIGATION_BAIDU
import xh.zero.xh_navigation.MapNavigationUtil.Companion.NAVIGATION_QQMAP
import xh.zero.xh_navigation.MapNavigationUtil.Companion.VEHICLE_TYPE_CAR

/** XhNavigationPlugin */
class XhNavigationPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Activity

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "xh.zero/navigation")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method) {
      "checkInstalledMapApps" -> {
        MapNavigationUtil.checkInstalledMapApps(context, error = {
          result.error("201", it, null)
        }) {hasAmap, hasBaidu, hasTencent ->
          val map = HashMap<String, Boolean>()
          map.apply {
            put("hasAmap", hasAmap)
            put("hasBaidu", hasBaidu)
            put("hasTencent", hasTencent)
          }
          result.success(map)
        }
      }
      "navigate" -> {
        val address: String = call.argument<String>("address") ?: ""
        val lat: Double? = call.argument<Double>("lat")
        val lng: Double? = call.argument<Double>("lng")
        val mapType: String = call.argument<String>("mapType") ?: ""

        if (lat != null && lng != null) {
          when(mapType) {
            "amap" -> MapNavigationUtil.navigate(context, lat, lng, address, NAVIGATION_AMAP, VEHICLE_TYPE_CAR)
            "baidu" -> MapNavigationUtil.navigate(context, lat, lng, address, NAVIGATION_BAIDU, VEHICLE_TYPE_CAR)
            "tencent" -> MapNavigationUtil.navigate(context, lat, lng, address, NAVIGATION_QQMAP, VEHICLE_TYPE_CAR)
          }
        }
        result.success(null)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    context = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivity() {
  }
}
