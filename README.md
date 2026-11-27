# GiftList

A secret, passcode-locked gift list app for tracking gifts across multiple people. Built with Jetpack Compose + Material 3.

## 🔑 Passcode

**The default passcode is `2026`.**

Enter it on the lock screen to access your lists. The app automatically re-locks when it is sent to the background.

> ℹ️ The passcode is stored inside the app itself. It stops someone glancing at your phone from seeing your lists, but it is **not** encryption — anyone who decompiles the APK can read it. Treat it as casual privacy, not real security.

## Features

- **Passcode lock** — full-screen 4-digit pin pad with shake-on-error
- **Two gift lists** in tabs (Alex & Alyssa by default) — list names are fully editable in Settings (add / rename / delete)
- **Gift fields**: To, From, Item name, Shipper, plus Arrived & Wrapped toggles
- **Colored status chips**: Arrived = red, Wrapped = green (the Christmas pair)
- **Settings**: manage lists, reset all gifts, version info at the bottom
- **Offline & private** — data is stored locally on device via SharedPreferences; no accounts, no network

## Install

1. Download the latest APK from the [Releases page](https://github.com/Jeremyscell82/GiftList/releases).
2. Open the downloaded `.apk` on your Android phone.
3. If prompted, allow "Install unknown apps" from your browser or files app.
4. Open **GiftList** and enter passcode `2026`.

## Requirements

- Android 13 (API 33) or newer

## Build from source

```bash
git clone https://github.com/Jeremyscell82/GiftList.git
cd GiftList
./gradlew assembleDebug      # debug APK
./gradlew assembleRelease    # signed release APK (needs keystore.properties)
```

The release build requires a signing keystore (`keystore.properties` + `app/giftlist-release.jks`), which are intentionally kept out of this repo. The GitHub Actions workflow builds and signs releases automatically from its own secrets whenever a `v*` tag is pushed.
