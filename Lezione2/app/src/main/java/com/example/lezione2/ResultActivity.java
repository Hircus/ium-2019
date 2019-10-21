package com.example.lezione2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.Serializable;

public class ResultActivity extends AppCompatActivity {
    Persona person;
    TextView nomeText, cognomeText, dataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        nomeText = findViewById(R.id.attrNome);
        cognomeText =findViewById(R.id.attrCognome);
        dataText = findViewById(R.id.attrData);

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
        dataText.setText(person.getData());
    }
}
