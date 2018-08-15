package com.example.alex.navigationdrawer;

/**
 * Created by alex on 15/08/2018.
 */

public class Consejo {
    String id;
    String titulo_consejo;
    String texto_consejo;

    public Consejo(String id, String titulo_consejo, String texto_consejo) {
        this.id = id;
        this.titulo_consejo = titulo_consejo;
        this.texto_consejo = texto_consejo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo_consejo() {
        return titulo_consejo;
    }

    public void setTitulo_consejo(String titulo_consejo) {
        this.titulo_consejo = titulo_consejo;
    }

    public String getTexto_consejo() {
        return texto_consejo;
    }

    public void setTexto_consejo(String texto_consejo) {
        this.texto_consejo = texto_consejo;
    }

    @Override
    public String toString() {
        return "Consejo{" +
                "id=" + id +
                ", titulo_consejo='" + titulo_consejo + '\'' +
                ", texto_consejo='" + texto_consejo + '\'' +
                '}';
    }
}
