package com.example.lezione3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class PieChartView extends View {
    /**
     * Centro del grafico, ricalcolato a ogni disegno
     */
    private PointF center = new PointF();
    /**
     * Rettangolo che contiene il grafico a torta
     */
    private RectF enclosing = new RectF();
    /**
     * Variabile di appoggio per il disegno dell'elemento selezionato
     */
    private float selectedStartAngle = 0.0f;

    /**
     * Colore di sfondo del controllo
     */
    private int backgroundColor = Color.WHITE;
    /**
     * Lista delle percentuali (float) da disegnare come fette della torta
     */
    private List<Float> percent;
    /**
     * Lista dei colori per ogni fetta della torta
     */
    private List<Integer> segmentColor;
    /**
     * Colore del bordo delle fette non selezionate
     */
    private int strokeColor;
    /**
     * Spessore del bordo delle fette non selezionate
     */
    private int strokeWidth;
    /**
     * Colore del bordo della fetta selezionata
     */
    private int selectedColor;
    /**
     * Spessore del bordo della fetta selezionata
     */
    private int selectedWidth = 8;
    /**
     * Indice della fetta selezionata nella lista delle percentuali
     */
    private int selectedIndex = 2;
    /**
     * Raggio della torta
     */
    private int radius = 100;

    /**
     * Il fattore di scala
     */
    private float zoom = 1.0f;

    /**
     * il punto in alto a sinistra del viewport rispetto al sistema di riferimento del controllo
     */
    private PointF translate = new PointF(-200,-300);

    /**
     * Posizione precedente del tocco per implementare il pan della vista
     */
    private PointF previousTouch = new PointF(0,0);

    /**
     * vero se sto eseguendo un'interazione multitouch, falso altrimenti
     */
    private boolean multitouch = false;

    /**
     * distanza fra due tocchi durante l'interazione multitouch
     */
    private double oldDistance = 0.0;


    /**
     * Procedura di disegno del controllo
     * @param canvas Il contesto grafico su cui disegnare
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // fase 1- disegno lo sfondo
        // utilizziamo questo oggetto per definire colori, font, tipi di linee ecc
        Paint paint = new Paint();
        // impostiamo l'antialiasing
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // cancelliamo lo sfondo (altrimenti è nero di default)
        paint.setColor(getBackgroundColor());
        Rect rect = new Rect();
        rect.left=0;
        rect.top=0;
        rect.right=getWidth();
        rect.bottom=getHeight();
        canvas.drawRect(rect,paint);

        // salvo le trasformazioni correnti
        canvas.save();
        // fase 2 - disegno la torta
        // applico la scalatura, usiamo un fattore omogeneo (uguale su x e y)
        canvas.scale(this.getZoom(), this.getZoom());
        // applico la traslazione del punto di vista
        canvas.translate(getTranslate().x, getTranslate().y);

        float p;//definisce la singola fetta in esame
        int c; //colore della singola fetta

        // calcolo il centro del cerchio e le dimensioni del quadrato in cui inscriverlo
        center.x= canvas.getWidth()/2;
        center.y= canvas.getHeight()/2;

        enclosing.top=center.y-radius;
        enclosing.bottom=center.y+radius;
        enclosing.left=center.x-radius;
        enclosing.right=center.x+radius;

        float alpha=-90.0f;  // angolo dal quale si inizia a disegnare
        float p2a=360.0f/100.0f;  // 1% in radianti

        // disegno la parte colorata (il fill)
        for(int i=0; i<percent.size(); i++){
            p=percent.get(i); //percentuale da rappresentare
            c=segmentColor.get(i); //colore della fetta da rappresentare
            paint.setColor(c);
            paint.setStyle(Paint.Style.FILL);
            // il disegno parte dall'angolo alpha e disegna un segmento circolare di ampiezza
            // p * p2a. Disegna in senso orario (al contrario dell'andamento degli angoli
            // nella circonferenza unitaria usuale).
            canvas.drawArc(enclosing, alpha, p*p2a,true, paint);
        alpha+=p*p2a;
        }
        alpha=-90.0f; //rimposto alpha

        // disegno il contorno (lo stroke)
        for (int i=0; i<percent.size(); i++){
            p=percent.get(i); //percentuale da rappresentare
            //c = segmentColor.get(i);
            paint.setColor(getStrokeColor());
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);

            // salvo l'angolo dell'elemento selezionato per il passo successivo
            if(i==selectedIndex){
                selectedStartAngle=alpha;
            }
            canvas.drawArc(enclosing, alpha, p*p2a,true, paint);
            alpha+=p*p2a;
        }

        // Nel caso in cui stia selezionando un elemento disegno il contorno dell'item selezionato
        if(selectedIndex >= 0 && selectedIndex < percent.size()) {
            // il valore selezionato e' valido
            paint.setColor(getSelectedColor());
            paint.setStrokeWidth(getSelectedWidth());
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(
                    enclosing,
                    getSelectedStartAngle(),
                    percent.get(selectedIndex) * p2a,
                    true,
                    paint);
        }
        // ripristino la situazione iniziale del cavas (non è strettamente necessario in
        // questo caso, ma ogni volta che si fa la save, si fa la restore
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tx = event.getX();//coordinate del tocco
        float ty = event.getY();
        // riporto le coordinate del tocco dal sistema di riferimento dello schermo
        // a quello del controllo. In pratica, applico le trasformazioni
        // (scalatura e traslazione) in ordine inverso
        float x = (tx / getZoom()) - getTranslate().x;
        float y = (ty / getZoom()) - getTranslate().y;

        switch (event.getAction()){//tipo di azione dell'evento
            case MotionEvent.ACTION_DOWN:  //l'utente sta premendo sullo schermo
                if(event.getPointerCount()==1){
                    selectedIndex = this.pickCorrelation(x,y); //salvo l'indice della fetta selezionata

                    // richiedo di aggiornare il disegno
                    this.invalidate(); //richiama la onDraw

                    this.getPreviousTouch().x =tx; //salvo le coordinate della pressione del dito dell'utente
                    this.getPreviousTouch().y = ty;

                    return true;

                }
                break;

            case MotionEvent.ACTION_MOVE: //la gesture è la seguente: l'utente sta premendo la schermata e contemporaneamente muove il dito
                switch(event.getPointerCount()) { //un dito
                    case 1:

                        if(multitouch){
                            // usciamo immediatamente se l'utente ha sollevato un dito dopo
                            // l'interazione multitouch
                            return true;
                        }
                        // recuperiamo il delta fra la posizione corrente e quella
                        // precedente. Dobbiamo dividere per il fattore di scala
                        // per avere la distanza nel sistema di riferimento
                        // originario (1.0f)
                        //NOTA: c'è differenza tra il sistema di riferimento dello schermo e quello del controllo di un certo fattore

                        float dx = (tx - this.previousTouch.x) / this.zoom;
                        float dy = (ty - this.previousTouch.y) / this.zoom;

                        this.previousTouch.x = tx; // le vecchie coordinate diventano le nuove
                        this.previousTouch.y = ty;

                        // aggiorniamo la traslazione spostandola di dx sulle x
                        // e di dy sulle y
                        this.translate.set(
                                this.translate.x + dx,
                                this.translate.y + dy
                        );
                        this.invalidate(); //aggiorno
                        return true;

                    case 2:
                        // qui gestiamo il pinch: questo case ha lo scopo di settare la variabile zoom che verrà chiamata in canvas.scale

                        // teniamo traccia del fatto che l'utente abbia iniziato un pinch
                        // (vedi sopra)
                        multitouch = true;

                        // recuperiamo la posizione corrente del tocco 1 e del tocco 2, creo due elementi di tipo PointerCoords
                        MotionEvent.PointerCoords touch1 = new MotionEvent.PointerCoords();
                        MotionEvent.PointerCoords touch2 = new MotionEvent.PointerCoords();

                        event.getPointerCoords(0, touch1); //prende le coordinate di touch1, 0 è l'indice dato da getPointerCount
                        event.getPointerCoords(1, touch2);

                        // calcoliamo la distanza corrente tra i due touch
                        double distance = Math.sqrt(
                                Math.pow(touch2.x - touch1.x, 2) +
                                        Math.pow(touch2.y - touch1.y, 2));

                        // confrontiamo con la precedente (oldDistance inizialmente è 0.0)
                        if (distance - oldDistance > 0) {
                            // ingrandisco la vista
                            zoom += 0.03;
                            this.invalidate();
                        }

                        if (distance - oldDistance < 0) {
                            // rimpicciolisco la vista
                            zoom -= 0.03;
                            this.invalidate();
                        }

                        oldDistance = distance;

                        return true;


                }

            case MotionEvent.ACTION_UP:
                // reset delle variabili di stato in modo tale da averle per la successiva interazione
                this.previousTouch.x = 0.0f;
                this.previousTouch.y = 0.0f;
                multitouch = false;
                oldDistance = 0.0f;
                return true;

        }
        return false;
    }
    /**
     * Restituisce l'indice della fetta di torta che contiene il punto di coordinate (x,y)
     * @param x L'ascissa del punto
     * @param y L'ordinata del punto
     * @return l'indice della fetta di torta
     */
    private int pickCorrelation(float x, float y){
        if(enclosing.contains(x, y)){ //controlliamo che sia dentro il rettangolo della vista
            // sottraggo alla x e alla y le coordinate del centro
            float dx = x - center.x;
            float dy = y - center.y;
            // ottengo la distanza dal centro (formula euclidea, è come scrivere sqrt(x-center.x)^2+(y-center.y)^2
            float r = (float) Math.sqrt(dx * dx + dy * dy);

            float cos = dx/r;
            float sin = - dy/r;
            // l'angolo varia tra -180 e 180
            double angle = Math.toDegrees(Math.atan2(sin, cos));
            Log.d("ANGLE", "angle: " + angle + " cos " + cos + " sin " + sin);
            // faccio in modo che l'angolo vari fra 90 e -270. Spazzando gli angoli
            // in senso orario, i valori degli angoli sono decrescenti, cosa che ci torna
            // utile per il for. Considerando per esempio i punti in cui la circonferenza
            // goniometrica incontra gli assi a partire da 90 gradi, la successione di
            // angoli è la seguente:
            // 90, 0, -90, -180, -270
            if(angle > 90 && angle < 360){
                angle = angle - 360; //stesso angolo ma in senso orario, android usa il sistema orario
            }

            float alpha = 90.0f; //qua stiamo usando il sistema di riferimento, la ricerca parte da 90
            float alpha1;
            float p2a = 360.0f / 100.0f;
            float p;
            for(int i = 0; i<percent.size(); i++){ //cicliamo sulla lista delle percentuali da disegnare
                p = percent.get(i); //grado della fetta che stiamo esaminando

                alpha1 =  alpha - p * p2a;//contiene l'estremo finale calcolato tramite p
                //alpha contiene l'estremo di partenza
                if(angle > alpha1 && angle < alpha){ //al primo ciclo alpha e alpha 1 assumeranno gli estremi della prima fetta e così via
                    return i;
                }
                alpha = alpha1; //alpha partirà dalla fine della fetta appena esaminata e alpha1 verrà ricalcolata

            }

        }else{
            return -1; //il tocco è fuori dal container, se clicco fuori infatti non seleziona nessuna fetta
        }
        return 1;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public PointF getTranslate() {
        return translate;
    }

    public void setTranslate(PointF translate) {
        this.translate = translate;
    }











    /**
     * Costruttore del controllo
     * @param context Il contesto grafico su cui disegnare
     */
    public PieChartView(Context context) {
        super(context);
    }

    /**
     * Costrutture del controllo
     * @param context Il contesto grafico su cui disegnare
     * @param attrs Gli attributi ricevuti dall'activity
     */
    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Costrutture del controllo
     * @param context Il contesto grafico su cui disegnare
     * @param attrs Gli attributi ricevuti dall'activity
     * @param defStyle Stili di disegno di default
     */
    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Restituisce il colore di sfondo
     * @return Il colore di sfondo
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Imposta il colore di sfondo
     * @param backgroundColor Il colore di sfondo
     */
    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Restituisce la lista delle percentuali da disegnare nel grafico,
     * @return La lista delle percentuali
     */
    public List<Float> getPercent() {
        return percent;
    }

    /**
     * Imposta la lista delle percentuali da disegnare nel grafico
     * @param percent La lista delle percentuali
     */
    public void setPercent(List<Float> percent) {
        this.percent = percent;
    }

    /**
     * Restituisce la lista dei colori con cui si disegnano le fette. L'indice dell'i-esimo colore
     * corrisponde alla i-esima percentuale nella lista delle percentuali.
     * @return
     */
    public List<Integer> getSegmentColor() {
        return segmentColor;
    }

    /**
     * Imposta la lista dei colori con cui si disegnano le fette. La lista deve avere
     * la stessa dimensione di quella delle percentuali
     * @param segmentColor la lista dei colori
     * @throws IllegalArgumentException se la lista dei colori e delle percentuali hanno dimensione diversa
     */
    public void setSegmentColor(List<Integer> segmentColor) {
        if(segmentColor.size() != percent.size()){
            throw  new IllegalArgumentException(
                    "La lista dei colori e delle percentuali devono avere la stessa dimensione");
        }
        this.segmentColor = segmentColor;
    }

    /**
     * Restituisce il raggio della torta
     * @return Il raggio della torta
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Imposta il raggio della torta
     * @param radius Il raggio della torta
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Restituisce il colore del bordo delle fette
     * @return il colore del bordo delle fette
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * Imposta il colore del bordo delle fette
     * @param strokeColor il colore del bordo delle fette
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * Restituisce la dimensione del bordo delle fette
     * @return La dimensione del bordo delle fette
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * Imposta la dimensione del bordo delle fette
     * @param strokeWidth la dimensione del bordo delle fette
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Restituisce il colore del bordo selezionato
     * @return Il colore del bordo selezionato
     */
    public int getSelectedColor() {
        return selectedColor;
    }

    /**
     * Imposta il colore del bordo selezionato
     * @param selectedColor Il colore del bordo selezionato
     */
    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * Restituisce la dimensione del bordo dell'elemento selezionato
     * @return la dimensione del bordo dell'elemento selezionato
     */
    public int getSelectedWidth() {
        return selectedWidth;
    }

    /**
     * Imposta la dimensione del bordo dell'elemento selezionato
     * @param selectedWidth la dimensione del bordo dell'elemento selezionato
     */
    public void setSelectedWidth(int selectedWidth) {
        this.selectedWidth = selectedWidth;
    }
}
