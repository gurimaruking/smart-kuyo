@echo off
set JAVA_HOME=C:\Users\robostadion\Android\jdk21
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d C:\Users\robostadion\OneDrive\claudecode\Apply\Android\ihai
"%JAVA_HOME%\bin\java.exe" "-Dorg.gradle.appname=gradlew" -classpath "gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain assembleDebug --no-daemon
