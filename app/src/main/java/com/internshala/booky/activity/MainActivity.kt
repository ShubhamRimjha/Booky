package com.internshala.booky.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.internshala.booky.R
import com.internshala.booky.fragment.AboutFragment
import com.internshala.booky.fragment.DashboardFragment
import com.internshala.booky.fragment.FavouritesFragment
import com.internshala.booky.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        drawerLayout = findViewById(R.id.drawerLayout)

        toolbar = findViewById(R.id.toolbar)

        navigationView = findViewById(R.id.navigationView)

        openFrag(DashboardFragment(), "Dashboard")

        setUpToolBar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openFrag(DashboardFragment(), "Dashboard")
                }
                R.id.about -> {
                    openFrag(AboutFragment(), "About")
                }
                R.id.profile -> {
                    openFrag(ProfileFragment(), "Profile")
                }
                R.id.favourites -> {
                    openFrag(FavouritesFragment(), "Favourites")
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.isHideOnContentScrollEnabled
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_book)

    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        if (drawerLayout.isOpen) drawerLayout.close()
        else {
            when (frag) {
                !is DashboardFragment -> openFrag(DashboardFragment(), "Dashboard")
                else -> super.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem)
            : Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openFrag(frag: Fragment, name: String) {
        Toast.makeText(this, name + "clicked", LENGTH_SHORT)
            .show()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, frag).commit()
        supportActionBar?.title = name
        drawerLayout.close()
    }

}