package com.example.gabriel.iapp.Utils;
public class SpinnerItems_Clase {

    private String id;
    private String name;
    private String texto;


    public SpinnerItems_Clase(String id, String name, String Texto) {
        this.id = id;
        this.name = name;
        this.texto = Texto;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }


    //to display object as a string in spinner
    public String toString() {
        return name;
    }

}