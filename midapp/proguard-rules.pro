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

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}



#httpdns
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.ta.**{*;}
-keep class com.ut.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.ta.**
-dontwarn com.ut.**

#cps
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.ta.**{*;}
-keep class com.ut.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.ta.**
-dontwarn com.ut.**
-keepclasseswithmembernames class ** {
native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.alipay.** {*;}
-dontwarn com.alipay.**
-keep class anet.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**

#hotfix
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/buidl/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
#防止inline
-dontoptimize

#man
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.ta.**{*;}
-keep class com.ut.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.ta.**
-dontwarn com.ut.**

#feedback
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.ta.**{*;}
-keep class com.ut.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.ta.**
-dontwarn com.ut.**
