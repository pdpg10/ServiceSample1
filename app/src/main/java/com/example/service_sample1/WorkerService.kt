package com.example.service_sample1

import android.app.IntentService
import android.content.Intent
import android.util.Log


class WorkerService : IntentService("WorkerService") {

    override fun onHandleIntent(p0: Intent?) {
        for (c in 0..10) {
            Thread.sleep(1000)
            Log.d("WorkerService","onHandleIntent sleep:$c")
        }
    }

}