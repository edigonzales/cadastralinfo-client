package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.location;
import static elemental2.dom.DomGlobal.fetch;
import static org.jboss.elemento.Elements.*;

import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
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
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.Location;
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

    private static final String EPSG_2056 = "EPSG:2056";
    private static final String EPSG_4326 = "EPSG:4326"; 
    private Projection projection;
    
    private HTMLElement container;

    private String MAP_DIV_ID = "map";
    private Map map;

    public static interface PersonMapper extends ObjectMapper<Person> {}
    public static interface RealEstateDPRMapper extends ObjectMapper<RealEstateDPR> {}

    public static class Person {

        private String firstName;
        private String lastName;
        private Integer age;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public Integer getAge() {
            return age;
        }
        
        public void setAge(Integer age) {
            this.age = age;
        }
    }

    
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
        console.log(DomGlobal.window.location);
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
        tabsPanel.appendChild(Tab.create("ÖREB")
                .appendChild(b().textContent("Home Content")));
        container.appendChild(tabsPanel.element());
       
        body().add(container);
        
        

        
        
        PersonMapper mapper = GWT.create( PersonMapper.class );   
        String json = "{\"age\" : 10}";
        Person p = mapper.read(json);
        console.log(p.getAge() + 1);
        
        
        this.processAv(tabAv, p.getAge());

        
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
	
	private void processAv(Tab tabAv, Integer age) {
	    Row row = Row.create();//createTabContainerRow("container-row-av");
        tabAv.appendChild(row);
        
        Column contentColumn = Column.span6();
        Column mapColumn = Column.span6();
        mapColumn.element().style.backgroundColor = "lightblue";


//        contentColumn
//            .appendChild(span().textContent("GB-Nr.: "))
//            .appendChild(span().textContent("198"));
        
        contentColumn
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("GB-Nr.:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent("198")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("E-GRID:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent("CH955832730623"))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Gemeinde:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent("Messen")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Grundbuch:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent("Messen"))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("BFS-Nr:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent("2457")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("NBIdent:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent("SO0200002457"))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Grundstücksart:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(age.toString())))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Grundstücksfläche:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(fmtDefault.format(19897)+ " m").add(span().css("sup").textContent("2")))));
        
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