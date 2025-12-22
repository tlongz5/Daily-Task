package com.example.anew.ui.activity.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.anew.databinding.ActivityLoginBinding
import com.example.anew.model.User
import com.example.anew.support.fakeData
import com.example.anew.support.getCurrentTime
import com.example.anew.support.saveUserToSharePrefAndDataLocal
import com.example.anew.ui.activity.main.MainActivity
import com.example.anew.viewmodelFactory.MyViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class LoginActivity : AppCompatActivity() {

    private val myViewModelFactory = MyViewModelFactory()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    private val activityResultLaucher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val signInAccount = task.getResult(ApiException::class.java)
                    loginViewModel.signInWithGoogle(signInAccount.idToken!!)
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //check user login before
        checkLogin()

        loginViewModel = ViewModelProvider(this, myViewModelFactory)[LoginViewModel::class.java]

        loginViewModel.authState.observe(this) { user ->
            if (user != null) {
                initUser()
            }
            Toast.makeText(
                this,
                if (user != null) "Đăng nhập thành công" else "Đăng nhập thất bại",
                Toast.LENGTH_SHORT
            ).show()
        }

        loginViewModel.userState.observe(this){ user ->
            if(user!=null){
                saveUserToSharePrefAndDataLocal(user, this)
                callIntent()
                finish()
            }
            else Log.d("user", "login failed")
        }

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = loginViewModel.signInIntent(this)
            activityResultLaucher.launch(signInIntent)
        }
    }

    private fun initUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            val url = swapDrawableToUrl()
            val user = User(
                currentUser.uid,
                "user${System.currentTimeMillis()}",
                currentUser.displayName.toString(),
                currentUser.email!!,
                url,
                ""
            )
            loginViewModel.initUser(user)
        }
    }

    private fun callIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun checkLogin() {
        val sharePref = getSharedPreferences("user", MODE_PRIVATE)
        val userId = sharePref.getString("uid", null)
        if(userId!=null){
            val name = sharePref.getString("name", null)
            val email = sharePref.getString("email", null)
            val avatar = sharePref.getString("avatar", null)
            val phoneNumber = sharePref.getString("phoneNumber", null)
            val username = sharePref.getString("username", null)

            fakeData.user = User(userId,username!!, name!!, email!!, avatar!!, phoneNumber!!)

            callIntent()
            finish()
        }
    }

    private fun swapDrawableToUrl(): String {
        //convert to bitmap
        val drawable = ContextCompat.getDrawable(this, fakeData.avatar.random())
        var bitmap = (drawable as BitmapDrawable).bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false)

        //bitmap convert to Uri
        val file = File(this.cacheDir,"temp_image.png")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        return file.absolutePath

    }
}