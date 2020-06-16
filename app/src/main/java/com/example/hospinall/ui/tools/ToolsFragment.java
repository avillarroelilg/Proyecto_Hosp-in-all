package com.example.hospinall.ui.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hospinall.MainActivity;
import com.example.hospinall.Preferences;
import com.example.hospinall.R;

import java.util.Objects;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    Button openPreferences, stopLock;
    String usuarioText, dbName, dispText, dispIDText;
    TextView usuario, nombre_database, url_database, nombre_dispositivo, id_dispositivo;
    Boolean dbUrlText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        openPreferences = root.findViewById(R.id.preferenceOpen);
        stopLock = root.findViewById(R.id.desfijar);
        usuario = root.findViewById(R.id.actualUser);
        nombre_database = root.findViewById(R.id.nombreDB);
        url_database = root.findViewById(R.id.URLdb);
        nombre_dispositivo = root.findViewById(R.id.nombreDisp);
        id_dispositivo = root.findViewById(R.id.id_disp);

        openPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.preferenceOpen:
                        startActivity(new Intent(getContext(), Preferences.class));
                }
            }
        });
        stopLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.desfijar:
                        Objects.requireNonNull(getActivity()).stopLockTask();
                        Toast.makeText(getContext(), R.string.unpinned_screen, Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;


    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean Nightmode = sharedPreferences.getBoolean("app_theme", false);
        if (!Nightmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        MainActivity mainActivity = new MainActivity();
        mainActivity.setImageView();
        usuarioText = prefs.getString("ActiveUser", null);
        dbName = sharedPreferences.getString("name_db", "Hosp-in-all DB");
        dbUrlText = sharedPreferences.getBoolean("app_theme", false);
        dispText = sharedPreferences.getString("tabletName", "DefaultTabletName");
        dispIDText = sharedPreferences.getString("tabletID", "0");


        usuario.setText(usuarioText);
        if (dbUrlText && url_database != null) {
            url_database.setText(R.string.night_theme);
        } else if (!dbUrlText && url_database !=null){
            url_database.setText(R.string.light_theme);
        }
        nombre_database.setText(dbName);
        nombre_dispositivo.setText(dispText);
        id_dispositivo.setText(dispIDText);

    }
}