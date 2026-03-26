package com.meooo27.wrapperexoplayer

import android.content.Context
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class SimplePlayer {
    private var player: ExoPlayer? = null
    private var listener: Listener? = null

    interface Listener {
        fun onCompleted()
    }

    fun init(context: Context, surface: Surface) {
        player = ExoPlayer.Builder(context).build().apply {
            setVideoSurface(surface)
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        listener?.onCompleted()
                    }
                }
            })
        }
    }

    fun play(url: String) {
        val item = MediaItem.fromUri(url)
        player?.let {
            it.setMediaItem(item)
            it.prepare()
            it.play()
        }
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
    }

    fun release() {
        player?.release()
        player = null
    }

    fun setListener(l: Listener?) {
        this.listener = l
    }
}