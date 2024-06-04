package cc.fastcv.notificationlistener

import android.content.Context
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log

object NotificationDispatcher {

    private const val TAG = "NotificationDispatcher"

    fun postNotification(sbn: StatusBarNotification, context: Context) {
        val title: CharSequence =
            sbn.notification.extras.getCharSequence("android.title")
                ?: ""
        val content: CharSequence =
            sbn.notification.extras.getCharSequence("android.text") ?: ""
        val appPackageName: String = sbn.packageName
        Log.d(
            TAG,
            "notification post:  title = $title   content = $content   appPackageName = $appPackageName "
        )

        if (isUnUseNotification(sbn)) {
            Log.e(TAG, "无用通知推送")
            return
        }

        if (isInCall(sbn.packageName)) {
            Log.e(TAG, "是来电")
            return
        }

        if (isBlackList(sbn.packageName)) {
            Log.e(TAG, "黑名单:默认不通知app")
            return
        }
        if (isSystemApp(sbn.packageName, context)) {
            Log.e(TAG, "系统类APP:默认不通知: $appPackageName")
            return
        }

        //推送
    }

    private fun isSystemApp(packageName: String, context: Context): Boolean {
        val result = try {
            val mPackageManager: PackageManager = context.packageManager
            val targetPkgInfo = mPackageManager.getPackageInfo(packageName, 64)
            val sys = mPackageManager.getPackageInfo("android", 64)
            targetPkgInfo?.signatures != null && sys.signatures[0] == targetPkgInfo.signatures[0]
        } catch (var5: PackageManager.NameNotFoundException) {
            false
        }
        return result
                && packageName != "com.android.server.telecom"
                && !packageName.contains("com.android.mms")
                && !packageName.contains("com.samsung.android.messaging")
                && !packageName.contains("com.samsung.android.dialer")
    }

    private fun isInCall(packageName: String): Boolean {
        return !(packageName != "com.android.incallui"
                && packageName != "com.android.contacts")
    }

    private fun isBlackList(packageName: String): Boolean {
        return packageName == "com.android.systemui"
                || packageName == "android"
                || packageName == "com.mfashiongallery.emag"
                || packageName == "com.android.deskclock"
                || packageName == "cn.baos.watch.w100"
                || packageName == "com.kugou.android"
                || packageName == "com.xiaomi.bsp.gps.nps"
                || packageName == "com.xiaomi.market"
                || packageName == "com.xiaomi.simactivate.service"
                || packageName == "com.xiaomi.mi_connect_service"
                || (packageName.startsWith("com.android") && !packageName.contains("com.samsung.android.messaging") && !packageName.contains(
            "com.android.mms"
        ) && packageName != "com.android.server.telecom")
                || packageName == "com.tencent.qqmusic"
    }

    private fun isUnUseNotification(sbn: StatusBarNotification): Boolean {
        return TextUtils.isEmpty(sbn.content())
    }

    fun StatusBarNotification.title(): CharSequence {
        return notification.extras.getCharSequence("android.title") ?: ""
    }

    fun StatusBarNotification.content(): CharSequence {
        return notification.extras.getCharSequence("android.text") ?: ""
    }

}