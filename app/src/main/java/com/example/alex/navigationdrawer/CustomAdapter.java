package com.example.alex.navigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alex on 04/08/2018.
 */

public class CustomAdapter extends ArrayAdapter <Opciones> {
    public CustomAdapter(Context context, ArrayList<Opciones> opciones) {
        super(context, 0, opciones);
    }
    // Esta clase surge como motivo de optimizar recursos. Cuando se hace scroll en una lista, en
    // vez de destruir sus elementos se pueden guargar y obtener tanto sus atributos como su ID, de
    // manera que no hace falta llamar constantemente al método findViewById() ni inflar las vistas.

    // La clase se llama ViewHolder, y tiene dos atributos: el txtview y la imgview. Cuando obtengo
    // una vista, compruebo si esta es nula. Si lo es, signigica que nunca antes se ha creado, así
    // que info lo vista y creo que una instancia de ViewHolder, asignándole a sus atributos las ID's
    // de las vistas. Así, solo llamo una única vez a la función getViewById(), guardando su resultado
    // en el ViewHolder. Por último, con setTag() guardo la vista en el atributo converView del método getView.
    // Si no era nula, significa que ya se había creado antes, por lo que tengo guardado tanto su ID
    // como la vista en sí, así que los recupero.
    private static class ViewHolder {
        TextView nombre_opcion;
        ImageView icono;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Opciones opcion = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_opciones, parent, false);

            // Lookup view for data populatio
            viewHolder.icono = convertView.findViewById(R.id.icono_opcion);
            viewHolder.nombre_opcion = convertView.findViewById(R.id.nombre_opcion);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.icono.setImageResource(opcion.getIcono());
        viewHolder.nombre_opcion.setText(opcion.getNombre_opcion());

        // Return the completed view to render on screen
        return convertView;
    }


}
