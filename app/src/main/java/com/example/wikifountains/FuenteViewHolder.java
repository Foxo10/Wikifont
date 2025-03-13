package com.example.wikifountains;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FuenteViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageViewFuente;
    public TextView textViewNombre;
    public TextView textViewUbicacion;
    public TextView textViewDescripcion;

    public FuenteViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewFuente = itemView.findViewById(R.id.imageViewFuente);
        textViewNombre = itemView.findViewById(R.id.textViewNombre);
        textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
        textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
    }
}
