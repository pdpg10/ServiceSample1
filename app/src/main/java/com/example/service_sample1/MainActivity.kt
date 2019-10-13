package com.example.service_sample1

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val KEY_TIME = "KEY_TIME"
const val MESSAGE_ACTION = "com.example.service_sample1.MESSAGE_ACTION"

class MainActivity : AppCompatActivity() {
    private var receiver: MyReceiver? = null
    private var isServiceConnected = false
    private var service: MyService? = null
    private val serviceConnection by lazy(LazyThreadSafetyMode.NONE) {
        createConnection()
    }

    private fun createConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {
                isServiceConnected = false
            }

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                isServiceConnected = true
                if (binder != null && binder is MyService.MyBinder) {
                    service = binder.getService()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener {
//            progressBar.visibility = View.VISIBLE
//            startServiceWithTime(3)
//            startServiceWithTime(3)
//            startServiceWithTime(2)
//        }

        bt_start_service.setOnClickListener {
            Intent(this, WorkerService::class.java)
                .apply {
//                    setPackage("com.example.serviceapp")
                    startService(this)
                }
        }

        bt_stop_service.setOnClickListener {
            Intent("com.example.serviceapp.MyService")
                .apply {
                    setPackage("com.example.serviceapp")
                    stopService(this)
                }
        }

        bt_bind_service.setOnClickListener {
            Intent("com.example.serviceapp.MyService")
                .apply {
                    setPackage("com.example.serviceapp")
                    bindService(this, serviceConnection, Context.BIND_AUTO_CREATE)
                }
        }

        bt_unbind_service.setOnClickListener {
            unbindService(serviceConnection)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(MESSAGE_ACTION)
        receiver = MyReceiver()
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        if (receiver != null)
            unregisterReceiver(receiver)
    }

    private fun startServiceWithTime(time: Int) {
//        val pi = createPendingResult(1001, Intent(), 0)
        Intent("com.example.serviceapp.MyService")
            .apply {
                setPackage("com.example.serviceapp")
                putExtra(KEY_TIME, time)
                startService(this)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            progressBar.apply {
                progress += 1
            }
        }
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent == null) return
            val result = intent.getIntExtra("result", -1)
            if (result != -1) {
                progressBar.progress = result
            }
        }

    }
}
