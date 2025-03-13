package com.example.wikifountains;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class FuenteAdapter extends RecyclerView.Adapter<FuenteAdapter.FuenteViewHolder> {
    private List<Fuente> fuentes;

    public FuenteAdapter(List<Fuente> fuentes) {
        this.fuentes = fuentes;
    }

    @NonNull
    @Override
    public FuenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fuente, parent, false);
        return new FuenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FuenteViewHolder holder, int position) {
        Fuente fuente = fuentes.get(position);
        holder.textViewNombre.setText(fuente.getNombre());
        holder.textViewUbicacion.setText(fuente.getUbicacion());
    }

    @Override
    public int getItemCount() {
        return fuentes.size();
    }

    public static class FuenteViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNombre;
        public TextView textViewUbicacion;

        public FuenteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
        }
    }
}
