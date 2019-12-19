# BonehexerAndroid

Projekt do předmětu TAMZII na VŠB.

Projekt obsahuje základní pilíře:

* Vykreslování grafiky ve 2D Canvas
* Optimalizace vykreslování do canvasu - 8bit bitmapy, vykreslování pouze zobrazovaných objektů, vykreslování pozadí..
* MediaPlayer pro přehrávání zvuků ze hry
* Animace herních objektů, vytváření Sprites ze jednoho Spritesheet
* Nepřátelská AI - pronásledování hráče v určitém radiusu, pokud jej ztratí, na chvíli se zastaví, hledá, a poté se opět náhodně pohybuje po mapě; využití rychlého algoritmu pro raytracing mezi nepřítelem a hráčem
* Náhodně generovaná mapa s generováním náhodných pozic pro spawn hráče, nepřátel a portálu do dalšího levelu
* Střílení ve 4 směrech, kolize s nepřáteli
* //Čítač nejvyššího dosaženého levelu, uložený do SharedPreferences
* Vlastní game engine - optimalizovaný loop (převzato z Minecraftu), optimalizace update() a draw() metod - vykreslují se pouze viditelné objetky
* Ošetřená kolize s okolím
* Debugovací mód pro analýzu chování AI a kolizních masek pohyblivých objektů
* Virtuální D-PAD s tlačítky pro ovládání
