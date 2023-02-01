package com.technokratz.drmsample

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.technokratz.drmsample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), Player.Listener, BandwidthMeter.EventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        bandwidthMeter.addEventListener(Handler(), this)

        val videoTrackSelectionFactory: AdaptiveTrackSelection.Factory =
            AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(this, videoTrackSelectionFactory)

        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector)
            .setBandwidthMeter(bandwidthMeter).build()
    }

    private fun initializePlayer() {
        player.addListener(this)
        binding.playerView.player = player
        binding.playerView.showController()
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        player.playWhenReady = true

        val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
            //ExPressPlay license
            //.setLicenseUri("https://wv.service.expressplay.com/hms/wv/rights/?ExpressPlayToken=BgA0u_NDKZwAJGVhODNlMmNmLTNiYzYtNGYxYS1iN2YyLTEyZDYzMDAyYzJiMwAAAHDAaLAeEoF5e2OygR27rDH-ofQ6F9xSNeroMBt1wE2dc2WZiWgun2i5iF5DCoq74qrrIx_o06Ce-1l3FKT8FyxjeQH_GAmMRRRx3YHuzZBXPGKotpnwaaS6rcyx8Kn3E8310ZACnJobxmTHTFITIhRb3Lhp_SfJQdSwCyQjKLORRGlCcPM")
            //EZDRM license
            .setLicenseUri("https://widevine-dash.ezdrm.com/proxy?pX=531CFA")
            .setMultiSession(true)
            .build()


        val item = MediaItem.Builder()
            //.setUri("https://storage.googleapis.com/drm-pallycon/shubham-fmp4/without-drm/sample_tj.m3u8")
            .setUri("https://cdn.noiceid.cc/transcoded/content-audio-1674773324802.m3u8")
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            //.setDrmConfiguration(drmConfiguration)
            .build()

        player.setMediaItem(item)
        player.prepare()
    }

    private fun releasePlayer() {
        player.stop()
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

    var bytes = 0L

    override fun onBandwidthSample(elapsedMs: Int, bytesTransferred: Long, bitrateEstimate: Long) {
        val bitrateInKB = bitrateEstimate * 0.000125
        val bytesInKB = bytesTransferred * 0.001
        val avgBitrate = (bytesTransferred * 8) / (elapsedMs / 1000)
        Log.d("BitrateSwitching", "elapsedMs : $elapsedMs, bytesTransferred : $bytesInKB, bitrateEstimate : $bitrateInKB")
        Log.d( "AverageBitrate", "Average bitrate (bps)= $avgBitrate");
        bytes = bytesTransferred
    }
}