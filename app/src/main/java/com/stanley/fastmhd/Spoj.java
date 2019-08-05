package com.stanley.fastmhd;

import java.util.ArrayList;

class Spoj {

    ZastavkyData vychodzia;
    ZastavkyData konecna;
    ZastavkyData prestup;
    ArrayList<String> linky = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();

    int value = 0;
    ArrayList<CasSpoja> casSpoja = new ArrayList<>();
    boolean priamySpoj;

    Spoj(ZastavkyData vychodzia, ZastavkyData konecna, boolean priamySpoj) {
        this.vychodzia = vychodzia;
        this.konecna = konecna;
        this.priamySpoj = priamySpoj;
    }

}
