package com.stanley.fastmhd;

import android.widget.Toast;

import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Scraper{

    //classic pole deklarovane dynamicky, 900 je len random cislo ktore dobre sedelo
    //na hashovanie zastavok do pola
	static ZastavkyData[] zastavky = new ZastavkyData[900];

	//vsetky zastavky, ktore maju v mene ciarku, co sposobovalo problem, v kode su ako vynimka
    //checkovane
	private static String[] zastavkyCiarka =
			{"Poštová, Martinus", "Sad J. Kráľa, Divadlo Aréna", "Vysoká, Tchibo Outlet",
					"Záhumenice, Drevona", "Prievoz, most", "Opletalova, VW5",
					"Bory, rázcestie", "Jána Jonáša, VW1",
					"Volkswagen, VW2", "Sihoť, BVS", "Devín, škola", "Štrbská, Hrad Devín",
					"Devín, Svätopluk",
					"Segnáre, nadchod", "Lamač, Staré záhrady", "Trnavská, NAD",
					"Vajnory, konečná",
					"Vajnory, nadjazd", "Studená, zastávka", "Žabí majer, záhrady",
					"Trávna, Drevona",
					"Avion, IKEA", "Galvaniho, ubytovňa Prima", "Letisko, parkovisko",
					"Vlčie hrdlo, záhrady",
					"Vlčie hrdlo, sídlisko", "Údernícka, kúpalisko", "Kopčianska, stred",
					"Kopčany, sídlisko",
					"Jarovce, záhrady", "Čunovo, záhrady", "Čunovo, priehrada", "Čunovo, kostol",
					"Patrónka, Kaufland", "Rajka, Benzinkút", "Rajka, Autóbuszforduló",
					"Rajka, Szabadság tér",
					"Rajka, Véradópark", "Wolfsthal, Bhf, Hauptstrasse 40",
					"Hainburg/Donau, Pressburger Reichsstrasse",
					"Hainburg/Donau, Hauptplatz", "Hainburg/Donau, Pfaffenbergweg",
					"Hainburg/Donau, Wiener Tor",
					"Hainburg/Donau, Ungartor/B9"};


	//hlavna metoda Scrapera,
	static void parseZastavky()
	{
				//pripoji sa na imhd server, vyberie relevantne elementy

				final String url = "https://imhd.sk/ba/trasy-liniek";
				Document d = parseDoc(url);
				Elements l = d.select("div[id='content']").first().select("tr");
				
					for(Element linka : l) {
						Element cisloLinky = linka.selectFirst("td.top");

						//zatial nezahrnujem linku 44 lebo ma ako jedina 4 smery

						if (cisloLinky != null && !cisloLinky.text().equals("44")) {

							String urlLinky = linka.select("a")
									.attr("abs:href");
							Element smerTam = linka.selectFirst("h3");
							Element smerSpat = linka.select("h3").last();
							Element vsZ = linka.selectFirst("td:not(.top)");

							//vyberie zastavky linky ako neoddeleny string

							String vsetkyZastavky = vsZ.text();
							String zastavkyTam;
							String zastavkySpat;

							//tento if-else block je tu kvoli linke 131 ktora ma len jeden smer

							if (smerTam != smerSpat) {
								zastavkyTam = vsetkyZastavky.substring(smerTam.text().length(),
                                        vsetkyZastavky.indexOf(smerSpat.text()) - 1);
								zastavkySpat = vsetkyZastavky.substring(vsetkyZastavky.
                                        indexOf(smerSpat.text()) + smerSpat.text().length());
							} else {
								zastavkyTam = vsetkyZastavky.substring(smerTam.text().length());
								zastavkySpat = zastavkyTam;
							}

							//oddeli zastavky podla ciariek s ohladom na zastavky, ktore ju maju
                            //v nazve

							for (String z : zastavkyCiarka) {
							    if (zastavkyTam.contains(z)) {
							        zastavkyTam = zastavkyTam.substring(0, zastavkyTam.indexOf(z)
                                            + z.indexOf(','))
                                            + zastavkyTam.substring(zastavkyTam.indexOf(z)
                                            + z.indexOf(',') + 1);
							    }
							    if (zastavkySpat.contains(z)) {
							        zastavkySpat = zastavkySpat.substring(0, zastavkySpat.indexOf(z)
                                            + z.indexOf(','))
                                            + zastavkySpat.substring(zastavkySpat.indexOf(z)
                                            + z.indexOf(',') + 1);
							    }

							}

							//rozdeli zastavky do poli, podla ciariek
							String[] zastavkyTamSplit = zastavkyTam.split(", ");
							String[] zastavkySpatSplit = zastavkySpat.split(", ");

							//zaradi zastavky do hlavneho pola zastavky
							for (int y = 0; y < zastavkyTamSplit.length; y++) {
							    pridajZastavku(zastavkyTamSplit[y], cisloLinky.text() +
                                        " " + smerTam.text().substring(2), urlLinky, y);
							}
							for (int j = 0; j < zastavkySpatSplit.length; j++) {
							    pridajZastavku(zastavkySpatSplit[j], cisloLinky.text() + " "
                                        + smerSpat.text().substring(2), urlLinky, j);

							}
						}
					}
	}

	
	private static Document parseDoc(String URL)
	{
        String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";
		Document doc = null;
		try
		{
			// Pripojenie k danej stranke, timeout je vacsi kvoli zariadeniam so slabsim pripojenim.
			Response res = Jsoup.connect(URL).timeout(20000).userAgent(userAgent).execute();
			doc = res.parse();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Internet connection error.");
		}
		
		return doc;

	}	

	//metoda na zahashovania zastavok do pola, setri cas
	private static void pridajZastavku (String target, String linka, String url, int id)
	{
	    //check ak je na zaciatku medzera (skoro ziadny pripad len pre istotu)
		if (target.charAt(0) == ' ')
		{
			target = target.substring(1);
		}
		
		if (target.charAt(target.length() - 1) == ' ')
		{
			target = target.substring(0,target.length() - 2);
		}

		//vyberie index custom hash funkciou
		int index = hash(target);

		//iterujeme v hlavnom poli zastavky, hladame volne miesta na zapis
		for (int i = index; i < zastavky.length; i++)
		{
			//pripad ked je najdena zastavka s rovnakym nazvom
			if (zastavky[i] != null && zastavky[i].meno.equals(target))
			{
			        //ak neobsahuje linku, pridame ju k zastavke
					if (!zastavky[i].linky.contains(linka))
					{
						zastavky[i].linky.add(linka);
						zastavky[i].poradieZastavky.add(id);
						zastavky[i].url.add(url);
					}
					return;
			}

			//najdene prazdne miesto na zahashovanom indexe, to znamena,
            //ze zastavka este nebola pridana
			else if (zastavky[i] == null)
			{
				ZastavkyData temp = new ZastavkyData(target);
				temp.linky.add(linka);
				temp.poradieZastavky.add(id);
				temp.url.add(url);
				zastavky[i] = temp;
				return;
			}
		}

		//ak nebolo najdene miesto od hashnuteho indexu po koniec pola,
        //iterujeme od zaciatku pola po hashnuty index
		for (int i = 0; i < index -1; i++)
		{
		    //princip zapisu rovnaky ako o loop vyssie
			if ((zastavky[i] != null) && zastavky[i].meno.equals(target))
			{
				if (!zastavky[i].linky.contains(linka))
				{
					zastavky[i].linky.add(linka);
					zastavky[i].poradieZastavky.add(id);
					zastavky[i].url.add(url);

				}
				return;

			}

			else if (zastavky[i] == null)
			{
				ZastavkyData temp = new ZastavkyData(target);
				temp.linky.add(linka);
				temp.poradieZastavky.add(id);
				temp.url.add(url);
				zastavky[i] = temp;
				return;
			}
	    }
    }

    //custom hash funkcia na priradenie indexu zastavkam
	private static int hash(String target)
	{
		long hash = 7;

		//hashujeme podla znakov v mene zastavky
		for (int i = 0; i < target.length(); i++)
		{
			hash = hash * 31 + target.charAt(i);
		}

		//distributneme na velkost celeho pola -1 a mame index
		return Math.abs((int) hash % zastavky.length - 1);															
	}


	//metoda na zistenie (zatial najblizsieho) casu linky
	static CasLinky scrapeCas(String url, String vychodzia, String konecna, String smer) {
		System.out.println("//////////////////////////////////////////////////////////////////");

		//stiahne html z url spoja
		Document d = parseDoc(url);

		smer = smer.replace("VW5, Devínska Nová Ves,", "VW5;");


		System.out.println("vychodzia: " + vychodzia);
		System.out.println("konecna: " + konecna);
		System.out.println("smer: " + smer + " url: " + url);
		Element e2 = d.selectFirst("table[class=tabulka]:has(td[colspan=2]:contains("
				+ smer + "))");
		System.out.println("element 2: " + e2.text());
		Element e3 = e2.selectFirst("td:contains(" + vychodzia + ")");
		System.out.println("element 3: " + e3.text());
		Element e4 = e3.selectFirst("a");
		System.out.println("element 4: " + e4.text());

		url = "https://imhd.sk" + e4.attr("href");
		d = parseDoc(url);
		String s = "tr[class='zastavky_riadok']:has(td:contains(" + konecna + "))";
		e4 = d.selectFirst(s);
		Element casy = d.selectFirst("tr.cp_odchody:has(td[class*='najblizsi'])");

		CasLinky temp = new CasLinky();
		if (casy != null)
		{
			System.out.println("casVSpoji: " + e4.selectFirst("td").text());
			System.out.println("hodina: " + casy.select("td.cp_hodina").text());
			System.out.println("minuta: " + casy.select("td[class *='najblizsi']").text()
					.substring(0,2));


			temp.casVSpoji = Integer.parseInt(e4.selectFirst("td").text());
			temp.hodina = Integer.parseInt(casy.select("td.cp_hodina").text());
			temp.minuta = Integer.parseInt(casy.select("td[class *='najblizsi']").text()
					.substring(0,2));
		}
		else
		{
			temp = null;
		}


        return temp;
    }


}

