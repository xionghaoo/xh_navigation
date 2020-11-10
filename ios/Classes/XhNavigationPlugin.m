#import "XhNavigationPlugin.h"
#if __has_include(<xh_navigation/xh_navigation-Swift.h>)
#import <xh_navigation/xh_navigation-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "xh_navigation-Swift.h"
#endif

@implementation XhNavigationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftXhNavigationPlugin registerWithRegistrar:registrar];
}
@end
