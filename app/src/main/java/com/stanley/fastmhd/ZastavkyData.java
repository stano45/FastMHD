package com.stanley.fastmhd;

import java.util.ArrayList;

class ZastavkyData {

    String meno;

    //zoznam cisiel liniek ktore navstevuju zastavku, v stringu (kvoli nocakom X spojom atd)
    ArrayList<String> linky = new ArrayList<>();

    //poradie zastavky, paralelne ku kazdej linke v array linky
    ArrayList<Integer> poradieZastavky = new ArrayList<>();

    //url kazdej linky paralelne k linkam v array linky
    ArrayList<String> url = new ArrayList<>();

    //class sa inicializuje minimalne s menom
    ZastavkyData(String meno) {
        this.meno = meno;
    }


}
