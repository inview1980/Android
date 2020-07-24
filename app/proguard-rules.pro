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


#-dontoptimize                        #不进行优化，建议使用此选项，
#-dontpreverify                       #不进行预校验,Android不需要,可加快混淆速度。
#-ignorewarnings                      #忽略警告
#-optimizationpasses 5               #指定代码的压缩级别
#
#-keepattributes Signature #范型
##native方法不混淆
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
##v4包不混淆
#-keep class androidx.appcompat.app.** { *; }
#-keep interface androidx.appcompat.app.** { *; }
#-keep class com.jph.android.entity.** { *; } #实体类不参与混淆
#-keep class com.jph.android.view.** { *; } #自定义控件不参与混淆
#-dontwarn com.alibaba.fastjison.**
#
##JavaBean
#-keepclassmembers public class cn.net.duqian.bean.** {
#   void set*(***);
#   *** get*();
#}
##-keep class com.xx.duqian_cloud.JavaScriptInterface { *; }#webview js
#
##忽略 libiary 混淆
##-keep class io.vov.vitamio.** { *; }
#
##butterknife不混淆
#-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder { *; }
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}