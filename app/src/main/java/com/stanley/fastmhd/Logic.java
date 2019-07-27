package com.stanley.fastmhd;

import java.util.ArrayList;
import java.util.Vector;

/*  TODO:
 *  - Zistit cas spoja z url v zastavkydata.url
 *  - Zistit kedy sa prestupuje podla zastavkydata.poradielinky
 *  - Ohodnotit spoj podla casovej zlozitosti
 *
 */



class Logic {

    private static InputZastavky input;
    static ArrayList<Spoj> spoje;


    static void execute(InputZastavky inputParam) {
        input = inputParam;
        najdiSpoj();
        ohodnotSpoje();
        zoradSpoje();
    }

    private static void najdiSpoj () {

        ArrayList<Spoj> mozneSpoje = new ArrayList<>();
        ZastavkyData vychodzia = Scraper.zastavky[input.vychodziaIndex];
        ZastavkyData konecna = Scraper.zastavky[input.konecnaIndex];

        //porovna linky vo vychodzej aj konecnej zastavke (toto je zistenie priameho spoja)
        for (int i = 0; i < vychodzia.linky.size(); i++) {
            for (int j = 0; j < konecna.linky.size(); j++) {

                //ak spoj navstevuje obe zastavky a zaroven id vychodzej
                //zastavky v spoji je mensie ako konecnej (ak vychodziu navstivi skor)

                if (vychodzia.linky.get(i).equals(konecna.linky.get(j))
                        && vychodzia.poradieZastavky.get(i) < konecna.poradieZastavky.get(j))
                {
                    //vytvorime novy objekt Spoj, ktory obsahuje info o danom spoji
                    Spoj temp = new Spoj(vychodzia, konecna, true);
                    temp.linky.add(vychodzia.linky.get(i));
                    temp.url.add(vychodzia.url.get(i));
                    mozneSpoje.add(temp);
                }
            }
        }


        //mega neefektivny algoritmus na vyhladanie liniek s prestupom - neskor zmenit ak bude cas
        for (int i = 0; i < vychodzia.linky.size(); i++) {
            String vLinka = vychodzia.linky.get(i);
            int vPoradie = vychodzia.poradieZastavky.get(i);

            for (int j = 0; j < konecna.linky.size(); j++) {
                String kLinka = konecna.linky.get(j);
                int kPoradie = konecna.poradieZastavky.get(j);
                for (int k = 0; k < Scraper.zastavky.length; k++) {

                    if (Scraper.zastavky[k] != null) {

                            if (Scraper.zastavky[k].linky.contains(vLinka)
                                    && Scraper.zastavky[k].linky.contains(kLinka))
                                {
                                    int vIndex = Scraper.zastavky[k].linky.indexOf(vLinka);
                                    int kIndex = Scraper.zastavky[k].linky.indexOf(kLinka);

                                    if (Scraper.zastavky[k].poradieZastavky.get(vIndex) > vPoradie
                                            && Scraper.zastavky[k].poradieZastavky.get(kIndex)
                                            < kPoradie)
                                    {
                                        Spoj temp = new Spoj(vychodzia, konecna, false);
                                        ZastavkyData prestup = Scraper.zastavky[k];
                                        temp.linky.add(vLinka);
                                        temp.linky.add(kLinka);
                                        temp.prestupy.add(Scraper.zastavky[k]);
                                        temp.url.add(vychodzia.url.get(vychodzia.linky
                                                .indexOf(vLinka)));
                                        temp.url.add(prestup.url.get(prestup.linky
                                                .indexOf(kLinka)));
                                        mozneSpoje.add(temp);
                                    }
                            }
                    }
                }
            }
        }

        spoje = mozneSpoje;
    }


    //metoda ohodnoti spoje podla vyuziteho casu
    private static void ohodnotSpoje ()
    {
        for (Spoj aktualnySpoj : spoje)
        {

            for (int i = 0; i < aktualnySpoj.linky.size(); i++)
            {
                if (aktualnySpoj.casLinky.get(i) != null)
                {
                    String vychodzia;
                    String konecna;
                    if (i >= aktualnySpoj.prestupy.size())
                    {
                        if (aktualnySpoj.prestupy.size() != 0)
                        {
                            vychodzia = aktualnySpoj.prestupy.get(i - 1).meno;
                        }
                        else
                        {
                            vychodzia = aktualnySpoj.vychodzia.meno;
                        }
                        konecna = aktualnySpoj.konecna.meno;
                    }
                    else
                    {
                        if (i > 0)
                        {
                            vychodzia = aktualnySpoj.prestupy.get(i).meno;
                        }
                        else
                        {
                            vychodzia = aktualnySpoj.vychodzia.meno;
                        }
                        konecna = aktualnySpoj.prestupy.get(i).meno;
                    }
                    System.out.println(vychodzia + " --------- " + konecna);

                    aktualnySpoj.casLinky.add(Scraper.scrapeCas(aktualnySpoj.url.get(i),
                            vychodzia, konecna, aktualnySpoj.linky.get(i).substring(4)));


                    aktualnySpoj.value += aktualnySpoj.casLinky.get(i).casVSpoji;
                }

            }

        }
    }

    private static void zoradSpoje ()
    {
        if (spoje.size() == 0)
        {
            System.out.println("Ziadne spoje nenajdene");
            return;
        }

        ArrayList<Spoj> left = new ArrayList<>();
        ArrayList<Spoj> right = new ArrayList<>();
        Spoj pivot = spoje.get(0);

        for (int i = 1; i < spoje.size(); i++)
        {
            if (spoje.get(i).value < pivot.value)
            {
                left.add(spoje.get(i));
            }
            else
            {
                right.add(spoje.get(i));
            }
        }

        spoje = merge(left, pivot, right);
    }

    private static ArrayList<Spoj> merge (ArrayList<Spoj> s1, Spoj pivot, ArrayList<Spoj> s2)
    {
        ArrayList<Spoj> merge = new ArrayList<>(s1);
        merge.add(pivot);
        merge.addAll(s2);
        return merge;
    }

}
