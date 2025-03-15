package com.example.wikifountains;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FuenteViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewNombre;
    public TextView textViewUbicacion;
    public TextView textViewDescripcion;

    public FuenteViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewNombre = itemView.findViewById(R.id.textViewNombre);
        textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
        textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
    }
}