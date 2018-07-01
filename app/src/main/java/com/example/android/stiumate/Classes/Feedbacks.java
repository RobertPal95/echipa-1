package com.example.android.stiumate.Classes;

import java.util.Calendar;

public class Feedbacks {
    private String nume, parere, poza;
    private Long data;

    public Feedbacks(String nume, String parere, String poza, Long data) {
        this.nume = nume;
        this.parere = parere;
        this.poza = poza;
        this.data = data;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getParere() {
        return parere;
    }

    public void setParere(String parere) { this.parere = parere; }

    public String getPoza() { return poza; }

    public void setPoza(String poza) { this.poza = poza; }

    public Long getData() { return data; }

    public void setData(Long data) { this.data = data; }
}

