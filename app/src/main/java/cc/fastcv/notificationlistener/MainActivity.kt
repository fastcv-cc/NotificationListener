package cc.fastcv.notificationlistener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), MusicInfoManager.OnMusicInfoChangeListener {

    private lateinit var btOpen: AppCompatButton

    private lateinit var llContent: LinearLayout

    private lateinit var cardMusic: CardView
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var ivRewind: ImageView
    private lateinit var ivState: ImageView
    private lateinit var ivForward: ImageView


    private lateinit var ivVolumeDown: ImageView
    private lateinit var seekBarVolume: SeekBar
    private lateinit var ivVolumeUp: ImageView

    private var musicInfo: MusicInfo? = null

    private lateinit var mAudioManager: AudioManager

    private var mCurrentVolume: Int = 0
    private var mMaxValue = 0
    private var mMinValue = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mCurrentVolume = mAudioManager.getStreamVolume(3)
        mMaxValue = mAudioManager.getStreamMaxVolume(3)
        if (VERSION.SDK_INT >= 28) {
            mMinValue = mAudioManager.getStreamMinVolume(3)
        }

        val filter = IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        val vr = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(TAG, "VolumeReceiver onReceive")
                seekBarVolume.progress = getCurrentVolume()
            }
        }
        registerReceiver(vr, filter)



        Log.d(
            TAG,
            "最大音量:" + this.mMaxValue + " 最小音量:" + this.mMinValue + "当前音量:" + this.mCurrentVolume
        )
        btOpen = findViewById(R.id.btOpen)

        llContent = findViewById(R.id.llContent)

        cardMusic = findViewById(R.id.cardMusic)
        tvTitle = findViewById(R.id.tvTitle)
        ivRewind = findViewById(R.id.ivRewind)
        ivState = findViewById(R.id.ivState)
        ivForward = findViewById(R.id.ivForward)

        ivVolumeDown = findViewById(R.id.ivVolumeDown)
        seekBarVolume = findViewById(R.id.seekBarVolume)
        ivVolumeUp = findViewById(R.id.ivVolumeUp)

        seekBarVolume.max = mMaxValue
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarVolume.min = mMinValue
        }
        seekBarVolume.progress = mCurrentVolume

        ivVolumeDown.setOnClickListener {
            turnDownVolume()
        }

        ivVolumeUp.setOnClickListener {
            turnUpVolume()
        }


        btOpen.setOnClickListener {
            openNotificationAccess(this)
        }

        ivRewind.setOnClickListener {
            previousMusic()
        }

        ivState.setOnClickListener {
            if (musicInfo != null) {
                if (musicInfo!!.state == 2) {
                    playMusic()
                } else {
                    stopMusic()
                }
            }
        }

        ivForward.setOnClickListener {
            nextMusic()
        }

        MusicInfoManager.addMusicInfoChangeListener(this)
        musicInfo = MusicInfoManager.getCurrentMusicInfo()
        if (musicInfo != null) {
            onMusicInfoChange(musicInfo!!)
        }
    }


    fun getCurrentVolume(): Int {
        return mAudioManager.getStreamVolume(3)
    }

    private fun turnUpVolume() {
        this.mCurrentVolume = this.mAudioManager.getStreamVolume(3)
        if (this.mCurrentVolume < this.mMaxValue) {
            ++this.mCurrentVolume
        }
        Log.d(TAG, "调大音量到:" + this.mCurrentVolume)
        mAudioManager.setStreamVolume(3, this.mCurrentVolume, AudioManager.FLAG_SHOW_UI)
    }

    private fun turnDownVolume() {
        this.mCurrentVolume = this.mAudioManager.getStreamVolume(3)
        if (this.mCurrentVolume > this.mMinValue) {
            --this.mCurrentVolume
        }
        Log.d(TAG, "调小音量到:" + this.mCurrentVolume)
        this.mAudioManager.setStreamVolume(3, this.mCurrentVolume, AudioManager.FLAG_SHOW_UI)
    }

    fun setVolume(watchValue: Int) {
        Log.d(TAG, "收到音量:" + watchValue + " 音量最大值:" + this.mMaxValue)
        this.mCurrentVolume = watchValue * this.mMaxValue / 100
        Log.d(TAG, "设置音量:" + this.mCurrentVolume + " 音量最大值:" + this.mMaxValue)
        this.mAudioManager.setStreamVolume(3, this.mCurrentVolume, AudioManager.FLAG_SHOW_UI)
    }

    private fun playMusic() {
        Log.d(TAG, "playMusic")
        this.doMusicControlKeyEvent(126)
    }

    private fun stopMusic() {
        Log.d(TAG, "stopMusic")
        this.doMusicControlKeyEvent(127)
    }

    private fun nextMusic() {
        Log.d(TAG, "nextMusic")
        this.doMusicControlKeyEvent(87)
    }

    private fun previousMusic() {
        Log.d(TAG, "previousMusic")
        this.doMusicControlKeyEvent(88)
    }

    private fun doMusicControlKeyEvent(keyCode: Int): Boolean {
        Log.d(TAG, "AudioManager control")
        val eventTime = SystemClock.uptimeMillis()
        val key = KeyEvent(eventTime, eventTime, 0, keyCode, 0)
        this.dispatchMediaKeyToAudioService(key)
        this.dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, 1))
        return false

//        LogUtil.d("RemoteControllerManager control")
//        this.mKeyEvent = KeyEvent(0, keyCode)
//        val down: Boolean = RemoteControllerManager.getInstance().getRemoteController()
//            .sendMediaKeyEvent(this.mKeyEvent)
//        this.mKeyEvent = KeyEvent(1, keyCode)
//        val up: Boolean = RemoteControllerManager.getInstance().getRemoteController()
//            .sendMediaKeyEvent(this.mKeyEvent)
//        down && up
    }

    private fun dispatchMediaKeyToAudioService(event: KeyEvent) {
        try {
            mAudioManager.dispatchMediaKeyEvent(event)
        } catch (var3: Exception) {
            var3.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        MusicInfoManager.removeMusicInfoChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (isEnabled(this)) {
            btOpen.visibility = View.GONE
            llContent.visibility = View.VISIBLE
        } else {
            btOpen.visibility = View.VISIBLE
            llContent.visibility = View.GONE
        }
    }

    override fun onMusicInfoChange(info: MusicInfo) {
        if (info.state == 2 || info.state == 3) {
            musicInfo = info
            cardMusic.visibility = View.VISIBLE
            tvTitle.text = info.title
            if (info.state == 3) {
                ivState.setImageResource(R.drawable.ic_play)
            } else {
                ivState.setImageResource(R.drawable.ic_pause)
            }
        } else {
            musicInfo = null
            cardMusic.visibility = View.GONE
        }
    }



}