package com.example.bk_foodcourt

import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.bk_foodcourt.menu.Promotion
import com.example.bk_foodcourt.order.OrderStatus
import com.google.firebase.Timestamp
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

fun formatText(value: Double): String {
    return String.format("%1$,.0f", value) + "đ"
}

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String) {
    if (url.isNotEmpty()) {
        Glide.with(view.context).load(url).into(view)
    } else {
        view.setImageResource(R.drawable.ic_food_default)
    }
}

@BindingAdapter("avatarUrl")
fun setAvatarUrl(view: CircleImageView, url: String) {
    if (url.isNotEmpty()) {
        Glide.with(view.context).load(url).into(view)
    } else {
        view.setImageResource(R.drawable.ic_food_default)
    }
}

@BindingAdapter(value = ["total_rating", "star_rating"])
fun setRatingStar(view: TextView, totalRatings: Int, starRating: Double) {
    if (totalRatings != 0) {
        val text = String.format("%.1f", starRating)
        view.text = text
    } else {
        view.text = view.resources.getString(R.string.no_rating)
    }
}

fun convertIntToTime(time: Int): String {
    val hours: Int = time / 60
    val minutes: Int = time % 60
    return String.format("%02d", hours) + ":" + String.format("%02d", minutes)
}

@BindingAdapter(value = ["open_time", "close_time"])
fun setOpeningHours(view: TextView, open_time: Int, close_time: Int) {
    val openingHours = convertIntToTime(open_time) + " - " + convertIntToTime(close_time)
    view.text = openingHours
}

@BindingAdapter(value = ["vendor_open_time", "vendor_close_time"])
fun checkOpeningHours(view: RelativeLayout, open_time: Int, close_time: Int) {
    val calendar = Calendar.getInstance()
    val time = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    view.isEnabled = time in open_time..close_time
}

@BindingAdapter("priceText")
fun setPriceFormatted(view: TextView, price: Double) {
    val text = formatText(price)
    view.text = text
}

@BindingAdapter("strike_price_text")
fun setStrikePriceFormatted(view: TextView, price: Double) {
    val text = formatText(price)
    view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    view.text = text
}

@BindingAdapter("quantityText")
fun setQuantityText(view: TextView, quantity: Int) {
    val text = "${quantity}x"
    view.text = text
}

@BindingAdapter(value = ["order_type", "store_name"])
fun setOrderDescription(view: TextView, orderType: String, storeName: String) {
    var text = "$storeName ("
    text += when (orderType) {
        "Pickup" -> view.resources.getString(R.string.pickup)
        else -> view.resources.getString(R.string.take_away)
    }
    text += ")"
    view.text = text
}

@BindingAdapter("order_status")
fun setOrderStatus(view: TextView, orderStatus: Int) {
    view.text = when (orderStatus) {
        OrderStatus.CONFIRMED.value -> view.resources.getString(R.string.confirm_status)
        OrderStatus.PROCESSING.value -> view.resources.getString(R.string.processing_status)
        OrderStatus.DONE_PROCESSING.value -> view.resources.getString(R.string.done_process_status)
        OrderStatus.FINISH.value -> view.resources.getString(R.string.finish_status)
        OrderStatus.CANCEL.value -> view.resources.getString(R.string.cancel_status)
        else -> view.resources.getString(R.string.pending_status)
    }
}

@BindingAdapter("order_time")
fun setOrderTime(view: TextView, orderTime: Timestamp) {
    view.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(orderTime.toDate())
}

@BindingAdapter("order_process")
fun setOrderProcessText(view: Button, orderStatus: Int) {
    when (orderStatus) {
        OrderStatus.CONFIRMED.value -> view.text = view.resources.getString(R.string.get_order)
        OrderStatus.PROCESSING.value -> view.text = view.resources.getString(R.string.done)
        else -> view.visibility = View.GONE
    }
}

@BindingAdapter("item_option")
fun setItemOption(view: TextView, option: String) {
    view.text = option.replace("\\n", "\n")
}

@BindingAdapter("promotion_description")
fun setPromotionDescription(view: TextView, promotion: Promotion) {
    val discountValue = when (promotion.type) {
        0 -> "${promotion.value}%"
        else -> String.format("%1$,.0f", promotion.value) + "đ"
    }

    view.text = view.resources.getString(
        R.string.order_discount_description,
        promotion.code,
        discountValue,
        String.format("%1$,.0f", promotion.orderFrom) + "đ",
        String.format("%1$,.0f", promotion.orderTo) + "đ",
        convertIntToTime(promotion.activateDayTime),
        convertIntToTime(promotion.expireDayTime)
    )

}