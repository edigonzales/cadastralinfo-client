package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.EventType.bind;
import static org.jboss.elemento.EventType.mouseout;
import static org.jboss.elemento.EventType.mouseover;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.icons.Icons;
import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ol.Map;

public class AvTabContent {    
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private HTMLElement root;
    private String MAP_DIV_ID = "map-av";
    private Map map;
    
    private void init() {
        
    }
    
    public AvTabContent(Tab tabAv) {
        Row row = Row.create();
        tabAv.appendChild(row);

        Column contentColumn = Column.span6();
        Column mapColumn = Column.span6();
        mapColumn.setId("map-av-column");
        mapColumn.element().style.backgroundColor = "lightblue";

        row.appendChild(mapColumn);
        
        
        HTMLElement mapElement = div().id(MAP_DIV_ID).element();
        mapColumn.appendChild(mapElement);
        map = MapPresets.getColorMap(MAP_DIV_ID);
        
        Loader loader;
        loader = Loader.create((HTMLElement) row.element(), LoaderEffect.ROTATION).setLoadingText(null);
        loader.start();
        
        DomGlobal.fetch("/av")
        .then(response -> {
            if (!response.ok) {
                return null;
            }
            return response.text();
        })
        .then(json -> {
            JsPropertyMap<?> parsed = Js.cast(Global.JSON.parse(json));
            
            // TODO: nur ab hier in die eigene Klasse!?
            
            /*
             * Allgemeine Informationen
             */
            
            Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                    .setSize(ButtonSize.SMALL)
                    .setContent("PDF")
                    .setBackground(Color.WHITE)
                    .elevate(0)
                    .style()
                    .setColor("#e53935")
                    .setBorder("1px #e53935 solid")
                    .setPadding("5px 5px 5px 0px;")
                    .setMinWidth(px.of(100)).get();
            
            pdfBtn.addClickListener(evt -> {
                Window.open("https://agi.so.ch", "_blank", null);
            });

            contentColumn
            .appendChild(Row.create().css("empty-row-5"))
            .appendChild(Row.create().css("content-row")
                    .appendChild(pdfBtn.element()))
            .appendChild(Row.create().css("empty-row-20"));

            
            JsPropertyMap<?> realEstate = Js.asPropertyMap(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate"));
                    
            String gbnr = "-";
            if (realEstate.get("Number") != null) {
                gbnr = Js.asString(realEstate.get("Number"));
            }
            
            String egrid = "-";
            if (realEstate.get("EGRID") != null) {
                egrid = Js.asString(realEstate.get("EGRID"));
            }
            
            String municipality = "-";
            if (realEstate.get("Municipality") != null) {
                municipality = Js.asString(realEstate.get("Municipality"));
            }
            
            String subunitOfLandRegister = "-";
            if (realEstate.get("SubunitOfLandRegister") != null) {
                subunitOfLandRegister = Js.asString(realEstate.get("SubunitOfLandRegister"));
            }
  
            String identND = "-";
            if (realEstate.get("IdentND") != null) {
                identND = Js.asString(realEstate.get("IdentND"));
            }            
            
            String type = "-";
            if (realEstate.get("Type") != null) {
                type = Js.asString(realEstate.get("Type"));
                
                if (type.equalsIgnoreCase("RealEstate")) {
                    type = "Liegenschaft";
                } else if (type.equalsIgnoreCase("Distinct_and_permanent_rights.BuildingRight")) {
                    type = "SelbstRecht.Baurecht";
                } else if (type.equalsIgnoreCase("Distinct_and_permanent_rights.right_to_spring_water")) {
                    type = "SelbstRecht.Quellenrecht";
                } else if (type.equalsIgnoreCase("Distinct_and_permanent_rights.concession")) {
                    type = "SelbstRecht.Konzessionsrecht";
                } else if (type.equalsIgnoreCase("Mineral_rights")) {
                    type = "Bergwerk";
                }
            }  
            
            String landRegistryArea = "-";
            if (realEstate.get("LandRegistryArea") != null) {
                String rawString = Js.asString(realEstate.get("LandRegistryArea"));
                landRegistryArea = fmtDefault.format(Double.valueOf(rawString));
            }  
            
            contentColumn
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("GB-Nr.:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(gbnr)))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("E-GRID:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(egrid))))
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Gemeinde:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(municipality)))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Grundbuch:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(subunitOfLandRegister))))
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("BFS-Nr:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent("-")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("NBIdent:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(identND))))
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Grundstücksart:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(type)))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Grundstücksfläche:")))
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-value").textContent(landRegistryArea + " m").add(span().css("sup").textContent("2")))))
            .appendChild(Row.create().css("empty-row"))
            
            /*
             * Gebäude (+ Adressen)
             */
            
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Gebäude:"))))
            .appendChild(Row.create().css("content-row-slim")
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-table-header-sm").textContent("EGID")))
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-table-header-sm right-align").textContent("Fläche")))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-table-header-sm").textContent("")))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-table-header-sm").textContent("projektiert")))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-table-header-sm").textContent("unterirdisch"))));

            // Das ist unschön: Weil die Umwandlung XML->JSON relativ dumm ist, kann es vorkommen,
            // dass anstelle eines Array, bloss ein einzelnes Objekt vorhanden ist.
            JsArray<?> buildings;
            if (JsArray.isArray(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate.Building"))) {
                buildings = Js.cast(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate.Building"));
            } else {
                buildings = JsArray.of(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate.Building"));
            }

            HashMap<String,JsPropertyMap<?>> postalAddresses = new HashMap<>();
            for (int i=0; i<buildings.length; i++) {
                JsPropertyMap<?> building = Js.asPropertyMap(buildings.getAt(i));
                
                String egid = "-";
                if (building.get("Egid") != null) {
                    egid = Js.asString(building.get("Egid"));
                }
                
                String area = "-";
                if (building.has("AreaShare")) {
                    String rawString = Js.asString(building.get("AreaShare"));
                    area = fmtInteger.format(Double.valueOf(rawString));
                } else if (building.has("Area")) {
                    String rawString = Js.asString(building.get("Area"));
                    area = fmtInteger.format(Double.valueOf(rawString));
                }

                String planned = "-";
                if (building.get("planned") != null) {
                    String rawString = Js.asString(building.get("planned"));
                    if (rawString.equalsIgnoreCase("true")) {
                        planned = "ja";
                    } else {
                        planned = "nein";
                    }
                }
                
                String undergroundStructure = "-";
                if (building.has("undergroundStructure")) {
                    String rawString = Js.asString(building.get("undergroundStructure"));
                    if (rawString.equalsIgnoreCase("true")) {
                        undergroundStructure = "ja";
                    } else {
                        undergroundStructure = "nein";
                    }
                }
                
                Row buildingRow = Row.create().css("content-row")
                        .appendChild(Column.span2()
                                .appendChild(span().css("content-value").textContent(egid)))
                        .appendChild(Column.span2()
                                .appendChild(span().css("content-value right-align").textContent(area + " m").add(span().css("sup").textContent("2"))))
                        .appendChild(Column.span1()
                                .appendChild(span().css("content-value").textContent("")))
                        .appendChild(Column.span1()
                                .appendChild(span().css("content-value").textContent(planned)))
                        .appendChild(Column.span1()
                                .appendChild(span().css("content-value").textContent(undergroundStructure)));
                
                bind(buildingRow.element(), mouseover, event -> {
                    buildingRow.element().style.backgroundColor = "rgba(255,0,0,0.2)";
                });
                
                bind(buildingRow.element(), mouseout, event -> {
                    buildingRow.element().style.backgroundColor = "white";
                });

                contentColumn
                .appendChild(buildingRow);
                
                JsArray<?> buildingEntries;
                if (JsArray.isArray(building.get("BuildingEntry"))) {
                    buildingEntries = Js.cast(building.get("BuildingEntry"));
                } else {
                    buildingEntries = JsArray.of(building.get("BuildingEntry"));
                }
              
                if (buildingEntries.length > 0) {
                    String addressString = "";
                    for (int j = 0; j < buildingEntries.length; j++) {
                        JsPropertyMap<?> entry = Js.asPropertyMap(buildingEntries.getAt(j));

                        if (entry != null && entry.has("PostalAddress")) {
                            JsPropertyMap<?> address = Js.asPropertyMap(entry.get("PostalAddress"));
                            
                            // Damit sortiert werden kann, brauchen wir einen Sortierschlüssel.
                            // In unserem Fall ist das der Strassenname plus Hausnummer.
                            String street = "";
                            if (address.has("Street")) {
                                street = Js.asString(address.get("Street"));
                            }
                            
                            String number = "";
                            if (address.has("Number")) {
                                number = Js.asString(address.get("Number"));
                            }
                            
                            // Hausnummer mit Nullen füllen, damit die Sortierung passt. Z.B. 5 vor 17.
                            String leadingZeroNumber = ("00000000" + number).substring(number.length());
                            postalAddresses.put(street+leadingZeroNumber, address);
                        }
                    }
                }                
            }
            contentColumn
            .appendChild(Row.create().css("empty-row"))
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Gebäudeadressen:"))))
            .appendChild(Row.create().css("content-row-slim")
                    .appendChild(Column.span5()
                            .appendChild(span().css("content-table-header-sm").textContent("Strasse / Hausnummer")))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-table-header-sm").textContent("PLZ")))
                    .appendChild(Column.span5()
                            .appendChild(span().css("content-table-header-sm").textContent("Ortschaft"))));
                        
            /*
             * Gebäudeadressen
             */
            
            List<String> sortedAddressesKeys = new ArrayList(postalAddresses.keySet());
            Collections.sort(sortedAddressesKeys);
            
            for (String key : sortedAddressesKeys) {
                JsPropertyMap<?> address = postalAddresses.get(key);
                
              String street = "";
              if (address.has("Street")) {
                  street = Js.asString(address.get("Street"));
              }
              
              String number = "";
              if (address.has("Number")) {
                  number = Js.asString(address.get("Number"));
              }
              
              String postalCode = "";
              if (address.has("PostalCode")) {
                  postalCode = Js.asString(address.get("PostalCode"));
              }
              
              String city = "";
              if (address.has("City")) {
                  city = Js.asString(address.get("City"));
              }

              Row addressRow = Row.create().css("content-row")
              .appendChild(Column.span5()
                      .appendChild(span().css("content-value").textContent(street + " " + number)))
              .appendChild(Column.span1()
                      .appendChild(span().css("content-value").textContent(postalCode)))
              .appendChild(Column.span5()
                      .appendChild(span().css("content-value").textContent(city)));
              
              bind(addressRow.element(), mouseover, event -> {
                  addressRow.element().style.backgroundColor = "rgba(255,0,0,0.2)";
              });
              
              bind(addressRow.element(), mouseout, event -> {
                  addressRow.element().style.backgroundColor = "white";
              });
              
              contentColumn
              .appendChild(addressRow);
            }
            
            /*
             * Bodenbedeckung + Flurnamen
             */
            Row landCoverShareLocalNameRow = Row.create();
            contentColumn
            .appendChild(Row.create().css("empty-row"))
            .appendChild(landCoverShareLocalNameRow);
           

            /*
             * Bodenbedeckung
             */
            Column landCoverShareColumn = Column.span6();
            landCoverShareColumn
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span6()
                            .appendChild(span().css("content-key").textContent("Bodenbedeckung:"))))
            .appendChild(Row.create().css("content-row-slim")
                    .appendChild(Column.span4()
                            .appendChild(span().css("content-table-header-sm").textContent("Art")))
                    .appendChild(Column.span4()
                            .appendChild(span().css("content-table-header-sm right-align").textContent("Fläche"))));

            JsArray<?> landCoverShares;
            if (JsArray.isArray(realEstate.get("LandCoverShare"))) {
                landCoverShares = Js.cast(realEstate.get("LandCoverShare"));
            } else {
                landCoverShares = JsArray.of(realEstate.get("LandCoverShare"));
            }

            HashMap<String,JsPropertyMap<?>> landCoverSharesMap = new HashMap<>();
            for (int l=0; l<landCoverShares.length; l++) {
                JsPropertyMap<?> landCoverShare = Js.asPropertyMap(landCoverShares.getAt(l));
                String description = Js.asString(landCoverShare.get("TypeDescription"));
                landCoverSharesMap.put(description, landCoverShare);
            }
            
            List<String> sortedLandCoverShareKeys = new ArrayList(landCoverSharesMap.keySet());
            Collections.sort(sortedLandCoverShareKeys);
            
            for (String key : sortedLandCoverShareKeys) {
                JsPropertyMap<?> landCoverShare = landCoverSharesMap.get(key);
                String description = Js.asString(landCoverShare.get("TypeDescription"));
                String rawString = Js.asString(landCoverShare.get("Area"));
                String area = fmtInteger.format(Double.valueOf(rawString));
             
                Row landCoverShareRow = Row.create().css("content-row")
                        .appendChild(Column.span4()
                                .appendChild(span().css("content-value").textContent(description)))
                        .appendChild(Column.span4()
                                .appendChild(span().css("content-value right-align").textContent(area + " m").add(span().css("sup").textContent("2"))));
                
                bind(landCoverShareRow.element(), mouseover, event -> {
                    landCoverShareRow.element().style.backgroundColor = "rgba(255,0,0,0.2)";
                });
                
                bind(landCoverShareRow.element(), mouseout, event -> {
                    landCoverShareRow.element().style.backgroundColor = "white";
                });
                
                landCoverShareColumn
                .appendChild(landCoverShareRow);
            }
            landCoverShareLocalNameRow.appendChild(landCoverShareColumn);
            
            
            /*
             * Flurnamen
             */
            Column localNameColumn = Column.span6();
            localNameColumn
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span6()
                            .appendChild(span().css("content-key").textContent("Flurnamen:"))));

            JsArray<?> localNames;
            if (JsArray.isArray(realEstate.get("LocalName"))) {
                localNames = Js.cast(realEstate.get("LocalName"));
            } else {
                localNames = JsArray.of(realEstate.get("LocalName"));
            }

            List<String> localNamesList = new ArrayList<>();
            for (int m=0; m<localNames.length; m++) {
                JsPropertyMap<?> localName = Js.asPropertyMap(localNames.getAt(m));
                String name = Js.asString(localName.get("Name"));
                localNamesList.add(name);
            }
            
            Collections.sort(localNamesList);
            Row localNameRow = Row.create().css("content-row")
                    .appendChild(Column.span12()
                            .appendChild(span().css("content-value").textContent(String.join(", ", localNamesList))));
            
            localNameColumn.appendChild(localNameRow);
            

            landCoverShareLocalNameRow.appendChild(localNameColumn);
            
            /*
             * Adressen: Geometer und Aufsicht
             */
            Row addressesRow = Row.create();
            contentColumn
            .appendChild(Row.create().css("empty-row"))
            .appendChild(addressesRow);

            /*
             * Geometeradresse
             */
            Column surveyorAddressColumn = Column.span6();
            {
                surveyorAddressColumn
                .appendChild(Row.create().css("content-row")
                        .appendChild(Column.span6()
                                .appendChild(span().css("content-key").textContent("Nachführungsgeometer:"))));

                JsPropertyMap<?> surveyorOffice = Js.asPropertyMap(realEstate.get("SurveyorOffice"));
                JsPropertyMap<?> surveyorOfficePerson = Js.asPropertyMap(surveyorOffice.get("Person"));
                JsPropertyMap<?> surveyorOfficeAddress = Js.asPropertyMap(surveyorOffice.get("Address"));
                
                StringBuilder addressHtml = new StringBuilder();
                String firstName = Js.asString(surveyorOfficePerson.get("FirstName"));
                String lastName = Js.asString(surveyorOfficePerson.get("LastName"));
                String street = Js.asString(surveyorOfficeAddress.get("Street"));
                String number = Js.asString(surveyorOfficeAddress.get("Number"));
                String postalCode = Js.asString(surveyorOfficeAddress.get("PostalCode"));
                String city = Js.asString(surveyorOfficeAddress.get("City"));
                String name = Js.asString(surveyorOffice.get("Name"));
                String phone = Js.asString(surveyorOffice.get("Phone"));
                String email = Js.asString(surveyorOffice.get("Email"));
                String web = Js.asString(surveyorOffice.get("Web"));
                
                addressHtml.append(firstName).append(" ").append(lastName).append("<br>");
                addressHtml.append(name).append("<br>");
                addressHtml.append(street).append(" ").append(number).append("<br>");
                addressHtml.append(postalCode).append(" ").append(city).append("<br><br>");
                addressHtml.append("Telefon").append(" ").append(phone).append("<br>");
                addressHtml.append("<a class=\"default-link\" href= \"mailto:"+email+"\">"+email+"</a><br>");
                addressHtml.append("<a class=\"default-link\" href= \""+web+"\">"+web+"</a>");
                
                Row surveyorAddressRow = Row.create().css("content-row")
                        .appendChild(Column.span12()
                                .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(addressHtml.toString()))));
                
                surveyorAddressColumn.appendChild(surveyorAddressRow);
            }
            addressesRow.appendChild(surveyorAddressColumn);
            
            /*
             * Vermessungsaufsicht
             */
            Column supervisionAddressColumn = Column.span6();
            {
                supervisionAddressColumn
                .appendChild(Row.create().css("content-row")
                        .appendChild(Column.span6()
                                .appendChild(span().css("content-key").textContent("Vermessungsaufsicht:"))));

                JsPropertyMap<?> supervisionOffice = Js.asPropertyMap(realEstate.get("SupervisionOffice"));
                JsPropertyMap<?> supervisionOfficeAddress = Js.asPropertyMap(supervisionOffice.get("Address"));
                
                StringBuilder supervisionAddressHtml = new StringBuilder();
                String street = Js.asString(supervisionOfficeAddress.get("Street"));
                String number = Js.asString(supervisionOfficeAddress.get("Number"));
                String postalCode = Js.asString(supervisionOfficeAddress.get("PostalCode"));
                String city = Js.asString(supervisionOfficeAddress.get("City"));
                String name = Js.asString(supervisionOffice.get("Name"));
                String phone = Js.asString(supervisionOffice.get("Phone"));
                String email = Js.asString(supervisionOffice.get("Email"));
                String web = Js.asString(supervisionOffice.get("Web"));

                supervisionAddressHtml.append(name).append("<br>");
                supervisionAddressHtml.append(street).append(" ").append(number).append("<br>");
                supervisionAddressHtml.append(postalCode).append(" ").append(city).append("<br><br>");
                supervisionAddressHtml.append("Telefon").append(" ").append(phone).append("<br>");
                supervisionAddressHtml.append("<a class=\"default-link\" href= \"mailto:"+email+"\">"+email+"</a><br>");
                supervisionAddressHtml.append("<a class=\"default-link\" href= \""+web+"\">"+web+"</a>");
                
                Row supervisionAddressRow = Row.create().css("content-row")
                        .appendChild(Column.span12()
                                .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(supervisionAddressHtml.toString()))));
                
                supervisionAddressColumn.appendChild(supervisionAddressRow);
            }
            
            addressesRow.appendChild(supervisionAddressColumn);

            
            console.log(realEstate);

            
            return null;
        }).catch_(error -> {
            console.log(error);
            return null;
        });
        
        loader.stop();
        
        
        
        


//        contentColumn
//            .appendChild(span().textContent("GB-Nr.: "))
//            .appendChild(span().textContent("198"));
        
        
        row.appendChild(contentColumn);
        
        
        root = row.element();

    }
    
    public HTMLElement element() {
        return root;
    }
    

}