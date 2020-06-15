package com.example.hospinall.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hospinall.AlarmPopUp;
import com.example.hospinall.MainActivity;
import com.example.hospinall.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {
    EditText nombre, contrasenya;
    Button signIn, signOut, emergency;
    ImageView userImage;
    DatabaseReference reff;
    String passwordDB = "", usercheck;
    boolean correctUser = false;
    boolean correctPassword = false;
    List<String> userList = new ArrayList<String>();
    private LoginViewModel loginViewModel;
    NavigationView navigationView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        // View main = inflater.inflate(R.layout.content_main, container, false);
        nombre = root.findViewById(R.id.edit_username);
        // userImage = root.findViewById(R.id.imageView);
        // navigationView = main.findViewById(R.id.nav_view);
        navigationView = Objects.requireNonNull(getActivity()).findViewById(R.id.nav_view);
        contrasenya = root.findViewById(R.id.edit_passwordUser);
        signIn = root.findViewById(R.id.btn_logIn);
        signOut = root.findViewById(R.id.btn_logOut);
        emergency = root.findViewById(R.id.unregistered_alarm);

        emergency.setOnClickListener(this);

        SharedPreferences prefs = getContext().getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String database = sharedPreferences.getString("name_db", "Database");

        reff = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        usercheck = reff.getKey();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        if (prefs.getString("loggedIn", "notLogged").equals("notLogged")) {
            signIn.setEnabled(true);
            signOut.setEnabled(false);
        } else {
            signIn.setEnabled(false);
            signOut.setEnabled(true);
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userList.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = nombre.getText().toString();

                for (String s : userList) {
                    if (s.equals(userName)) {
                        correctUser = true;
                    }
                }
                if (!correctUser) {
                    Toast.makeText(getContext(), R.string.wrong_user, Toast.LENGTH_LONG).show();
                } else {
                    reff.child(userName).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            passwordDB = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();

                            if (passwordDB.equals(contrasenya.getText().toString())) {
                                Toast.makeText(getContext(), R.string.login_completed, Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("ActiveUser", userName);
                                editor.apply();
                                //   nombre.getText().clear();
                                //   contrasenya.getText().clear();
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setImageView();
                                prefs.edit().putString("loggedIn", "logged").apply();
                                Menu menu = navigationView.getMenu();
                                if (userName.contains("Admin")) {
                                    menu.findItem(R.id.nav_home).setVisible(true);
                                    menu.findItem(R.id.nav_send).setVisible(true);
                                    menu.findItem(R.id.nav_share).setVisible(true);
                                    menu.findItem(R.id.nav_tools).setVisible(true);
                                    menu.findItem(R.id.userAlarmFragment).setVisible(true);
                                    menu.findItem(R.id.listaAlarmasPacientes).setVisible(true);
                                } else if (userName.contains("Tech")) {
                                    menu.findItem(R.id.nav_home).setVisible(false);
                                    menu.findItem(R.id.nav_send).setVisible(false);
                                    menu.findItem(R.id.nav_share).setVisible(true);
                                    menu.findItem(R.id.nav_tools).setVisible(true);
                                    menu.findItem(R.id.userAlarmFragment).setVisible(false);
                                    menu.findItem(R.id.listaAlarmasPacientes).setVisible(false);
                                } else if (userName.contains("Patient")) {
                                    menu.findItem(R.id.nav_home).setVisible(false);
                                    menu.findItem(R.id.nav_send).setVisible(false);
                                    menu.findItem(R.id.nav_share).setVisible(false);
                                    menu.findItem(R.id.nav_tools).setVisible(false);
                                    menu.findItem(R.id.userAlarmFragment).setVisible(true);
                                    menu.findItem(R.id.listaAlarmasPacientes).setVisible(true);
                                } else if (userName.isEmpty()) {
                                    Toast.makeText(getContext(), R.string.empty_username, Toast.LENGTH_LONG).show();

                                } else {
                                    menu.findItem(R.id.nav_home).setVisible(true);
                                    menu.findItem(R.id.nav_send).setVisible(true);
                                    menu.findItem(R.id.nav_share).setVisible(false);
                                    menu.findItem(R.id.nav_tools).setVisible(false);
                                    menu.findItem(R.id.userAlarmFragment).setVisible(false);
                                    menu.findItem(R.id.listaAlarmasPacientes).setVisible(true);
                                }
                                signIn.setEnabled(false);
                                signOut.setEnabled(true);
                                nombre.setEnabled(false);
                                contrasenya.setEnabled(false);
                            } else {
                                Toast.makeText(getContext(), R.string.wrong_passwd, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d("sig In", userName + " ," + contrasenya.getText().toString());

                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ActiveUser", "No user");
                editor.apply();
                MainActivity.removeImageView();
                nombre.getText().clear();
                contrasenya.getText().clear();
                prefs.edit().putString("loggedIn", "logged").apply();
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.nav_home).setVisible(false);
                menu.findItem(R.id.nav_send).setVisible(false);
                menu.findItem(R.id.nav_share).setVisible(false);
                menu.findItem(R.id.nav_tools).setVisible(false);
                menu.findItem(R.id.userAlarmFragment).setVisible(false);
                menu.findItem(R.id.listaAlarmasPacientes).setVisible(false);
                signIn.setEnabled(true);
                signOut.setEnabled(false);
                nombre.setEnabled(true);
                contrasenya.setEnabled(true);
                Toast.makeText(getContext(), R.string.user_logout, Toast.LENGTH_LONG).show();

            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), AlarmPopUp.class));
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (v.getId() == R.id.unregistered_alarm) {
            Log.i("click", "Unregistered Test");
            prefs.edit().putString("alarmCodeColor", "Unregistered Alarm").apply();
            prefs.edit().putString("alarmType", "Unregistered").apply();
        } else {
            Log.i("click", "Unregistered ID");
        }
    }

}