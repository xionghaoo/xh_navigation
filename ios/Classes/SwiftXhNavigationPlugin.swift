import Flutter
import UIKit

public class SwiftXhNavigationPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "xh.zero/navigation", binaryMessenger: registrar.messenger())
        let instance = SwiftXhNavigationPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "checkInstalledMapApps":
            let hasAmap = UIApplication.shared.canOpenURL(URL(string: "iosamap://")!)
            let hasBaidu = UIApplication.shared.canOpenURL(URL(string: "baidumap://")!)
//            let hasTencent = UIApplication.shared.canOpenURL(URL(string: "qqmap://")!)
            if !hasAmap && !hasBaidu {
                result(FlutterError(code: "201", message: "您没有安装高德或百度地图", details: nil))
            } else {
                result([
                    "hasAmap": hasAmap,
                    "hasBaidu": hasBaidu,
                    "hasTencent": false
                ])
            }
        case "navigate":
            if let arguments = call.arguments as? [String:Any?],
                let mapType = arguments["mapType"] as? String,
                let lat = arguments["lat"] as? Double,
                let lng = arguments["lng"] as? Double,
                let address = arguments["address"] as? String {
                switch mapType {
                case "amap":
                    amapNavigation(address: address, lat: lat, lng: lng)
                case "baidu":
                    baiduNavigation(address: address, lat: lat, lng: lng)
                case "tencent":
                    tencentNavigation(address: address, lat: lat, lng: lng)
                default:
                    break;
                }
            }
            result(nil)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    private func amapNavigation(address: String, lat: Double, lng: Double) {
           let scheme = URL(string: "iosamap://navi?sourceApplication=applicationName&poiname=fangheng&poiid=BGVIS&lat=\(lat)&lon=\(lng)&dev=1&style=2")!
           if #available(iOS 10.0, *) {
               UIApplication.shared.open(scheme, options: [:], completionHandler: nil)
           } else {
               UIApplication.shared.openURL(scheme)
           }
       }
       
       private func baiduNavigation(address: String, lat: Double, lng: Double) {
           let scheme = URL(string: "baidumap://map/navi?location=\(lat),\(lng)&coord_type=gcj02&type=BLK&src=ios.baidu.openAPIdemo")!
           if #available(iOS 10.0, *) {
               UIApplication.shared.open(scheme, options: [:], completionHandler: nil)
           } else {
               UIApplication.shared.openURL(scheme)
           }
       }
       
       private func tencentNavigation(address: String, lat: Double, lng: Double) {
           let scheme = URL(string: "qqmap://map/routeplan?type=drive&from=&fromcoord=CurrentLocation&to=\(address)&tocoord=\(lat),\(lng)&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77")
           if let scheme = scheme {
               if #available(iOS 10.0, *) {
                   UIApplication.shared.open(scheme, options: [:], completionHandler: nil)
               } else {
                   UIApplication.shared.openURL(scheme)
               }
           }
       }
}
