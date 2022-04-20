package com.example.damxat.Views.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.damxat.Adapter.RecyclerXatAdapter;
import com.example.damxat.Model.User;
import com.example.damxat.Model.Xat;
import com.example.damxat.Model.XatGroup;
import com.example.damxat.R;
import com.example.damxat.Views.Activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Retrofit;

public class XatFragment extends Fragment {

    DatabaseReference ref;
    View view;
    FirebaseUser firebaseUser;
    String userid;
    Bundle bundle;
    Boolean isXatUser;
    ArrayList<Xat> arrayXats;
    ArrayList<String> arrayUsers;
    EditText txtMessage;

    String token;

    ImageButton microState;
    ArrayList<String> resultAudioRecord;

    private int RecordAudioRequestCode=1;
    private int ActivityResultRequestCode=2;

    XatGroup group;
    String groupName;

    public XatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_xat, container, false);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
            }
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Obtenemos los datos superiores del chat (usuarios, grupo etc)
        bundle = getArguments();

        if(bundle.getString("type").equals("xatuser")){
            isXatUser = true;
            getUserXat();
        }else{
            isXatUser = false;
            groupName = bundle.getString("group");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(groupName);
            readGroupMessages(groupName);
        }


        ImageButton btnMessage = view.findViewById(R.id.btnMessage);
        microState = view.findViewById(R.id.btnAudioRecord);
        txtMessage = view.findViewById(R.id.txtMessage);


        //Boton para enviar el mensaje con el texto que se haya introducido en el input
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMessage.getText().toString();
                //String title = txtTitle.
                token = "fCfslGvqT6CYmG_ubPHGsC:APA91bGGfEVLdHa6i6c8hr-8K3ztS7DtWcSl45BvxhD-n_qvIThLK-5kE3V6lkr-_z9u2kch9CqJKNEKF-ShdydIeMTWVXnpMUanjmGSPvk42PQeLyczl-rXOTdosTy1LM5BhOBMwLlu";

                if(!msg.isEmpty()){
                    sendMessage(firebaseUser.getUid(), msg, isXatUser);
                    //Send notification
                    sendNoti();

                }else{
                    Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("");
            }
        });

        //Boton para enviar el mensaje con el texto que se haya introducido en el input
        microState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                microState.setImageResource(R.drawable.ic_baseline_mic);
                Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, digues quelcom!");
                startActivityForResult(speechRecognizerIntent, ActivityResultRequestCode);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getContext(),"Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultRequestCode){
            if(resultCode == RESULT_OK && data != null){
                microState.setImageResource(R.drawable.ic_baseline_mic_off);
                resultAudioRecord=data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
                txtMessage.setText(resultAudioRecord.get(0));
            }
        }
    }

    //Método para obtener los mensajes del chat específico
    public void getUserXat(){
        if(getArguments()!=null) {
            userid = bundle.getString("user");

            //Obtenemos la referencia de un usuario en concreto de Firebase
            ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);

            //Agregamos un evento de conexión
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Declaramos un objeto "User"
                    User user = dataSnapshot.getValue(User.class);

                    //Una vez tenemos el usuario, cambiamos el titulo por el usuario
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(user.getUsername());

                    //Llamamos al metodo para recoger los mensajes del chat actual
                    readUserMessages();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



    public void sendMessage(String sender, String message, boolean isXatUser){
        //Comprobamos si el chat es privado o de un grupo
        if(isXatUser==true){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            String receiver = userid;
            Xat xat = new Xat(sender, receiver, message);
            ref.child("Xats").push().setValue(xat);
        }else{
            ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

            Xat xat = new Xat(sender, message);

            if(arrayXats==null) {
                arrayXats = new ArrayList<Xat>();
                arrayXats.add(xat);
            }else{
                arrayXats.add(xat);
            }

            if(group.getUsers()==null){
                arrayUsers = new ArrayList<String>();
                arrayUsers.add(firebaseUser.getUid());
            }else{
                if(!group.getUsers().contains(firebaseUser.getUid())){
                    arrayUsers.add(firebaseUser.getUid());
                }
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("xats", arrayXats);
            hashMap.put("users", arrayUsers);
            ref.updateChildren(hashMap);
        }
    }

    public void readUserMessages(){
        arrayXats = new ArrayList<>();

        //Obtenemos la referencia de los chats de Firebase
        ref = FirebaseDatabase.getInstance().getReference("Xats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayXats.clear();

                //Agregamos 1 por 1 los mensajes que hay en ese chat (todos)
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Xat xat = postSnapshot.getValue(Xat.class);
                    //Solo agregamos los mensajes que sean del remitente y destinatarios actuales
                    if(xat.getReceiver().equals(userid) && xat.getSender().equals(firebaseUser.getUid()) ||
                            xat.getReceiver().equals(firebaseUser.getUid()) && xat.getSender().equals(userid)){
                        arrayXats.add(xat);
                        Log.i("logTest",xat.getMessage());
                    }
                }

                //Llamamos al método para refrescar el recycler
                updateRecycler();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }


    public void readGroupMessages(String groupName){

        //Obtenemos la referencia de los grupos de nuestro firebase
        ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);


        //En base a nuestro firebase, intentamos establecer conexión
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Si hemos conectado, en base al grupo actual, obtenemos sus mensajes
                group = dataSnapshot.getValue(XatGroup.class);

                arrayXats = group.getXats();

                if(arrayXats!=null) {
                    updateRecycler();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }

    public void updateRecycler(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerXat);
        RecyclerXatAdapter adapter = new RecyclerXatAdapter(arrayXats, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}