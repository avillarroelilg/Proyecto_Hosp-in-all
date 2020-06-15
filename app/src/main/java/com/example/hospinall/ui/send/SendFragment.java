package com.example.hospinall.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospinall.AdapterWarning;
import com.example.hospinall.R;
import com.example.hospinall.Warnings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;
    private WebView mWebView;

    List<Warnings> listaWarnings;

    private RecyclerView rvWarnings;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    DatabaseReference reff;
    DatabaseReference reffInner;
    String idTablet;
    String timestamp;
    String alarmType;

    List<String> idList = new ArrayList<String>();
    List<String> timeList = new ArrayList<String>();
    List<String> alarmTypeList = new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        reff = FirebaseDatabase.getInstance().getReference().child("Active Warnings");
        // idTablet = (Objects.requireNonNull(reff.getKey())).substring((reff.getKey()).indexOf(' ') + 1);
        List<String> testList = new ArrayList<>();
        listaWarnings = new ArrayList<Warnings>();

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //    Toast.makeText(getContext(), "B1", Toast.LENGTH_LONG).show();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    idList.add(snap.getKey());
                    testList.add(snap.getKey());
                }


                rvWarnings = (RecyclerView) root.findViewById(R.id.rvWarnings);

                for (int i = 0; i < idList.size(); i++) {
                    String alarmType = Objects.requireNonNull(dataSnapshot.child(idList.get(i)).child("tipo_Alarma").getValue()).toString();
                    int alarmColor = 0;
                    String time = Objects.requireNonNull(dataSnapshot.child(idList.get(i)).child("time").getValue()).toString();
                    String descrip = Objects.requireNonNull(dataSnapshot.child(idList.get(i)).child("description").getValue()).toString();
                    switch (alarmType) {
                        case "Blue Alarm":
                            alarmColor = R.drawable.alarm_bell_blue_24dp;
                            break;
                        case "Green Alarm":
                            alarmColor = R.drawable.alarm_bell_green_24dp;
                            break;
                        case "Yellow Alarm":
                            alarmColor = R.drawable.alarm_bell_yellow_24dp;
                            break;
                        case "Red Alarm":
                            alarmColor = R.drawable.alarm_bell_red_24dp;
                            break;
                    }
                    listaWarnings.add(new Warnings(alarmColor, alarmType, idList.get(i), time, descrip));

                }

                rvWarnings.setHasFixedSize(true);

                lManager = new LinearLayoutManager(getContext());
                rvWarnings.setLayoutManager(lManager);

                adapter = new AdapterWarning(listaWarnings);
                rvWarnings.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "B2", Toast.LENGTH_LONG).show();

            }
        });

        for (int i = 0; i < idList.size(); i++) {
            reffInner = FirebaseDatabase.getInstance().getReference().child("Active Warnings").child("ID " + idList.get(i));
            reffInner.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Toast.makeText(getContext(), "C1", Toast.LENGTH_LONG).show();

                    timeList.add(Objects.requireNonNull(dataSnapshot.child("time").getValue()).toString());
                    alarmTypeList.add(Objects.requireNonNull(dataSnapshot.child("tipo_Alarma").getValue()).toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "C2", Toast.LENGTH_LONG).show();

                }
            });
        }
        return root;
    }

}