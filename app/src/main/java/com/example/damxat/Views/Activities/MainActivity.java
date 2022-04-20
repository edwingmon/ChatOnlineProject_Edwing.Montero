package com.example.damxat.Views.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.damxat.Constants.Constants;
import com.example.damxat.R;
import com.example.damxat.Views.Fragments.GroupsFragment;
import com.example.damxat.Views.Fragments.MyXatsFragment;
import com.example.damxat.Views.Fragments.UserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    Toolbar toolbar;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.i(Constants.TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        setToken(token);
                        Log.i(Constants.TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyXatsFragment()).commit();

        //Obtenemos la instancia del usuario que se ha logueado
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.top_menu);


        BottomNavigationView bottomNav = findViewById(R.id.main_menu);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_xats:
                    selectedFragment = new MyXatsFragment();
                    break;

                case R.id.nav_group:
                    selectedFragment = new GroupsFragment();
                    break;

                case R.id.nav_users:
                    selectedFragment = new UserFragment();

                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        });
    }

    private void setToken(String tokken){
        //Ponemos el estado de conexión facilitado a nuestro usuario
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);
        ref.updateChildren(hashMap);
    }

    private void status(String status){
        //Ponemos el estado de conexión facilitado a nuestro usuario
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        ref.updateChildren(hashMap);
    }

    //Método para establecer el estado online de nuestro usuario
    @Override
    protected void onResume(){
        super.onResume();
        status("online");
    }

    //Método para establecer el estado offline de nuestro usuario
    @Override
    protected void onPause(){
        super.onPause();
        status("offline");
    }

    //Metodo que crea el menú superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    //Metodo para mostrar el menu con el boton para salir de la sesion
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("TESTMENU", "hola" + item.getItemId());
        switch(item.getItemId()){
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;
        }
        return false;
    }

}