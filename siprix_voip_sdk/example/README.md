# siprix_voip_sdk_example



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
      * Open the file:
        siprix_voip_sdk/example/lib/main.dart

      * Scroll to around line 38, where the main() function is defined.

      * Place your cursor just before the main() function, and click Run (or press F5) to start the app.

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

Demonstrates how to use the siprix_voip_sdk plugin.

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://docs.flutter.dev/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://docs.flutter.dev/cookbook)

For help getting started with Flutter development, view the
[online documentation](https://docs.flutter.dev/), which offers tutorials,
samples, guidance on mobile development, and a full API reference.
