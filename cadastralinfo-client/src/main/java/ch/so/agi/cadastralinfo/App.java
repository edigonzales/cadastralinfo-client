package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.location;
import static elemental2.dom.DomGlobal.fetch;
import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.dominokit.domino.ui.dropdown.DropDownMenu;
import org.dominokit.domino.ui.forms.SuggestBox;
import org.dominokit.domino.ui.forms.AbstractSuggestBox.DropDownPositionDown;
import org.dominokit.domino.ui.forms.SuggestBoxStore;
import org.dominokit.domino.ui.forms.SuggestItem;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.icons.MdiIcon;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.themes.Theme;
import org.dominokit.domino.ui.utils.HasSelectionHandler.SelectionHandler;
import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;

import com.gargoylesoftware.htmlunit.javascript.host.Console;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsString;
import elemental2.core.JsNumber;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Headers;
import elemental2.dom.Location;
import elemental2.dom.Node;
import elemental2.dom.RequestInit;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ol.Coordinate;
import ol.Extent;
import ol.Feature;
import ol.FeatureOptions;
import ol.Map;
import ol.OLFactory;
import ol.View;
import ol.format.GeoJson;
import ol.layer.Base;
import ol.layer.VectorLayerOptions;
import ol.proj.Projection;
import ol.proj.ProjectionOptions;
import ol.source.Vector;
import ol.source.VectorOptions;
import ol.style.Fill;
import ol.style.Stroke;
import ol.style.Style;
import proj4.Proj4;

public class App implements EntryPoint {

    // Application settings
    private String myVar;

    // TODO: Utils
    // Format settings 
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private static final String EPSG_2056 = "EPSG:2056";
    private static final String EPSG_4326 = "EPSG:4326"; 
    private Projection projection;
    
    private HTMLElement container;

    private String ID_ATTR_NAME = "id";
    private String HIGHLIGHT_VECTOR_LAYER_ID = "highlight_vector_layer";
    private String HIGHLIGHT_VECTOR_FEATURE_ID = "highlight_fid";
    private String MAP_DIV_ID = "map";

    private Map map;

    private SuggestBox suggestBox;
    //private Feature parcel;
    private String egrid = null;
    
    private Tab tabAv;
    private AvElement avElement;

    private String SEARCH_SERVICE_URL = "https://geo.so.ch/api/search/v2/?filter=ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge,ch.so.agi.av.grundstuecke.rechtskraeftig&searchtext=";    
    private String DATA_SERVICE_URL = "https://geo.so.ch/api/data/v1/";

    
	public void onModuleLoad() {
	    init();
	}
	
	@SuppressWarnings("unchecked")
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
               
        container = div().id("container").element();
        body().add(container);

        Location location = DomGlobal.window.location;

        HTMLElement logoDiv = div().css("logo")
                .add(div()
                        .add(img().attr("src", DomGlobal.window.location.href + "Logo.png").attr("alt", "Logo Kanton")).element())
                .element();
        container.appendChild(logoDiv);
        
        HTMLElement searchContainerDiv = div().id("search-container").element();
        //searchContainerDiv.style.backgroundColor = "wheat";
        //searchContainerDiv.style.height = CSSProperties.HeightUnionType.of("100px");
        container.appendChild(searchContainerDiv);

        SuggestBoxStore dynamicStore = new SuggestBoxStore() {
            @Override
            public void filter(String value, SuggestionsHandler suggestionsHandler) {
                if (value.trim().length() == 0) {
                    return;
                }
                
                RequestInit requestInit = RequestInit.create();
                Headers headers = new Headers();
                headers.append("Content-Type", "application/x-www-form-urlencoded");
                requestInit.setHeaders(headers);
                
                DomGlobal.fetch(SEARCH_SERVICE_URL + value.trim().toLowerCase(), requestInit)
                .then(response -> {
                    if (!response.ok) {
                        return null;
                    }
                    return response.text();
                })
                .then(json -> {
                    List<SuggestItem<SearchResult>> featureResults = new ArrayList<SuggestItem<SearchResult>>();
                    List<SuggestItem<SearchResult>> suggestItems = new ArrayList<>();
                    JsPropertyMap<?> parsed = Js.cast(Global.JSON.parse(json));
                    JsArray<?> results = Js.cast(parsed.get("results"));
                    for (int i = 0; i < results.length; i++) {
                        JsPropertyMap<?> resultObj = Js.cast(results.getAt(i));
                                                    
                        if (resultObj.has("feature")) {
                            JsPropertyMap feature = (JsPropertyMap) resultObj.get("feature");
                            String display = ((JsString) feature.get("display")).normalize();
                            String dataproductId = ((JsString) feature.get("dataproduct_id")).normalize();
                            String idFieldName = ((JsString) feature.get("id_field_name")).normalize();
                            int featureId = new Double(((JsNumber) feature.get("feature_id")).valueOf()).intValue();
                            List<Double> bbox = ((JsArray) feature.get("bbox")).asList();
 
                            SearchResult searchResult = new SearchResult();
                            searchResult.setLabel(display);
                            searchResult.setDataproductId(dataproductId);
                            searchResult.setIdFieldName(idFieldName);
                            searchResult.setFeatureId(featureId);
                            searchResult.setBbox(bbox);
                            searchResult.setType("feature");
                            
                            Icon icon;
                            if (dataproductId.contains("gebaeudeadressen")) {
                                icon = Icons.ALL.mail();
                            } else if (dataproductId.contains("grundstueck")) {
                                icon = Icons.ALL.home();
                            } else if (dataproductId.contains("flurname"))  {
                                icon = Icons.ALL.terrain();
                            } else {
                                icon = Icons.ALL.place();
                            }
                            
                            SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, searchResult.getLabel(), icon);
                            featureResults.add(suggestItem);
//                            suggestItems.add(suggestItem);                            
                        }
                    }
                    suggestItems.addAll(featureResults);
                    suggestionsHandler.onSuggestionsReady(suggestItems);
                    return null;
                }).catch_(error -> {
                    console.log(error);
                    return null;
                });
            }

            @Override
            public void find(Object searchValue, Consumer handler) {
                if (searchValue == null) {
                    return;
                }
                HTMLInputElement el =(HTMLInputElement) suggestBox.getInputElement().element();
                SearchResult searchResult = (SearchResult) searchValue;
                SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, el.value);
                handler.accept(suggestItem);
            }
        };
        
        suggestBox = SuggestBox.create("Suche: Grundstücke und Adressen", dynamicStore);
        suggestBox.addLeftAddOn(Icons.ALL.search());
        suggestBox.setAutoSelect(false);
        suggestBox.setFocusColor(Color.RED);
        suggestBox.setFocusOnClose(false);
        
        HTMLElement resetIcon = Icons.ALL.close().setId("SearchResetIcon").element();
        resetIcon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                HTMLInputElement el =(HTMLInputElement) suggestBox.getInputElement().element();
                el.value = "";
                suggestBox.unfocus();
//                ol.source.Vector vectorSource = map.getHighlightLayer().getSource();
//                vectorSource.clear(false); 
            }
        });
        suggestBox.addRightAddOn(resetIcon);

        suggestBox.getInputElement().setAttribute("autocomplete", "off");
        suggestBox.getInputElement().setAttribute("spellcheck", "false");
        DropDownMenu suggestionsMenu = suggestBox.getSuggestionsMenu();
        suggestionsMenu.setPosition(new DropDownPositionDown());
        suggestionsMenu.setSearchable(false);
        
        suggestBox.addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(Object value) {
                SuggestItem<SearchResult> item = (SuggestItem<SearchResult>) value;
                SearchResult result = (SearchResult) item.getValue();
                
                RequestInit requestInit = RequestInit.create();
                Headers headers = new Headers();
                headers.append("Content-Type", "application/x-www-form-urlencoded"); // CORS and preflight...
                requestInit.setHeaders(headers);
                
                if (result.getType().equalsIgnoreCase("feature")) {
                    String dataproductId = result.getDataproductId();
                    String idFieldName = result.getIdFieldName();
                    String featureId = String.valueOf(result.getFeatureId());
                    
                    DomGlobal.fetch(DATA_SERVICE_URL + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+featureId+"]]", requestInit)
                    .then(response -> {
                        if (!response.ok) {
                            return null;
                        }
                        return response.text();
                    })
                    .then(json -> {
                        Feature[] features = (new GeoJson()).readFeatures(json); 
                        egrid = Js.asString(features[0].getProperties().get("egrid"));                        
                        
                        addFeaturesToHighlightingVectorLayer(features);
                        addAvElement(egrid);
                        return null;
                    }).catch_(error -> {
                        console.log(error);
                        return null;
                    });
                    
                    // Zoom to feature.
                    List<Double> bbox = result.getBbox();                 
                    Extent extent = new Extent(bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3));
                    View view = map.getView();
                    double resolution = view.getResolutionForExtent(extent);
                    view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 1);
                    double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                    double y = extent.getLowerLeftY() + extent.getHeight() / 2;
                    view.setCenter(new Coordinate(x,y));
                }
            }
        });

        searchContainerDiv.appendChild(Row.create()
                .appendChild(Column.span6()
                        .appendChild(div().id("suggestbox").add(suggestBox).element())).element());
        
        Row rootContentRow = Row.create().setId("root-content-row");
        container.appendChild(rootContentRow.element());

        Column mapContentCol = Column.span6().setId("map-content-col");
        rootContentRow.appendChild(mapContentCol);
        
        Column textContentCol = Column.span6().setId("text-content-col");
        rootContentRow.appendChild(textContentCol);
        
        // Add the Openlayers map (element) to the body.
        HTMLElement mapElement = div().id(MAP_DIV_ID).css("elevation-1").element();
        mapContentCol.appendChild(mapElement);
        map = MapPresets.getColorMap(MAP_DIV_ID);

        TabsPanel tabsPanel = TabsPanel.create()
                .setId("tabs-panel")
                .setColor(Color.RED);
        
        tabAv = Tab.create("AMTLICHE VERMESSUNG");
        Tab tabGrundbuch = Tab.create("GRUNDBUCH");
        Tab tabOereb = Tab.create("ÖREB");
                
        
        tabsPanel.appendChild(tabAv);
        tabsPanel.appendChild(tabGrundbuch);
        tabsPanel.appendChild(Tab.create("ÖREB-KATASTER")
                .appendChild(b().textContent("Home Content")));
        textContentCol.appendChild(tabsPanel.element());
       
        //tabAv.appendChild(new AvElement(map, egrid).element());

        
        /*
        
        
        //this.processAv(tabAv);
        
        AvTabContent avTabContent = new AvTabContent(tabAv);
        //tabAv.appendChild(avTabContent.element());
*/
                
        console.log("fubar");
	}
	
	// TODO: Es darf nur ein AvElement geben, das upgedated wird.
	// Es braucht eine update-Methode.
	private void addAvElement(String egrid) {
	    if (avElement != null) {
	        tabAv.removeChild(avElement);
	    }
	    avElement = new AvElement(map, egrid);
	    tabAv.appendChild(avElement.element());
	}
	
	private void addFeaturesToHighlightingVectorLayer(Feature[] features) {
	    ol.layer.Vector vectorLayer = (ol.layer.Vector) getMapLayerById(HIGHLIGHT_VECTOR_LAYER_ID);
	    if (vectorLayer == null) {
	        vectorLayer = createHighlightVectorLayer();
	    }
	    Vector vectorSource = vectorLayer.getSource();
	    vectorSource.clear(false);
	    vectorSource.addFeatures(features);
	}
	
    private ol.layer.Vector createHighlightVectorLayer() {
        Style style = new Style();
        Stroke stroke = new Stroke();
        stroke.setWidth(6);
        //stroke.setColor(new ol.color.Color(249, 128, 0, 1.0));
        stroke.setColor(new ol.color.Color(230, 0, 0, 0.6));
        style.setStroke(stroke);
        //Fill fill = new Fill();
        //fill.setColor(new ol.color.Color(255, 255, 80, 0.6));
        //style.setFill(fill);

//        ol.Collection<Feature> featureCollection = new ol.Collection<Feature>();
//        for (Feature feature : features) {
//            feature.setStyle(style);
//            featureCollection.push(feature);
//        }

        VectorOptions vectorSourceOptions = OLFactory.createOptions();
        //vectorSourceOptions.setFeatures(features);
        Vector vectorSource = new Vector(vectorSourceOptions);
        
        VectorLayerOptions vectorLayerOptions = OLFactory.createOptions();
        vectorLayerOptions.setSource(vectorSource);
        vectorLayerOptions.setStyle(style);
        ol.layer.Vector vectorLayer = new ol.layer.Vector(vectorLayerOptions);
        vectorLayer.set(ID_ATTR_NAME, HIGHLIGHT_VECTOR_LAYER_ID);
        map.addLayer(vectorLayer);
        return vectorLayer;
    }
	
    private void removeHighlightVectorLayer() {
        Base vlayer = getMapLayerById(HIGHLIGHT_VECTOR_LAYER_ID);
        map.removeLayer(vlayer);
    }
    
    private Base getMapLayerById(String id) {
        ol.Collection<Base> layers = map.getLayers();
        for (int i = 0; i < layers.getLength(); i++) {
            Base item = layers.item(i);
            try {
                String layerId = item.get(ID_ATTR_NAME);
                if (layerId == null) {
                    continue;
                }
                if (layerId.equalsIgnoreCase(id)) {
                    return item;
                }
            } catch (Exception e) {
                console.log(e.getMessage());
                console.log("should not reach here");
            }
        }
        return null;
    }	
}