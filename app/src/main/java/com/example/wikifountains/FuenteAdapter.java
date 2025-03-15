package com.example.wikifountains;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FuenteAdapter extends RecyclerView.Adapter<FuenteViewHolder> {
    private List<Fuente> fuentes;

    public FuenteAdapter(List<Fuente> fuentes) {
        this.fuentes = fuentes;
    }

    @NonNull
    @Override
    public FuenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de cada ítem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fuente, parent, false);
        return new FuenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FuenteViewHolder holder, int position) {
        // Obtener la fuente en la posición actual
        Fuente fuente = fuentes.get(position);

        // Asignar los datos a las vistas
        holder.textViewNombre.setText(fuente.getNombre());
        holder.textViewUbicacion.setText(fuente.getUbicacion());
        holder.textViewDescripcion.setText(fuente.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return fuentes.size();
    }
}