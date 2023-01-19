package com.soham.passman.activity

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soham.passman.R
import com.soham.passman.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        sharedPref = getSharedPreferences("PassMan", MODE_PRIVATE)
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        binding.btnSignUp.setOnClickListener {
            if(binding.masterPasswordEdtTxt.text.toString() == binding.masterPasswordAgainEdtTxt.text.toString()){
                signIn()
            }
            else{
                binding.masterPasswordAgainEdtTxt.error = "Passwords Don't match"
            }
        }
        binding.masterPasswordAgainEdtTxt.doOnTextChanged { text, start, before, count ->

        }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val hashMap =  HashMap<String,String>()
                    hashMap["account"] = ""
                    hashMap["userName"] = ""
                    hashMap["password"] = ""
                    hashMap["color"] = ""
                    hashMap["date"] = ""
                    db.collection(user?.uid!!)
                        .document("initial")
                        .set(hashMap)
                        .addOnSuccessListener {
                            sharedPref.edit().putString("secretKey",binding.masterPasswordAgainEdtTxt.text.toString()).commit()
                            updateUI(user)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signIn() {
            val signInIntent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
//        else {
//            binding.txtMasterPasswordAgain.error = "Passwords Don't Match"
//        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            finish()
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            intent.putExtra("userId",user.uid)
            startActivity(intent)
        }
    }
}
