package com.example.android.stiumate.Classes;

public class Medalii {
    private String nume, reusita, piatra;

    public Medalii(String nume, String reusita, String piatra) {
        this.nume = nume;
        this.reusita = reusita;
        this.piatra = piatra;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getReusita() {
        return reusita;
    }

    public void setReusita(String reusita) {
        this.reusita = reusita;
    }

    public String getPiatra() {
        return piatra;
    }

    public void setPiatra(String piatra) {
        this.piatra = piatra;
    }
}
