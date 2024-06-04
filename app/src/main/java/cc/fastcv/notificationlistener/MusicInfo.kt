package cc.fastcv.notificationlistener

data class MusicInfo(var title: String, var duration: Long, var state: Int) {
    override fun toString(): String {
        return "MusicInfo(title='$title', duration=$duration, state=$state)"
    }
}