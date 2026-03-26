package com.meooo27.wrapperexoplayer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private var simplePlayerInstance: Any? = null
    private var setListenerMethod: java.lang.reflect.Method? = null
    private var initMethod: java.lang.reflect.Method? = null
    private var playMethod: java.lang.reflect.Method? = null
    private var pauseMethod: java.lang.reflect.Method? = null
    private var stopMethod: java.lang.reflect.Method? = null
    private var releaseMethod: java.lang.reflect.Method? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surfaceView = SurfaceView(this)
        setContentView(surfaceView)

        try {
            // 1️⃣ Copy jar từ assets ra filesDir
            val jarFile = File(filesDir, "simpleplayer-fat.jar")

            // On Android 14+, we must ensure the file is NOT writable before loading.
            // If it exists, we delete it to ensure we can write the fresh version.
            if (jarFile.exists()) {
                jarFile.delete()
            }

            assets.open("simpleplayer-fat.jar").use { input ->
                FileOutputStream(jarFile).use { output ->
                    input.copyTo(output)
                }
            }

            // 🔥 CRITICAL FIX: Set the file to read-only.
            // Android 14 (API 34) throws SecurityException if a loaded dex file is writable.
            jarFile.setReadOnly()

            // 2️⃣ Load jar bằng DexClassLoader
            val dexLoader = DexClassLoader(
                jarFile.absolutePath,
                codeCacheDir.absolutePath,
                null,
                classLoader
            )

            // 3️⃣ Load class SimplePlayer
            val clazz = dexLoader.loadClass("com.meooo27.wrapperexoplayer.SimplePlayer")
            simplePlayerInstance = clazz.getConstructor().newInstance()

            // 4️⃣ Get method references
            // Note: Using clazz.classLoader to find the internal Listener interface
            val listenerClass = Class.forName("com.meooo27.wrapperexoplayer.SimplePlayer\$Listener", true, dexLoader)

            setListenerMethod = clazz.getMethod("setListener", listenerClass)
            initMethod = clazz.getMethod("init", Context::class.java, android.view.Surface::class.java)
            playMethod = clazz.getMethod("play", String::class.java)
            pauseMethod = clazz.getMethod("pause")
            stopMethod = clazz.getMethod("stop")
            releaseMethod = clazz.getMethod("release")

            // 5️⃣ Init player
            initMethod?.invoke(simplePlayerInstance, this, surfaceView.holder.surface)

            // 6️⃣ Set listener using Proxy
            val listener = java.lang.reflect.Proxy.newProxyInstance(
                dexLoader,
                arrayOf(listenerClass)
            ) { _, method, _ ->
                if (method.name == "onCompleted") {
                    Log.d("TestJar", "Playback completed!")
                }
                null
            }
            setListenerMethod?.invoke(simplePlayerInstance, listener)

            // 7️⃣ Play URL
            playMethod?.invoke(simplePlayerInstance, "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")

        } catch (e: Exception) {
            Log.e("TestJar", "Error loading jar", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            releaseMethod?.invoke(simplePlayerInstance)
        } catch (e: Exception) {
            // ignore
        }
    }
}