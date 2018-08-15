package com.example.alex.navigationdrawer;

import java.util.ArrayList;

/**
 * Created by alex on 04/08/2018.
 */

public class Opciones {
    Integer icono;
    String nombre_opcion;

    public Opciones(Integer icono, String nombre_opcion) {
        this.icono = icono;
        this.nombre_opcion = nombre_opcion;
    }

    public Integer getIcono() {
        return icono;
    }

    public void setIcono(Integer icono) {
        this.icono = icono;
    }

    public String getNombre_opcion() {
        return nombre_opcion;
    }

    public void setNombre_opcion(String nombre_opcion) {
        this.nombre_opcion = nombre_opcion;
    }
}
