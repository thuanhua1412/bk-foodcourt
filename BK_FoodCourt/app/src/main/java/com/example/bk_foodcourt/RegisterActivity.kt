package com.example.bk_foodcourt

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bk_foodcourt.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    var mUsername: EditText? = null
    var mPassword: EditText? = null
    var mConfirmPW: EditText? = null
    var mFullname: EditText? = null
    var mPhonenumber: EditText? = null
    var mAddress: EditText? = null
    var mBirthday: EditText? = null
    var mGender: RadioGroup? = null
    var mMale: RadioButton? = null
    var mFemale: RadioButton? = null
    var mOther: RadioButton? = null
    var mButtonSignUp: Button? = null
    var fAuth: FirebaseAuth? = null
    var mLogin: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initialization
        mUsername = findViewById(R.id.etUsername)
        mPassword = findViewById(R.id.etPassword)
        mConfirmPW = findViewById(R.id.etConfirmPassword)
        mFullname = findViewById(R.id.etFullname)
        mPhonenumber = findViewById(R.id.etPhoneNumber)
        mAddress = findViewById(R.id.etAddress)
        mBirthday = findViewById(R.id.etBirthday)
        mGender = findViewById(R.id.rgGender)
        mMale = findViewById(R.id.rbMale)
        mFemale = findViewById(R.id.rbFemale)
        mOther = findViewById(R.id.rbOther)
        mButtonSignUp = findViewById(R.id.btnSignUp)
        mLogin = findViewById(R.id.tvLogin)
        // authentication
        fAuth = FirebaseAuth.getInstance()

        // check Login or not
        if (fAuth!!.currentUser != null) {
            startActivity(Intent(applicationContext, OrderActivity::class.java))
            finish()
        }
        // Register button click
        mButtonSignUp!!.setOnClickListener(View.OnClickListener {
            val username = mUsername!!.getText().toString().trim { it <= ' ' }
            val password = mPassword!!.getText().toString().trim { it <= ' ' }
            val confirmPassword =
                mConfirmPW!!.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(username)) {
                mUsername!!.setError("You must enter a username.")
                return@OnClickListener
            } else if (TextUtils.isEmpty(password)) {
                mPassword!!.setError("You must enter password.")
                return@OnClickListener
            } else if (password.length < 6) {
                mPassword!!.setError("Your password must be at least 6 characters.")
                return@OnClickListener
            } else if (password == confirmPassword == false) {
                mPassword!!.setError("Your password and confirm password don't match.")
                return@OnClickListener
            }


            // register the user in firebase
            fAuth!!.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Sign up successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        fAuth!!.signOut()
                        startActivity(
                            Intent(
                                applicationContext,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error! " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        })
        mLogin!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        })
    }
}