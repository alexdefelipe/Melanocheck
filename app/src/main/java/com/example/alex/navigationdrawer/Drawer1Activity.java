package com.example.alex.navigationdrawer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;



public class Drawer1Activity extends AppCompatActivity {

    private ListView drawerMenu;
    private CustomAdapter menuOptions;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer1);
        initViews();

        // Indico el nombre de las opciones que contendrá el menú lateral...
        String [] opcionesMenu = {"Comprobar mancha", "Protégete", "UV", "Acerca de"};
        Integer [] iconos = {R.drawable.ic_photo_camera_black_24dp, R.drawable.ic_add_box_black_24dp,
                R.drawable.ic_wb_sunny_black_24dp, R.drawable.ic_person_black_24dp};

        // ... y las meto en el ArrayList
        final ArrayList <Opciones> drawerMenuOptions = new ArrayList<>();
        for (int i=0;i<opcionesMenu.length;i++) {
            Opciones opcion = new Opciones(iconos[i],opcionesMenu[i]);
            drawerMenuOptions.add(opcion);
        }

        // El ArrayAdaptar me permitirá acoplay el ArrayList al ListView
        menuOptions = new CustomAdapter(this,drawerMenuOptions);
        drawerMenu = findViewById(R.id.drawer_menu);

        drawerMenu.setAdapter(menuOptions);
        drawerMenu.setOnItemClickListener(new DrawerItemClickListener());

        selectItem(0);
    }

    private void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment;
        Bundle args = new Bundle();

        if (position == 0) {
            fragment = new ComprobarManchaFragment();
            args.putInt(ComprobarManchaFragment.ARG_ID_ENTRADA_SELECCIONADA, position);
        } else if (position == 1){
            fragment = new ProtegeteFragment();
            args.putInt(ProtegeteFragment.ARG_ID_ENTRADA_SELECIONADA, position);
        } else if (position == 2){
            fragment = new UvFragment();
            args.putInt(UvFragment.ARG_ID_ENTRADA_SELECCIONADA, position);
        } else if (position == 3){
            fragment = new AcercaDeFragment();
            args.putInt(AcercaDeFragment.ARG_ID_ENTRADA_SELECCIONADA, position);
        } else {
            fragment = new ErrorFragment();
            args.putInt(ErrorFragment.ARG_ID_ENTRADA_SELECCIONADA, position);
        }

        fragment.setArguments(args);
        Log.d("Posicion",Integer.toString(position));

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerMenu.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(drawerMenu);
    }
}
