# cadastralinfo-client

## todo
- Suche (fetch) im Hauptfenster: Wie oft muss das gemacht werden? Kann copy/past Code verhindert werden?
- Wie XXXElement strukturieren? Kann man etwas verallgemeinern (vererben)? Ablauf? Daten holen, parsen, rendern? "Cards" im Konstruktor und Hauptdiv erst sichbar machen, wenn alles fertig?
- ...
- clear all Button
  * Hat "resetIcon"-Button auch (oder nur dieser) dieses Verhalten?
  * resetIcon nicht sauber zentriert
- nested fetch -> then()



- GBDBS
  * Mehr Testfälle. Anschliessend überlegen wie XML geparsed wird (resp. welche Klassen benötigt werden)
  * ...
- AV
  * JSON über Bord werfen und XML verwenden

- AV-Plänli -> Button mit Auswahl oder zwei Button.

- ÖREB:
  * XMLUtils: Auslesen einer bestimmten Sprache
  * Sind die Layer sichbar, falls ein anderes Thema aktiviert ist?

## Fragen
- Suche nach allen Grundstücken? Also auch solche, die nicht in AV sind.
- Fall Adresssuche mehrere Resultate liefert. Wie handhaben? Momentan wird features[0] verwendet.
- ...


curl -X POST "https://geo.so.ch/api/v1/landreg/print?TEMPLATE=A4-Hoch&scale=6000&rotation=0&extent=2607319%2C1227537%2C2608495%2C1229001&SRS=EPSG%3A2056&GRID_INTERVAL_X=1000&GRID_INTERVAL_Y=1000&DPI=200" -H "accept: application/json"



