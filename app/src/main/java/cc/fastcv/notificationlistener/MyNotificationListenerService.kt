package cc.fastcv.notificationlistener

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RemoteController
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MyNotificationListenerService : NotificationListenerService(),
    RemoteController.OnClientUpdateListener {

    companion object {
        private const val TAG = "MyNLS"
    }

    private lateinit var remoteController: RemoteController

    private val musicInfo = MusicInfo("",0,0)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        registerRemoteController(this, this)
    }

    @SuppressLint("WrongConstant")
    private fun registerRemoteController(
        context: Context,
        updateListener: RemoteController.OnClientUpdateListener?
    ) {
        Log.d(TAG, "注册 registerRemoteController")
        remoteController = RemoteController(context, updateListener);
        var registered: Boolean
        try {
            registered =
                (context.getSystemService("audio") as AudioManager).registerRemoteController(this.remoteController)
        } catch (var6: NullPointerException) {
            registered = false
        } catch (var7: Exception) {
            registered = false
            Log.d(TAG, var7.message ?: "null")
        }
        if (registered) {
            Log.d(TAG, "注册 registerRemoteController registered:true")
            try {
                this.remoteController.setSynchronizationMode(1)
            } catch (var5: IllegalArgumentException) {
                var5.printStackTrace()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "NotificationListener notification service重启模式:$flags")
        return NotificationListenerService.START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "消息监听服务notification service onDestroy")
        super.onDestroy()
    }

    override fun onListenerConnected() {
        Log.d(TAG, "notification service onListenerConnected")
    }

    override fun onNotificationRankingUpdate(rankingMap: NotificationListenerService.RankingMap?) {
        Log.d(TAG, "notification service onNotificationRankingUpdate rankingMap = $rankingMap ")
    }

    override fun onNotificationPosted(
        sbn: StatusBarNotification?,
        rankingMap: NotificationListenerService.RankingMap?
    ) {
        Log.d(TAG, "notification service onNotificationPosted")
        sbn?.let {
            NotificationDispatcher.postNotification(it,baseContext)
        }

    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: NotificationListenerService.RankingMap?
    ) {
//        Log.d(TAG, "notification service onNotificationRemoved")
//        sbn?.let {
//            val title: String =
//                it.notification.extras.getString("android.title")
//                    ?: ""
//            val content: String? =
//                it.notification.extras.getString("android.text")
//            val appPackageName: String = it.packageName
//            Log.d(
//                TAG,
//                "notification remove:  title = $title   content = $content   appPackageName = $appPackageName "
//            )
//        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "notification service onListenerDisconnected")
    }

    @Deprecated("Deprecated in Java")
    override fun onClientChange(clearing: Boolean) {
        Log.d(TAG, "onClientChange clearing:$clearing")
        musicInfo.state = 0
        logMusicInfo()
    }

    private fun logMusicInfo() {
        Log.d(TAG, "$musicInfo")
        MusicInfoManager.notifyMusicInfo(musicInfo)
    }


    /**
     * public final static int PLAYSTATE_STOPPED            = 1;
     *     /**
     *      * Playback state of a RemoteControlClient which is paused.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *     public final static int PLAYSTATE_PAUSED             = 2;
     *     /**
     *      * Playback state of a RemoteControlClient which is playing media.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *     public final static int PLAYSTATE_PLAYING            = 3;
     *     /**
     *      * Playback state of a RemoteControlClient which is fast forwarding in the media
     *      *    it is currently playing.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *     public final static int PLAYSTATE_FAST_FORWARDING    = 4;
     *     /**
     *      * Playback state of a RemoteControlClient which is fast rewinding in the media
     *      *    it is currently playing.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *     public final static int PLAYSTATE_REWINDING          = 5;
     *     /**
     *      * Playback state of a RemoteControlClient which is skipping to the next
     *      *    logical chapter (such as a song in a playlist) in the media it is currently playing.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *     public final static int PLAYSTATE_SKIPPING_FORWARDS  = 6;
     *     /**
     *      * Playback state of a RemoteControlClient which is skipping back to the previous
     *      *    logical chapter (such as a song in a playlist) in the media it is currently playing.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *     public final static int PLAYSTATE_SKIPPING_BACKWARDS = 7;
     *     /**
     *      * Playback state of a RemoteControlClient which is buffering data to play before it can
     *      *    start or resume playback.
     *      *
     *      * @see #setPlaybackState(int)
     *      */
     *
     *     public final static int PLAYSTATE_BUFFERING          = 8;
     *     /**
     *      * RemoteControlClient的播放状态，由于内部错误，该客户端无法执行任何与播放相关的操作。这种情况的示例是尝试从服务器流式传输数据时没有网络连接，或者尝试播放基于订阅的内容时用户凭据过期。
     *      */
     *     public final static int PLAYSTATE_ERROR              = 9;
     *     /**
     *      * 未声明任何播放状态时播放状态的值。故意隐藏为应用程序不应该设置这样的播放状态值。
     *      */
     *     public final static int PLAYSTATE_NONE               = 0;
     *
     */
    @Deprecated("Deprecated in Java")
    override fun onClientPlaybackStateUpdate(state: Int) {
        Log.d(TAG, "onClientPlaybackStateUpdate state:$state")
    }

    @Deprecated("Deprecated in Java")
    override fun onClientPlaybackStateUpdate(
        state: Int,
        stateChangeTimeMs: Long,
        currentPosMs: Long,
        speed: Float
    ) {
        Log.d(
            TAG,
            "onClientPlaybackStateUpdate   state = $state   stateChangeTimeMs = $stateChangeTimeMs   currentPosMs = $currentPosMs   speed = $speed"
        )
        musicInfo.state = state
        logMusicInfo()
    }

    @Deprecated("Deprecated in Java")
    override fun onClientTransportControlUpdate(transportControlFlags: Int) {
        Log.d(TAG, "onClientTransportControlUpdate   transportControlFlags:$transportControlFlags")
    }

    @Deprecated("Deprecated in Java")
    override fun onClientMetadataUpdate(metadataEditor: RemoteController.MetadataEditor) {
        val title = metadataEditor.getString(7, null as String?)
        val artist = metadataEditor.getString(2, null as String?)
        val album = metadataEditor.getString(1, null as String?)
        val duration = metadataEditor.getLong(9, -1L)
        Log.d(
            TAG,
            "MetadataUpdate 歌曲信息更新: title = $title   artist = $artist    album = $album   duration = $duration"
        )
        musicInfo.title = title
        musicInfo.duration = duration
        logMusicInfo()
    }

}