import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_ar_plugin/flutter_ar_plugin.dart';

void main() => runApp(MyApp());


class MyApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return MaterialApp(
      home: Scaffold(
        body: Container(
          decoration: const BoxDecoration(
            image: DecorationImage(
                alignment: Alignment(-.2, 0),
                image: AssetImage(
                    'assets/images/background.png'),
                fit: BoxFit.cover),
          ),
          alignment: Alignment.bottomCenter,
          padding: EdgeInsets.only(bottom: 150),
          child: RaisedButton(
            color: Colors.blue,
            textColor: Colors.white,
            padding: EdgeInsets.only(left: 35,top: 15,right: 35,bottom: 15),
            onPressed: FlutterArPlugin.startArActivity,
            child: (Text('Start Scaning')),
          ),
        ),
      ),
    );
  }
}