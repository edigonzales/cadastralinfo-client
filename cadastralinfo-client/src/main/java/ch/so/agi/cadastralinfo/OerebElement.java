package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.Elements.body;

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
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.lists.ListGroup;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Elevation;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.gwtproject.i18n.client.NumberFormat;
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
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.dom.URL;
import elemental2.dom.URLSearchParams;

public class OerebElement implements IsElement<HTMLElement> {
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private Loader loader;
    private final HTMLElement root;
    private HTMLDivElement container;
    private Tab tabConcerned;
    private Tab tabNotConcerned;
    private Tab tabWithout;
    
    private String egrid;
    private List<ConcernedTheme> concernedThemes;
    private List<String> notConcernedThemes;
    private List<String> withoutThemes;

    private List<String> themesOrderingList = Stream.of(
            "ch.SO.NutzungsplanungGrundnutzung", "ch.SO.NutzungsplanungUeberlagernd",
            "ch.SO.NutzungsplanungSondernutzungsplaene", "ch.SO.Baulinien", "MotorwaysProjectPlaningZones",
            "MotorwaysBuildingLines", "RailwaysProjectPlanningZones", "RailwaysBuildingLines",
            "AirportsProjectPlanningZones", "AirportsBuildingLines", "AirportsSecurityZonePlans", "ContaminatedSites",
            "ContaminatedMilitarySites", "ContaminatedCivilAviationSites", "ContaminatedPublicTransportSites",
            "GroundwaterProtectionZones", "GroundwaterProtectionSites", "NoiseSensitivityLevels", "ForestPerimeters",
            "ForestDistanceLines", "ch.SO.Einzelschutz")
            .collect(Collectors.toList());

    public OerebElement() {
        root = div().id("oereb-element").element();
    }
    
    public void update(String egrid, String oerebServiceBaseUrl) {
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
            
            
            //console.log(xml);
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
                            theme.getLaws().add(document);
                        } else if (documentType.equalsIgnoreCase("Hint")) {
                            theme.getHints().add(document);
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
              //String cardTitle = theme.getSubtheme()==null ? theme.getName() : theme.getSubtheme(); 
              Card card = Card.create(theme.getName())
                      .setCollapsible()
                      .collapse()
                      .elevate(Elevation.LEVEL_0);

              concernedContainer.appendChild(card.element());
              
              card.getBody().addHideListener(new HideCompletedHandler() {
                  @Override
                  public void onHidden() {
                      console.log("hidden3");

                  }
              });
              
              card.getBody().addShowListener(new ShowCompletedHandler() {
                  @Override
                  public void onShown() {
                      console.log("show3");
                  }
              });

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
    
    @Override
    public HTMLElement element() {
        return root;
    }
}
