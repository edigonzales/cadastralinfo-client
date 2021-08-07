# cadastralinfo-client

## todo
- fetch für E-GRID / Grundstück nur einmal (Klick in Karte und Suche). Falls möglich wegen Adresssuche?
- Fall Adresssuche mehrere Resultate liefert. Wie handhaben? Momentan wird features[0] verwendet.

- AV-Plänli -> Button mit Auswahl oder zwei Button.

## Fragen
- Suche nach allen Grundstücken? Also auch solche, die nicht in AV sind.
- ...


curl -X POST "https://geo.so.ch/api/v1/landreg/print?TEMPLATE=A4-Hoch&scale=6000&rotation=0&extent=2607319%2C1227537%2C2608495%2C1229001&SRS=EPSG%3A2056&GRID_INTERVAL_X=1000&GRID_INTERVAL_Y=1000&DPI=200" -H "accept: application/json"