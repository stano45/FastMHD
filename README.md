# FastMHD

**English**

FastMHD is my first attempt at a bigger programming project. It is an app for android, which scrapes data from the website imhd.sk (public transport site of Bratislava, Slovakia) and finds an optimal connection between two stops.
I started this project in order to gain experience in Java and Android development. I've learned tons of design patterns and strategies for building a project of this size.

Technical details:
The app uses the Jsoup scraping library for Java, which is used to handle http requests. This lib gets utilized for downloading data about all bus stops and connections. After this download the user gets to input the desired connection (it's enough to to search for a substring of a stop name (e.g. mole in Molecova), after that the user confirms his/her choice. After that a search gets executed that looks for the most optimal connection - the logic looks for all available connection options, then sorts them by time (shortest first).

Java source files found [here](app/src/main/java/com/stanley/fastmhd/).

**Slovak**

FastMHD je moj prvy pokus o vacsi projekt, je to Android appka, ktora scrapuje data zo stranky imhd.sk, a vyhodnocuje spoje medzi 2 zastavkami.
Islo hlavne o ziskanie skusenosti v Jave a Android Studio IDE. Naucil som sa plno design patterns v android kodovani a mnoho dalsich veci o 
stavani velkych projektoch.

Technicke detaily:
Aplikacia vyuziva na scraping kniznicu Jsoup, ktora je aplikovatelna na http requesty (co v tomto pripade bohate stacilo). Ako prve stiahne potrebne info, potom da kontrolu pouzivatelovi, ktory zada zastavku, z ktorej ide a zastavku, na ktoru sa chce
dostat. Z inputu aplikacia vyhladava podretazec mena zastavky, takze na vyhladanie zastavky Molecova staci len napriklad vyhladat "mole". Toto
prinasa samozrejme conflicty s roznymi zastavkami (pretoze sa da vyhladavat od 3 znakov vyssie), tak ak sa nejaky vyskytne, pouzivatel si zo 
zoznamu vyberie pozadovanu zastavku. Nasledne sa vyhladaju jednolinkove spoje, potom spoje s jednym prestupom. Dvojprestupove spoje aplikacia
nepodporuje (mozno v buducnosti :D). Kazdy spoj sa casovo ohodnoti, spoje sa zoradia podla trvania a zobrazia v prehladnom zozname.

Vyhladavanie spojov je momentalne dost pomale, pretoze kazda linka v kazdom spoji vytvori http request. Tieto data stahovat vopred a
udrziavat aktualne v nejakej databaze sa mi nechcelo, tak som to takto vyriesil, len aby aplikacia splnila ucel.
