package com.example.android.stiumate.Classes;

public class Lectii {
    private String nume, poza;
    private Integer scor_obtinut, scor_maxim;

    public Lectii(String nume, String poza, Integer scor_obtinut, Integer scor_maxim) {
        this.nume = nume;
        this.poza = poza;
        this.scor_obtinut = scor_obtinut;
        this.scor_maxim = scor_maxim;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPoza() {
        return poza;
    }

    public void setPoza(String poza) {
        this.poza = poza;
    }

    public Integer getScor_obtinut() {
        return scor_obtinut;
    }

    public void setScor_obtinut(Integer scor_obtinut) {
        this.scor_obtinut = scor_obtinut;
    }

    public Integer getScor_maxim() {
        return scor_maxim;
    }

    public void setScor_maxim(Integer scor_maxim) {
        this.scor_maxim = scor_maxim;
    }
}
