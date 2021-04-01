package com.example.bk_foodcourt.menu.cart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.home.Store
import com.example.bk_foodcourt.login.User
import com.example.bk_foodcourt.menu.CartItem
import com.example.bk_foodcourt.menu.Dish
import com.example.bk_foodcourt.menu.MenuViewModel
import com.example.bk_foodcourt.menu.Promotion
import com.example.bk_foodcourt.notificationService.Token
import com.example.bk_foodcourt.order.Order
import com.example.bk_foodcourt.order.OrderItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat
import java.util.*

class CartViewModel : ViewModel() {
    private var firestore = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!
    private var userInfo = User()
    private var storeInfo = Store()
    private var storeId = ""

    var subtotal = MutableLiveData(0.0)
    var total = MutableLiveData(0.0)
    var applicableFee = MutableLiveData(0.0)
    var promo = MutableLiveData(0.0)

    val cartList = MutableLiveData<List<CartItem>>()
    private val promotionList = mutableListOf<Promotion>()
    val codeList = MutableLiveData<List<String>>()
    private var applyPosition = -1

    val showCartItemDetailEvent = MutableLiveData<Dish>()
    val goToHomeEvent = MutableLiveData<Boolean>()
    val clearTextEvent = MutableLiveData<Boolean>()
    val sendNotificationEvent = MutableLiveData<String>()

    var errorMessage = MutableLiveData<Int>()
    var navigateToMenuFragmentEvent = MutableLiveData<Boolean>()
    private val calendar = Calendar.getInstance()

    init {
        getCart()
        getUserInfo()
    }

    private fun getCart() {
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.d(MenuViewModel.TAG, exception.toString())
                } else if (querySnapshot != null) {
                    val list = mutableListOf<CartItem>()
                    var subTotal = 0.0
                    for (document in querySnapshot) {
                        val item = document.toObject(CartItem::class.java)
                        item.id = document.id
                        subTotal += item.total
                        list.add(item)
                    }
                    if (list.isNotEmpty()) {
                        storeId = list[0].storeId
                        getPromotionCodes()
                        Log.d("CartViewModel", list[0].storeId)
                    } else {
                        storeId = ""
                        navigateToMenuFragmentEvent.value = true
                    }
                    subtotal.value = subTotal
                    applicableFee.value = subTotal / 10
                    applyPromotion(applyPosition)
                    cartList.value = list
                    Log.d("CartViewModel", subTotal.toString())
                }
            }
    }

    private fun getUserInfo() {
        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                userInfo = document.toObject(User::class.java)!!
                userInfo.id = document.id
            }
    }

    fun getStoreInfo(store: Store) {
        storeInfo = store
    }

    private fun getPromotionCodes() {
        if (storeId.isNotEmpty()) {
            val list = mutableListOf<String>()
            val todayTimestamp = Timestamp.now()
            firestore.collection("stores")
                .document(storeId)
                .collection("promotions")
                .whereEqualTo("status", true)
                .whereEqualTo("scope", 0)
                .whereLessThanOrEqualTo("activateDay", todayTimestamp)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val promotion = document.toObject(Promotion::class.java)
                        promotion.id = document.id
                        list.add(promotion.code)
                        promotionList.add(promotion)
                    }
                    codeList.value = list
                }
        }
    }

    fun showCartItemDetail(item: CartItem) {
        Log.d("CartViewModel", item.options)
        val dish = Dish(
            name = item.name,
            price = item.baseTotal / item.quantity,
            discountPrice = item.total / item.quantity,
            description = item.description,
            options = item.options,
            quantity = item.quantity,
            imageUrl = item.imageUrl,
            cartItemId = item.id,
            storeId = item.storeId
        )
        showCartItemDetailEvent.value = dish
    }

    fun applyPromotion(position: Int) {
        if (position == -1) {
            total.value = subtotal.value!! + applicableFee.value!!
        } else {
            val promotion = promotionList[position]
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val time = hour * 60 + minute
            var valid = false

            if (promotion.activateDayTime > time) {
                errorMessage.value = R.string.promo_active_time_error
            } else if (time > promotion.expireDayTime) {
                errorMessage.value = R.string.promo_expire_time_error
            } else if (promotion.orderFrom > subtotal.value!!) {
                errorMessage.value = R.string.promo_min_error
            } else if (subtotal.value!! > promotion.orderTo) {
                errorMessage.value = R.string.promo_max_error
            } else {
                valid = true
            }

            if (valid) {
                if (applyPosition != position) {
                    applyPosition = position
                    errorMessage.value = R.string.apply_promo_success
                }
                if (promotion.type == 1) {
                    promo.value = promotion.value
                } else {
                    promo.value = promotion.value * subtotal.value!! / 100
                }
                total.value = subtotal.value!! + applicableFee.value!! - promo.value!!
            } else {
                clearTextEvent.value = true
                applyPosition = -1
                promo.value = 0.0
                total.value = subtotal.value!! + applicableFee.value!!
            }
        }
    }

    fun checkout(orderType: String, ownerID: String) {
        var promotionCode = ""
        cartList.value?.let { list ->
            if (applyPosition != -1) {
                promotionCode = codeList.value!![applyPosition]
            }
            if (storeId.isNotEmpty()) {
                firestore.collection("vendor_tokens")
                    .document(ownerID)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val token = document.toObject(Token::class.java)!!
                            if (token.token.isNotEmpty()) {
                                sendNotificationEvent.value = token.token
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.d("CartViewModel", it.toString())
                    }
                val order = Order(
                    userID = userInfo.id,
                    userName = userInfo.name,
                    userPhoneNumber = userInfo.phoneNumber,
                    storeID = storeInfo.id,
                    storeName = storeInfo.name,
                    storeHotline = storeInfo.hotline,
                    promotion = promo.value!!,
                    promotionCode = promotionCode,
                    total = total.value!!,
                    applicableFee = applicableFee.value!!,
                    type = orderType,
                    status = 0,
                    time = Timestamp.now()
                )
                firestore.collection("order")
                    .add(order)
                    .addOnSuccessListener { ref ->
                        for (item in list) {
                            val orderItem = OrderItem(
                                orderId = ref.id,
                                name = item.name,
                                quantity = item.quantity,
                                total = item.total,
                                options = item.options
                            )
                            firestore.collection("order")
                                .document(ref.id)
                                .collection("orderItems")
                                .add(orderItem)
                                .addOnSuccessListener {
                                    firestore.collection("users")
                                        .document(currentUser.uid)
                                        .collection("cart")
                                        .document(item.id)
                                        .delete()
                                }
                        }
                        if (promotionCode.isNotEmpty()) {
                            val promotion = promotionList[applyPosition]
                            if (promotion.numUsed == promotion.numAllowed - 1) {
                                firestore.collection("stores")
                                    .document(storeId)
                                    .collection("promotions")
                                    .document(promotion.id)
                                    .delete()
                                goToHomeEvent.value = true
                            } else {
                                firestore.collection("stores")
                                    .document(storeId)
                                    .collection("promotions")
                                    .document(promotion.id)
                                    .update("numUsed", FieldValue.increment(1))
                                    .addOnSuccessListener {
                                        goToHomeEvent.value = true
                                    }
                            }
                        } else {
                            goToHomeEvent.value = true
                        }
                    }
                    .addOnFailureListener {
                        Log.d("CartViewModel", it.toString())
                    }
            } else {
                errorMessage.value = R.string.empty_cart
            }
        }
    }
}