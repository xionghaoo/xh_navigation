import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
// import 'package:ourea/utils/screen_adaption.dart';

class ProgressDialog {
  BuildContext context;
  String message;

  ProgressDialog(this.context, {this.message = "加载中..."});

  show() {
    showDialog<void>(
        context: context,
        barrierDismissible: false,
        builder: (context) => WillPopScope(
          // 允许用户点返回关闭
          onWillPop: () async => true,
          child: Dialog(
            backgroundColor: Colors.transparent,
            elevation: 0,
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(4)
            ),
            child: Container(
              height: 80,
              alignment: Alignment.center,
              child: Container(
                width: 80,
                height: 80,
                alignment: Alignment.center,
                decoration: BoxDecoration(
                    color: Colors.black26,
                    borderRadius: BorderRadius.circular(4)
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.grey),
                      strokeWidth: 2,
                    ),
                    SizedBox(height: 8,),
                    Text(message, style: TextStyle(color: Colors.grey, fontSize: 12),),
                  ],
                ),
              ),
            ),
          ),
        )
    );
  }

  dismiss() {
    Navigator.of(context).pop();
  }
}