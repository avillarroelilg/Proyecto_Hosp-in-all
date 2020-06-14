package com.example.hospinall.ui.hexadecimalgen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hospinall.R;

import java.util.Random;

public class HexaFragment extends Fragment implements View.OnClickListener {
    EditText cajaHex;
    TextView ID;
    private HexaViewModel hexaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        hexaViewModel =
                ViewModelProviders.of(this).get(HexaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        cajaHex = root.findViewById(R.id.insertarID);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // cajaHex.setText(sharedPreferences.getString("tabletID", "0"));
        root.findViewById(R.id.randomIDGen).setOnClickListener(this);
        root.findViewById(R.id.manualIDGen).setOnClickListener(this);
        ID = root.findViewById(R.id.id_disp);
        ID.setText(sharedPreferences.getString("tabletID", "0"));
        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.randomIDGen:

                Log.i("click", getString(R.string.hex_gener));
                createHexadecimal();
                break;
            case R.id.manualIDGen:
                if (!hexadecimalCheck()) {
                    Toast.makeText(getContext(), R.string.not_hex_warning, Toast.LENGTH_LONG).show();
                } else {
                    String contenido = cajaHex.getText().toString().toUpperCase();
                    if (contenido.length() == 4) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("tabletID", contenido);
                        editor.commit();
                        ID.setText(contenido);
                        cajaHex.getText().clear();

                    } else {
                        Toast.makeText(getContext(), R.string.hex_length, Toast.LENGTH_LONG).show();
                    }
                }
            default:
                Log.i("click", "Unregistered onclick");
                break;

        }
    }

    /**
     * Generates a random Hexadecimal number.
     */

    private void createHexadecimal() {
        Random rand = new Random();
        int myRandomNumber = rand.nextInt(0xfff) + 0xfff;
        String result = Integer.toHexString(myRandomNumber);
        result = result.toUpperCase();
        ID.setText(result);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("tabletID", result);
        editor.commit();

    }

    /**
     * Checks if the number given by the user is Hexadecimal.
     * @return The number's Hexadecimal or not.
     */
    private Boolean hexadecimalCheck() {
        String contenido = cajaHex.getText().toString();
        Boolean isHex;
        try {
            int testCont = Integer.parseInt(contenido.toLowerCase(), 16);
            isHex = true;
        } catch (NumberFormatException e) {
            isHex = false;
        }
        return (isHex);
    }

}