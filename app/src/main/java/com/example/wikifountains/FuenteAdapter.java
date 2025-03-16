package com.example.wikifountains;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class FuenteAdapter extends RecyclerView.Adapter<FuenteViewHolder> {
    private List<Fuente> fuentes;
    private EliminarFuenteDialog.EliminarFuenteListener listener;

    // Constructor simplificado: solo recibe la lista de fuentes
    public FuenteAdapter(List<Fuente> fuentes) {
        this.fuentes = fuentes;
    }

    // Método para asignar el listener
    public void setEliminarFuenteListener(EliminarFuenteDialog.EliminarFuenteListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FuenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fuente, parent, false);
        return new FuenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FuenteViewHolder holder, int position) {
        Fuente fuente = fuentes.get(position);
        holder.textViewNombre.setText(fuente.getNombre());
        holder.textViewLocalidad.setText(fuente.getLocalidad());
        holder.textViewCalle.setText(fuente.getCalle());

        // Listener para eliminar una fuente
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                EliminarFuenteDialog dialog = new EliminarFuenteDialog(fuente);
                dialog.setEliminarFuenteListener(listener);
                dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "EliminarFuenteDialog");
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return fuentes.size();
    }
}