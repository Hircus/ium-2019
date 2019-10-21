package com.example.lezione2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FormActivity extends AppCompatActivity {

    EditText nome, cognome, data;
    Button inserisci;
    TextView errorText;
    Persona person;
    // nella vostra applicazione sarà qualcosa del tipo
    //"com.example.il_vostro_nome_utente.nome_progetto.Persona"
    public static final String PERSON_EXTRA="package com.example.lezione2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_activity);
        // creo un nuovo oggetto Persona
        person = new Persona();
        // recupero gli id dei vari campi
        nome = findViewById(R.id.inputNome);
        cognome = findViewById(R.id.inputCognome);
        data = findViewById(R.id.inputData);
        inserisci = findViewById(R.id.inserisciButton);
        errorText = findViewById(R.id.errorText);

        // di default la visibilità dell'error text è gone
        errorText.setVisibility(View.GONE);

        inserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // aggiorno il contenuto di persona
                UpdatePerson();
                // crea l'oggetto di tipo Intent, ci serve per far comunicare le due activity
                Intent showResult = new Intent(FormActivity.this, ResultActivity.class);
                // inserisci l'oggetto persona dentro l'Intent
                showResult.putExtra(PERSON_EXTRA, person);
                // richiama l'activity ResultActivity
                startActivity(showResult);
            }
        });
    }

    private void UpdatePerson(){
        // aggiorna il contenuto di persona usando i dati inseriti dall'utente
        this.person.setNome(this.nome.getText().toString());
        this.person.setCognome(this.cognome.getText().toString());
        this.person.setData(this.data.getText().toString());
    }
}
