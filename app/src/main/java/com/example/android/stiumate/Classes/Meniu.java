package com.example.android.stiumate.Classes;

public class Meniu {
    private String intrebare, indiciu1, indiciu2, raspuns;

    public Meniu(String intrebare, String indiciu1, String indiciu2, String raspuns) {
        this.intrebare = intrebare;
        this.indiciu1 = indiciu1;
        this.indiciu2 = indiciu2;
        this.raspuns = raspuns;
    }

    public String getIntrebare() {
        return intrebare;
    }

    public void setIntrebare(String intrebare) {
        this.intrebare = intrebare;
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
