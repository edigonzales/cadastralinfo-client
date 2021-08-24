package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.location;
import static elemental2.dom.DomGlobal.fetch;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
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
import org.jboss.elemento.HtmlContentBuilder;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.MapElement;

//import com.google.gwt.xml.client.Document;
//import com.google.gwt.xml.client.Element;
//import com.google.gwt.xml.client.XMLParser;
import org.gwtproject.xml.client.Document;
import org.gwtproject.xml.client.Element;
import org.gwtproject.xml.client.XMLParser;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsString;
import elemental2.core.JsNumber;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Headers;
import elemental2.dom.Location;
import elemental2.dom.Node;
import elemental2.dom.RequestInit;
import elemental2.dom.URL;
import elemental2.dom.URLSearchParams;
import elemental2.dom.Window;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ol.Coordinate;
import ol.Extent;
import ol.Feature;
import ol.FeatureOptions;
import ol.Map;
import ol.MapBrowserEvent;
import ol.OLFactory;
import ol.Overlay;
import ol.OverlayOptions;
import ol.View;
import ol.format.GeoJson;
import ol.geom.Geometry;
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

    // Application configuration
    private String myVar;
    private String AV_SERVICE_BASE_URL;
    private String GB_SERVICE_BASE_URL;
    private String OEREB_SERVICE_BASE_URL; // = "https://geo.so.ch/api/oereb/"; //extract/reduced/xml/CH857632820629
    private String GWR_SERVICE_BASE_URL;
    private String SEARCH_SERVICE_URL = "https://geo.so.ch/api/search/v2/?filter=ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge,ch.so.agi.av.grundstuecke.rechtskraeftig&searchtext=";    
    private String DATA_SERVICE_URL = "https://geo.so.ch/api/data/v1/";

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
    private HTMLElement mapElement;
    private Overlay realEstatePopup;

    private SuggestBox suggestBox;
    //private Feature parcel;
    //private String egrid = null;
    
    private TabsPanel tabsPanel;
    private Tab tabAv;
    private AvElement avElement;
    private GrundbuchElement grundbuchElement;
    private OerebElement oerebElement;
    private Loader loader;
    private int processed = 0;
    
	public void onModuleLoad() {
        DomGlobal.fetch("/settings")
        .then(response -> {
            if (!response.ok) {
                return null;
            }
            return response.text();
        })
        .then(json -> {
            //console.log(json);
            JsPropertyMap<?> parsed = Js.cast(Global.JSON.parse(json));      
            AV_SERVICE_BASE_URL = Js.asString(parsed.get("avServiceBaseUrl"));
            OEREB_SERVICE_BASE_URL = Js.asString(parsed.get("oerebServiceBaseUrl"));
            GWR_SERVICE_BASE_URL = Js.asString(parsed.get("gwrServiceBaseUrl"));
            
            init();
            
            return null;
        }).catch_(error -> {
            loader.stop();
            console.log(error);
            return null;
        });
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
        
        container.addEventListener("processed", new EventListener() {
            @Override
            public void handleEvent(Event evt) {                
                processed += 1;
                if (processed == 3) {
                    processed = 0;
                    mapElement.style.pointerEvents = "auto";
                }
            }
        });

        Location location = DomGlobal.window.location;
        if (location.pathname.length() > 1) {
            location.pathname += "/"; 
        }
        HTMLElement logoDiv = div().css("logo")
                .add(div()
                        .add(img().attr("src", location.protocol + "//" + location.host + location.pathname + "Logo.png").attr("alt", "Logo Kanton")).element()).element();
        container.appendChild(logoDiv);
        
        HTMLElement searchContainerDiv = div().id("search-container").element();
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
        suggestBox.setFocusColor(Color.RED_DARKEN_3);
        suggestBox.setFocusOnClose(false);
        
        HTMLElement resetIcon = Icons.ALL.close().setId("SearchResetIcon").element();
        resetIcon.style.cursor = "pointer";
        resetIcon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                HTMLInputElement el =(HTMLInputElement) suggestBox.getInputElement().element();
                el.value = "";
                suggestBox.unfocus();
                reset();
            }
        });
        suggestBox.addRightAddOn(resetIcon);

        suggestBox.getInputElement().setAttribute("autocomplete", "off");
        suggestBox.getInputElement().setAttribute("spellcheck", "false");
        DropDownMenu suggestionsMenu = suggestBox.getSuggestionsMenu();
        suggestionsMenu.setPosition(new DropDownPositionDown());
        suggestionsMenu.setSearchable(false);
        
        suggestBox.addSelectionHandler(new MySelectionHandler());

        Button helpBtn = Button.create(Icons.ALL.help_circle_outline_mdi())
                .setSize(ButtonSize.SMALL)
                .setContent("Hilfe")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#c62828")
                .setBorder("1px #c62828 solid")
                //.setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(100)).get();
        
        searchContainerDiv.appendChild(Row.create()
                .appendChild(Column.span6()
                        .appendChild(div().id("suggestbox").add(suggestBox).element())).element());
                //.appendChild(Column.span5())
                //.appendChild(Column.span1().appendChild(div().id("help-button").add(helpBtn))).element());
        
        Row rootContentRow = Row.create().setId("root-content-row");
        container.appendChild(rootContentRow.element());

        Column mapContentCol = Column.span6().setId("map-content-col");
        rootContentRow.appendChild(mapContentCol);
        
        Column textContentCol = Column.span6().setId("text-content-col");
        rootContentRow.appendChild(textContentCol);
        
        // Add the Openlayers map (element) to the body.
        mapElement = div().id(MAP_DIV_ID).element();
        mapContentCol.appendChild(mapElement);
        map = MapPresets.getColorMap(MAP_DIV_ID);
        map.addSingleClickListener(new MapSingleClickListener());

        tabsPanel = TabsPanel.create()
                .setId("tabs-panel")
                .setBackgroundColor(Color.RED_DARKEN_3)
                .setColor(Color.WHITE);

        tabAv = Tab.create("AMTLICHE VERMESSUNG");
        avElement = new AvElement(map, AV_SERVICE_BASE_URL, GWR_SERVICE_BASE_URL);
        tabAv.appendChild(avElement);
        
        Tab tabGrundbuch = Tab.create("GRUNDBUCH");
        grundbuchElement = new GrundbuchElement();
        tabGrundbuch.appendChild(grundbuchElement);
        
        Tab tabOereb = Tab.create("ÖREB-KATASTER");
        oerebElement = new OerebElement(map);
        tabOereb.appendChild(oerebElement);
        
        Tab tabHelp = Tab.create(Icons.ALL.help_circle_outline_mdi());
        tabHelp.setId("help-tab");
        HelpElement helpElement = new HelpElement();
        tabHelp.appendChild(helpElement);
                
        tabsPanel.appendChild(tabAv);
        tabsPanel.appendChild(tabGrundbuch);
        tabsPanel.appendChild(tabOereb);
        tabsPanel.appendChild(tabHelp);
        tabsPanel.activateTab(tabHelp, false);
                
        textContentCol.appendChild(tabsPanel.element());
             
        String href = DomGlobal.window.location.href;
        URL url = new URL(href);
        URLSearchParams params = url.searchParams;

        if (params.get("egrid") != null) {
            String egrid = params.get("egrid");
            reset();
            avElement.reset();
            grundbuchElement.reset();
            oerebElement.reset();

            RequestInit requestInit = RequestInit.create();
            Headers headers = new Headers();
            headers.append("Content-Type", "application/x-www-form-urlencoded");
            requestInit.setHeaders(headers);
            
            DomGlobal.fetch(SEARCH_SERVICE_URL + egrid.trim().toLowerCase(), requestInit)
            .then(response -> {
                if (!response.ok) {
                    return null;
                }
                return response.text();
            })
            .then(json -> {
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

                        String requestUrl = DATA_SERVICE_URL + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+featureId+"]]";
                        
                        DomGlobal.fetch(requestUrl, requestInit)
                        .then(response -> {
                            if (!response.ok) {
                                return null;
                            }
                            return response.text();
                        })
                        .then(result -> {                            
                            Feature[] features = (new GeoJson()).readFeatures(result); 
                            Feature[] fs = new Feature[] {features[0]};
                            addFeaturesToHighlightingVectorLayer(fs);
                            
                            avElement.reset();
                            grundbuchElement.reset();
                            oerebElement.reset();
                            avElement.update(egrid);
                            grundbuchElement.update(egrid, AV_SERVICE_BASE_URL);
                            oerebElement.update(egrid, OEREB_SERVICE_BASE_URL);
                            updateUrlLocation(egrid);
                            
                            if (tabsPanel.getActiveTab().getId().equalsIgnoreCase("help-tab")) {
                                tabsPanel.activateTab(0);
                            }

                            return null;
                        }).catch_(error -> {
                            console.log(error);
                            return null;
                        });

                        // TODO: method
                        // Zoom to feature.
                        Extent extent = new Extent(bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3));
                        View view = map.getView();
                        double resolution = view.getResolutionForExtent(extent);
                        view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 1);
                        double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                        double y = extent.getLowerLeftY() + extent.getHeight() / 2;
                        view.setCenter(new Coordinate(x,y));
                    }
                }
                return null;
            }).catch_(error -> {
                console.log(error);
                return null;
            });
        }
        //console.log("fubar");
	}
		
	
	private void updateUrlLocation(String egrid) {
	    URL url = new URL(DomGlobal.location.href);
        String host = url.host;
        String protocol = url.protocol;
        String pathname = url.pathname;
        URLSearchParams params = url.searchParams;
        params.set("egrid", egrid);

        String newUrl = protocol + "//" + host + pathname + "?" + params.toString(); 
        updateUrlWithoutReloading(newUrl);
	}
	
	private void reset() {
        removeHighlightVectorLayer();
        avElement.reset();
        grundbuchElement.reset();
        oerebElement.reset();
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

        VectorOptions vectorSourceOptions = OLFactory.createOptions();
        Vector vectorSource = new Vector(vectorSourceOptions);
        
        VectorLayerOptions vectorLayerOptions = OLFactory.createOptions();
        vectorLayerOptions.setSource(vectorSource);
        vectorLayerOptions.setStyle(style);
        ol.layer.Vector vectorLayer = new ol.layer.Vector(vectorLayerOptions);
        vectorLayer.set(ID_ATTR_NAME, HIGHLIGHT_VECTOR_LAYER_ID);
        vectorLayer.setZIndex(10000);
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
    
    public final class MapSingleClickListener implements ol.event.EventListener<MapBrowserEvent> {
        @Override
        public void onEvent(MapBrowserEvent event) {
            mapElement.style.pointerEvents = "none";
            Coordinate coordinate = event.getCoordinate();
            
            RequestInit requestInit = RequestInit.create();
            Headers headers = new Headers();
            headers.append("Content-Type", "application/x-www-form-urlencoded");
            requestInit.setHeaders(headers);
            
            DomGlobal.fetch(DATA_SERVICE_URL + "ch.so.agi.av.grundstuecke.rechtskraeftig/?bbox="+coordinate.getX()+","+coordinate.getY()+","+coordinate.getX()+","+coordinate.getY(), requestInit)
            .then(response -> {
                if (!response.ok) {
                    return null;
                }
                return response.text();
            })
            .then(json -> {
                Feature[] features = (new GeoJson()).readFeatures(json); 
                
                Geometry geometry;
                if (features.length > 1 && event != null) {
                    HTMLElement closeButton = span().add(Icons.ALL.close()).element(); 
                    closeButton.style.cursor = "pointer";
                    
                    HtmlContentBuilder<HTMLDivElement> popupBuilder = div().id("realestate-popup");
                    popupBuilder.add(
                            div().id("realestate-popup-header")
                            .add(span().textContent("Grundstücke"))
                            .add(span().id("realestate-popup-close").add(closeButton))
                            ); 

                    HashMap<String, String> egridMap = new HashMap<String, String>();
                    HashMap<String, Feature> featureMap = new HashMap<String, Feature>();
                    for (Feature feature : features) {
                        //console.log(feature);
                        String egrid = Js.asString(feature.getProperties().get("egrid"));
                        String number = Js.asString(feature.getProperties().get("nummer"));
                        String type = Js.asString(feature.getProperties().get("art_txt"));
                        egridMap.put(egrid, egrid);
                        featureMap.put(egrid, feature);

                        String label = new String("GB-Nr.: " + number + " ("+type+")");
                        HTMLDivElement row = div().id(egrid).css("realestate-popup-row")
                                .add(span().textContent(label)).element();
                        
                        bind(row, mouseover, evt -> {
                            row.style.backgroundColor = "#efefef";
                            row.style.cursor = "pointer";
                            Feature[] fs = new Feature[] {feature};
                            addFeaturesToHighlightingVectorLayer(fs);
                        });

                        bind(row, mouseout, evt -> {
                            row.style.backgroundColor = "white";
                        });
                        
                        bind(row, click, evt -> {
                            //console.log("Get extract from a map click (multiple click result): " + row.getAttribute("id"));                            
                            map.removeOverlay(realEstatePopup);
                            
                            Feature f = featureMap.get(row.getAttribute("id"));
                            Feature[] fs = new Feature[] {f};
                            addFeaturesToHighlightingVectorLayer(fs);
                            
                            Extent extent = f.getGeometry().getExtent();
                            View view = map.getView();
                            double resolution = view.getResolutionForExtent(extent);
                            view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 1);
                            double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                            double y = extent.getLowerLeftY() + extent.getHeight() / 2;
                            view.setCenter(new Coordinate(x,y));

                            avElement.reset();
                            grundbuchElement.reset();
                            oerebElement.reset();
                            avElement.update(egridMap.get(row.getAttribute("id")));
                            grundbuchElement.update(egridMap.get(row.getAttribute("id")), AV_SERVICE_BASE_URL);
                            oerebElement.update(egridMap.get(row.getAttribute("id")), OEREB_SERVICE_BASE_URL);
                            updateUrlLocation(egrid);
                            
                            if (tabsPanel.getActiveTab().getId().equalsIgnoreCase("help-tab")) {
                                tabsPanel.activateTab(0);
                            }
                        });                        
                        popupBuilder.add(row);
                    }
                    
                    HTMLElement popupElement = popupBuilder.element();     
                    bind(closeButton, click, evt -> {
                        map.removeOverlay(realEstatePopup);
                    });
                    
                    DivElement overlay = Js.cast(popupElement);
                    OverlayOptions overlayOptions = OLFactory.createOptions();
                    overlayOptions.setElement(overlay);
                    overlayOptions.setPosition(event.getCoordinate());
                    overlayOptions.setOffset(OLFactory.createPixel(0, 0));
                    realEstatePopup = new Overlay(overlayOptions);
                    map.addOverlay(realEstatePopup);
                } else {
                    String egrid = Js.asString(features[0].getProperties().get("egrid"));
                    
                    Extent extent = features[0].getGeometry().getExtent();
                    View view = map.getView();
                    double resolution = view.getResolutionForExtent(extent);
                    view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 1);
                    double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                    double y = extent.getLowerLeftY() + extent.getHeight() / 2;
                    view.setCenter(new Coordinate(x,y));

                    addFeaturesToHighlightingVectorLayer(features);
                    
                    avElement.reset();
                    grundbuchElement.reset();
                    oerebElement.reset();
                    avElement.update(egrid);
                    grundbuchElement.update(egrid, AV_SERVICE_BASE_URL);
                    oerebElement.update(egrid, OEREB_SERVICE_BASE_URL);
                    updateUrlLocation(egrid);
                    
                    if (tabsPanel.getActiveTab().getId().equalsIgnoreCase("help-tab")) {
                        tabsPanel.activateTab(0);
                    }
                }
                //mapElement.style.pointerEvents = "auto";
                return null;
            }).catch_(error -> {
                mapElement.style.pointerEvents = "auto";
                console.log(error);
                return null;
            });            
        }
    }
    
    public class MySelectionHandler implements SelectionHandler {
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
                
                String requestUrl;
                if (dataproductId.equalsIgnoreCase("ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge")) {
                    List<Double> bbox = result.getBbox();                 
                    String bboxStr = bbox.get(0).toString()+","+bbox.get(1).toString()+","+bbox.get(2).toString()+","+bbox.get(3).toString();
                    requestUrl = DATA_SERVICE_URL + "ch.so.agi.av.grundstuecke.rechtskraeftig" + "/?bbox="+bboxStr;
                } else {
                    requestUrl = DATA_SERVICE_URL + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+featureId+"]]";
                }
                
                DomGlobal.fetch(requestUrl, requestInit)
                .then(response -> {
                    if (!response.ok) {
                        return null;
                    }
                    return response.text();
                })
                .then(json -> {
                    // TODO:
                    // Fall Adresssuche mehrere Resultate liefert. Welches soll automatisch (?) 
                    // verwendet werden?
                    // Auswahl? UX?
                    
                    Feature[] features = (new GeoJson()).readFeatures(json); 
                    String egrid = Js.asString(features[0].getProperties().get("egrid"));                        
                    
                    Feature[] fs = new Feature[] {features[0]};
                    addFeaturesToHighlightingVectorLayer(fs);
                    
                    avElement.reset();
                    grundbuchElement.reset();
                    oerebElement.reset();
                    avElement.update(egrid);
                    grundbuchElement.update(egrid, AV_SERVICE_BASE_URL);
                    oerebElement.update(egrid, OEREB_SERVICE_BASE_URL);
                    updateUrlLocation(egrid);
                    
                    if (tabsPanel.getActiveTab().getId().equalsIgnoreCase("help-tab")) {
                        tabsPanel.activateTab(0);
                    }
                    
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
    }

    // Update the URL in the browser without reloading the page.
    private static native void updateUrlWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
}