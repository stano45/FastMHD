package com.stanley.fastmhd;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;


/*
TODO:   - vo vyhodnoteni spoja asynctask na casy
        - vyhodnotit najlepsi spoj podla casu, pri conflictoch brat do uvahy pocet prestupov
        - spravit prehladny list (new class?), ten proceduralne vypisat na novu activity
        - handle bad internet connection
        - save data to disk, use at will, learn how to check version
        - disable back button on choice activity?

TOLEARN:

 */

public class MainActivity extends AppCompatActivity {

    private String vDirtyInput;
    private String kDirtyInput;
    private MozneZastavky mozneZ;
    private InputZastavky input = new InputZastavky();
    private boolean inputDone = false;
    private boolean zastavkyVyhladane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

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
                if (zastavkyVyhladane)
                {
                    vyberZastavky();
                    inputDone = false;
                    zastavkyVyhladane = false;
                }
                b.setEnabled(true);
            }
        });
    }

    //vycisti input

    public boolean getInput() {
        TextView vInput = findViewById(R.id.vychodziaInput);
        TextView kInput = findViewById(R.id.konecnaInput);

        String vychodzia = simplifyString("" + vInput.getText());
        String konecna = simplifyString("" + kInput.getText());

        if (vychodzia.length() < 3 || konecna.length() < 3)
        {
            Toast toast = Toast.makeText(this, "Vyhladavajte s minimalne 3 znakmi.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        vDirtyInput = vychodzia;
        kDirtyInput = konecna;
        return true;
    }

    public boolean vyhladajZastavky()
    {
        String vychodzia = vDirtyInput;
        String konecna = kDirtyInput;
        ArrayList<Integer> mozneV = new ArrayList<>();

        for (int i = 0; i < Scraper.zastavky.length; i++)
        {
            if (Scraper.zastavky[i] != null)
            {
                if (simplifyString(Scraper.zastavky[i].meno).contains(vychodzia))
                {
                    mozneV.add(i);
                }
            }

        }

        if (mozneV.size() == 0)
        {
            Toast toast = Toast.makeText(this,
                    "Vychodzia zastavka nenajdena.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }


        ArrayList<Integer> mozneK = new ArrayList<>();



        for (int i = 0; i < Scraper.zastavky.length; i++)
        {
            if (Scraper.zastavky[i] != null)
            {
                if (simplifyString(Scraper.zastavky[i].meno).contains(konecna))
                {
                    mozneK.add(i);
                }
            }

        }

        if (mozneK.size() == 0)
        {
            Toast toast = Toast.makeText(this,
                    "Konecna zastavka nenajdena. Skuste znovu.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        MozneZastavky temp = new MozneZastavky(mozneV, mozneK);
        mozneZ = temp;

        return true;
    }

    public void vyberZastavky()
    {
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
                System.out.println("Vychodzia: " + Scraper.zastavky[input.vychodziaIndex].meno);
                input.konecnaIndex = data.getIntExtra("K_INT", 0);
                System.out.println("Konecna: " + Scraper.zastavky[input.konecnaIndex].meno);
                Logic.execute(input);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast toast = Toast.makeText(this,
                        "Vyber neuspesny, skuste znova.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public static String simplifyString(String s)
    {
        final String special = "áéíóúäôďťňľčžšŕľ";
        final String normal = "aeiouaodtnlczsrl";

        s = s.toLowerCase();

        for (int i = 0; i < s.length(); i++)
        {
            if (special.indexOf(s.charAt(i)) != -1)
            {
                char nChar = normal.charAt(special.indexOf(s.charAt(i)));

                if (i == 0)
                {
                    s = nChar + s.substring(1 , s.length());
                }
                else
                {
                    s = s.substring(0, i) + nChar + s.substring(i + 1, s.length());
                }
            }
        }

        return s;


    }

    public class MozneZastavky
    {
        private ArrayList<Integer> mozneV;
        private ArrayList<Integer> mozneK;

        MozneZastavky(ArrayList<Integer> mozneV, ArrayList<Integer> mozneK)
        {
            this.mozneV = mozneV;
            this.mozneK = mozneK;
        }

    }
}



