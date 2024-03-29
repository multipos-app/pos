app_name=multipos
SHELL := /bin/bash
PATH := $(PATH)

pos:
	./gradlew installEnGenericDebug
	/home/q/Android/Sdk/platform-tools/adb shell am start -n cloud.multipos.pos/cloud.multipos.pos.Pos

log:
	/home/q/Android/Sdk/platform-tools/adb logcat | grep '\multiPos\|AndroidRun'

clean:
	./gradlew clean cleanBuild
