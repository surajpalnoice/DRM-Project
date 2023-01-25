package com.technokratz.drmsample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.technokratz.drmsample.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), Player.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        player = SimpleExoPlayer.Builder(this).build()
    }

    private fun initializePlayer() {
        player.addListener(this)
        binding.playerView.player = player
        binding.playerView?.showController()
        binding.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        player.playWhenReady = true

        val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
            .setLicenseUri("https://wv.service.expressplay.com/hms/wv/rights/?ExpressPlayToken=BgA0u_NDKeUAJGVhODNlMmNmLTNiYzYtNGYxYS1iN2YyLTEyZDYzMDAyYzJiMwAAAHDEVBq5A0Ynrb647-q2c4HsqFU1nC6OPRCx_tq-alhEmhs3RjR6Hd28Yc-qAjOEdfJ_Ty0U4aY_PGJltOjZ_dOK1upvxGy-xUCAfhBbgxhKpxMnplxM08FPCjfFs-zXe9wFRwq4VFnSAi5XOkZ3LBY3ekpTyxOC86zTbSc5Rl8EkRgPoqE")
            .setMultiSession(true)
            .build()


        val item = MediaItem.Builder()
//            .setUri("https://cdn.noiceid.cc/transcoded/content-audio-1674540799671.m3u8")
            .setUri("https://storage.googleapis.com/drm-pallycon/shubham-fmp4/expressplay/sample_tj.m3u8")
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setDrmConfiguration(drmConfiguration)
            .build()

        player.setMediaItem(item)
        player.prepare()
    }

    private fun releasePlayer() {
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        print("onPlayerError ${error.message}")
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23) {
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


//    private fun getfMP4MediaSource(): MediaSource {
////        val requestHeader = HashMap<String, String>()
////        requestHeader["pallycon-customdata-v2"] = "eyJrZXlfcm90YXRpb24iOmZhbHNlLCJyZXNwb25zZV9mb3JtYXQiOiJvcmlnaW5hbCIsInVzZXJfaWQiOiJ0ZXN0LW1wMyIsImRybV90eXBlIjoid2lkZXZpbmUiLCJzaXRlX2lkIjoiOE5RSiIsImhhc2giOiJZaUMyK1NTa1pCSS9HQXIyWC9UQmRvSXoweHVDU2c1NUcwWDZleUhYRXNRPSIsImNpZCI6InNhbXBsZV8xMjgweDcyMF9zdXJmaW5nX3dpdGhfYXVkaW8tNC5tcDQiLCJwb2xpY3kiOiIyUjd1czdCQzZIYW14eEw0QnVyUXFqckpkS3V6dHk3YTg0cHRmajZqbkhJPSIsInRpbWVzdGFtcCI6IjIwMjMtMDEtMTlUMjE6Mzg6MTZaIn0="
//
////        val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
////            .setLicenseUri("https://license-global.pallycon.com/ri/licenseManager.do")
////            .setMultiSession(true)
////            .setLicenseRequestHeaders(requestHeader)
////            .build()
//
//        //val songUrl = "https://storage.googleapis.com/noice-prod-content/transcoded/content-audio-1662243457449.m3u8"
//        val songUrl = "https://storage.googleapis.com/drm-pallycon/shubham-fmp4/without-drm/sample_tj.m3u8"
//        val mediaItem = MediaItem.Builder()
//            .setMediaId(UUID.randomUUID().toString())
//            .setUri(songUrl)
////            .setDrmConfiguration(drmConfiguration)
//            .build()
//
//        val dataSource = DefaultDataSource.Factory(this, httpDataSourceFactory)
//            .setTransferListener(bandwidthMeter)
//
//        return DefaultMediaSourceFactory(dataSource)
//            .createMediaSource(mediaItem)
//    }
//
//    private fun getPallyConDRMMediaSource(): MediaSource {
//        val requestHeader = HashMap<String, String>()
//        requestHeader["pallycon-customdata-v2"] = "eyJrZXlfcm90YXRpb24iOmZhbHNlLCJyZXNwb25zZV9mb3JtYXQiOiJvcmlnaW5hbCIsInVzZXJfaWQiOiJ0ZXN0LW1wMyIsImRybV90eXBlIjoid2lkZXZpbmUiLCJzaXRlX2lkIjoiOE5RSiIsImhhc2giOiJZaUMyK1NTa1pCSS9HQXIyWC9UQmRvSXoweHVDU2c1NUcwWDZleUhYRXNRPSIsImNpZCI6InNhbXBsZV8xMjgweDcyMF9zdXJmaW5nX3dpdGhfYXVkaW8tNC5tcDQiLCJwb2xpY3kiOiIyUjd1czdCQzZIYW14eEw0QnVyUXFqckpkS3V6dHk3YTg0cHRmajZqbkhJPSIsInRpbWVzdGFtcCI6IjIwMjMtMDEtMTlUMjE6Mzg6MTZaIn0="
//
//        val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
//            .setLicenseUri("https://license-global.pallycon.com/ri/licenseManager.do")
//            .setMultiSession(true)
//            .setLicenseRequestHeaders(requestHeader)
//            .build()
//
//        //val songUrl = "https://storage.googleapis.com/noice-prod-content/transcoded/content-audio-1662243457449.m3u8"
//        val songUrl = "https://s3.eu-central-1.amazonaws.com/cdn.videos.raply.io/drm/fmp4-4/cmaf/master.m3u8"
//        val mediaItem = MediaItem.Builder()
//            .setMediaId(UUID.randomUUID().toString())
//            .setUri(songUrl)
//            .setDrmConfiguration(drmConfiguration)
//            .build()
//
//        val dataSource = DefaultDataSource.Factory(this, httpDataSourceFactory)
//            .setTransferListener(bandwidthMeter)
//
//        return DefaultMediaSourceFactory(dataSource)
//            .createMediaSource(mediaItem)
//    }
//
//    private fun buildHttpDataSourceFactory(): HttpDataSource.Factory {
//        return DefaultHttpDataSource.Factory()
//            .setTransferListener(bandwidthMeter)
//            .setUserAgent(userAgent)
//    }
}