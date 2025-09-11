# APK Uploader

APK Uploader is a desktop application for developers that simplifies the process of installing
Android application packages (APKs) on connected devices.

## Features

* **APK File Selection:** Easily browse and select APK files from your computer.
* **Device Discovery:** Automatically detects all Android devices connected to your computer via
  ADB (Android Debug Bridge).
* **Simple Installation:** Install APKs with a single click.
* **Real-time Progress:** Monitor the installation progress.
* **Cross-platform:** Runs on any desktop operating system that supports Java.

## Prerequisites

Before using APK Uploader, you need to
have [Android Debug Bridge (ADB)](https://developer.android.com/tools/releases/platform-tools)
installed and accessible from your system's command line.

## How to Use

1. **Launch the application:** Run the application from your IDE or by using the command line.
2. **Select an APK:** Click the "Pick" button to open a file dialog and choose the APK file you want
   to install.
3. **Choose a Device:** A list of connected devices will be displayed. Select the device on which
   you want to install the APK.
4. **Install:** Click the "Proceed" button to start the installation.
5. **Monitor Progress:** The application will show the installation progress. A success message will
   be displayed upon completion.

## Build and Run

To build and run the development version of the desktop app, use the run configuration from the run
widget in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```
