package com.example.service_sample1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.concurrent.Executors

const val TAG = "MyService"
//const val KEY_TIME = "KEY_TIME"

class MyService : Service() {
    private val executor = Executors.newFixedThreadPool(1)
    private var list: MutableList<Pair<Int, Boolean>>? = null
    private val binder = MyBinder()

    override fun onBind(p0: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return true
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind")
        super.onRebind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        service =this
        Log.d(TAG, "onCreate")
        list = mutableListOf()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand startId:$startId")
        val time = intent?.getIntExtra(KEY_TIME, 0) ?: 0
//        val pi = intent?.getParcelableExtra<PendingIntent>("pi")
        executor.execute(Task(startId, time))
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        service =null
        Log.d(TAG, "onDestroy")
        list = null
    }

    inner class Task(
        private val startId: Int,
        private val time: Int
    ) : Runnable {
        override fun run() {
            Log.d(TAG, "run started $startId")
            try {
                Thread.sleep(time * 1000L)
                list?.add(Pair(startId, true))
                Log.d(TAG, "run $startId finished successfully")
                val intent = Intent("com.example.service_sample1.MESSAGE_ACTION")
                    .apply { putExtra("result", startId) }
                sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Oops $startId finished with fail", e)
            }
            stop()
        }

        private fun stop() {
//            Log.d(TAG, "stoped $startId ${stopSelfResult(startId)}")
        }

    }

    inner class MyBinder : Binder() {
        fun getService() = this@MyService
    }

    companion object {
        var service: MyService? = null
    }

}