#import "FlutterArPlugin.h"
#import <flutter_ar_plugin/flutter_ar_plugin-Swift.h>

@implementation FlutterArPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterArPlugin registerWithRegistrar:registrar];
}
@end
