import 'package:flutter/material.dart';
import 'package:xh_navigation_example/progress_dialog.dart';
import 'package:xh_navigation/navigation_service.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  
  final navigationService = NavigationService();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Builder(
          builder: (context) => Center(
            child: FlatButton(
              child: Text("导航"),
              onPressed: () {

                showNavigator(context, "深圳东站", 22.598358, 114.119936);
              },
            ),
          ),
        ),
      ),
    );
  }

  showNavigator(BuildContext context, String address, double lat, double lng) {
    final progressDialog = ProgressDialog(context);
    progressDialog.show();
    print("hello ");
    navigationService.checkInstalledMapApps().then((result) {
      print("checkInstalledMapApps: $result");
      progressDialog.dismiss();

      final hasAmap = result["hasAmap"];
      final hasBaidu = result["hasBaidu"];
      final hasTencent = result["hasTencent"];

      showDialog<void>(
          context: context,
          builder: (context) {
            return Dialog(
              backgroundColor: Colors.transparent,
              elevation: 0,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(4)
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(4),
                child: Container(
                  decoration: BoxDecoration(
                    color: Colors.white,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      Padding(
                        padding: EdgeInsets.symmetric(vertical: xdp(15)),
                        child: Text("请选择导航地图", style: TextStyle(fontWeight: FontWeight.bold, fontSize: xdp(16)),),
                      ),
                      dividerWidget,
                      hasAmap
                          ? InkBox(
                        child: Container(
                          height: xdp(60),
                          child: Center(
                            child: Text("高德地图", style: TextStyle(fontSize: xdp(15)),),
                          ),
                        ),
                        onTap: () {
                          navigationService.navigate("amap", address, lat, lng);
                          Navigator.pop(context);
                        },
                      )
                          : SizedBox(),
                      hasAmap && hasBaidu ? dividerWidget : SizedBox(),
                      hasBaidu
                          ? InkBox(
                        child: Container(
                          height: xdp(60),
                          child: Center(
                            child: Text("百度地图", style: TextStyle(fontSize: xdp(15))),
                          ),
                        ),
                        onTap: () {
                          navigationService.navigate("baidu", address, lat, lng);
                          Navigator.pop(context);
                        },
                      )
                          : SizedBox(),
                      hasBaidu && hasTencent || (hasAmap && hasTencent) ? dividerWidget : SizedBox(),
                      hasTencent
                          ? InkBox(
                        child: Container(
                          height: xdp(60),
                          child: Center(
                            child: Text("腾讯地图", style: TextStyle(fontSize: xdp(15))),
                          ),
                        ),
                        onTap: () {
                          navigationService.navigate("tencent", address, lat, lng);
                          Navigator.pop(context);
                        },
                      )
                          : SizedBox()
                    ],
                  ),
                ),
              ),
            );
          }
      );
    }).catchError((e) {
      print("error: $e");
      progressDialog.dismiss();
      // showToast(e.message);
    });
  }
  
  xdp(double num) => num;
}


/// 水波纹效果
class InkBox extends StatelessWidget {

  final BorderRadius borderRadius;
  final Color color;
  final Widget child;
  final Function onTap;

  InkBox({
    this.borderRadius,
    this.color,
    @required this.onTap,
    @required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      // 必须指定裁剪类型，默认是不裁剪
      clipBehavior: Clip.antiAlias,
      borderRadius: borderRadius,
      child: Ink(
        // 对InkWell包裹的widget进行装饰，可以添加padding
        decoration: BoxDecoration(
          color: color == null ? Colors.white : color,
          borderRadius: borderRadius,
        ),
        child: InkWell(
          child: child,
          onTap: onTap,
        ),
      ),
    );
  }
}

const dividerWidget = const Divider(height: 0.5, thickness: 0.5, color: Colors.white54,);
