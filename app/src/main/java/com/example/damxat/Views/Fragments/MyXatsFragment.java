package com.example.damxat.Views.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.damxat.Adapter.RecyclerMyXatsAdapter;
import com.example.damxat.Adapter.RecyclerUserAdapter;
import com.example.damxat.Model.User;
import com.example.damxat.Model.Xat;
import com.example.damxat.R;
import com.example.damxat.Views.Activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.FileLock;
import java.util.ArrayList;


public class MyXatsFragment extends Fragment {

    RecyclerView recyclerXats;

    boolean arrayWithData = false;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference refUsers;
    DatabaseReference refXats;

    ArrayList<User> arrayUsers = new ArrayList<>();
    ArrayList<String> arrayUsersId = new ArrayList<>();

    View myxatsView;

    public MyXatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myxatsView = inflater.inflate(R.layout.fragment_my_xats, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My Xats");

        recyclerXats = myxatsView.findViewById(R.id.recyclerMyXats);
        recyclerXats.setLayoutManager(new LinearLayoutManager(getContext()));

        refXats = FirebaseDatabase.getInstance().getReference("Xats");
        //Agregamos un evento de firebase, comprobará si se ha conectado
        refXats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayUsersId.clear();
                arrayUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Xat validXat = snapshot.getValue(Xat.class);
                    boolean exists=false;
                    if(validXat.getReceiver().equals(firebaseUser.getUid())){
                        if(!arrayWithData){
                            arrayUsersId.add(validXat.getSender());
                            arrayWithData=true;
                        }
                        if(arrayWithData){
                            for (int i=0; i < arrayUsersId.size(); i++){
                                if(validXat.getSender().equals(arrayUsersId.get(i))){
                                    exists=true;
                                }
                            }
                            if(!exists){
                                arrayUsersId.add(validXat.getSender());
                            }
                        }
                    }
                    if(validXat.getSender().equals(firebaseUser.getUid())){
                        if(arrayWithData==false){
                            arrayUsersId.add(validXat.getReceiver());
                            arrayWithData=true;
                        }
                        if(arrayWithData){
                            for (int i=0; i < arrayUsersId.size(); i++){
                                if(validXat.getReceiver().equals(arrayUsersId.get(i))){
                                    exists=true;
                                }
                            }
                            if(!exists){
                                arrayUsersId.add(validXat.getReceiver());
                            }
                        }
                    }

                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("userInfo", "HE FALLADO");
            }
        });

        return myxatsView;


    }

    public void getUsers(){
        refUsers = FirebaseDatabase.getInstance().getReference("Users");

        //Agregamos un evento de firebase, comprobará si se ha conectado
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayUsers.clear();
                /*Irá cogiendo 1 por 1 los usuarios registrados en firebase y los irá
                    añadiendo a nuestra lista de usuarios*/
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(!user.getId().equals(firebaseUser.getUid())){
                        for (int i =0; i < arrayUsersId.size(); i++){
                            if(user.getId().equals(arrayUsersId.get(i))){
                                arrayUsers.add(user);
                            }
                        }
                    }
                }

                RecyclerUserAdapter adapter = new RecyclerUserAdapter(arrayUsers, getContext());
                recyclerXats.setAdapter(adapter);
                recyclerXats.addItemDecoration(new DividerItemDecoration(myxatsView.getContext(), DividerItemDecoration.VERTICAL));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}