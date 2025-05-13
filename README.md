# 🔐 Port Activity Logger - Android Security Utility App

## ⚡ Overview

**Port Activity Logger** is a **powerful security utility application for Android devices**, designed to give users full transparency and control over their device’s physical activity. Whether you're leaving your phone for repair, suspect unauthorized access, or simply want peace of mind — **this app becomes your digital guardian**.

Developed entirely in **Kotlin**, this lightweight yet highly robust tool **silently logs and monitors critical hardware events** to ensure your device's ports and access points are not being tampered with.

---

## 📲 What It Does

Port Activity Logger captures and securely logs the following device events:

### 🔌 **Charging Events**
- ✅ Detects **when a charger is connected**
- ❌ Detects **when the charger is disconnected**

### 📶 **SIM Activity**
- 📤 Logs **SIM card ejection**
- 📥 Logs **SIM card insertion**

### 🔄 **Device Power Status**
- ⏻ Logs **when the phone is turned ON**
- ⛔ Logs **when the phone is turned OFF**

### 🔗 **USB/OTG/Data Cable Events**
- 🖇️ Detects when **USB cables are connected or disconnected**
- 🔄 Identifies **file transfers or device-to-device connections**

---

## 🔐 Security Features

Your data is valuable — and so is your privacy. That’s why **Port Activity Logger includes multiple layers of security** to safeguard sensitive information and application access.

### 🧩 **Access Control**
- Set a **custom password or PIN** to protect the application
- Optional **biometric authentication** support (if the device supports it)

### 🚨 **Tamper Detection**
- Notifies the user if the **app is forcefully stopped, tampered with, or uninstalled**
- Shows an optional warning notification if **unauthorized deletion is attempted**

### 🕵️ **Discreet Monitoring**
- **Runs in the background** without disrupting normal device usage
- Completely **offline and secure** — no data is sent to the cloud

### 🔍 **Transparency-First**
- Every event is **timestamped and logged**
- Logs can be **exported** for forensic or legal use
- No hidden services, no root access required, **100% user consent driven**

---

## 💡 Use Cases

- ✅ Leaving your device for repair? Know exactly what happened while it was out of your hands.
- ✅ Suspect someone is snooping? Get silent logs of SIM and USB activity.
- ✅ Parents or professionals? Keep tabs on unauthorized charging or device usage.

---

## 📦 Built With

- 👨‍💻 **Kotlin** – Modern, concise, safe, and expressive programming language for Android
- 🧰 Android Jetpack Libraries
- 🔄 Android Broadcast Receivers
- 🛡️ Local Storage Encryption
- 🔒 BiometricPrompt API (Optional)

---

## 📁 App Architecture
📦 port-activity-logger/
┣ 📂 app
┃ ┣ 📂 src
┃ ┃ ┣ 📂 main
┃ ┃ ┃ ┣ 📂 java
┃ ┃ ┃ ┃ ┣ 📂 com.yourpackage.logger
┃ ┃ ┃ ┃ ┃ ┣ 📄 BootReceiver.kt
┃ ┃ ┃ ┃ ┃ ┣ 📄 PowerReceiver.kt
┃ ┃ ┃ ┃ ┃ ┣ 📄 UsbReceiver.kt
┃ ┃ ┃ ┃ ┃ ┣ 📄 SimReceiver.kt
┃ ┃ ┃ ┃ ┃ ┣ 📄 LoggerService.kt
┃ ┃ ┃ ┃ ┃ ┣ 📄 AuthenticationManager.kt
┃ ┃ ┃ ┣ 📂 res
┃ ┃ ┃ ┃ ┣ 📄 activity_main.xml
┃ ┃ ┃ ┣ 📄 AndroidManifest.xml


---

## 🔧 Permissions Used

| Permission | Purpose |
|-----------|---------|
| `RECEIVE_BOOT_COMPLETED` | Start monitoring on device boot |
| `READ_PHONE_STATE` | Detect SIM insert/eject |
| `BATTERY_STATS` | Detect charger connection/disconnection |
| `FOREGROUND_SERVICE` | Background operation |
| `PACKAGE_USAGE_STATS` | Monitor uninstall attempts (optional prompt) |

> All permissions are **declared transparently** and **requested only with user consent**, following **Google Play Store** policy guidelines.

---

## 🔒 Privacy First

We take your privacy seriously. **No data leaves your device. Ever.**  
All logs are stored locally and **encrypted** for your security.

---

## 🚀 How to Use

1. **Install the app** on your Android device.
2. Grant the **required permissions** when prompted.
3. Set up your **PIN/password lock** for app access.
4. Let the app run in the background. All events will be logged automatically.
5. Open the app anytime to **view, search, or export** logs.

---

## 🌐 Future Features (Coming Soon)

- 📤 Cloud sync option (opt-in)
- 📊 Visual graphs and daily summaries
- 🔔 Smart notifications for suspicious activity
- 📍 Geo-tagging events
- 🧠 AI-driven anomaly detection

---

## 🏆 Why Port Activity Logger Stands Out

✅ No root required  
✅ Lightweight and battery-efficient  
✅ Built with modern Android standards  
✅ Designed for everyday users and IT professionals  
✅ Offline, transparent, secure

---

## 📧 Feedback and Support

We’re constantly improving! If you encounter any issues, have suggestions, or want to contribute:

📩 Email: `uzaifkhan7867@gmail.com`  


---

## ⚠️ Disclaimer

This application is intended for **personal device monitoring only**. Unauthorized tracking or surveillance of others’ devices without their consent is strictly prohibited and may be illegal in your jurisdiction.

---

## 📜 License

MIT License
Copyright (c) 2025
Permission is hereby granted, free of charge, to any person obtaining a copy...


Stay one step ahead.  
**Secure your device like a pro — with Port Activity Logger.**
