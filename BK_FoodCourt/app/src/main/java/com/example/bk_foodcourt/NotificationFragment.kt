package com.example.bk_foodcourt

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_notification.view.*


class NotificationFragment : Fragment() {
    var option: Spinner? = null
        //var order: Spinner? = null
    var description: EditText? = null
    override  fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_notification, container, false)

        option = view.findViewById(R.id.spinnerChannel)
        //order = view.findViewById(R.id.spinnerOrder)
        description = view.findViewById(R.id.etDescription)
        val Adapter = ArrayAdapter.createFromResource(
            this.context!!,
            R.array.channel_choice,
            R.layout.support_simple_spinner_dropdown_item
        )
        Adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        option!!.setAdapter(Adapter)

        val btnSend: Button = view.findViewById(R.id.btnNotification)

        btnSend.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Notification sent!", Toast.LENGTH_SHORT).show()
        })

        return view
    }
//    public val CHANNEL_ID: String = "User Channel"
//
//    var channel: Spinner? = null
//    //var order: Spinner? = null
//    var description: EditText? = null
//
//    // notification
//    val builder = NotificationCompat.Builder(this.context!!, CHANNEL_ID)
//        .setSmallIcon(R.drawable.baseline_notifications_black_24dp)
//        .setContentTitle("Notification")
//        .setContentText(description.toString().trim())
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        // Set the intent that will fire when the user taps the notification
//    // Spinner
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        var view: View = inflater.inflate(R.layout.fragment_notification, container, false)
//
//        channel = view.findViewById(R.id.spinnerChannel)
//        //order = view.findViewById(R.id.spinnerOrder)
//        description = view.findViewById(R.id.etDescription)
//        val btnSend: Button = view.findViewById(R.id.btnNotification)
//        setChannelContent()
//
//        btnSend.setOnClickListener(View.OnClickListener {
//            Toast.makeText(activity, "Notification sent!", Toast.LENGTH_SHORT).show()
//            createNotificationChannel()
//            with(NotificationManagerCompat.from(this.context!!)) {
//                // notificationId is a unique int for each notification that you must define
//                notify(1, builder.build())
//            }
//
//        })
//
//        return view
//    }
//
//
//    private fun setChannelContent() {
//        val Adapter = ArrayAdapter.createFromResource(
//            this.context!!,
//            R.array.channel_choice,
//            R.layout.support_simple_spinner_dropdown_item
//        )
//        Adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
//        channel!!.setAdapter(Adapter)
//    }
//
//    private fun createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "User Channel"
//            val descriptionText = description.toString().trim()
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                this.context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//

}
