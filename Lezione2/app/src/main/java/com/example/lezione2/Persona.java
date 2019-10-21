package com.example.lezione2;

import java.io.Serializable;

public class Persona implements Serializable {
    // nota: sono gli stessi dati che chiediamo nel form
    private String nome, cognome, data;

    public Persona(String nome, String cognome, String data){
        this.setNome(nome);
        this.setCognome(cognome);
        this.setData(data);
    }

    public Persona(){
        this.setNome("");
        this.setCognome("");
        this.setData("");
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

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data=data;
    }

}
