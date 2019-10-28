package com.example.lezione2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class ResultActivity extends AppCompatActivity {
    Persona person;
    TextView nomeText, cognomeText, dataText;
    Button confirm;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        nomeText = findViewById(R.id.attrNome);
        cognomeText =findViewById(R.id.attrCognome);
        dataText = findViewById(R.id.attrData);
        confirm=findViewById(R.id.conferma);

        // recupero l'intent mandato dal FormActivity
        Intent intent = getIntent();
        Serializable obj = intent.getSerializableExtra(FormActivity.PERSON_EXTRA);

        if(obj instanceof Persona){
            person = (Persona) obj;
        }
        // se non ho compilato i campi creo un nuovo oggetto Persona vuoto
        else {
            person = new Persona();
        }

        // associazioni tra gli elementi del file xml con gli attributi di persona
        nomeText.setText(person.getNome());
        cognomeText.setText(person.getCognome());
        dataText.setText(format.format(person.getData().getTime()));//METODO della classe Persona

        //Quando premo il bottone conferma voglio tornare indietro
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //quando clicco il bottone ok la result activity finisce e torno alla principale,
                // sarebbe buona norma pulire i campi
                finish();
            }
        });
    }
}
