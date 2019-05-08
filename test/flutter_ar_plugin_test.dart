import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_ar_plugin/flutter_ar_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_ar_plugin');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FlutterArPlugin.platformVersion, '42');
  });
}
