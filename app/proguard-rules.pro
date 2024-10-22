# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep class com.hihonor.mcs.system.diagnosis.**{*;}
#1.仅订阅稳定性故障：
-keep class com.hihonor.mcs.system.diagnosis.core.BasePayload { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.ANRMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.BaseFaultMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.TombstoneMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.CrashMetirc { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.FdleakMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.KilledMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.KilledMetric$KilledType
-keep class com.hihonor.mcs.system.diagnosis.core.stability.MemoryleakMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.MemoryleakMetric$MemoryLeakType
-keep class com.hihonor.mcs.system.diagnosis.core.stability.ThreadleakMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.stability.StabilityPayload { *;}
#2.仅订阅性能故障：
-keep class com.hihonor.mcs.system.diagnosis.core.BasePayload { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.ActivityStartTimeoutMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.AnimationJankFrameMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.AppSkipFrameMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.AppStartupSlowMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.BasePerformanceMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.FlingJankFrameMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.PerformancePayload { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.performance.BasePerformanceMetric$OperationType
-keep class com.hihonor.mcs.system.diagnosis.core.performance.BasePerformanceMetric$OptSceneType
#3.仅订阅功耗故障：
-keep class com.hihonor.mcs.system.diagnosis.core.BasePayload { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermalMetric { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermalPayload { *;}
-keep class com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermalMetric$PowerType
#4.仅订阅压力故障：
-keep class com.hihonor.mcs.system.diagnosis.core.pressure.**{*;}