package com.android.notes;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements com.android.notes.ui.NoteFragment.Controller, com.android.notes.ui.NoteListFragment.Controller, com.android.notes.ui.AuthFragment.Controller {

    private boolean isLandscape;
    private com.android.notes.domain.Navigation navigation;
    private static final String NOTES_LIST_FRAGMENT_TAG = "NOTES_LIST_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLandscape = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;
        readSettings();
        navigation = new com.android.notes.domain.Navigation(getSupportFragmentManager());
        if (com.android.notes.domain.User.nameUser == null) {
            getNavigation().addFragment(R.id.main_container, com.android.notes.ui.AuthFragment.newInstance(), "");
        } else {
            initView();
            getNavigation().addFragment(R.id.main_container, new com.android.notes.ui.NoteListFragment(), "NOTES_LIST_FRAGMENT_TAG");
        }
    }

    @Override
    public void openMainScreen() {
        initView();
        getNavigation().addFragment(R.id.main_container, new com.android.notes.ui.NoteListFragment(), "NOTES_LIST_FRAGMENT_TAG");
    }

    private void initView() {
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    // регистрация drawer
    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_about) {
                getNavigation().addFragment(R.id.main_container, new com.android.notes.ui.AboutAppFragment(), "");
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return toolbar;
    }

    @Override
    public void openNoteScreen(com.android.notes.domain.Note note, int position) {
        int idView = isLandscape ? R.id.detail_container : R.id.main_container;
        getNavigation().addFragment(idView, com.android.notes.ui.NoteFragment.newInstance(note, position, false), "");
    }

    @Override
    public void saveResult(com.android.notes.domain.Note note, int position) {
        getSupportFragmentManager().popBackStack();
        com.android.notes.ui.NoteListFragment noteListFragment = (com.android.notes.ui.NoteListFragment) getSupportFragmentManager().findFragmentByTag(NOTES_LIST_FRAGMENT_TAG);
        assert noteListFragment != null;
        noteListFragment.addUpdateNote(note, position);
    }

    // Чтение настроек
    private void readSettings() {
        // Специальный класс для хранения настроек
        SharedPreferences sharedPref = getSharedPreferences(com.android.notes.domain.Settings.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        // Считываем значения настроек
        com.android.notes.domain.Settings.fontSize = sharedPref.getInt(com.android.notes.domain.Settings.FONT_SIZE_KEY, 20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Здесь определяем меню приложения (активити)
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search); // поиск пункта меню поиска
        SearchView searchText = (SearchView) search.getActionView(); // строка поиска
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // реагирует на конец ввода поиска
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            // реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getNavigation().addFragment(R.id.main_container, new com.android.notes.ui.SettingsFragment(), "");
            return true;
        } else if (id == R.id.action_main) {
            com.android.notes.ui.NoteListFragment noteListFragment = (com.android.notes.ui.NoteListFragment) getSupportFragmentManager().findFragmentByTag(NOTES_LIST_FRAGMENT_TAG);
            getNavigation().addFragment(R.id.main_container, noteListFragment, "");
            return true;
        } else if (id == R.id.action_sort) {
            Toast.makeText(this, "Сортировка пока не работает", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public com.android.notes.domain.Navigation getNavigation() {
        return navigation;
    }

}
