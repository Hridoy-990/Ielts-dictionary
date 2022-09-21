package com.example.ieltsdictionary

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.ieltsdictionary.databinding.ActivityMainBinding
import com.example.ieltsdictionary.databinding.ItemDrawerHeaderBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var onScreenKeyboard: InputMethodManager
    private lateinit var historyFragment: HistoryFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var favoriteFragment: FavouriteFragment
    private lateinit var listeningFragment: ListeningFragment
    private lateinit var readingFragment: ReadingFragment
    private lateinit var writingFragment: WritingFragment

    private lateinit var headerViewBinding: ItemDrawerHeaderBinding
    private lateinit var toggle: ActionBarDrawerToggle

    private val APP_RATING_URI = "market://details?id="
    private val APP_RATING_URI_NEW = "https://play.google.com/store/apps/details?id="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeFragment = HomeFragment()
        historyFragment = HistoryFragment()
        favoriteFragment = FavouriteFragment()
        listeningFragment = ListeningFragment()
        readingFragment  = ReadingFragment()
        writingFragment = WritingFragment()

        binding.progressBar.visibility = View.VISIBLE
        binding.tvLoading.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.progress = 1
            setFragment(homeFragment , "Home")
            binding.progressBar.visibility = View.INVISIBLE
            binding.tvLoading.visibility = View.INVISIBLE

        }, 3000)

        setUpToolbar()
        setupNavigationDrawer()
    }

    //region Setup Toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setFragment(fragment: Fragment, label: String) {
        supportFragmentManager.beginTransaction().replace(R.id.view_fragment_container, fragment)
            .commit()
        supportActionBar?.title = label
    }

    override fun onBackPressed() {

        if (supportFragmentManager.findFragmentById(R.id.view_fragment_container) is HomeFragment) {
            super.onBackPressed()
        } else setFragment(homeFragment, "Home")
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.showOverflowMenu()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.historyFragment -> {
                setFragment(historyFragment, "History")
            }
            R.id.favoriteFragment -> {

                setFragment(favoriteFragment, "Favorite")
            }
            R.id.homeFragment -> {
                setFragment(homeFragment, "Home")
            }
            R.id.ShareApp -> {
                shareApp(this)
            }
            R.id.rateUs -> {
                openAppRating("com.apps.eng_ban_dictionary_offline")
            }
            else -> {
                hideKeyboard(this , binding.viewFragmentContainer)
                toggle.onOptionsItemSelected(item)
            }
        }
        return true
    }


    //endregion

    //region Setup Navigation Drawer
    private fun setupNavigationDrawer() {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navDrawer.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(
            this,
            binding.layoutDrawer,
            R.string.label_open,
            R.string.label_close
        )
        binding.layoutDrawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        headerViewBinding = ItemDrawerHeaderBinding.inflate(layoutInflater)
        binding.navDrawer.addHeaderView(headerViewBinding.root)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.historyFragment -> {
                setFragment(historyFragment, "History")
            }
            R.id.favoriteFragment -> {
                setFragment(favoriteFragment, "Favorite")
            }
            R.id.homeFragment -> {
                setFragment(homeFragment, "Home")
            }
            R.id.listeningFragment -> {
                setFragment(listeningFragment , "Listening")
            }
            R.id.readingFragment -> {
                setFragment(readingFragment , "Reading")
            }
            R.id.writingFragment -> {
                setFragment(writingFragment , "Writing")
            }
            R.id.ShareApp -> {
                shareApp(this)
            }
            R.id.rateUs -> {
                openAppRating("com.apps.eng_ban_dictionary_offline")
            }
            else -> {
                hideKeyboard(this , binding.viewFragmentContainer)
                toggle.onOptionsItemSelected(item)
            }
        }
        return true
    }
    //endregion


    private fun shareApp(context: Context) {
        val textToShow = """
            http://play.google.com/store/apps/details?id=com.apps.eng_ban_dictionary_offline
            """.trimIndent()
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT, textToShow
        )
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }

    fun Activity.openAppRating(packageName: String? = null) {
        try {
            val uri: Uri = if(packageName != null) {
                Uri.parse("$APP_RATING_URI$packageName")
            } else {
                Uri.parse("$APP_RATING_URI${ContactsContract.Directory.PACKAGE_NAME}")
            }

            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            val uri: Uri = if(packageName != null) {
                Uri.parse("$APP_RATING_URI_NEW$packageName")
            } else {
                Uri.parse("$APP_RATING_URI_NEW${ContactsContract.Directory.PACKAGE_NAME}")
            }
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    uri
                )
            )
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        onScreenKeyboard = (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
        onScreenKeyboard.hideSoftInputFromWindow(view.windowToken, 0)
    }
}