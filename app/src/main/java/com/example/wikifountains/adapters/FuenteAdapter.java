package com.example.wikifountains.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wikifountains.EliminarFuenteDialog;
import com.example.wikifountains.data.Fuente;
import com.example.wikifountains.R;
import com.example.wikifountains.activities.EditFuenteActivity;

import java.util.List;
public class FuenteAdapter extends RecyclerView.Adapter<FuenteViewHolder> {
    private List<Fuente> fuentes;
    private EliminarFuenteDialog.EliminarFuenteListener listener;
    private OnGuardarNotificacionClickListener guardarNotificacionClickListener;

    // Constructor simplificado: solo recibe la lista de fuentes
    public FuenteAdapter(List<Fuente> fuentes) {
        this.fuentes = fuentes;
    }
    public interface OnGuardarNotificacionClickListener {
        void onGuardarNotificacionClick(Fuente fuente);
    }

    public void setEliminarFuenteListener(EliminarFuenteDialog.EliminarFuenteListener listener) {
        this.listener = listener;
    }
    public void setOnGuardarNotificacionClickListener(OnGuardarNotificacionClickListener listener) {
        this.guardarNotificacionClickListener = listener;
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

        if (holder.imageViewMap != null) {
            // Diseño horizontal: mostrar coordenadas y descripción
            String coords = fuente.getLatitud() + ", " + fuente.getLongitud();
            holder.textViewLocalidad.setText(coords);
            holder.textViewCalle.setText(fuente.getDescripcion());

            holder.imageViewMap.setOnClickListener(v -> {
                String uri = "geo:" + fuente.getLatitud() + "," + fuente.getLongitud() +
                        "?q=" + fuente.getLatitud() + "," + fuente.getLongitud() +
                        "(" + Uri.encode(fuente.getNombre()) + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                v.getContext().startActivity(intent);
            });
        } else {
            // Diseño vertical: nombre, localidad y calle con acciones
            holder.textViewLocalidad.setText(fuente.getLocalidad());
            holder.textViewCalle.setText(fuente.getCalle());

            holder.imageViewEdit.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditFuenteActivity.class);
                intent.putExtra("fuente", fuente);
                v.getContext().startActivity(intent);
            });

            holder.imageViewGuardarNotificacion.setOnClickListener(v -> {
                if (guardarNotificacionClickListener != null) {
                    guardarNotificacionClickListener.onGuardarNotificacionClick(fuente);
                }
            });
        }

        // Listener para eliminar una fuente (al mantener presionado el elemento)
        holder.itemView.setOnLongClickListener(v -> {
            EliminarFuenteDialog dialog = new EliminarFuenteDialog(fuente);
            dialog.setEliminarFuenteListener(listener);
            dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "EliminarFuenteDialog");
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return fuentes.size();
    }


}