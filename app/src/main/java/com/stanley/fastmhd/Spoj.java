package com.stanley.fastmhd;

import java.util.ArrayList;

class Spoj {

    ZastavkyData vychodzia;
    ZastavkyData konecna;
	ArrayList<String> linky = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<ZastavkyData> prestupy = new ArrayList<>();
	int value = 0;
	ArrayList<CasLinky> casLinky = new ArrayList<>();
	boolean priamySpoj;

	Spoj (ZastavkyData vychodzia, ZastavkyData konecna, boolean priamySpoj)
    {
        this.vychodzia = vychodzia;
        this.konecna = konecna;
        this.priamySpoj = priamySpoj;
    }


}
