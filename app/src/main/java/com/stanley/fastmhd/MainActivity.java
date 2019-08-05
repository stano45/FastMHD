package com.stanley.fastmhd;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    private String vDirtyInput;
    private String kDirtyInput;
    private MozneZastavky mozneZ;
    private InputZastavky input = new InputZastavky();
    private boolean inputDone = false;
    private boolean zastavkyVyhladane = false;

    public static String simplifyString(String s) {
        final String special = "áéíóúäôďťňľčžšŕľ";
        final String normal = "aeiouaodtnlczsrl";

        s = s.toLowerCase();

        for (int i = 0; i < s.length(); i++) {
            if (special.indexOf(s.charAt(i)) != -1) {
                char nChar = normal.charAt(special.indexOf(s.charAt(i)));

                if (i == 0) {
                    s = nChar + s.substring(1);
                } else {
                    s = s.substring(0, i) + nChar + s.substring(i + 1);
                }
            }
        }

        return s;


    }

    //vycisti input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        TextView text = findViewById(R.id.vychodziaInput);
        text.setText("");
        text = findViewById(R.id.konecnaInput);
        text.setText("");

        final Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.setEnabled(false);
                if (!inputDone) {
                    inputDone = getInput();
                    System.out.println("input done");
                }
                if (!zastavkyVyhladane && inputDone) {
                    zastavkyVyhladane = vyhladajZastavky();
                    System.out.println("search done");
                    inputDone = false;
                }
                if (zastavkyVyhladane) {
                    vyberZastavky();
                    inputDone = false;
                    zastavkyVyhladane = false;
                }
                b.setEnabled(true);
            }
        });
    }

    public boolean getInput() {
        TextView vInput = findViewById(R.id.vychodziaInput);
        TextView kInput = findViewById(R.id.konecnaInput);

        String vychodzia = simplifyString("" + vInput.getText());
        String konecna = simplifyString("" + kInput.getText());

        if (vychodzia.length() < 3 || konecna.length() < 3) {
            Toast toast = Toast.makeText(this, "Vyhladavajte s minimalne 3 znakmi.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        vDirtyInput = vychodzia;
        kDirtyInput = konecna;
        return true;
    }

    public boolean vyhladajZastavky() {
        String vychodzia = vDirtyInput;
        String konecna = kDirtyInput;
        ArrayList<Integer> mozneV = new ArrayList<>();

        for (int i = 0; i < Scraper.zastavky.length; i++) {
            if (Scraper.zastavky[i] != null) {
                if (simplifyString(Scraper.zastavky[i].meno).contains(vychodzia)) {
                    mozneV.add(i);
                }
            }

        }

        if (mozneV.size() == 0) {
            Toast toast = Toast.makeText(this,
                    "Vychodzia zastavka nenajdena.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }


        ArrayList<Integer> mozneK = new ArrayList<>();


        for (int i = 0; i < Scraper.zastavky.length; i++) {
            if (Scraper.zastavky[i] != null) {
                if (simplifyString(Scraper.zastavky[i].meno).contains(konecna)) {
                    mozneK.add(i);
                }
            }

        }

        if (mozneK.size() == 0) {
            Toast toast = Toast.makeText(this,
                    "Konecna zastavka nenajdena. Skuste znovu.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        mozneZ = new MozneZastavky(mozneV, mozneK);

        return true;
    }

    public void vyberZastavky() {
        Intent intent = new Intent(this, ChoiceActivity.class);
        Bundle extras = new Bundle();
        extras.putIntegerArrayList("VYCHODZIE_Z", mozneZ.mozneV);
        extras.putIntegerArrayList("KONECNE_Z", mozneZ.mozneK);
        intent.putExtras(extras);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                input.vychodziaIndex = data.getIntExtra("V_INT", 0);
                //System.out.println("Vychodzia: " + Scraper.zastavky[input.vychodziaIndex].meno);
                input.konecnaIndex = data.getIntExtra("K_INT", 0);
                //System.out.println("Konecna: " + Scraper.zastavky[input.konecnaIndex].meno);
                new CasyAsync().execute();
                findViewById(R.id.vychodziaInput).setVisibility(View.INVISIBLE);
                findViewById(R.id.konecnaInput).setVisibility(View.INVISIBLE);
                findViewById(R.id.button).setVisibility(View.INVISIBLE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.vyhladavamText).setVisibility(View.VISIBLE);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast toast = Toast.makeText(this,
                        "Vyber neuspesny, skuste znova.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public class MozneZastavky {
        private ArrayList<Integer> mozneV;
        private ArrayList<Integer> mozneK;

        MozneZastavky(ArrayList<Integer> mozneV, ArrayList<Integer> mozneK) {
            this.mozneV = mozneV;
            this.mozneK = mozneK;
        }

    }

    public class CasyAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Logic.execute(input);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findViewById(R.id.vychodziaInput).setVisibility(View.VISIBLE);
            findViewById(R.id.konecnaInput).setVisibility(View.VISIBLE);
            findViewById(R.id.button).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.vyhladavamText).setVisibility(View.GONE);
            for (Spoj s : Logic.spoje) {
                if (s.value != 1000) {
                    System.out.println("-----------------------------------------");
                    System.out.println("Hodnota:" + s.value);
                    System.out.println("V:" + s.vychodzia.meno);
                    for (int i = 0; i < s.linky.size(); i++) {
                        System.out.println(s.linky.get(i));
                        System.out.println(s.url.get(i));
                        if (i < s.casSpoja.size() - 1) {
                            System.out.println("Cas #" + i + ": " + s.casSpoja.get(i).casVSpoji);
                        }
                        if (!s.priamySpoj) {
                            System.out.println("P: " + s.prestup.meno);
                        }
                    }
                }

                System.out.println("K: " + s.konecna.meno);
                System.out.println();
            }

            Intent i = new Intent();
            i.setClass(getApplicationContext(), ConnectionsListActivity.class);
            startActivity(i);


        }
    }
}



