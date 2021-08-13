package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

import java.util.ArrayList;
import java.util.List;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.cards.Card;
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
import org.gwtproject.xml.client.Document;
import org.gwtproject.xml.client.Element;
import org.gwtproject.xml.client.XMLParser;
import org.jboss.elemento.IsElement;

import com.google.gwt.user.client.Window;

import ch.so.agi.cadastralinfo.xml.XMLUtils;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;

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
    private List<String> notConcerned;
    private List<String> without;


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

        loader.stop();
    }
    
    private void parseResponse(String xml) {
        Document doc = XMLParser.parse(xml);
        
        // Concerned themes
        
        
        // Not concerned themes
        List<Element> notConcernedThemesList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Extract/NotConcernedTheme", notConcernedThemesList);
        notConcerned = new ArrayList<String>();
        for (Element element : notConcernedThemesList) {
            notConcerned.add(XMLUtils.getElementValueByPath(element, "Text/Text"));
        }
        notConcerned.sort(String.CASE_INSENSITIVE_ORDER);
        
        // Themes without data
        List<Element> withoutThemesList = new ArrayList<Element>();
        XMLUtils.getElementsByPath(doc.getDocumentElement(), "Extract/ThemeWithoutData", withoutThemesList);
        without = new ArrayList<String>();
        for (Element element : withoutThemesList) {
            without.add(XMLUtils.getElementValueByPath(element, "Text/Text"));
        }
        without.sort(String.CASE_INSENSITIVE_ORDER);

        
        
    }
    
    private void renderResponse() {
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
                    .setItems(notConcerned);
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
                    .setItems(without);
            withoutContainer.appendChild(listGroup.element());       
            tabWithout.appendChild(withoutContainer);
        }

    }
    
    
    
    @Override
    public HTMLElement element() {
        return root;
    }
}
