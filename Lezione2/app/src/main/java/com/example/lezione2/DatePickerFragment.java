package com.example.lezione2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    // ricapitolando, mi servirà:
    //- un elemento Calendar per il calendario
    // un elemento datepicker
    //- un Dialog
    //- un listener personalizzato per la mia classe
    private Calendar date;

    //creo il listener (vedi metodi interfaccia)
    private DatePickerFragmentListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreateDialog(savedInstanceState);//uso il metodo di Dialog

        //SE è null gli metto valori di default
        if(date==null){
            date = Calendar.getInstance();
            date.set(Calendar.YEAR, 1995);
            date.set(Calendar.MONTH, Calendar.JANUARY);
            date.set(Calendar.DAY_OF_MONTH, 1);
        }

        final DatePicker datePicker = new DatePicker(getActivity()); //come costruttore prende il contesto
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //è il dialog (la finestra vuota) su cui
        // mettere il datepicker, contiene anche i bottoni

        builder.setView(datePicker); //qua settiamo il datepicker all'interno della finestra vuota


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //quando premiamo ok stiamo assegnando a date
                date.set(Calendar.YEAR, datePicker.getYear()); //mese, giorno e anno selezionato
                date.set(Calendar.MONTH, datePicker.getMonth());
                date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                if(listener!=null){
                    listener.onDatePickerFragmentOkButton(DatePickerFragment.this, date); //genera l'evento che aggiorna i valori
                    //della data nella text view
                }
            }
        });

        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.onDatePickerFragmentCancelButton(DatePickerFragment.this);//genera l'evento che aggiorna i valori
                            //della data nella text view
                        }
                    }
                }
        );

        //creo tramite il builder il dialog
        return builder.create();
    }
    //interazione con il picker
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setOnDatePickerFragmentChanged(DatePickerFragmentListener l){
        this.listener = l; //questa funzione viene chiamata nella main activity per inizializzare l'attributo listener
        //nella classe DatePickerFragment
    }
    //stiamo creando noi degli handle, interfaccia con i metodi che gestiranno gli eventi
    public interface DatePickerFragmentListener{
        public void onDatePickerFragmentOkButton( DialogFragment dialog, Calendar date );
        public void onDatePickerFragmentCancelButton( DialogFragment dialog );
    }
}

