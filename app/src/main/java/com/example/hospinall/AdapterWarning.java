package com.example.hospinall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class AdapterWarning extends RecyclerView.Adapter<AdapterWarning.UsuarioViewHolder> {

    private List<Warnings> listaWarnings;

    public AdapterWarning(List<Warnings> listaWarnings) {
        this.listaWarnings = listaWarnings;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.warning_card_view, viewGroup, false);
        return new UsuarioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder usuarioViewHolder, int i) {
        usuarioViewHolder.ivIcon.setImageResource(listaWarnings.get(i).getIcon());
        usuarioViewHolder.tvAlarmType.setText(listaWarnings.get(i).getAlarmType());
        usuarioViewHolder.tvDeviceID.setText(listaWarnings.get(i).getDeviceID());
        usuarioViewHolder.tvTime.setText(listaWarnings.get(i).getTime());
        usuarioViewHolder.tvDescrip.setText(listaWarnings.get(i).getDescrip());
    }

    @Override
    public int getItemCount() {
        return listaWarnings.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvAlarmType, tvDeviceID, tvTime, tvDescrip;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.imagen);
            tvAlarmType = itemView.findViewById(R.id.tvAlarmType);
            tvDeviceID = itemView.findViewById(R.id.tvDeviceID);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescrip = itemView.findViewById(R.id.tvDescrip);
        }
    }
}
