package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.img;
import static org.jboss.elemento.Elements.a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.collapsible.Collapsible.HideCompletedHandler;
import org.dominokit.domino.ui.collapsible.Collapsible.ShowCompletedHandler;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.lists.ListGroup;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.popover.PopupPosition;
import org.dominokit.domino.ui.popover.Tooltip;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Elevation;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.dominokit.domino.ui.utils.TextNode;
import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.gwtproject.xml.client.Element;
import org.gwtproject.xml.client.XMLParser;
import org.jboss.elemento.IsElement;

import com.google.gwt.user.client.Window;

import ch.so.agi.cadastralinfo.models.oereb.ConcernedTheme;
import ch.so.agi.cadastralinfo.models.oereb.Document;
import ch.so.agi.cadastralinfo.models.oereb.Office;
import ch.so.agi.cadastralinfo.models.oereb.ReferenceWMS;
import ch.so.agi.cadastralinfo.models.oereb.Restriction;
import ch.so.agi.cadastralinfo.xml.XMLUtils;
import elemental2.dom.CSSProperties;
import elemental2.dom.CustomEvent;
import elemental2.dom.CustomEventInit;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.dom.URL;
import elemental2.dom.URLSearchParams;
import ol.OLFactory;
import ol.layer.Base;
import ol.layer.Image;
import ol.layer.LayerOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;

public class OerebElement implements IsElement<HTMLElement> {
    private String ID_ATTR_NAME = "id";

    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private final HTMLElement root;
    private ol.Map map;
    private HTMLDivElement container;
    private Loader loader;
    private Tab tabConcerned;
    private Tab tabNotConcerned;
    private Tab tabWithout;
    
    private String egrid;
    private List<ConcernedTheme> concernedThemes;
    private List<String> notConcernedThemes;
    private List<String> withoutThemes;
    private ArrayList<String> oerebWmsLayers = new ArrayList<String>();

    private List<String> themesOrderingList = Stream.of(
            "ch.SO.NutzungsplanungGrundnutzung", "ch.SO.NutzungsplanungUeberlagernd",
            "ch.SO.NutzungsplanungSondernutzungsplaene", "ch.SO.Baulinien", "MotorwaysProjectPlaningZones",
            "MotorwaysBuildingLines", "RailwaysProjectPlanningZones", "RailwaysBuildingLines",
            "AirportsProjectPlanningZones", "AirportsBuildingLines", "AirportsSecurityZonePlans", "ContaminatedSites",
            "ContaminatedMilitarySites", "ContaminatedCivilAviationSites", "ContaminatedPublicTransportSites",
            "GroundwaterProtectionZones", "GroundwaterProtectionSites", "NoiseSensitivityLevels", "ForestPerimeters",
            "ForestDistanceLines", "ch.SO.Einzelschutz")
            .collect(Collectors.toList());

    public OerebElement(ol.Map map) {
        root = div().id("oereb-element").element();
        this.map = map;
    }
    
    public void reset() {
        if (container != null) {
            container.remove();
        }
        removeOerebWmsLayers();
    }
    
    public void update(String egrid, String oerebServiceBaseUrl) {
        removeOerebWmsLayers();
        this.egrid = egrid;
                
        if (container != null) {
            container.remove();
        }
        container = div().element();
        container.style.padding = CSSProperties.PaddingUnionType.of("10px"); 
        root.appendChild(container);
        
        loader = Loader.create(root, LoaderEffect.ROTATION).setLoadingText("");
        loader.start();

        Button pdfBtn = Button.create(Icons.ALL.file_pdf_box_outline_mdi())
                .setSize(ButtonSize.SMALL)
                .setContent("Auszug")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#c62828")
                .setBorder("1px #c62828 solid")
                .setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(100)).get();
        
        Tooltip.create(pdfBtn, "ÖREB-Katasterauszug").position(PopupPosition.TOP);
        
        pdfBtn.addClickListener(evt -> {
            Window.open(oerebServiceBaseUrl+"extract/reduced/pdf/geometry/"+egrid, "_blank", null);
        });

        container.appendChild(pdfBtn.element());
        container.appendChild(Row.create().css("empty-row-20").element());

        TabsPanel tabsPanel = TabsPanel.create()
                .setId("tabs-panel")
                .setBackgroundColor(Color.WHITE)
                .setColor(Color.RED_DARKEN_3);
        
        tabConcerned = Tab.create("Betroffene Themen".toUpperCase());
        tabNotConcerned = Tab.create("Nicht betroffene Themen".toUpperCase());
        tabWithout = Tab.create("Nicht vorhandene Themen".toUpperCase());
        
        tabsPanel.appendChild(tabConcerned);
        tabsPanel.appendChild(tabNotConcerned);
        tabsPanel.appendChild(tabWithout);
        container.appendChild(tabsPanel.element());

        
        DomGlobal.fetch("/oereb?egrid="+this.egrid)
        .then(response -> {
            if (!response.ok) {
                return null;
            }
            return response.text();
        })
        .then(xml -> {
            parseResponse(xml);
            renderResponse();

            // TODO: abstrakte klasse
            CustomEventInit eventInit = CustomEventInit.create();
            eventInit.setBubbles(true);
            CustomEvent event = new CustomEvent("processed", eventInit);
            root.dispatchEvent(event);

            return null;
        }).catch_(error -> {
            loader.stop();
            console.log(error);
            return null;
        });

        //loader.stop();
    }
    
    private void parseResponse(String xml) {
        org.gwtproject.xml.client.Document doc = XMLParser.parse(xml);
        
        // Concerned themes
        
        // Funktioniert für V1.0 nur für Kanton SO und solche, die die Subthemen gleich abhandeln.
        // In OEREB v2.0 ist es eindeutig spezifiziert und funktioniert schweizweit.
        // Theme/Text/Text entspricht im GUI einem Element zum Aufklappen (z.B. Nutzungsplanung überlagernd).
        // Ein Aufklapp-Element entspricht einem ConcernedTheme-Objekt. Ein solches Objekt kann
        // beliebig viele Restrictions haben.
        // Eine Restriction wird über typeCode und typeCodelist gruppiert.
        // Annahme: Restrictions mit gleichem TypeTuple haben die gleichen Dokumente (so falsch
        // ist das nicht, oder sogar definitiv logisch/richtig?)
        
        HashMap<String,ConcernedTheme> concernedThemesMap = new HashMap<>();
        List<Element> restrictionOnLandownershipList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Extract/RealEstate/RestrictionOnLandownership", restrictionOnLandownershipList);
        for (Element element : restrictionOnLandownershipList) {
            String themeName = XMLUtils.getElementValueByPath(element, "Theme/Text/Text");
            if (!concernedThemesMap.containsKey(themeName)) {
                ConcernedTheme theme = new ConcernedTheme();
                theme.setCode(XMLUtils.getElementValueByPath(element, "Theme/Code"));
                theme.setName(themeName);
                theme.setSubtheme(XMLUtils.getElementValueByPath(element, "SubTheme"));                
                
                // WMS
                theme.setLegendAtWeb(XMLUtils.getElementValueByPath(element, "Map/LegendAtWeb"));
                String layerOpacity = XMLUtils.getElementValueByPath(element, "Map/layerOpacity");
                String layerIndex = XMLUtils.getElementValueByPath(element, "Map/layerIndex");
                
                // TODO: 
                // - map-Param
                // - port 
                // - params: case insensitive. mit entries() loopen
                String wms = XMLUtils.getElementValueByPath(element, "Map/ReferenceWMS");
                URL wmsUrl = new URL(wms);                
                String host = wmsUrl.host;
                String protocol = wmsUrl.protocol;
                String pathname = wmsUrl.pathname;
                URLSearchParams params = wmsUrl.searchParams;
                String layers = params.get("LAYERS");
                String imageFormat = params.get("FORMAT");
                String baseUrl = protocol + "//" + host + pathname;

                ReferenceWMS referenceWMS = new ReferenceWMS();
                referenceWMS.setBaseUrl(baseUrl);
                referenceWMS.setImageFormat(imageFormat);
                referenceWMS.setLayers(layers);
                referenceWMS.setLayerOpacity(Double.valueOf(layerOpacity));
                referenceWMS.setLayerIndex(Integer.valueOf(layerIndex));
                theme.setReferenceWMS(referenceWMS);
                
                // ResponsibleOffice
                List<Element> officeList = new ArrayList<Element>();
                XMLUtils.getElementsByPath(element, "ResponsibleOffice", officeList);
                for (Element officeElement : officeList) {
                    String officeName = XMLUtils.getElementValueByPath(officeElement, "Name/LocalisedText/Text");
                    String officeAtWeb = XMLUtils.getElementValueByPath(officeElement, "OfficeAtWeb");
                    Office office = new Office();
                    office.setName(officeName);
                    office.setOfficeAtWeb(officeAtWeb);
                    theme.getResponsibleOffice().add(office);
                }
                concernedThemesMap.put(themeName, theme);
            }
                  
            String typeCode = XMLUtils.getElementValueByPath(element, "TypeCode");
            String typeCodelist = XMLUtils.getElementValueByPath(element, "TypeCodelist");
            TypeTuple typeTuple = new TypeTuple(typeCode, typeCodelist);
            
            String areaShare = XMLUtils.getElementValueByPath(element, "AreaShare");
            String partInPercent = XMLUtils.getElementValueByPath(element, "PartInPercent");
            String lengthShare = XMLUtils.getElementValueByPath(element, "LengthShare");
            String nrOfPoints = XMLUtils.getElementValueByPath(element, "NrOfPoints");

            ConcernedTheme theme = concernedThemesMap.get(themeName);
            if(theme.getRestrictions().containsKey(typeTuple)) {
                //console.log(typeTuple + " bereits vorhanden");
                
                Restriction restriction = theme.getRestrictions().get(typeTuple);
                if (areaShare != null) {
                    restriction.updateAreaShare(Integer.valueOf(areaShare));
                }
                if (partInPercent != null) {
                    restriction.updatePartInPercent(Double.valueOf(partInPercent));
                }
                if (lengthShare != null) {
                    restriction.updateLengthShare(Integer.valueOf(lengthShare));
                }
                if (nrOfPoints != null) {
                    restriction.updateNrOfPoints(Integer.valueOf(nrOfPoints));
                }                
            } else {
                Restriction restriction = new Restriction();                
                restriction.setInformation(XMLUtils.getElementValueByPath(element, "Information/LocalisedText/Text"));
                restriction.setTypeCode(XMLUtils.getElementValueByPath(element, "typeCode"));
                restriction.setTypeCodelist(XMLUtils.getElementValueByPath(element, "typeCodelist"));
                restriction.setSymbolRef(XMLUtils.getElementValueByPath(element, "SymbolRef"));

                if (areaShare != null) {
                    restriction.setAreaShare(Integer.valueOf(areaShare));
                }
                if (partInPercent != null) {
                    restriction.setPartInPercent(Double.valueOf(partInPercent));
                }
                if (lengthShare != null) {
                    restriction.setLengthShare(Integer.valueOf(lengthShare));
                }
                if (nrOfPoints != null) {
                    restriction.setNrOfPoints(Integer.valueOf(nrOfPoints));
                }
                theme.getRestrictions().put(typeTuple, restriction);
                
                List<Element> legalProvisionsList = new ArrayList<Element>();
                XMLUtils.getElementsByPath(element, "LegalProvisions", legalProvisionsList);
                for (Element legalProvisionsElement : legalProvisionsList) {                    
                    // unique key
                    //String textAtWeb = XMLUtils.getElementValueByPath(legalProvisionsElement, "TextAtWeb/LocalisedText/Text");

                    Document document = new Document();
                    document.setAbbreviation(XMLUtils.getElementValueByPath(legalProvisionsElement, "Abbreviation/LocalisedText/Text"));
                    document.setOfficialNumber(XMLUtils.getElementValueByPath(legalProvisionsElement, "OfficialNumber"));
                    document.setOfficialTitle(XMLUtils.getElementValueByPath(legalProvisionsElement, "OfficialTitle/LocalisedText/Text"));
                    document.setTextAtWeb(XMLUtils.getElementValueByPath(legalProvisionsElement, "TextAtWeb/LocalisedText/Text"));
                    document.setTitle(XMLUtils.getElementValueByPath(legalProvisionsElement, "Title/LocalisedText/Text"));
                    
                    theme.getLegalProvisions().add(document);
                    
                    // Gesetze und Hinweise
                    List<Element> referenceList = new ArrayList<Element>();
                    XMLUtils.getElementsByPath(legalProvisionsElement, "Reference", referenceList);
                    for (Element referenceElement : referenceList) {
                        String documentType = XMLUtils.getElementValueByPath(referenceElement, "DocumentType");
                        
                        Document referenceDocument = new Document();
                        referenceDocument.setAbbreviation(XMLUtils.getElementValueByPath(referenceElement, "Abbreviation/LocalisedText/Text"));
                        referenceDocument.setOfficialNumber(XMLUtils.getElementValueByPath(referenceElement, "OfficialNumber"));
                        referenceDocument.setOfficialTitle(XMLUtils.getElementValueByPath(referenceElement, "OfficialTitle/LocalisedText/Text"));
                        referenceDocument.setTextAtWeb(XMLUtils.getElementValueByPath(referenceElement, "TextAtWeb/LocalisedText/Text"));
                        referenceDocument.setTitle(XMLUtils.getElementValueByPath(referenceElement, "Title/LocalisedText/Text"));

                        if (documentType.equalsIgnoreCase("Law")) {
                            theme.getLaws().add(referenceDocument);
                        } else if (documentType.equalsIgnoreCase("Hint")) {
                            theme.getHints().add(referenceDocument);
                        } 
                    }                    
                } 
            } 
        }
        concernedThemes = new ArrayList<ConcernedTheme>(concernedThemesMap.values());
        concernedThemes.sort(compare);
        
        // Not concerned themes
        List<Element> notConcernedThemesList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Extract/NotConcernedTheme", notConcernedThemesList);
        notConcernedThemes = new ArrayList<String>();
        for (Element element : notConcernedThemesList) {
            notConcernedThemes.add(XMLUtils.getElementValueByPath(element, "Text/Text"));
        }
        notConcernedThemes.sort(String.CASE_INSENSITIVE_ORDER);
        
        // Themes without data
        List<Element> withoutThemesList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Extract/ThemeWithoutData", withoutThemesList);
        withoutThemes = new ArrayList<String>();
        for (Element element : withoutThemesList) {
            withoutThemes.add(XMLUtils.getElementValueByPath(element, "Text/Text"));
        }
        withoutThemes.sort(String.CASE_INSENSITIVE_ORDER);
    }
    
    private void renderResponse() {        
        {
            HTMLDivElement concernedContainer = div().element();
            tabConcerned.appendChild(concernedContainer);

            for (ConcernedTheme theme : concernedThemes) {
                Image wmsLayer = createOerebWmsLayer(theme.getReferenceWMS());
                map.addLayer(wmsLayer);

                String layerId = theme.getReferenceWMS().getLayers();
                oerebWmsLayers.add(layerId);                    

                
                Card card = Card.create(theme.getName())
                        .setId(theme.getName())
                        .setCollapsible()
                        .collapse()
                        .elevate(Elevation.LEVEL_0);
                concernedContainer.appendChild(card.element());

                card.getBody().addHideListener(new HideCompletedHandler() {
                    @Override
                    public void onHidden() {
                        Image wmsLayer = (Image) getMapLayerById(layerId);
                        wmsLayer.setVisible(false); 
                    }
                });

                card.getBody().addShowListener(new ShowCompletedHandler() {
                    @Override
                    public void onShown() {
                        Image wmsLayer = (Image) getMapLayerById(layerId);
                        wmsLayer.setVisible(true); 
                    }
                });

                card
                .appendChild(Row.create().css("content-row-slim")
                        .appendChild(Column.span7()
                                .appendChild(span().css("content-table-header-sm").textContent("Typ")))
                        .appendChild(Column.span1()
                                .appendChild(span().css("content-table-header-sm").textContent("")))
                        .appendChild(Column.span2()
                                .appendChild(span().css("content-table-header-sm right-align").textContent("Anteil")))
                        .appendChild(Column.span2()
                                .appendChild(span().css("content-table-header-sm right-align").textContent("Anteil in %"))));

                for(Map.Entry<TypeTuple, Restriction> entry : theme.getRestrictions().entrySet()) {
                    Restriction restriction = entry.getValue();
                    
                    String share = "";
                    String partInPercent = "";
                    if (restriction.getAreaShare() != null) {
                        if (restriction.getAreaShare() < 0.1) {
                            share = "< 0.1 m<span class=\"sup\">2</span>";
                            partInPercent = "-";
                        } else {
                            share = fmtInteger.format(restriction.getAreaShare()) + " m<span class=\"sup\">2</span>";
                            partInPercent = fmtPercent.format(restriction.getPartInPercent());
                        }
                    } else if (restriction.getLengthShare() != null) {
                        if (restriction.getLengthShare() < 0.1) {
                            share = "< 0.1 m";
                        } else {
                            share = fmtInteger.format(restriction.getLengthShare()) + " m";
                        }                        
                    } else if (restriction.getNrOfPoints() != null) {
                        share = fmtInteger.format(restriction.getNrOfPoints());
                    }
                    
                    HTMLElement symbol = img().attr("src", restriction.getSymbolRef())
                            .attr("alt", "Symbol " + restriction.getInformation())
                            .attr("width", "30px")
                            .style("border: 1px solid black").element();
                    
                    Row row = Row.create().css("content-row")
                            .appendChild(Column.span7()
                                    .appendChild(span().css("content-value").textContent(restriction.getInformation())))
                            .appendChild(Column.span1()
                                    .appendChild(span().css("content-value").add(symbol)))
                            .appendChild(Column.span2()
                                    .appendChild(span().css("content-value right-align").innerHtml(SafeHtmlUtils.fromTrustedString(share))))
                            .appendChild(Column.span2()
                                     .appendChild(span().css("content-value right-align").textContent(partInPercent)));

                    card.appendChild(row);
                }
                
                if (theme.getLegendAtWeb() != null) {
                    card.appendChild(Row.create().css("empty-row-10", "stripline"));
                    card.appendChild(Row.create().css("empty-row-10"));
                    
                    HTMLElement legendLink = a()
                            .attr("class", "default-link")
                            .add(TextNode.of("Vollständige Legende anzeigen")).element();
                    card.appendChild(legendLink);
                    
                    HTMLElement legendImage = img()
                            .attr("src", theme.getLegendAtWeb())
                            .attr("alt", "Legende").element();
                    legendImage.style.display = "none";
                    card.appendChild(legendImage);

                    legendLink.addEventListener("click", new EventListener() {
                        @Override
                        public void handleEvent(Event evt) {
                            if (legendImage.style.display == "none") {
                                legendImage.style.display = "block";
                                legendLink.innerHTML = "Vollständige Legende verbergen";
                            } else {
                                legendImage.style.display = "none";
                                legendLink.innerHTML = "Vollständige Legende anzeigen";
                            }
                        }
                    });
                }
                
                card.appendChild(Row.create().css("empty-row-10", "stripline"));
                card.appendChild(Row.create().css("empty-row-10"));
                card.appendChild(Row.create().css("content-row")
                        .appendChild(Column.span12()
                                .appendChild(span().css("content-key").textContent("Rechtsvorschriften:"))));

                for (Document document : theme.getLegalProvisions()) {
                    String linkName;
                    if (document.getOfficialTitle() != null) {
                        linkName = document.getOfficialTitle();
                    } else {
                        linkName = document.getTitle();
                    }
                    HTMLElement link = a().css("default-link")
                            .attr("href", document.getTextAtWeb())
                            .attr("target", "_blank")
                            .add(TextNode.of(linkName)).element();
                    card.appendChild(div().add(link).element());

                    String additionalText = document.getTitle();
                    if (document.getOfficialNumber() != null) {
                        additionalText += " Nr. " + document.getOfficialNumber();
                    }
                    card.appendChild(div().add(TextNode.of(additionalText)).element());
                    card.appendChild(div().css("empty-row-5").element());
                }
                
                card.appendChild(Row.create().css("empty-row-10"));
                card.appendChild(Row.create().css("content-row")
                        .appendChild(Column.span12()
                                .appendChild(span().css("content-key").textContent("Gesetze:"))));

                for (Document document : theme.getLaws()) {
                    String linkName;
                    if (document.getOfficialTitle() != null) {
                        linkName = document.getOfficialTitle();
                    } else {
                        linkName = document.getTitle();
                    }
                    
                    if (document.getAbbreviation() != null) {
                        linkName += " (" + document.getAbbreviation() + ")";
                    }
                    
                    if (document.getOfficialNumber() != null) {
                        linkName += ", " + document.getOfficialNumber();
                    }

                    HTMLElement link = a().css("default-link")
                            .attr("href", document.getTextAtWeb())
                            .attr("target", "_blank")
                            .add(TextNode.of(linkName)).element();
                    card.appendChild(div().add(link).element());
                    card.appendChild(div().css("empty-row-5").element());
                }
                
                card.appendChild(Row.create().css("empty-row-10", "stripline"));
                card.appendChild(Row.create().css("empty-row-10"));
                card.appendChild(Row.create().css("content-row")
                        .appendChild(Column.span12()
                                .appendChild(span().css("content-key").textContent("Zuständige Stelle:"))));

                for (Office office : theme.getResponsibleOffice()) {
                    HTMLElement link = a().css("default-link")
                            .attr("href", office.getOfficeAtWeb())
                            .attr("target", "_blank")
                            .add(TextNode.of(office.getName())).element();
                    card.appendChild(div().add(link).element());
                    card.appendChild(div().css("empty-row-5").element());
                }

                
            }

        }
        
        
        {
            HTMLDivElement notConcernedContainer = div().element();       
            ListGroup<String> listGroup = ListGroup.<String>create()
                    .setBordered(false)
                    .setItemRenderer((listGroup1, listItem) -> {
                        listItem.appendChild(div()
                                .css(Styles.padding_10)
                                .css("themes-list")
                                .add(span().textContent(listItem.getValue())));                        
                    })
                    .setItems(notConcernedThemes);
            notConcernedContainer.appendChild(listGroup.element());       
            tabNotConcerned.appendChild(notConcernedContainer);
        }
        
        {
            HTMLDivElement withoutContainer = div().element();       
            ListGroup<String> listGroup = ListGroup.<String>create()
                    .setBordered(false)
                    .setItemRenderer((listGroup1, listItem) -> {
                        listItem.appendChild(div()
                                .css(Styles.padding_10)
                                .css("themes-list")
                                .add(span().textContent(listItem.getValue())));                        
                    })
                    .setItems(withoutThemes);
            withoutContainer.appendChild(listGroup.element());       
            tabWithout.appendChild(withoutContainer);
        }
        loader.stop();
    }
    
    private Comparator<ConcernedTheme> compare = new Comparator<ConcernedTheme>() {
        public int compare(ConcernedTheme t1, ConcernedTheme t2) {
            if (t1.getSubtheme() != null && t2.getSubtheme() == null) {
                return themesOrderingList.indexOf(t1.getSubtheme()) - themesOrderingList.indexOf(t2.getCode());
            }

            if (t2.getSubtheme() != null && t1.getSubtheme() == null) {
                return themesOrderingList.indexOf(t1.getCode()) - themesOrderingList.indexOf(t2.getSubtheme());
            }

            if (t1.getSubtheme() != null && t2.getSubtheme() != null) {
                return themesOrderingList.indexOf(t1.getSubtheme()) - themesOrderingList.indexOf(t2.getSubtheme());
            }
            return themesOrderingList.indexOf(t1.getCode()) - themesOrderingList.indexOf(t2.getCode());
        }
    };
    
    private Image createOerebWmsLayer(ReferenceWMS referenceWms) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(referenceWms.getLayers());

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();

        String baseUrl = referenceWms.getBaseUrl();

        imageWMSOptions.setUrl(baseUrl);
        imageWMSOptions.setParams(imageWMSParams);
        imageWMSOptions.setRatio(1.5f);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        Image wmsLayer = new Image(layerOptions);
        wmsLayer.set(ID_ATTR_NAME, referenceWms.getLayers());
        wmsLayer.setVisible(false);
        wmsLayer.setOpacity(referenceWms.getLayerOpacity());
 
        return wmsLayer;
    }
    
    private void removeOerebWmsLayers() {
        for (String layerId : oerebWmsLayers) {
            Image rlayer = (Image) getMapLayerById(layerId);
            map.removeLayer(rlayer);
        }
        oerebWmsLayers.clear();
    }

    // TODO: utils
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
                //console.log(e.getMessage());
                //console.log("should not reach here");
            }
        }
        return null;
    }


    @Override
    public HTMLElement element() {
        return root;
    }
}
