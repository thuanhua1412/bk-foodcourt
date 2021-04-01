package com.example.bk_foodcourt.notificationService

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MessageQueue private constructor(private val context: Context) {
    private var requestQueue: RequestQueue?

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext())
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        getRequestQueue()!!.add(req)
    }

    companion object {
        private var instance: MessageQueue? = null

        @Synchronized
        fun getInstance(context: Context): MessageQueue? {
            if (instance == null) {
                instance = MessageQueue(context)
            }
            return instance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}