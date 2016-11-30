@echo off
adb kill-server
adb wait-for-device
:: adb push C_security /sdcard/C_security
:: adb wait-for-device
:: adb usb 
:: adb wait-for-device
adb root
adb wait-for-device
adb remount
adb wait-for-device
adb shell setenforce 0
adb shell "echo 7 > /sys/module/mmc_core/parameters/debug_level"
pause
