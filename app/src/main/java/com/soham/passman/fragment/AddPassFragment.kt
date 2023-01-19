package com.soham.passman.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.passman.R
import com.soham.passman.databinding.FragmentAddPassBinding
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

class AddPassFragment : Fragment() {
    private lateinit var binding: FragmentAddPassBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPassBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        sharedPref = requireActivity().getSharedPreferences("PassMan",Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val IV = ByteArray(16)
        val random = SecureRandom()
        var selectedColor = "Green"
        random.nextBytes(IV)

        binding.btnAddNewPass.setOnClickListener {
            if(!TextUtils.isEmpty(binding.txtPassword.text)
                && !TextUtils.isEmpty(binding.txtAccount.text)
                && !TextUtils.isEmpty(binding.txtUserName.text)){
                try {
                    val encrypt = encrypt(binding.txtPassword.text.toString(),IV)
                    val hashMap = HashMap<String,String>()
                    val time = Calendar.getInstance().time
                    val timeInString = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(time)
                    hashMap["account"] = binding.txtAccount.text.toString()
                    hashMap["userName"] = binding.txtUserName.text.toString()
                    hashMap["password"] = encrypt!!
                    hashMap["color"] = selectedColor
                    hashMap["time"] = timeInString
                    db.collection(user!!.uid).document().set(hashMap).addOnSuccessListener {
                        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                        binding.apply {
                            btnDropDownArrow.callOnClick()
                            cardView1.callOnClick()
                            txtAccount.text?.clear()
                            txtUserName.text?.clear()
                            txtPassword.text?.clear()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
//                if(TextUtils.isEmpty(binding.txtPassword.text)){
//                    binding.txtLayoutPassword.error = "Can't be empty"
//                }
//                else if(TextUtils.isEmpty(binding.txtUserName.text)){
//                    binding.txtLayoutUserName.error = "Can't be empty"
//                }
//                else{
//                    binding.txtLayoutAccount.error = "Can't be empty"
//                }
            }
        }
        binding.btnDropDownArrow.setOnClickListener {
            binding.apply {
                if( cardView2.visibility == View.GONE && cardView3.visibility == View.GONE
                    || cardView1.visibility == View.GONE && cardView3.visibility == View.GONE
                    || cardView1.visibility == View.GONE && cardView2.visibility == View.GONE){
                    cardView1.visibility = View.VISIBLE
                    cardView2.visibility = View.VISIBLE
                    cardView3.visibility = View.VISIBLE
                    btnDropDownArrow.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_up_arrow
                    ))
                }
                else{
                    when(selectedColor){
                        "Green"->{
                            cardView1.visibility = View.VISIBLE
                            cardView2.visibility = View.GONE
                            cardView3.visibility = View.GONE
                        }
                        "Yellow"->{
                            cardView2.visibility = View.VISIBLE
                            cardView1.visibility = View.GONE
                            cardView3.visibility = View.GONE
                        }
                        "Red"->{
                            cardView3.visibility = View.VISIBLE
                            cardView1.visibility = View.GONE
                            cardView2.visibility = View.GONE
                        }
                    }
                    btnDropDownArrow.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_down_arrow
                    ))
                }
            }
        }
        binding.cardView1.setOnClickListener {
            selectedColor = "Green"
            binding.apply{
                cardView2.visibility = View.GONE
                cardView3.visibility = View.GONE
                btnDropDownArrow.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_down_arrow
                ))
            }
        }
        binding.cardView2.setOnClickListener {
            selectedColor = "Yellow"
            binding.apply{
                cardView1.visibility = View.GONE
                cardView3.visibility = View.GONE
                btnDropDownArrow.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_down_arrow
                ))
            }
        }
        binding.cardView3.setOnClickListener {
            selectedColor = "Red"
            binding.apply{
                cardView1.visibility = View.GONE
                cardView2.visibility = View.GONE
                btnDropDownArrow.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_down_arrow
                ))
            }
        }
        return binding.root
    }

    fun encrypt(
        plaintext: String,
        IV: ByteArray?
    ): String? {
        val cipher: Cipher = Cipher.getInstance("AES")
        val keySpec = generateKey(sharedPref.getString("secretKey",null)!!)
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encVal = cipher.doFinal(plaintext.toByteArray())
        val encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT)
        return encryptedValue
    }

    fun generateKey(password: String): SecretKeySpec {
        val keyDigest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray()
        keyDigest.update(bytes, 0, bytes.size)
        val key = keyDigest.digest()
        return SecretKeySpec(key, "AES")
    }

}