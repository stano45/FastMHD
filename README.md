# FastMHD
FastMHD je moj prvy pokus o vacsi projekt, je to Android appka, ktora scrapuje data zo stranky imhd.sk, a vyhodnocuje spoje medzi 2 zastavkami.
Islo hlavne o ziskanie skusenosti v Jave a Android Studio IDE. Naucil som sa plno design patterns v android kodovani a mnoho dalsich veci o 
stavani velkych projektoch.

Technicke detaily:
Aplikacia vyuziva na scraping kniznicu Jsoup, ktora je aplikovatelna na html requesty (co v tomto pripade bohate stacilo). Ako prve stiahne data
o zastavkach a linkach z imhd.sk/trasy-liniek, potom da kontrolu pouzivatelovi, ktory zada zastavku, z ktorej ide a zastavku, na ktoru sa chce
dostat. Z inputu aplikacia vyhladava podretazec mena zastavky, takze na vyhladanie zastavky Molecova staci len napriklad vyhladat "mole". Toto
prinasa samozrejme conflicty s roznymi zastavkami (pretoze sa da vyhladavat od 3 znakov vyssie), tak ak sa nejaky vyskytne, pouzivatel si zo 
zoznamu vyberie pozadovanu zastavku. Nasledne sa vyhladaju jednolinkove spoje, potom spoje s jednym prestupom. Dvojprestupove spoje aplikacia
nepodporuje (mozno v buducnosti :D). Kazdy spoj sa casovo ohodnoti, spoje sa zoradia podla trvania a zobrazia v prehladnom zozname.

Vyhladavanie spojov je momentalne dost pomale, pretoze kazda linka v kazdom spoji vytvori http request. Tieto data stahovat vopred a
udrziavat aktualne v nejakej databaze sa mi nechcelo, tak som to takto vyriesil, len aby aplikacia splnila ucel.
