package com.stanley.fastmhd;

import java.util.ArrayList;

class Logic {

    static ArrayList<Spoj> spoje;
    private static InputZastavky input;

    static void execute(InputZastavky inputParam) {
        input = inputParam;
        najdiSpoj();
        ohodnotSpoje();
        sort(spoje, 0, spoje.size() - 1);
    }

    private static void najdiSpoj() {

        ArrayList<Spoj> mozneSpoje = new ArrayList<>();
        ZastavkyData vychodzia = Scraper.zastavky[input.vychodziaIndex];
        ZastavkyData konecna = Scraper.zastavky[input.konecnaIndex];

        //porovna linky vo vychodzej aj konecnej zastavke (toto je zistenie priameho spoja)
        for (int i = 0; i < vychodzia.linky.size(); i++) {
            for (int j = 0; j < konecna.linky.size(); j++) {

                //ak spoj navstevuje obe zastavky a zaroven id vychodzej
                //zastavky v spoji je mensie ako konecnej (ak vychodziu navstivi skor)

                if (vychodzia.linky.get(i).equals(konecna.linky.get(j))
                        && vychodzia.poradieZastavky.get(i) < konecna.poradieZastavky.get(j)) {
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
                                && Scraper.zastavky[k].linky.contains(kLinka)) {
                            int vIndex = Scraper.zastavky[k].linky.indexOf(vLinka);
                            int kIndex = Scraper.zastavky[k].linky.indexOf(kLinka);

                            if (Scraper.zastavky[k].poradieZastavky.get(vIndex) > vPoradie
                                    && Scraper.zastavky[k].poradieZastavky.get(kIndex)
                                    < kPoradie) {
                                Spoj temp = new Spoj(vychodzia, konecna, false);
                                ZastavkyData prestup = Scraper.zastavky[k];
                                temp.linky.add(vLinka);
                                temp.linky.add(kLinka);
                                temp.prestup = Scraper.zastavky[k];
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
    private static void ohodnotSpoje() {
        for (Spoj aktualnySpoj : spoje) {
            String vychodzia = null, konecna = null, hodina = null, minuta = null;
            for (int i = 0; i < aktualnySpoj.linky.size(); i++) {
                if (aktualnySpoj.priamySpoj) {
                    vychodzia = aktualnySpoj.vychodzia.meno;
                    konecna = aktualnySpoj.konecna.meno;
                    hodina = java.time.LocalTime.now().toString().substring(0, 2);
                    minuta = java.time.LocalTime.now().toString().substring(3, 5);
                } else {
                    if (i == 0) {
                        vychodzia = aktualnySpoj.vychodzia.meno;
                        konecna = aktualnySpoj.prestup.meno;
                        hodina = java.time.LocalTime.now().toString().substring(0, 2);
                        minuta = java.time.LocalTime.now().toString().substring(3, 5);
                    }
                    if (i == 1) {
                        vychodzia = aktualnySpoj.prestup.meno;
                        konecna = aktualnySpoj.konecna.meno;
                        minuta = Integer.toString(aktualnySpoj.casSpoja.get(0).casVSpoji + 4
                                + Integer.parseInt(minuta));
                        if (Integer.parseInt(minuta) > 60) {
                            minuta = Integer.toString(Integer.parseInt(minuta) % 60);
                            hodina = Integer.toString(Integer.parseInt(hodina) + 1);

                        }

                    }
                }

                aktualnySpoj.casSpoja.add(Scraper.scrapeCas(aktualnySpoj.url.get(i),
                        vychodzia, konecna, aktualnySpoj.linky.get(i).substring(4),
                        Integer.parseInt(hodina), Integer.parseInt(minuta)));
                if (aktualnySpoj.casSpoja.get(aktualnySpoj.casSpoja.size() - 1) == null) {
                    aktualnySpoj.value = 1000;
                    break;
                }

                aktualnySpoj.value += aktualnySpoj.casSpoja.get(i).casVSpoji;

            }
        }
    }

    private static int partition(ArrayList<Spoj> spoje, int low, int high) {
        int pivot = spoje.get(high).value;
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (spoje.get(j).value <= pivot) {
                i++;

                // swap arr[i] and arr[j]
                Spoj temp = spoje.get(i);
                spoje.set(i, spoje.get(j));
                spoje.set(j, temp);
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        Spoj temp = spoje.get(i + 1);
        spoje.set(i + 1, spoje.get(high));
        spoje.set(high, temp);

        return i + 1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    private static void sort(ArrayList<Spoj> spoje, int low, int high) {
        if (low < high) {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(spoje, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(spoje, low, pi - 1);
            sort(spoje, pi + 1, high);
        }
    }

}
