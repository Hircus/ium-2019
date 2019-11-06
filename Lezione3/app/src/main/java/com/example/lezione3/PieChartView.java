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
    private int backgroundColor = Color.WHITE;
    private List<Float> percent;//lista per le fette della torta
    private List<Integer> segmentColor; //lista per i colori di ogni fetta
    private RectF enclosing=new RectF();
    private PointF center = new PointF();
    private int radius=100;
    private int strokeColor;
    private int strokeWidth;
    private int selectedIndex=2; //indice della fetta selezionata
    private float selecetedStartAngle=0.0f; //variabile di appoggio per il disegno
    private PointF previousTouch=new PointF(0,0);
    private int selectedColor;
    private int selectedWidth = 8;
    private float selectedStartAngle = 0.0f; //variabile d'appoggio per il disegno
    private float zoom = 1.0f;
    private PointF translate = new PointF(-200,-300);
    private boolean multitouch = false;
    private double oldDistance = 0.0;


    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getBackgroundColor());
        Rect rect = new Rect();
        rect.left=0;
        rect.top=0;
        rect.right=getWidth();
        rect.bottom=getHeight();
        canvas.drawRect(rect,paint);

        canvas.save();
        canvas.scale(this.getZoom(), this.getZoom());
        canvas.translate(getTranslate().x, getTranslate().y);
        float p;//definisce la singola fetta in esame
        int c; //colore della singola fetta

        center.x= canvas.getWidth()/2;
        center.y= canvas.getHeight()/2;

        enclosing.top=center.y-radius;
        enclosing.bottom=center.y+radius;
        enclosing.left=center.x-radius;
        enclosing.right=center.x+radius;

        float alpha=-90.0f;
        float p2a=360.0f/100.0f;
        for(int i=0; i<percent.size(); i++){
            p=percent.get(i); //percentuale da rappresentare
            c=segmentColor.get(i); //colore della fetta da rappresentare
            paint.setColor(c);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawArc(enclosing, alpha, p*p2a,true, paint);
        alpha+=p*p2a;
        }
        alpha=-90.0f;

        for (int i=0; i<percent.size(); i++){
            p=percent.get(i); //percentuale da rappresentare
            //c = segmentColor.get(i);
            paint.setColor(getStrokeColor());
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            if(i==selectedIndex){
                selectedStartAngle=alpha;
            }
            canvas.drawArc(enclosing, alpha, p*p2a,true, paint);
            alpha+=p*p2a;
        }


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
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tx = event.getX();//coordinate del tocco
        float ty = event.getY();
        float x = (tx / getZoom()) - getTranslate().x;
        float y = (ty / getZoom()) - getTranslate().y;

        switch (event.getAction()){//tipo di azione dell'evento
            case MotionEvent.ACTION_DOWN:  //l'utente sta premendo sullo schermo
                if(event.getPointerCount()==1){
                    selectedIndex = this.pickCorrelation(x,y); //salvo l'indice della fetta selezionata

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

    private int pickCorrelation(float x, float y){
        if(enclosing.contains(x, y)){ //controlliamo che sia dentro il rettangolo della vista
            float dx = x - center.x;
            float dy = y - center.y;
            float r = (float) Math.sqrt(dx * dx + dy * dy);

            float cos = dx/r;
            float sin = - dy/r;
            double angle = Math.toDegrees(Math.atan2(sin, cos));
            Log.d("ANGLE", "angle: " + angle + " cos " + cos + " sin " + sin);
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











    public PieChartView(Context context) {
        super(context);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<Float> getPercent() {
        return percent;
    }

    public void setPercent(List<Float> percent) {
        this.percent = percent;
    }

    public List<Integer> getSegmentColor() {
        return segmentColor;
    }

    public void setSegmentColor(List<Integer> segmentColor) {
        if(segmentColor.size() != percent.size()){
            throw new IllegalArgumentException
                    ("La lista dei colori e delle percentuali devono avere la stessa dimensione");
        }
        this.segmentColor = segmentColor;
    }

    public RectF getEnclosing() {
        return enclosing;
    }

    public void setEnclosing(RectF enclosing) {
        this.enclosing = enclosing;
    }

    public PointF getCenter() {
        return center;
    }

    public void setCenter(PointF center) {
        this.center = center;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public float getSelecetedStartAngle() {
        return selecetedStartAngle;
    }

    public void setSelecetedStartAngle(float selecetedStartAngle) {
        this.selecetedStartAngle = selecetedStartAngle;
    }

    public PointF getPreviousTouch() {
        return previousTouch;
    }

    public void setPreviousTouch(PointF previousTouch) {
        this.previousTouch = previousTouch;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public int getSelectedWidth() {
        return selectedWidth;
    }

    public void setSelectedWidth(int selectedWidth) {
        this.selectedWidth = selectedWidth;
    }

    public float getSelectedStartAngle() {
        return selectedStartAngle;
    }

    public void setSelectedStartAngle(float selectedStartAngle) {
        this.selectedStartAngle = selectedStartAngle;
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

    public boolean isMultitouch() {
        return multitouch;
    }

    public void setMultitouch(boolean multitouch) {
        this.multitouch = multitouch;
    }

    public double getOldDistance() {
        return oldDistance;
    }

    public void setOldDistance(double oldDistance) {
        this.oldDistance = oldDistance;
    }
}
