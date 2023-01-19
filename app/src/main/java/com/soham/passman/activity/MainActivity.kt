package com.soham.passman.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.soham.passman.*
import com.soham.passman.adapter.ViewPagerAdapter
import com.soham.passman.data.Data
import com.soham.passman.databinding.ActivityMainBinding
import com.soham.passman.fragment.AboutUsFragment
import com.soham.passman.fragment.AddPassFragment
import com.soham.passman.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewPager = binding.viewPager
        val fragmentList = arrayListOf(
            HomeFragment(),
            AddPassFragment(),
            AboutUsFragment()
        )
        val viewPagerAdapter = ViewPagerAdapter(fragmentList,this.supportFragmentManager,lifecycle)
        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = 0
        binding.bottomNavigation.setOnItemSelectedListener{
            when(it.itemId){
                R.id.menu_home ->{
                    viewPager.currentItem = 0
                    binding.fab.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                    binding.bottomNavigation.selectedItemId = R.id.menu_placeHolder
                    binding.btnMenu.visibility = View.VISIBLE
                }
                R.id.menu_aboutUs ->{
                    viewPager.currentItem = 2
                    binding.fab.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                    binding.bottomNavigation.selectedItemId = R.id.menu_placeHolder
                    binding.btnMenu.visibility = View.GONE
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            viewPager.currentItem = 1
            binding.fab.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_200))
            binding.bottomNavigation.selectedItemId = R.id.menu_placeHolder
            binding.btnMenu.visibility = View.GONE
        }

        val popup = PopupMenu(this, binding.btnMenu)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.option_menu, popup.menu)

        binding.btnMenu.setOnClickListener {
            popup.show()
            popup.setOnMenuItemClickListener {item->
                val menu = popup.menu
                for (i in 0 until menu.size()) {
                    menu.getItem(i).setChecked(false)
                }
//                item.isChecked = true

                when(item.itemId){
                    R.id.sortByDate->{
                        Constants.list?.let { it1 -> Constants.homeAdapter.updateList(it1) }
                        item.isChecked = !item.isChecked
                    }
                    R.id.showGreen->{
                        val newList =  ArrayList<Data>()
                        for(data in Constants.list!!){
                            if(data.color=="Green"){
                                newList.add(data)
                            }
                        }
                        Constants.homeAdapter.updateList(newList)
                        item.isChecked = !item.isChecked
                    }
                    R.id.showRed->{
                        val newList =  ArrayList<Data>()
                        for(data in Constants.list!!){
                            if(data.color=="Red"){
                                newList.add(data)
                            }
                        }
                        Constants.homeAdapter.updateList(newList)
                        item.isChecked = !item.isChecked
                    }
                    R.id.showYellow->{
                        val newList =  ArrayList<Data>()
                        for(data in Constants.list!!){
                            if(data.color=="Yellow"){
                                newList.add(data)
                            }
                        }
                        Constants.homeAdapter.updateList(newList)
                        item.isChecked = !item.isChecked
                    }
                }
                true
            }
        }

        binding.viewPager.isUserInputEnabled = false

    }
}