package com.soham.passman.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.soham.passman.Constants
import com.soham.passman.adapter.RecyclerViewAdapter
import com.soham.passman.data.Data
import com.soham.passman.databinding.FragmentHomeBinding
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        sharedPref = requireActivity().getSharedPreferences("PassMan", Context.MODE_PRIVATE)
        val passwordsList = ArrayList<Data>()
        val IV = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(IV)
//        db.collection(user.uid).get().addOnSuccessListener {
//            for(doc in it.documents){
//                Toast.makeText(context, doc["userName"].toString(), Toast.LENGTH_SHORT).show()
//                passwordsList.add(Data(doc["account"].toString(),doc["userName"].toString(),doc["password"].toString(),doc["color"].toString()))
//            }
            adapter = RecyclerViewAdapter(passwordsList,requireContext())
            Constants.homeAdapter = adapter
            binding.recyclerLayoutHomeFragment.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            binding.recyclerLayoutHomeFragment.adapter = adapter
            try {
                db.collection(user.uid).orderBy("time",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                    val newList = ArrayList<Data>()
                    for(doc in value!!){
                        if(doc.id != "initial"){
                            newList.add(Data(doc.id,doc["account"].toString(),doc["userName"].toString(),decrypt(doc["password"].toString(),IV).toString(),doc["color"].toString(),doc["time"].toString()))
                            println(decrypt(doc["password"].toString(),IV))
                        }
                    }
                    adapter.updateList(newList)
                    Constants.list = newList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

//        }

        return binding.root
    }

    private fun generateKey(password: String): SecretKeySpec {
        val keyDigest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray()
        keyDigest.update(bytes, 0, bytes.size)
        val key = keyDigest.digest()
        return SecretKeySpec(key, "AES")
    }

    private fun decrypt(cipherText: String, IV: ByteArray?): String? {
        try {
            val cipher = Cipher.getInstance("AES")
            val keySpec = generateKey(sharedPref.getString("secretKey", null)!!)
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decodedValue = Base64.decode(cipherText, Base64.DEFAULT)
            val decValue = cipher.doFinal(decodedValue)
            return String(decValue)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

}