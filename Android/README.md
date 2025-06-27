# MTGChatbot

## Purpose
This purpose of this project was to build a model that utilized some form of Aritificial Intelligence. For this case, the app is a Magic the Gathering Chat bot that will inform the user about general card data, specific rules for the card, and which set the card is present in.

## Required Software
In order to edit this app you will need [Android Studio](https://developer.android.com/studio?gad_source=1&gbraid=0AAAAAC-IOZkRzVkn0T9vu-nf8JLBf_Npv&gclid=CjwKCAjwn6LABhBSEiwAsNJrjsjog51mW--a_QL9c6xkyRMb3EgyC82dCQrEod5DDqDwcQY-nwiChhoC7uMQAvD_BwE&gclsrc=aw.ds) and [Git](https://git-scm.com/).

## Project Setup
To edit the project you will need to clone/fork the repositor
```sh
git clone https://github.com/shama2025/MTGChatbot.git
```
```sh
git fork https://github.com/shama2025/MTGChatbot.git
```
The file hirearchy should look like this:
```
MTGChatbotProject/
├── app/                                        # Android app module (source code, resources)
│   └── ...                                    # Contains src/, res/, AndroidManifest.xml, etc.
├── gradle/                                    # Gradle wrapper config
│   └── ...                                    # Wrapper JAR and properties
├── .gitignore                                 # Git ignored files
├── README.md                                  # Project description
├── build.gradle.kts                           # Top-level Gradle script (Kotlin DSL)
├── gradle.properties                          # Gradle project settings
├── gradlew                                    # Gradle wrapper script (Unix)
├── gradlew.bat                                # Gradle wrapper script (Windows)
├── settings.gradle.kts                        # Gradle module definitions
├── src/                                       # Main source directory for the app
│   ├── main/                                  # Main application code
│   │   ├── java/com/mashaffer/mymtgchatbot/   # Your app's Kotlin/Java code
│   │   └── res/                               # App resources like layouts, drawables, etc.
│   │       ├── drawable/                      # Images and icons
│   │       │   └── ic_launcher-playstore.png  # Play Store launcher icon
│   │       ├── layout/                        # XML layouts
│   │       ├── values/                        # Values like colors, strings, styles
│   │       └── ...                            # Other resource types (e.g., mipmaps)
│   ├── androidTest/                           # Instrumented tests (runs on Android device/emulator)
│   │   └── java/com/mashaffer/mymtgchatbot/   # UI and instrumentation tests
│   ├── test/                                  # Unit tests (runs on JVM)
│   │   └── java/com/mashaffer/mymtgchatbot/   # Unit test classes
├── AndroidManifest.xml                        # The Android manifest file

```

Once here the res directory and main directory will house the kotlin and xml files needed to update the view and functionalty

## Running App
In order to run the app using Android Studio you will need to [create an AVD](https://developer.android.com/studio/run/managing-avds) or [pair your samsung phone with Android Studio](https://developer.android.com/codelabs/basic-android-kotlin-compose-connect-device#0)

Then you need to click the run button found at the top of the IDE, it may take a few moments but once it runs your UI should look similar to the image below

![appMainActivity](assets/appMainActivity.gif)

