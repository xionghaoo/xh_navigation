
import 'dart:async';

import 'package:flutter/services.dart';

class XhNavigation {
  static const MethodChannel _channel =
      const MethodChannel('xh_navigation');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
