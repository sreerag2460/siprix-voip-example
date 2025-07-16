import 'package:flutter/material.dart';
import 'package:siprix_voip_sdk_example/global_variables.dart';

Future<void> setUpServiceLocator() async {
  serviceLocator.allowReassignment = true;
  serviceLocator.registerSingleton<GlobalKey<NavigatorState>>(
    GlobalKey<NavigatorState>(),
    instanceName: 'call_list',
  );
}
