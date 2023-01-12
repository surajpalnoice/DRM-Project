package com.technokratz.drmsample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.technokratz.drmsample.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var userAgent: String? = null
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var httpDataSourceFactory : DataSource.Factory
    private lateinit var bandwidthMeter : DefaultBandwidthMeter
    private lateinit var binding: ActivityMainBinding

    private var playerEventListener: Player.Listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            val errorString: String? = when (error.errorCode) {
                ExoPlaybackException.TYPE_RENDERER -> {
                    error.localizedMessage
                }
                ExoPlaybackException.TYPE_SOURCE -> {
                    error.localizedMessage
                }
                ExoPlaybackException.TYPE_UNEXPECTED -> {
                    error.localizedMessage
                }
                else -> {
                    error.localizedMessage
                }
            }
            Toast.makeText(this@MainActivity, errorString, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        bandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
        httpDataSourceFactory = buildHttpDataSourceFactory()
        trackSelector = DefaultTrackSelector(this, AdaptiveTrackSelection.Factory())
    }

    private fun initializePlayer() {

        // player setting
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setBandwidthMeter(bandwidthMeter)
            .build()

        binding.playerView.player = player

        player?.apply {
            setMediaSource(getMediaSource())
            addListener(playerEventListener)
            addAnalyticsListener(EventLogger(trackSelector as MappingTrackSelector, "ExoPlayerEventLogger"))
            prepare()
            play()
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun getMediaSource(): MediaSource {
        val requestHeader = HashMap<String, String>()
        requestHeader["pallycon-customdata-v2"] = "eyJrZXlfcm90YXRpb24iOmZhbHNlLCJyZXNwb25zZV9mb3JtYXQiOiJvcmlnaW5hbCIsInVzZXJfaWQiOiJ0ZXN0LW1wMyIsImRybV90eXBlIjoid2lkZXZpbmUiLCJzaXRlX2lkIjoiOE5RSiIsImhhc2giOiJFRkJCV29tcHZiLzJqSTJJUjd4cU5zQkFBM2dBZndUVWVYQmFZQ0dwQnM0PSIsImNpZCI6ImZpbGVfZXhhbXBsZV9NUDNfNzAwS0IiLCJwb2xpY3kiOiIyUjd1czdCQzZIYW14eEw0QnVyUXFqckpkS3V6dHk3YTg0cHRmajZqbkhJPSIsInRpbWVzdGFtcCI6IjIwMjMtMDEtMTJUMTQ6MjA6NDhaIn0="

        val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
            .setLicenseUri("https://license-global.pallycon.com/ri/licenseManager.do")
            .setMultiSession(true)
            .setLicenseRequestHeaders(requestHeader)
            .build()

        //val songUrl = "https://storage.googleapis.com/noice-prod-content/transcoded/content-audio-1662243457449.m3u8"
        val songUrl = "https://storage.googleapis.com/drm-pallycon/drm/cmaf/master.m3u8"
        val mediaItem = MediaItem.Builder()
            .setMediaId(UUID.randomUUID().toString())
            .setUri(songUrl)
            .setDrmConfiguration(drmConfiguration)
            .build()

        val dataSource = DefaultDataSource.Factory(this, httpDataSourceFactory)
            .setTransferListener(bandwidthMeter)

        return DefaultMediaSourceFactory(dataSource)
            .createMediaSource(mediaItem)
    }

    private fun buildHttpDataSourceFactory(): HttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
            .setTransferListener(bandwidthMeter)
            .setUserAgent(userAgent)
    }
}