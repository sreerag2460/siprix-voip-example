## 1.0.16
- Improved CallRecording (capture local+remote sound, use mp3 encoder, write mono or stereo)
- Added new ini properties 'recordStereo', 'useDnsSrv'
- Flutter example app: Added 'Record' menu, updated 'PlayFile' menu impl
- Android.Fixed bug which prevents stop foreground service after re-create Activity
- Android.Fixed crash when library instance has been destroyed and re-initialized in the same process
- Android.Fixed crash on create service and resolve app name

## 1.0.15
- Fixed bug with sending statusCode in the 'onTerminated' callback
- Added ability to switch calls automatically after call un-held, connected
- iOS: Fixed switching between calls and join calls to conference when enabled CallKit

## 1.0.14
- Added new ini property 'UnregOnDestroy'
- Updated 'CdrsModel' with ability to set MaxNumber of items and access items from inherited class
- Android: Set compileSdk version to 35
- Android.Notifications:
  * Set notification style 'CallStyle.forIncomingCall' on devices with SDK_INT>=31
  * Added ability to set notification icon in app resources
  * Added ability to customize notification using own native class
- Android.Permissions:
  * Request permission 'BLUETOOTH_CONNECT' in runtime, prevent crash when it missed
  * Request camera permission only when manifest contains it
- Android: Fix vibrate in background
- iOS: Send events from library to the app using UI thread

## 1.0.13
- Android: Updated SiprixRinger implementation
- Android: Redesigned plugin with ability to work in background isolate and handle push notif
- Updated example app with Firebase push notification related functionality

## 1.0.12
- iOS: Redesigned and improved CallKit+PushKit implementation
- Updated example app with PushKit related functionality

## 1.0.11
- Added ability to send and receive text messages (SIP MESSAGE request)
- Added ability to override DisplayName in outgoing call (method 'Dest_SetDisplayName')
- Added ability to handle received MediaControlEvent 'picture_fast_update'
- Fixed bug in 'RewriteContactIp' option implementation when TCP/TLS transport is using
- Fixed parsing RTCP FB parameters of video in SDP
- Android: added ability to switch camera by invoke 'setVideoDevice(0)'
- Android: updated permissions request functionality
- iOS: Added PushKit support

## 1.0.10
* iOS: Fixed closing app caused by SIGPIPE signal
* Android: Send callIncoming/accept events to the app only after sync accounts
* Android: Restored foreground service permissions in example app manifest 
  (plugin doesn't add them by default)
* Added more arguments to the method 'CallsModel::sendDtmf'

## 1.0.9
* iOS: Updated TLS transport implementation (use TLS1.3 by default, ability to use also 1.2 and 1.0)
* iOS: Improved ability to detect transports lose/switch and automatically restore registration
* iOS: Added more detailed log output for some cases
* iOS: Added CallKit icon
* Android: Removed foreground service permissions

## 1.0.8
* Android, iOS: Fixed potential crash when app switched between networks and updates registration 
* Generate random port number on plugin level when adding new account

## 1.0.7
* Android: Added ability to handle AirPlaneMode ON/OFF; Fixes related to handle networks switching; 
* iOS: Fixes related to handle networks switching and restore registration when app becomes active; 

## 1.0.6
* iOS: Fixed crash when app restored from background

## 1.0.5
* iOS: Added ability to re-create transports when app become active after long time in background
* Fixed wrong argument of method 'updateAccount' in 'platform_interface'

## 1.0.4
* Added CallKit support to iOS (library automatically manages it)
* Fixed logs flooding with UDP transport error

## 1.0.3
* Updated iOS lib (added MinimumOSVersion in plist)

## 1.0.2
* Fixed handling case when app adds duplicate subscription.
* Now library raises error 'ESubscrAlreadyExist' and also returns existing subscrId

## 1.0.1
* Fixed podspec file for ios/macos
* Added documentation comments
* Added new ini property 'brandName'
* Enabled ability to make attended transfer when call on hold

## 1.0.0
* Initial release. 
* Includes SiprixSDK in binary form for 5 platforms and ready to use models for easy UI development.
