package kr.ac.konkuk.instagramclone

import Fragments.NotificationsFragment
import Fragments.ProfileFragment
import Fragments.SearchFragment
import android.app.Notification
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import Fragments.homeFragment

class MainActivity : AppCompatActivity() {

    internal var selectedFragment: Fragment ? = null


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = homeFragment()
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.nav_add_post -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                selectedFragment = NotificationsFragment()
            }
            R.id.nav_profile -> {
                selectedFragment = ProfileFragment()
            }
        }

        if(selectedFragment != null)
        {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                selectedFragment!!
            ).commit()
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        //왜 있는건지 알기
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            homeFragment()
        ).commit()


//        moveToFragment(HomeFragment())
    }


//    private fun moveToFragment(fragment: Fragment)
//    {
//        val fragmentTrans = supportFragmentManager.beginTransaction()
//        fragmentTrans.replace(R.id.fragment_container, fragment)
//        fragmentTrans.commit()
//    }
}