# siprix_voip_sdk


How to start
==============

1. Install Flutter SDK
    Windows / macOS / Linux:
        Go to: https://flutter.dev/docs/get-started/install

        Download Flutter SDK for your OS.

        Extract it to a preferred location, e.g., C:\src\flutter (Windows) or ~/development/flutter (macOS/Linux).
2. Set Environment Variable (PATH)
      * Windows:
            Search “Environment Variables” in Start Menu.

            Add:
            C:\src\flutter\bin
            to the System PATH.

      * macOS / Linux (in terminal):
            export PATH="$PATH:`pwd`/flutter/bin"
    
3. Run Flutter Doctor
      * Open terminal or command prompt: 'flutter doctor'

4. Install Visual Studio Code
      * Download from: https://code.visualstudio.com/

      * Install normally.


5. Install VS Code Extensions
      * In VS Code:

            Open Extensions Panel (Ctrl+Shift+X / Cmd+Shift+X)

      * Install extensions:

            ✅ Flutter

            ✅ Dart

6. Set Up Android Emulator or Device
    Option A: Android Studio Emulator
      * Download Android Studio

      * Open it → Tools > Device Manager

      * Create a new emulator (Pixel 5 or any device).

      * Start the emulator.

    Option B: Real Android Device
      * Enable Developer Options on your phone.

      * Turn on USB Debugging.

      * Connect phone via USB.

      * Run: 'flutter devices'

7. Set up xcode 
  download xcode 

- 


Running the Siprix Example Application (Flutter)
-----------------------------------------------


1. Clean and Get Packages
      * Open Terminal or VS Code Terminal.

      * Navigate to the example folder inside the Siprix VoIP SDK: 'cd siprix-voip-sdk/example'

      * Run the following commands:
          'flutter clean'
          'flutter pub get'
          This ensures that the project is reset and all necessary packages are installed correctly.

2. Open Main File and Run

      1. Requirements
            Mac with macOS installed

            Xcode (latest version)

            Flutter SDK installed

            Physical iPhone

            Apple ID or Developer Account (Free or Paid)

      2. Connect Device

            * enable developer option in ios device (settings -> privacy& security)

            * Plug in your iPhone via cable

            * Unlock your iPhone

            * Trust the Mac if prompted (on device and on Mac)

            * Run in terminal: 'flutter devices'
            You should see your iPhone listed.

      * Configure Xcode for First Use
            Open iOS project in Xcode:

                  open ios/Runner.xcworkspace (you can see ios folder inside siprix_voip_sdk/example folder. Right click and open in xcode)
            Then:

                  Go to Runner → Signing & Capabilities tab

                  Set your team under Signing

                  Use your Apple ID (auto sign-in available)

                  Set a unique bundle identifier like com.yourname.appname (already done this is bundle identifier 'com.example.siprixvoiptrial')
      

      * Open the file:
        siprix_voip_sdk/example/lib/main.dart

      * Scroll to around line 38, where the main() function is defined.

      * Place your cursor just before the main() function, and click Run to start the app.

      OR use following commands (Navigate to the siprix_voip_sdk/example)

      * 'flutter clean; flutter pub get; cd ios; pod deintegrate; pod install; pod update;'   (Removes the build/ and .dart_tool/ directories.clearing cached build files)

      * 'flutter run'

      * select your device from the list using device order number example (1,2,3)



3.  Add Your SIP Account

      When the app launches, it will land on the Account screen.

      * Tap the ➕ Plus icon to add a new SIP account.

      * Fill in the following details:

          Server

          Extension / Username

          Password

          Other optional settings

      This will register your account with the SIP server.

4. Configure Advanced Account Features
    * Navigate to the account_add.dart(siprix_voip_sdk/example/lib/account_add.dart) screen.

    * Here you can configure advanced settings for your account, such as:

        Codecs

        Secure Media

        Transport

        Other SIP-related features

5. Enable Foreground Mode (Android Only)
    * Go to the Settings screen within the app.

    * Enable the Foreground Mode toggle.

    🔐 This is required for keeping the VoIP service running in the background on Android.    

6. View Logs
    * You can view runtime logs in the Logs section of the app.

    * This helps with debugging and tracking call states, registrations, errors, etc.


===============

Siprix VoIP SDK plugin for embedding voice-over-IP (VoIP) audio/video calls based on SIP/RTP protocols into Flutter applications.
It contains native SIP client implementations for 5 platforms: Android, iOS, MacOS, Windows, Linux and unified API for all them. 

Plugin implements ready to use SIP VoIP Client with ability to:
- Add multiple SIP accounts
- Make/receive multiple calls (Audio and Video)
- Manage calls with: hold, mute microphone/camera, send/receive DTMF, ...
- Record sound of call to mp3 file/play sound to call from mp3 file
- Join calls to conference, blind and attended transfer
- Secure SIP signaling (using TLS) and call media (using SRTP)
- Detect network changes and automatically update registration/switch and restore call(s) media
- Echo cancelation and noise suppression
- Create BLF/Presence subscriptions and monitor state of remote extension(s)
- Send and receive text messages
- Ready to use models for fast and easy UI creating
- Embedded PushKit+CallKit support in iOS version of plugin
- Embedded FCM support for Android version of plugin

## Usage

### Add dependency in pubspec.yaml
```
dependencies:
  siprix_voip_sdk: ^1.0.16
  provider: ^6.1.1
```

### Add imports
```
import 'package:provider/provider.dart';

import 'package:siprix_voip_sdk/accounts_model.dart';
import 'package:siprix_voip_sdk/network_model.dart';
import 'package:siprix_voip_sdk/calls_model.dart';
import 'package:siprix_voip_sdk/cdrs_model.dart';
import 'package:siprix_voip_sdk/devices_model.dart';
import 'package:siprix_voip_sdk/logs_model.dart';
import 'package:siprix_voip_sdk/siprix_voip_sdk.dart';
```

### Prepare models

```dart
void main() async {
  AccountsModel accountsModel = AccountsModel();
  CallsModel callsModel = CallsModel(accountsModel);
  runApp(
    MultiProvider(providers:[
      ChangeNotifierProvider(create: (context) => accountsModel),
      ChangeNotifierProvider(create: (context) => callsModel),
    ],
    child: const MyApp(),
  ));
}
```
### Init SDK
```dart
class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    _initializeSiprix();
  }

  void _initializeSiprix([LogsModel? logsModel]) async {
    InitData iniData = InitData();
    iniData.license  = "...license-credentials...";
    iniData.logLevelFile = LogLevel.info;
    //- uncomment if required -//
    //iniData.enableCallKit = true;
    //iniData.enablePushKit = true;
    //iniData.unregOnDestroy = false;
    SiprixVoipSdk().initialize(iniData, logsModel);
  }
```

### Build UI, add accounts/calls
```dart
Widget buildBody() {
    final accounts = context.watch<AccountsModel>();
    final calls = context.watch<CallsModel>();
    return Column(children: [
      ListView.separated(shrinkWrap: true,
        itemCount: accounts.length,
        separatorBuilder: (BuildContext context, int index) => const Divider(height: 1),
        itemBuilder: (BuildContext context, int index) {
          AccountModel acc = accounts[index];
          return
            ListTile(title: Text(acc.uri, style: Theme.of(context).textTheme.titleSmall),
                subtitle: Text(acc.regText),
                tileColor: Colors.blue
            );
        },
      ),
      ElevatedButton(onPressed: _addAccount, child: const Icon(Icons.add_card)),
      ElevatedButton(onPressed: _addCall, child: const Icon(Icons.add_call)),
      ...
}

void _addAccount() {
    AccountModel account = AccountModel();
    account.sipServer = "192.168.0.122";
    account.sipExtension = "1016";
    account.sipPassword = "12345";
    account.expireTime = 300;
    context.read<AccountsModel>().addAccount(account)
      .catchError(showSnackBar);
}


void _addCall() {
    final accounts = context.read<AccountsModel>();
    if(accounts.selAccountId==null) return;

    CallDestination dest = CallDestination("1012", accounts.selAccountId!, false);

    context.read<CallsModel>().invite(dest)
      .catchError(showSnackBar);
}
  
void showSnackBar(dynamic err) {
  ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(err)));
}
  
```

[More detailed integration guide](https://docs.siprix-voip.com/rst/flutter.html#integration-into-flutter-application)

Please contact [support@siprix-voip.com](mailto:support@siprix-voip.com) if you have technical questions.


## How to integrate PushKit+CallKit support?
[See detailed manual here](https://docs.siprix-voip.com/rst/ioscallkit.html#integrate-pushkit-callkit-into-flutter-application)


## How to integrate Android FCM?
[See detailed manual here](https://docs.siprix-voip.com/rst/flutter.html#android-add-firebase-push-notifications)


## How to use this library without provider?

Library doesn't have any limitations related to provider.
You can copy source code of existing models to your project and use as you need/want.
Also you can create own classes and directly invoke library's methods like:\
`int callId = await SiprixVoipSdk().invite(dest) ?? 0;`\
`int accId  = await SiprixVoipSdk().addAccount(acc) ?? 0;`\

The same is true for listening events - add own class as listener which will handles events:\
`SiprixVoipSdk().accListener = MyAccStateListener(regStateChanged : onRegStateChanged);`


## Limitations

Siprix doesn't provide VoIP services, but in the same time doesn't have backend limitations and can connect to any SIP (Server) PBX or make direct calls between clients.
For testing app you need an account(s) credentials from a SIP service provider(s). 
Some features may be not supported by all SIP providers.

Attached Siprix SDK works in trial mode and has limited call duration - it drops call after 60sec.
Upgrading to a paid license removes this restriction, enabling calls of any length.

Please contact [sales@siprix-voip.com](mailto:sales@siprix-voip.com) for more details.

## More resources

Product website: [siprix-voip.com](https://www.siprix-voip.com/product/)

Manual: [docs.siprix-voip.com](https://docs.siprix-voip.com)


## Screenshots

<a href="https://docs.siprix-voip.com/screenshots/Flutter_Accounts.png"  title="Accounts list Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Accounts_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_CallAdd.png"  title="Add call Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_CallAdd_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_Calls.png"  title="Call in progress Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Calls_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_CallsDtmf.png"  title="Call in progress DTMF Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_CallsDtmf_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_BLF.png"  title="BLF subscription Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_BLF_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_Messages.png"  title="Messages Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Messages_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_Logs.png"  title="Logs Android">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Logs_Mini.png" width="50"></a>

<a href="https://docs.siprix-voip.com/screenshots/Flutter_Accounts_Win.png"  title="Accounts list Windows">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Accounts_Win_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_Calls_Win.png"  title="Call in progress Windows">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Calls_Win_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_CallsDtmf_Win.png"  title="Call in progress DTMF Windows">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_CallsDtmf_Win_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_BLF_Win.png"  title="BLF subscription Windows">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_BLF_Win_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_Messages_Win.png"  title="Messages Windows">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Messages_Win_Mini.png" width="50"></a>
<a href="https://docs.siprix-voip.com/screenshots/Flutter_Logs_Win.png"  title="Logs Windows">
<img src="https://docs.siprix-voip.com/screenshots/Flutter_Logs_Win_Mini.png" width="50"></a>