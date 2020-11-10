import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:xh_navigation/xh_navigation.dart';

void main() {
  const MethodChannel channel = MethodChannel('xh_navigation');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await XhNavigation.platformVersion, '42');
  });
}
