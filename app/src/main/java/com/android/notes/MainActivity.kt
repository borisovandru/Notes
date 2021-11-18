package com.android.notes

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.notes.User.nameUser
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), NoteFragment.Controller, NoteListFragment.Controller,AuthFragment.Controller {
    private var isLandscape = false
    lateinit var  navigation: Navigation
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isLandscape = resources.configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE
        readSettings()
        navigation = Navigation(supportFragmentManager)
        if (nameUser == null) {
            navigation.addFragment(R.id.main_container, AuthFragment.newInstance(), "")
        } else {
            initView()
            navigation.addFragment(
                R.id.main_container,
                NoteListFragment(),
                "NOTES_LIST_FRAGMENT_TAG"
            )
        }
    }

    override fun openMainScreen() {
        initView()
        navigation.addFragment(R.id.main_container, NoteListFragment(), "NOTES_LIST_FRAGMENT_TAG")
    }

    private fun initView() {
        val toolbar = initToolbar()
        initDrawer(toolbar)
    }

    // регистрация drawer
    private fun initDrawer(toolbar: Toolbar) {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Обработка навигационного меню
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            if (id == R.id.action_about) {
                navigation.addFragment(R.id.main_container, AboutAppFragment(), "")
                drawer.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }
            false
        }
    }

    private fun initToolbar(): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        return toolbar
    }

    override fun openNoteScreen(note: Note, position: Int) {
        val idView = if (isLandscape) R.id.detail_container else R.id.main_container
        navigation.addFragment(idView, NoteFragment.newInstance(note, position, false), "")
    }

    override fun saveResult(note: Note, position: Int) {
        supportFragmentManager.popBackStack()
        val noteListFragment =
            (supportFragmentManager.findFragmentByTag(NOTES_LIST_FRAGMENT_TAG) as NoteListFragment?)!!
        noteListFragment.addUpdateNote(note, position)
    }

    // Чтение настроек
    private fun readSettings() {
        // Специальный класс для хранения настроек
        val sharedPref = getSharedPreferences(Settings.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
        // Считываем значения настроек
        Settings.fontSize = sharedPref.getInt(Settings.FONT_SIZE_KEY, 20)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Здесь определяем меню приложения (активити)
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.action_search) // поиск пункта меню поиска
        val searchText = search.actionView as SearchView // строка поиска
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // реагирует на конец ввода поиска
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                return true
            }
            // реагирует на нажатие каждой клавиши
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Обработка выбора пункта меню приложения (активити)
        return when (item.itemId) {
            R.id.action_settings -> {
                navigation.addFragment(R.id.main_container, SettingsFragment(), "")
                true
            }
            R.id.action_main -> {
                val noteListFragment = supportFragmentManager.findFragmentByTag(
                    NOTES_LIST_FRAGMENT_TAG
                ) as NoteListFragment?
                navigation.addFragment(R.id.main_container, noteListFragment, "")
                true
            }
            R.id.action_sort -> {
                Toast.makeText(this, "Сортировка пока не работает", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val NOTES_LIST_FRAGMENT_TAG = "NOTES_LIST_FRAGMENT_TAG"
    }
}