package com.example.anew.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.anew.R
import com.example.anew.databinding.ActivityLoginBinding
import com.example.anew.model.User
import com.example.anew.data.local.saveUserToSharePrefAndDataLocal
import com.example.anew.ui.activity.main.MainActivity
import com.example.anew.viewmodelfactory.MyViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.lifecycleScope
import com.example.anew.data.local.MyHelper
import com.example.anew.utils.swapBitmapToUrl
import kotlinx.coroutines.launch

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
        if (currentUser != null) {
            lifecycleScope.launch {
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

            MyHelper.user = User(userId,username!!, name!!, email!!, avatar!!, phoneNumber!!)

            callIntent()
            finish()
        }
    }

    suspend fun swapDrawableToUrl(): String {
        //convert to bitmap
        val drawable = ContextCompat.getDrawable(this, MyHelper.avatar.random())
        return swapBitmapToUrl(this,drawable!!)
    }
}