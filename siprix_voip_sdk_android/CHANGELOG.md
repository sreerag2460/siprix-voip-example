## 1.0.10
- Improved CallRecording (capture local+remote sound, use mp3 encoder, write mono or stereo)
- Added new ini properties 'recordStereo', 'useDnsSrv'
- Fixed bug which prevents stop foreground service after re-create Activity
- Fixed crash when library instance has been destroyed and re-initialized in the same process
- Fixed crash on create service and resolve app name

## 1.0.9
- Fixed bug with sending statusCode in the 'onTerminated' callback
- Added ability to switch calls automatically after call un-held, connected

## 1.0.8
- Added new ini property 'UnregOnDestroy'
- Set compileSdk version to 35
- Notifications:
  * Set notification style 'CallStyle.forIncomingCall' on devices with SDK_INT>=31
  * Added ability to set notification icon in app resources
  * Added ability to customize notification using own native class
- Permissions:
  * Request permission 'BLUETOOTH_CONNECT' in runtime, prevent crash when it missed
  * Request camera permission only when manifest contains it
- Fix vibrate in background

## 1.0.7
- Updated SiprixRinger implementation (don't set audioManager mode; modified vibraror)

## 1.0.6
- Added ability to send and receive text messages (SIP MESSAGE request)
- Added ability to override DisplayName in outgoing call (method 'Dest_SetDisplayName')
- Added ability to handle received MediaControlEvent 'picture_fast_update'
- Fixed bug in 'RewriteContactIp' option implementation when TCP/TLS transport is using
- Fixed parsing RTCP FB parameters of video in SDP
- Android: updated permissions request functionality
- Android: added ability to switch camera by invoke 'setVideoDevice(0)'

## 1.0.5
* Send call incoming/accepts events to the app only after sync accounts
  (happens when activity destroyed, but service continues running and received new call)

## 1.0.4
* Fixed potential crash when app switched between networks 

## 1.0.3
* Added ability to handle AirPlaneMode ON/OFF
* Fixes related to handle networks switching; 

## 1.0.2 * 
* Fixed handling case when app adds duplicate subscription.
* Now library raises error 'ESubscrAlreadyExist' and also returns existing subscrId

## 1.0.1
* Added new ini property 'brandName'
* Enabled ability to make attended transfer when call on hold

## 1.0.0
* Initial release. 
* Contains implementation based on method channels.
