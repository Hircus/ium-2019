package com.example.lezione2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Persona implements Serializable {
    // nota: sono gli stessi dati che chiediamo nel form
    private String nome, cognome;
    private Calendar data;

    public Persona(String nome, String cognome, String data){
        this.setNome(nome);
        this.setCognome(cognome);
        //this.setData(data);
    }

    public Persona(){
        this.setNome("");
        this.setCognome("");
        //this.setData("");
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome=nome;
    }

    public String getCognome(){
        return cognome;
    }

    public void setCognome(String cognome){
        this.cognome=cognome;
    }

    public void setBirthDate(Calendar birthDate) {
        this.data = birthDate;
    }

    public Calendar getData(){
        return data;
    }
    /*public String getData(){
        return data;
    }

    public void setData(String data){
        this.data=data;
    }*/

}
