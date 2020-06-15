package com.example.hospinall;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.hospinall.ui.send.SendViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaAlarmasPacientes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaAlarmasPacientes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListaAlarmasPacientes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaAlarmasPacientes.
     */
    // TODO: Rename and change types and number of parameters

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

    public static ListaAlarmasPacientes newInstance(String param1, String param2) {
        ListaAlarmasPacientes fragment = new ListaAlarmasPacientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lista_alarmas_pacientes, container, false);
        reff = FirebaseDatabase.getInstance().getReference().child("Active Patient Warnings");
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


                rvWarnings = (RecyclerView) root.findViewById(R.id.rvPatientsList);

                for (int i = 0; i < idList.size(); i++) {
                    String alarmType = Objects.requireNonNull(dataSnapshot.child(idList.get(i)).child("tipo_Alarma").getValue()).toString();
                    int alarmColor = 0;
                    String time = Objects.requireNonNull(dataSnapshot.child(idList.get(i)).child("time").getValue()).toString();
                    String descrip = Objects.requireNonNull(dataSnapshot.child(idList.get(i)).child("description").getValue()).toString();
                    switch (alarmType) {
                        case "Doctor Request Alarm":
                            alarmColor = R.drawable.doct_alert;
                            break;
                        case "Emergency Alarm":
                            alarmColor = R.drawable.emergency_alarm;
                            break;
                        case "Medicine Reminder Alarm":
                            alarmColor = R.drawable.med_reminder;
                            break;
                        case "Bathroom Alarm":
                            alarmColor = R.drawable.bathroom_alarm;
                            break;
                        case "Unregistered Alarm":
                            alarmColor = R.drawable.unregistered_alarm;
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
            reffInner = FirebaseDatabase.getInstance().getReference().child("Active Patient Warnings").child("ID " + idList.get(i));
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
