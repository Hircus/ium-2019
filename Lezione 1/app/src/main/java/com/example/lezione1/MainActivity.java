package com.example.lezione1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // variabili di classe
    Button incremento, decremento;
    EditText input;
    SeekBar seekbar;

    // inizializzazione parametri calcolatrice
    int minValue =0;
    int maxValue =100;
    int modelValue=50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // chiamata super (sempre necessaria)
        super.onCreate(savedInstanceState);
        // caricamento elementi grafici (il file .xml a cui è associato)
        setContentView(R.layout.activity_main);

        //prende i riferimenti degli elementi grafici
        incremento = (Button) findViewById(R.id.incremento);
        decremento = (Button) findViewById(R.id.decremento);
        input = (EditText) findViewById(R.id.input);
        seekbar = (SeekBar)  findViewById(R.id.seekbar);

        //posiziona la seekbar su 50 e aggiorna i valori di partenza
        updateValue(modelValue);
        incremento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // input.setText("+1"); : parte 1 esercitazione
                updateValue(++modelValue);
            }
        });

        decremento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // input.setText("-1"); : parte 1 esercitazione
                updateValue(--modelValue);
            }
        });

        //listener della seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //avvisa l'utente ogni volta che c'è una modifica nella seekbar
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateValue(seekBar.getProgress());
            }
            //avvisa l'utente quando inizia il tocco
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //avvisa l'utente quando finisce il tocco
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateValue(seekBar.getProgress());
            }
        });
    }

    protected  void updateValue(int newValue){
        //verifico che newValue sia nel range e minore di 100
        newValue = newValue > maxValue ? maxValue : newValue;
        newValue = newValue < minValue ? minValue : newValue;

        //aggiorno il valore visualizzato della seekbar
        if(this.seekbar.getProgress() != modelValue){
            this.seekbar.setProgress(modelValue);
        }
        //aggiorno la variabile che indica il valore attuale della calcolatrice
        this.modelValue = newValue;
        input.setText(""+this.modelValue);
    }


// esempio funzionamento del circolo di vita di un'activity
   /* @Override
    protected void onStop(){
        super.onStop();

        TextView testo = (TextView) findViewById(R.id.titolomodificabile);
        testo.setText("Siamo in stato di onStop");
    }*/
}
