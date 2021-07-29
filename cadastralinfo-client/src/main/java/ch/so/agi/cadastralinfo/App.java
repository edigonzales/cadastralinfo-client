package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.location;
import static elemental2.dom.DomGlobal.fetch;
import static org.jboss.elemento.Elements.*;

import java.util.ArrayList;

import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.themes.Theme;
import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import ch.so.agi.cadastralinfo.models.av.RealEstateDPR;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsString;
import elemental2.core.JsNumber;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.Location;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ol.Extent;
import ol.Map;
import ol.OLFactory;
import ol.proj.Projection;
import ol.proj.ProjectionOptions;
import proj4.Proj4;

public class App implements EntryPoint {

    // Application settings
    private String myVar;

    // Format settings
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private static final String EPSG_2056 = "EPSG:2056";
    private static final String EPSG_4326 = "EPSG:4326"; 
    private Projection projection;
    
    private HTMLElement container;

    private String MAP_DIV_ID = "map";
    private Map map;

//    public static interface PersonMapper extends ObjectMapper<Person> {}
//    public static interface RealEstateDPRMapper extends ObjectMapper<RealEstateDPR> {}
//
//    public static class Person {
//
//        private String firstName;
//        private String lastName;
//        private Integer age;
//
//        public String getFirstName() {
//            return firstName;
//        }
//
//        public void setFirstName(String firstName) {
//            this.firstName = firstName;
//        }
//
//        public String getLastName() {
//            return lastName;
//        }
//
//        public void setLastName(String lastName) {
//            this.lastName = lastName;
//        }
//        
//        public Integer getAge() {
//            return age;
//        }
//        
//        public void setAge(Integer age) {
//            this.age = age;
//        }
//    }

    
	public void onModuleLoad() {
	    init();
	}
	
	public void init() {
	    // Registering EPSG:2056 / LV95 reference frame.
//        Proj4.defs(EPSG_2056, "+proj=somerc +lat_0=46.95240555555556 +lon_0=7.439583333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs");
//        ol.proj.Proj4.register(Proj4.get());
//
//        ProjectionOptions projectionOptions = OLFactory.createOptions();
//        projectionOptions.setCode(EPSG_2056);
//        projectionOptions.setUnits("m");
//        projectionOptions.setExtent(new Extent(2420000, 1030000, 2900000, 1350000));
//        projection = new Projection(projectionOptions);
//        Projection.addProjection(projection);
        
        // Change Domino UI color scheme.
        Theme theme = new Theme(ColorScheme.RED);
        theme.apply();

        // Add the Openlayers map (element) to the body.
//        HTMLElement mapElement = div().id(MAP_DIV_ID).element();
//        body().add(mapElement);
//        map = MapPresets.getColorMap(MAP_DIV_ID);
        
        container = div().id("container").element();
        Location location = DomGlobal.window.location;

        HTMLElement logoDiv = div().css("logo")
                .add(div()
                        .add(img().attr("src", DomGlobal.window.location.href + "Logo.png").attr("alt", "Logo Kanton")).element())
                .element();
        container.appendChild(logoDiv);
        
        HTMLElement searchContainerDiv = div().id("search-container").element();
        searchContainerDiv.style.backgroundColor = "wheat";
        searchContainerDiv.style.height = CSSProperties.HeightUnionType.of("100px");
        container.appendChild(searchContainerDiv);
        
        TabsPanel tabsPanel = TabsPanel.create().setColor(Color.RED);
        tabsPanel.setId("tabs-panel");
        
        Tab tabAv = Tab.create("AMTLICHE VERMESSUNG");
        Tab tabGrundbuch = Tab.create("GRUNDBUCH");
        Tab tabOereb = Tab.create("ÖREB");
        
        //Row containerRowAv = createTabContainerRow("container-row-av");
        //tabAv.appendChild(containerRowAv);
        
//        Row containerRowGrundbuch = createTabContainerRow("container-row-grundbuch");
//        tabGrundbuch.appendChild(containerRowGrundbuch);
        
        tabsPanel.appendChild(tabAv);
        tabsPanel.appendChild(tabGrundbuch);
        tabsPanel.appendChild(Tab.create("ÖREB-KATASTER")
                .appendChild(b().textContent("Home Content")));
        container.appendChild(tabsPanel.element());
       
        body().add(container);
        
        

        
        
//        PersonMapper mapper = GWT.create( PersonMapper.class );   
//        String json = "{\"age\" : 10}";
//        Person p = mapper.read(json);
//        console.log(p.getAge() + 1);
        
        
        this.processAv(tabAv);

        
        /*
        RealEstateDPRMapper aaaaaa = GWT.create( RealEstateDPRMapper.class );
        
        String json = "{\"egrid\":\"CH955832730623\",\"fosnNr\":0,\"landRegistryArea\":19897}";
        RealEstateDPR person = aaaaaa.read( json );
        console.log(person.getLandRegistryArea());
        
        String foo = person.getEgrid();
        console.log(foo);
        
        String area = String.valueOf(person.getLandRegistryArea());
        Integer areaInt = (person.getLandRegistryArea());
        console.log(area);
        console.log(areaInt);
        */
        
        console.log("fubar");
	}
	
	private void processAv(Tab tabAv) {
	    Row row = Row.create();//createTabContainerRow("container-row-av");
	    tabAv.appendChild(row);

        Column contentColumn = Column.span6();
        Column mapColumn = Column.span6();
        mapColumn.element().style.backgroundColor = "lightblue";

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
            
            //console.log(parsed.nestedGet("GetExtractByIdResponse.Extract"));
            
            
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
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span3()
                            .appendChild(span().css("content-key").textContent("Gebäude:"))))
            .appendChild(Row.create().css("content-row-slim")
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-table-header-sm").textContent("EGID")))
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-table-header-sm").textContent("Fläche")))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-table-header-sm").textContent("projektiert")))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-table-header-sm").textContent("unterirdisch")))
                    .appendChild(Column.span4()
                            .appendChild(span().css("content-table-header-sm").textContent("Adressen"))));

            // Das ist unschön: Weil die Umwandlung XML->JSON relativ dumm ist, kann es vorkommen,
            // dass anstelle eines Array, bloss ein einzelnes Objekt vorhanden ist.
            JsArray<?> buildings;
            if (JsArray.isArray(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate.Building"))) {
                buildings = Js.cast(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate.Building"));
            } else {
                buildings = JsArray.of(parsed.nestedGet("GetExtractByIdResponse.Extract.RealEstate.Building"));
            }


            for (int i=0; i<buildings.length; i++) {
                JsPropertyMap<?> building = Js.asPropertyMap(buildings.getAt(i));
                
                String egid = "-";
                if (building.get("EGID") != null) {
                    egid = Js.asString(building.get("EGID"));
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
                
//                contentColumn
//                .appendChild(Row.create().css("content-row")
//                        .appendChild(Column.span2()
//                                .appendChild(span().css("content-value").textContent(egid)))
//                        .appendChild(Column.span2()
//                                .appendChild(span().css("content-value right-align").textContent(area + " m").add(span().css("sup").textContent("2"))))
//                        .appendChild(Column.span1()
//                                .appendChild(span().css("content-value").textContent(planned)))
//                        .appendChild(Column.span1()
//                                .appendChild(span().css("content-value").textContent(undergroundStructure))));

                Row buildingRow = Row.create().css("content-row");
                buildingRow
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-value").textContent(egid)))
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-value right-align").textContent(area + " m").add(span().css("sup").textContent("2"))))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-value").textContent(planned)))
                    .appendChild(Column.span1()
                            .appendChild(span().css("content-value").textContent(undergroundStructure)));
                
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
                        if (entry.has("PostalAddress")) {
                            JsPropertyMap<?> address = Js.asPropertyMap(entry.get("PostalAddress"));
                            
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
                            
                            if (j > 0) {
                                addressString += "<br>";
                            }
                            
                            addressString += street + " " + number + ", " + postalCode + " " + city;
                        }
                    }
                    buildingRow
                        .appendChild(Column.span4()
                                .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(addressString))));
                    
                    
                }
                contentColumn.appendChild(buildingRow);

                
                console.log(buildingEntries);
//                
                
            }
            
            
//            building.forEach(b -> {
//                console.log("a");
//                return null;
//            })
            
//            console.log(parsed.nestedGetAsAny("GetExtractByIdResponse.Extract"));
            
//            console.log(Js.asPropertyMap(parsed.get("GetExtractByIdResponse")).get);
//            JsArray<?> results = Js.cast(parsed.get("results"));
//            for (int i = 0; i < results.length; i++) {
//                JsPropertyMap<?> feature = Js.cast(results.getAt(i));
//                console.log(feature);
////                JsPropertyMap<?> attrs = Js.cast(feature.get("attrs"));
//
//                
//            }
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
        row.appendChild(mapColumn);
        
        
	}
	
	private Row createTabContainerRow(String id) {
        Row row = Row.create();
        row.setId(id);
        row.appendChild(Column.span6().appendChild(div().style("background-color: lightblue").textContent("fubar")));
        row.appendChild(Column.span6().appendChild(div().style("background-color: pink").textContent("fubar")));
        return row;
	}
}