package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Elevation;
import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.xml.client.Document;
import org.gwtproject.xml.client.Element;
import org.gwtproject.xml.client.Node;
import org.gwtproject.xml.client.NodeList;
import org.gwtproject.xml.client.XMLParser;
import org.gwtproject.i18n.shared.DateTimeFormat;
import org.gwtproject.safehtml.shared.SafeHtmlUtils;
import org.jboss.elemento.IsElement;

import com.google.gwt.user.client.Window;

import elemental2.core.Global;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ch.so.agi.cadastralinfo.models.grundbuch.AVBemerkung;
import ch.so.agi.cadastralinfo.models.grundbuch.Anmerkung;
import ch.so.agi.cadastralinfo.models.grundbuch.BeteiligtesGrundstueck;
import ch.so.agi.cadastralinfo.models.grundbuch.Dienstbarkeit;
import ch.so.agi.cadastralinfo.models.grundbuch.Grundstueck;
import ch.so.agi.cadastralinfo.models.grundbuch.HaengigesGeschaeft;
import ch.so.agi.cadastralinfo.models.grundbuch.MutationsNummer;
import ch.so.agi.cadastralinfo.models.grundbuch.Person;
import ch.so.agi.cadastralinfo.xml.XMLUtils;

public class GrundbuchElement implements IsElement<HTMLElement> {
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private Loader loader;
    private final HTMLElement root;
    private HTMLDivElement container;
    private Card generalCard;
    private Card dominatingCard;
    private Card propertyCard;
    private Card noteCard;
    private Card servitudeCard;
    private Card realBurdenCard;
    private Card pendingCard;
    private String egrid;
    
    public GrundbuchElement() {
        root = div().id("gb-element").element();
    }
    
    public void update(String egrid, String grundbuchServiceBaseUrl) {
        this.egrid = egrid;
        
        // FIXME 
        // TODO
        // Fake E-GRID, damit man die statischen Beispiele verwenden kann.
        // Round-robin-mässig sollen verschiedene statische Auszüge angefordert
        // werden.
        List<String> egrids = new ArrayList<String>() {{
            add("CH707716772202");
        }};
        Random rand = new Random();
        this.egrid = egrids.get(rand.nextInt(egrids.size()));
        
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
                .setContent("Grundbuchauszug")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#c62828")
                .setBorder("1px #c62828 solid")
                .setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(100)).get();
               
        pdfBtn.disable();
        
        pdfBtn.addClickListener(evt -> {
            Window.open(grundbuchServiceBaseUrl+"/extract/pdf/geometry/"+egrid, "_blank", null);
        });

        container.appendChild(pdfBtn.element());
        container.appendChild(Row.create().css("empty-row-20").element());
        
        generalCard = Card.create("Allgemeine Informationen")
                .setCollapsible()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(generalCard.element());

        propertyCard = Card.create("Eigentum")
                .setCollapsible()
                //.collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(propertyCard.element());

        dominatingCard = Card.create("Dominierende Grundstücke")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(dominatingCard.element());

        noteCard = Card.create("Anmerkungen")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(noteCard.element());

        servitudeCard = Card.create("Dienstbarkeiten")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(servitudeCard.element());

        realBurdenCard = Card.create("Grundlasten")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(realBurdenCard.element());

        pendingCard = Card.create("Hängige Geschäfte")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(pendingCard.element());
        
        DomGlobal.fetch("/grundbuch?egrid="+this.egrid)
        .then(response -> {
            if (!response.ok) {
                return null;
            }
            return response.text();
        })
        .then(xml -> {
            processResponse(xml);
            return null;
        }).catch_(error -> {
            loader.stop();
            console.log(error);
            return null;
        });
        
        loader.stop();
    }
    
    private void processResponse(String xml) {        
        // Parsing XML
        Document doc = XMLParser.parse(xml);
         
        // Grundstücke
        List<Element> grundstueckeList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Body/GetParcelsByIdResponse/Grundstueck", grundstueckeList);
        
        Map<String, Grundstueck> grundstuecke = new HashMap<String, Grundstueck>();
        for (Element grundstueckElement : grundstueckeList) {
            Grundstueck grundstueck = new Grundstueck();
            NodeList childNodes = grundstueckElement.getChildNodes();
            for (int i=0; i<childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    Element childElement = (Element) childNodes.item(i);
                    
                    String nodeName = childElement.getNodeName();
                    if (nodeName.contains(":Liegenschaft")) {
                        grundstueck.setGrundstuecksart("Liegenschaft");
                    } else if (nodeName.contains(":GewoehnlichesSDR")) {
                        grundstueck.setGrundstuecksart("GewoehnlichesSDR");
                    } 
                    
                    String nummerLang = XMLUtils.getElementValueByPath((Element)childNodes.item(i), "Nummer");
                    if (nummerLang != null) {
                        String grundstueckEgrid = nummerLang.substring(0, 14);
                        if (grundstueckEgrid.equalsIgnoreCase(egrid)) {
                            grundstueck.setHauptGrundstueck(true);
                        } else {
                            grundstueck.setHauptGrundstueck(false);
                        }
                        
                        String nummerKurz = nummerLang.substring(15).replaceAll(":", " / ").replace("  ", " - ");
                        if (nummerLang.endsWith(":")) {
                            nummerKurz += "-";
                        }
                        grundstueck.setNummerKurz(nummerKurz);
                        
                        nummerLang = this.egrid + " / " + nummerKurz;
                        grundstueck.setNummerLang(nummerLang);
                                                
                        grundstueck.setEgrid(grundstueckEgrid);
                    }
                    
                    String gbPlanNummer = XMLUtils.getElementValueByPath((Element)childNodes.item(i), "GBPlan/Nummer");
                    if (gbPlanNummer != null) {
                        grundstueck.setPlannr(gbPlanNummer);
                    }
                    
                    String fuehrungsart = XMLUtils.getElementValueByPath((Element)childNodes.item(i), "Inhalt*/Fuehrungsart");
                    if (fuehrungsart != null) {
                        grundstueck.setFuehrungsart(fuehrungsart);
                    }
                    
                    String flaechenmass = XMLUtils.getElementValueByPath((Element)childNodes.item(i), "Inhalt*/Flaechenmass");
                    if (flaechenmass != null) {
                        grundstueck.setFlaeche(flaechenmass);
                    }

                    String gbamt = XMLUtils.getElementValueByPath(childElement, "GBAmt/Name");
                    if (gbamt != null) {
                        grundstueck.setGbamt(gbamt);
                    }
                    
                    String bfsnr = XMLUtils.getElementValueByPath(childElement, "Gemeinde/municipalityId");
                    if (bfsnr != null) {
                        grundstueck.setBfsnr(bfsnr);
                    }
                    
                    String gemeinde = XMLUtils.getElementValueByPath(childElement, "Gemeinde/municipalityName");
                    if (gemeinde != null) {
                        grundstueck.setGemeinde(gemeinde);
                    }
                    
                    String kantonaleUnterartStichwort = XMLUtils.getElementValueByPath(childElement, "KantonaleUnterartStichwort/Stichwort");
                    if (kantonaleUnterartStichwort != null) {
                        grundstueck.setKantonaleUnterartStichwort(kantonaleUnterartStichwort);
                    }
                    
                    String kantonaleUnterartZusatz = XMLUtils.getElementValueByPath(childElement, "KantonaleUnterartZusatz");
                    if (kantonaleUnterartZusatz != null) {
                        grundstueck.setKantonaleUnterartZusatz(kantonaleUnterartZusatz);
                    }
                    
                    // Hängige Geschäfte
                    List<Element> haengigeGeschaefteList = new ArrayList<Element>();
                    XMLUtils.getElementsByPath(childElement, "HaengigesGeschaeft", haengigeGeschaefteList);
                    List<HaengigesGeschaeft> haengigeGeschaefte = new ArrayList<HaengigesGeschaeft>();
                    for (int l=0; l<haengigeGeschaefteList.size(); l++) {
                        HaengigesGeschaeft haengigesGeschaeft = new HaengigesGeschaeft();
                        Element haengigesGeschaeftElement = haengigeGeschaefteList.get(l);
                        
                        haengigesGeschaeft.setEgbtbid(XMLUtils.getElementValueByPath(haengigesGeschaeftElement, "EGBTBID", "-"));
                        haengigesGeschaeft.setTagebuchNummer(XMLUtils.getElementValueByPath(haengigesGeschaeftElement, "TagebuchNummer", "-"));
                        String tagebuchDatumZeit = XMLUtils.getElementValueByPath(haengigesGeschaeftElement, "TagebuchDatumZeit", "-");
                        if (tagebuchDatumZeit.length() > 1) {
                            // TODO Utils, falls mehrfach.
                            DateTimeFormat dateFormatRead = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ss");
                            Date date = dateFormatRead.parse(tagebuchDatumZeit);
                            
                            DateTimeFormat dateFormatWrite = DateTimeFormat.getFormat("dd. MMMM yyyy HH:mm:ss");
                            tagebuchDatumZeit = dateFormatWrite.format(date);
                        }
                        haengigesGeschaeft.setTagebuchDatumZeit(XMLUtils.getElementValueByPath(haengigesGeschaeftElement, "TagebuchDatumZeit", "-"));
                        haengigesGeschaeft.setGeschaeftsfallbeschreibungStichwort(XMLUtils.getElementValueByPath(haengigesGeschaeftElement, "GeschaeftsfallbeschreibungStichwort", "-"));
                        haengigesGeschaeft.setGeschaeftsfallbeschreibungZusatz(XMLUtils.getElementValueByPath(haengigesGeschaeftElement, "GeschaeftsfallbeschreibungZusatz", "-"));

                        haengigeGeschaefte.add(haengigesGeschaeft);
                    }
                    grundstueck.setHaengigeGeschaefte(haengigeGeschaefte);
                    
                    // Letzte vollzogene Mutation
                    List<Element> letzteVollzogeneMutationList = new ArrayList<Element>();
                    XMLUtils.getElementsByPath(childElement, "letzteVollzogeneMutation", letzteVollzogeneMutationList);
                    if (letzteVollzogeneMutationList.size() > 0) {
                        Element element = letzteVollzogeneMutationList.get(0);
                        MutationsNummer mutationsNummer = new MutationsNummer();
                        mutationsNummer.setNummer(XMLUtils.getElementValueByPath(element, "Nummer"));
                        mutationsNummer.setAmtlVermKreis(XMLUtils.getElementValueByPath(element, "AmtlVermKreis"));
                        grundstueck.setLetzteVollzogeneMutation(mutationsNummer);
                    }
                    
                    // AV-Bemerkung
                    List<Element> avBemerkungList = new ArrayList<Element>();
                    XMLUtils.getElementsByPath(childElement, "AVBemerkung", avBemerkungList);
                    List<AVBemerkung> avBemerkungen = new ArrayList<AVBemerkung>();
                    for (Element element : avBemerkungList) {
                        AVBemerkung avBemerkung = new AVBemerkung();
                        avBemerkung.setArt(XMLUtils.getElementValueByPath(element, "Art"));
                        avBemerkung.setAndereArt(XMLUtils.getElementValueByPath(element, "AndereArt", "-"));
                        avBemerkung.setBemerkung(XMLUtils.getElementValueByPath(element, "Bemerkung", "-"));
                        avBemerkungen.add(avBemerkung);
                    }
                    grundstueck.setAvBemerkungen(avBemerkungen);
                }
            }
            grundstuecke.put(grundstueck.getEgrid(), grundstueck);    
        }
        
        // Personen
        List<Element> personenList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Body/GetParcelsByIdResponse/Person/*", personenList);
        
        List<Person> personen = new ArrayList<Person>(); 
        for (Element element : personenList) {
            console.log(element.getNodeName());
            
            NodeList inhaltList = element.getChildNodes();
            for (int i=0; i<inhaltList.getLength(); i++) {
                if (inhaltList.item(i) instanceof Element) {
                    Element inhaltElement = (Element) inhaltList.item(i);
                    console.log(inhaltElement.getNodeName());
                    String nummer = XMLUtils.getElementValueByPath(inhaltElement, "Nummer");
                    
                    // "Inhalt*": kein "bis"-Attribut ist aktueller Inhalt

                    
                    
                    
                }
            }
            
            
            
            
        }
        
        
        
        // Rechte: Eigentum TODO
        
        // Rechte: Anmerkungen
        List<Element> anmerkungenList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Body/GetParcelsByIdResponse/Recht/Anmerkung", anmerkungenList);

        List<Anmerkung> anmerkungen = new ArrayList<Anmerkung>();
        for(Element element : anmerkungenList) {
            Anmerkung anmerkung = new Anmerkung();
            anmerkung.setAlteNummer(XMLUtils.getElementValueByPath(element, "InhaltAnmerkung/alteNummer", "-"));
            anmerkung.setArtStichwort(XMLUtils.getElementValueByPath(element, "InhaltAnmerkung/ArtStichwort/Stichwort", "-"));
            anmerkung.setArtZusatz(XMLUtils.getElementValueByPath(element, "InhaltAnmerkung/ArtZusatz", "-"));
            
            String ablaufdatum = XMLUtils.getElementValueByPath(element, "InhaltAnmerkung/Ablaufdatum");
            if (ablaufdatum != null) {
                DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(ablaufdatum);
                anmerkung.setAblaufdatum(date);
            }
            
            List<Element> beteiligteList = new ArrayList<Element>();
            XMLUtils.getElementsByPath(element, "beteiligtesGrundstueck", beteiligteList);
            List<BeteiligtesGrundstueck> beteiligte = new ArrayList<BeteiligtesGrundstueck>();
            for (Element beteiligteElement : beteiligteList) {
                BeteiligtesGrundstueck beteiligtes = new BeteiligtesGrundstueck();
                String egbtbid = beteiligteElement.getAttribute("vonEGBTBID");
                if (egbtbid != null) {
                    beteiligtes.setEgbtbid(egbtbid);
                }
                String tagebuchNummer = beteiligteElement.getAttribute("vonTagebuchNummer");
                if (tagebuchNummer != null) {
                    beteiligtes.setTagebuchNummer(tagebuchNummer);
                    anmerkung.setTagebuchNummer(tagebuchNummer);
                }
                String tagebuchDatumZeit = beteiligteElement.getAttribute("vonTagebuchDatumZeit");
                if (tagebuchDatumZeit != null) {
                    beteiligtes.setTagebuchDatumZeit(tagebuchDatumZeit);
                    
                    DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ss");
                    Date date = dateFormat.parse(tagebuchDatumZeit);
                    anmerkung.setTagebuchDatumZeit(date);
                }
                String ref = XMLUtils.getElementValueByPath(beteiligteElement, "ref", "-");
                beteiligtes.setRef(ref);
                
                beteiligte.add(beteiligtes);
            }
            anmerkung.setBeteiligte(beteiligte);
            
            List<Element> lastRechtAnmerkungList = new ArrayList<Element>();
            XMLUtils.getElementsByPath(element, "LastRechtAnmerkung", lastRechtAnmerkungList);
            for (Element lastRechtAnmerkungElement : lastRechtAnmerkungList) {
                List<Element> belasteteList = new ArrayList<Element>();
                XMLUtils.getElementsByPath(lastRechtAnmerkungElement, "belastetesGrundstueck", belasteteList);
                List<String> belastete = new ArrayList<String>();
                for (Element belasteteElement: belasteteList) {
                    String ref = XMLUtils.getElementValueByPath(belasteteElement, "ref", "-");
                    belastete.add(ref);
                }
                anmerkung.setBelastete(belastete);
            }
            //console.log(anmerkung);            
            anmerkungen.add(anmerkung);
        }

        // Rechte: Dienstbarkeiten
        List<Element> dienstbarkeitenList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Body/GetParcelsByIdResponse/Recht/Dienstbarkeit", dienstbarkeitenList);

        List<Dienstbarkeit> dienstbarkeiten = new ArrayList<Dienstbarkeit>();
        for (Element element : dienstbarkeitenList) {
            Dienstbarkeit dienstbarkeit = new Dienstbarkeit();
            dienstbarkeit.setAlteNummer(XMLUtils.getElementValueByPath(element, "InhaltDienstbarkeit/alteNummer", "-"));
            dienstbarkeit.setArtStichwort(XMLUtils.getElementValueByPath(element, "InhaltDienstbarkeit/ArtStichwort/Stichwort", "-"));
            dienstbarkeit.setArtZusatz(XMLUtils.getElementValueByPath(element, "InhaltDienstbarkeit/ArtZusatz", "-"));
            
            String ablaufdatum = XMLUtils.getElementValueByPath(element, "InhaltDienstbarkeit/Ablaufdatum");
            if (ablaufdatum != null) {
                DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(ablaufdatum);
                dienstbarkeit.setAblaufdatum(date);
            }
            String istVerselbstaendigt = XMLUtils.getElementValueByPath(element, "InhaltDienstbarkeit/istVerselbstaendigt");
            if (istVerselbstaendigt != null && istVerselbstaendigt.equalsIgnoreCase("true")) {
                dienstbarkeit.setIstVerselbstaendigt(true);
            }
            
            List<Element> beteiligteList = new ArrayList<Element>();
            XMLUtils.getElementsByPath(element, "beteiligtesGrundstueck", beteiligteList);
            List<BeteiligtesGrundstueck> beteiligte = new ArrayList<BeteiligtesGrundstueck>();
            for (Element beteiligteElement : beteiligteList) {
                BeteiligtesGrundstueck beteiligtes = new BeteiligtesGrundstueck();
                String egbtbid = beteiligteElement.getAttribute("vonEGBTBID");
                if (egbtbid != null) {
                    beteiligtes.setEgbtbid(egbtbid);
                }
                String tagebuchNummer = beteiligteElement.getAttribute("vonTagebuchNummer");
                if (tagebuchNummer != null) {
                    beteiligtes.setTagebuchNummer(tagebuchNummer);
                    dienstbarkeit.setTagebuchNummer(tagebuchNummer);
                }
                String tagebuchDatumZeit = beteiligteElement.getAttribute("vonTagebuchDatumZeit");
                if (tagebuchDatumZeit != null) {
                    beteiligtes.setTagebuchDatumZeit(tagebuchDatumZeit);
                    
                    DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-ddTHH:mm:ss");
                    Date date = dateFormat.parse(tagebuchDatumZeit);
                    dienstbarkeit.setTagebuchDatumZeit(date);
                }
                String ref = XMLUtils.getElementValueByPath(beteiligteElement, "ref", "-");
                beteiligtes.setRef(ref);
                
                beteiligte.add(beteiligtes);
            }
            dienstbarkeit.setBeteiligte(beteiligte);

            List<Element> lastRechtAnmerkungList = new ArrayList<Element>();
            XMLUtils.getElementsByPath(element, "LastRechtDienstbarkeit", lastRechtAnmerkungList);
            for (Element lastRechtAnmerkungElement : lastRechtAnmerkungList) {
                List<Element> belasteteList = new ArrayList<Element>();
                XMLUtils.getElementsByPath(lastRechtAnmerkungElement, "belastetesGrundstueck", belasteteList);
                List<String> belastete = new ArrayList<String>();
                for (Element belasteteElement: belasteteList) {
                    String ref = XMLUtils.getElementValueByPath(belasteteElement, "ref", "-");
                    belastete.add(ref);
                }
                dienstbarkeit.setBelastete(belastete);
            }
            //console.log(dienstbarkeit);
            dienstbarkeiten.add(dienstbarkeit);
        }

       
        
        /*
         *  Rendering output
         */
        
        // Allgemeine Informationen
        Grundstueck hauptGrundstueck = grundstuecke.get(egrid);
        
        generalCard
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("GB-Nr.:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getNummerKurz())))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("E-GRID:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getEgrid()))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Gemeinde:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getGemeinde())))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("BfS-Nr.:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getBfsnr()))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Grundstücksart:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getGrundstuecksart())))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Kantonale Unterart:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getKantonaleUnterartStichwort() + " " + hauptGrundstueck.getKantonaleUnterartZusatz()))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Führungsart:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getFuehrungsart())))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Grundbuch:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getGbamt()))))
        .appendChild(Row.create().css("content-row")
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Grundstücksfläche:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getFlaeche() + " m").add(span().css("sup").textContent("2"))))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-key").textContent("Plan-Nr.:")))
                .appendChild(Column.span3()
                        .appendChild(span().css("content-value").textContent(hauptGrundstueck.getPlannr()))));
        
        // Anmerkungen
        noteCard
        .appendChild(Row.create().css("content-row-slim")
                .appendChild(Column.span2()
                        .appendChild(span().css("content-table-header-sm").textContent("Tagebucheintrag")))
                .appendChild(Column.span2()
                        .appendChild(span().css("content-table-header-sm").textContent("Nummer")))
                .appendChild(Column.span7()
                        .appendChild(span().css("content-table-header-sm").textContent("Beschreibung"))));
        
        anmerkungen.sort((o1,o2) -> o1.getTagebuchDatumZeit().compareTo(o2.getTagebuchDatumZeit()));
        for (Anmerkung anmerkung : anmerkungen) {            
            DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
            String datum = dateFormat.format(anmerkung.getTagebuchDatumZeit());

            String nummer = anmerkung.getTagebuchNummer() + "<br>" + anmerkung.getAlteNummer();
            String beschreibung = anmerkung.getArtStichwort() + " " + anmerkung.getArtZusatz();
            beschreibung += "<br>" + "Frist bis: " + dateFormat.format(anmerkung.getAblaufdatum()); 
            
            for (String belastete : anmerkung.getBelastete()) {
                beschreibung += "<br>" + "Zulasten: <a class=\"default-link\" href=\""+ belastete.substring(0, 14) + "\">" + belastete.substring(0, 14) + "</a>";
            }

            noteCard
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-value").textContent(datum)))
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(nummer))))
                    .appendChild(Column.span7()
                            .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(beschreibung)))));
        }        
        
        // Dienstbarkeiten
        servitudeCard
        .appendChild(Row.create().css("content-row-slim")
                .appendChild(Column.span2()
                        .appendChild(span().css("content-table-header-sm").textContent("Tagebucheintrag")))
                .appendChild(Column.span2()
                        .appendChild(span().css("content-table-header-sm").textContent("Nummer")))
                .appendChild(Column.span7()
                        .appendChild(span().css("content-table-header-sm").textContent("Beschreibung"))));

        dienstbarkeiten.sort((o1,o2) -> o1.getTagebuchDatumZeit().compareTo(o2.getTagebuchDatumZeit()));
        for (Dienstbarkeit dienstbarkeit : dienstbarkeiten) {            
            DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
            String datum = dateFormat.format(dienstbarkeit.getTagebuchDatumZeit());

            String nummer = dienstbarkeit.getTagebuchNummer() + "<br>" + dienstbarkeit.getAlteNummer();
            String beschreibung = "";
            if (dienstbarkeit.isIstVerselbstaendigt()) {
                beschreibung = "Verselbständigt<br>";
            }
            beschreibung += dienstbarkeit.getArtStichwort() + " " + dienstbarkeit.getArtZusatz();
            beschreibung += "<br>" + "Frist bis: " + dateFormat.format(dienstbarkeit.getAblaufdatum()); 
            
            for (String belastete : dienstbarkeit.getBelastete()) {
                beschreibung += "<br>" + "Zulasten: <a class=\"default-link\" href=\""+ belastete.substring(0, 14) + "\">" + belastete.substring(0, 14) + "</a>";
            }

            servitudeCard
            .appendChild(Row.create().css("content-row")
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-value").textContent(datum)))
                    .appendChild(Column.span2()
                            .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(nummer))))
                    .appendChild(Column.span7()
                            .appendChild(span().css("content-value").innerHtml(SafeHtmlUtils.fromTrustedString(beschreibung)))));
        }        

    }
   
    
    
    @Override
    public HTMLElement element() {
        return root;
    }

}
