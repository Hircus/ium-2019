package com.example.lezione2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FormActivity extends AppCompatActivity {

    EditText nome, cognome, data;
    Button inserisci;
    TextView errorText;
    Persona person;
    // nella vostra applicazione sarà qualcosa del tipo
    //"com.example.il_vostro_nome_utente.nome_progetto.Persona"
    public static final String PERSON_EXTRA="package com.example.lezione2";
    //attributo della classe appena creata
    DatePickerFragment datePickerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_activity);
        datePickerFragment = new DatePickerFragment();

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
                //quando premo INVIO la prima cosa che controllo è che la funzione abbia restituito
                // true, quindi non ci siano errori
                if(checkInput()) {
                    // aggiorno il contenuto di persona
                    UpdatePerson();
                    // crea l'oggetto di tipo Intent, ci serve per far comunicare le due activity
                    Intent showResult = new Intent(FormActivity.this, ResultActivity.class);
                    // inserisci l'oggetto persona dentro l'Intent
                    showResult.putExtra(PERSON_EXTRA, person);
                    // richiama l'activity ResultActivity
                    startActivity(showResult);
                }
            }
        });

        //Configurazione Eventi Dialog Calendar, data è ancora l'edit text che esiste, stiamo
        // infatti settando il listener dell'edit text, vogliamo che quando si clicchi compaia il
        // fragment
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //il fragment manager è colui che dirige tutti i fragment
                datePickerFragment.show(getSupportFragmentManager(),"date picker");
            }
        });

        //questa funzione permette di controllare che l'utente non scriva nella textview
        data.setOnFocusChangeListener(new View.OnFocusChangeListener() { //funzione di view
            @Override
            public void onFocusChange(View v, boolean hasFocus) { //metodo chiamato quando lo stato della view cambia
                if(hasFocus){
                    datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        //bottoni OK e ANNULLA
        datePickerFragment.setOnDatePickerFragmentChanged(new DatePickerFragment.DatePickerFragmentListener() {
            @Override
            public void onDatePickerFragmentOkButton(DialogFragment dialog, Calendar date) {
                //Associo il comportamento del bottone OK all'edit text della data, voglio che una
                // volta selezionata quindi ho premuto ok, l'edit text mostri la data selezionata
                // tramite il datepicker
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                data.setText(format.format(date.getTime()));
            }

            @Override
            public void onDatePickerFragmentCancelButton(DialogFragment dialog) {

            }
        });
    }

    private void UpdatePerson(){
        // aggiorna il contenuto di persona usando i dati inseriti dall'utente
        this.person.setNome(this.nome.getText().toString());
        this.person.setCognome(this.cognome.getText().toString());
        this.person.setBirthDate(this.datePickerFragment.getDate());
    }

    //Controlla l'input dell'utente sui campi,
    // return true se è andato a buon fine, false altrimenti
    private boolean checkInput(){
        int errors = 0;

        if(nome.getText() == null || nome.getText().length() == 0){
            nome.setError("Inserire il nome");
            errors++;
        }
        else {
            nome.setError(null);
        }

        if(cognome.getText() == null || cognome.getText().length() == 0){
            cognome.setError("Inserire il cognome");
            errors++;
        }
        else {
            cognome.setError(null);
        }
        if(data.getText() == null || data.getText().length() == 0){
            data.setError("Inserire la data di nascita");
            errors++;
        }
        else{
            data.setError(null);
        }

        switch (errors){
            case 0:
                errorText.setVisibility(View.GONE);
                errorText.setText("");
                break;
            case 1:
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Si è verificato un Errore");
                break;
            default:
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Si sono verificati "+errors+" errori");
                break;
        }
        //se non ci sono errori ritorna true
        return errors == 0;
    }
}
