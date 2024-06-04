package cc.fastcv.notificationlistener

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.NotificationManagerCompat


private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

private const val ACTION_NOTIFICATION_LISTENER_SETTINGS =
    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

/**
 * 判断通知监听服务是否开启
 */
fun isEnabled(context: Context): Boolean {
    val pkgName: String = context.packageName
    val flat: String = Settings.Secure.getString(
        context.contentResolver,
        ENABLED_NOTIFICATION_LISTENERS
    )
    if (!TextUtils.isEmpty(flat)) {
        val names = flat.split(":").toTypedArray()
        for (i in names.indices) {
            val cn = ComponentName.unflattenFromString(names[i])
            if (cn != null) {
                if (TextUtils.equals(pkgName, cn.packageName)) {
                    return true
                }
            }
        }
    }
    return false
}

/**
 * 跳转去开启通知监听服务
 */
fun openNotificationAccess(context: Context) {
    context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
}


fun areNotificationsEnabled(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}