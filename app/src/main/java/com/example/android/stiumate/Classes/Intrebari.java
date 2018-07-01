package com.example.android.stiumate.Classes;


public class Intrebari {
    private String text_intrebare, indiciu1, indiciu2, raspuns;

    public Intrebari(String text_intrebare, String indiciu1, String indiciu2, String raspuns) {
        this.text_intrebare = text_intrebare;
        this.indiciu1 = indiciu1;
        this.indiciu2 = indiciu2;
        this.raspuns = raspuns;
    }

    public String getText_intrebare() {
        return text_intrebare;
    }

    public void setText_intrebare(String text_intrebare) {
        this.text_intrebare = text_intrebare;
    }

    public String getIndiciu1() {
        return indiciu1;
    }

    public void setIndiciu1(String indiciu1) {
        this.indiciu1 = indiciu1;
    }

    public String getIndiciu2() {
        return indiciu2;
    }

    public void setIndiciu2(String indiciu2) {
        this.indiciu2 = indiciu2;
    }

    public String getRaspuns() {
        return raspuns;
    }

    public void setRaspuns(String raspuns) {
        this.raspuns = raspuns;
    }
}
