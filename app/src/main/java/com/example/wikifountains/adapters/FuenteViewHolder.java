package com.example.wikifountains.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wikifountains.R;

public class FuenteViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewNombre;
    public TextView textViewLocalidad;
    public TextView textViewCalle;
    public ImageView imageViewEdit;
    public ImageView imageViewGuardarNotificacion;
    public ImageView imageViewMap;
    public FuenteViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewNombre = itemView.findViewById(R.id.textViewNombre);
        textViewLocalidad = itemView.findViewById(R.id.textViewLocalidad);
        textViewCalle = itemView.findViewById(R.id.textViewCalle);
        imageViewEdit = itemView.findViewById(R.id.imageViewEdit);
        imageViewGuardarNotificacion = itemView.findViewById(R.id.imageViewNotify);
        imageViewMap = itemView.findViewById(R.id.imageViewMap);
    }
}