import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
// import 'package:ourea/utils/dialog_utils.dart';
// import 'package:ourea/utils/screen_adaption.dart';
// import 'package:ourea/widgets/common.dart';
// import 'package:ourea/widgets/progress_dialog.dart';

class NavigationService {
  static NavigationService _instance;

  factory NavigationService() {
    if (_instance == null) {
      final methodChannel = MethodChannel("xh.zero/navigation");
      _instance =  NavigationService._private(methodChannel);
    }
    return _instance;
  }

  NavigationService._private(this._methodChannel);

  static const MAP_TYPE_BAIDU = "baidu";
  static const MAP_TYPE_AMAP = "amap";
  static const MAP_TYPE_TENCENT = "tencent";

  final MethodChannel _methodChannel;

  /// 跳转到第三方地图app导航
  /// mapType: 地图类型
  /// address: 导航地址
  /// lat，lng: 经纬度
  navigate(String mapType, String address, double lat, double lng) {
    _methodChannel.invokeMethod("navigate", {
      "address": address,
      "lat": lat,
      "lng": lng,
      "mapType": mapType
    });
  }

  /// 检查手机是否安装高德、百度、腾讯地图
  /// 假设future的返回为result：
  /// final hasAmap = result["hasAmap"];
  /// final hasBaidu = result["hasBaidu"];
  /// final hasTencent = result["hasTencent"];
  checkInstalledMapApps() => _methodChannel.invokeMethod("checkInstalledMapApps");
}