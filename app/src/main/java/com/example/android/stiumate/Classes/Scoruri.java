package com.example.android.stiumate.Classes;

public class Scoruri {
    private String nume;
    private String poza;
    private Integer scorUtilizator;

    public Scoruri(String nume, String poza, Integer scorUtilizator) {
        this.nume = nume;
        this.scorUtilizator = scorUtilizator;
        this.poza = poza;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Integer getScor() {
        return scorUtilizator;
    }

    public void setScor(Integer scorUtilizator) {
        this.scorUtilizator = scorUtilizator;
    }

    public String getPoza() { return poza; }

    public void setPoza(String poza) { this.poza = poza; }
}

