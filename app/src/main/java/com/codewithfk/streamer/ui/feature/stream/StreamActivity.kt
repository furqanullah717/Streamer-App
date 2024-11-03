package com.codewithfk.streamer.ui.feature.stream

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codewithfk.streamer.R
import com.codewithfk.streamer.data.StreamHandler
import com.codewithfk.streamer.data.model.StreamData
import com.codewithfk.streamer.utils.APP_ID
import com.codewithfk.streamer.utils.APP_SIGN
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment
import com.zegocloud.uikit.prebuilt.livestreaming.internal.components.ZegoLeaveLiveStreamingListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StreamActivity : AppCompatActivity() {
    @Inject
    lateinit var handler: StreamHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stream)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addFragment()
    }


    private fun addFragment() {
        val appID: Long = APP_ID
        val appSign: String = APP_SIGN
        val currentUser = Firebase.auth.currentUser!!
        val userID: String = currentUser.email!!
        val userName: String = currentUser.email!!

        val isHost = intent.getBooleanExtra("host", false)
        val liveID = intent.getStringExtra("liveID")
        val config = if (isHost) {
            ZegoUIKitPrebuiltLiveStreamingConfig.host()
        } else {
            ZegoUIKitPrebuiltLiveStreamingConfig.audience()
        }
        config.leaveLiveStreamingListener = ZegoLeaveLiveStreamingListener {
            if (isHost) {
                liveID?.let {
                    handler.deleteStream(it)
                }
                finish()
            } else {
                finish()
            }
        }
        val fragment: ZegoUIKitPrebuiltLiveStreamingFragment =
            ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(
                appID, appSign, userID, userName, liveID, config
            )
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commitNow()
    }

    companion object {
        fun newIntent(context: Context, data: StreamData, isHost: Boolean): Intent {
            val intent = Intent(context, StreamActivity::class.java)
            intent.putExtra("host", isHost)
            intent.putExtra("liveID", data.liveID)
            return intent
        }
    }
}