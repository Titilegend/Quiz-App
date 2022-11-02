package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quizapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding:ActivityLoginBinding
    val auth = FirebaseAuth.getInstance()

    lateinit var googleSigninClient:GoogleSignInClient
    lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)
        val textOfGoogleButton = loginBinding.googleSignin.getChildAt(0) as TextView
        textOfGoogleButton.text = "Continue with google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 18F
        //register activity launcher
        registerActivityForGoogleSignin()
        loginBinding.buttonSignin.setOnClickListener {
            val userEmail = loginBinding.editTextEmail.text.toString()
            val userPassword = loginBinding.editTextPassword.text.toString()
            signInUser(userEmail,userPassword)
        }
        loginBinding.googleSignin.setOnClickListener {
        signinGoogle()
        }
        loginBinding.textViewSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity,SignupActivity::class.java)
            startActivity(intent)
        }
        loginBinding.textViewForgotPassword.setOnClickListener {
        val intent =Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
        startActivity(intent)
        }
    }
    fun signInUser(userEmail:String,userPassword:String){
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task->
            if(task.isSuccessful){
               Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
               val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        //checks info  about the user
        val user = auth.currentUser
        if(user!=null){
            Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun signinGoogle(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
             //an id token is requested
            .requestIdToken("137106460858-q0rgiahu9ggqptbfhaik552ghmml6oae.apps.googleusercontent.com")
            .requestEmail().build()

        googleSigninClient = GoogleSignIn.getClient(this,gso)

        signIn()

    }
    private fun signIn(){
        val signInIntent:Intent = googleSigninClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }
    private fun registerActivityForGoogleSignin(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result->
                val resultCode = result.resultCode
                val data = result.data

                if(resultCode == RESULT_OK && data!=null){
                 //create thread that will get data from google in the background
                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)
                }
            })
    }
    private fun firebaseSignInWithGoogle(task : Task<GoogleSignInAccount>){
        try{
            val account:GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }catch(e:ApiException){
           Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }
    private fun firebaseGoogleAccount(account:GoogleSignInAccount){
        val authCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener {task->
            if(task.isSuccessful){
                //get user data
                val user = auth.currentUser

            }else{

            }
        }
    }
}