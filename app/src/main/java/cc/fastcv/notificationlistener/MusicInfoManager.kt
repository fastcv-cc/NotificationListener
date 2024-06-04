package cc.fastcv.notificationlistener

object MusicInfoManager {

    private var currentMusicInfo:MusicInfo? = null

    private var listeners = mutableListOf<OnMusicInfoChangeListener>()

    fun addMusicInfoChangeListener(listener: OnMusicInfoChangeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeMusicInfoChangeListener(listener: OnMusicInfoChangeListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
        }
    }

    interface OnMusicInfoChangeListener {
        fun onMusicInfoChange(info: MusicInfo)
    }

    fun getCurrentMusicInfo() = currentMusicInfo

    fun notifyMusicInfo(info: MusicInfo) {
        currentMusicInfo = info
        for (listener in listeners) {
            listener.onMusicInfoChange(info)
        }
    }

}