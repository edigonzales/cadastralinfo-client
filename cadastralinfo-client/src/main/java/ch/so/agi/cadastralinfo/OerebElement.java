package ch.so.agi.cadastralinfo;

import static elemental2.dom.DomGlobal.console;
import static org.dominokit.domino.ui.style.Unit.px;
import static org.jboss.elemento.Elements.div;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Elevation;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.gwtproject.i18n.client.NumberFormat;
import org.jboss.elemento.IsElement;

import com.google.gwt.user.client.Window;

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
    private Card generalCard;

    private String egrid;


    public OerebElement() {
        root = div().id("oereb-element").element();
    }
    
    public void update(String egrid, String oerebServiceBaseUrl) {
        this.egrid = egrid;
        
        DomGlobal.fetch("/oereb?egrid="+this.egrid)
        .then(response -> {
            if (!response.ok) {
                return null;
            }
            return response.text();
        })
        .then(xml -> {
            //parseResponse()
            //renderOutput()
            
            
            //console.log(xml);
            return null;
        }).catch_(error -> {
            loader.stop();
            console.log(error);
            return null;
        });

        
        
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

//        generalCard = Card.create("Allgemeine Informationen")
//                .setCollapsible()
//                .elevate(Elevation.LEVEL_0);        
//        container.appendChild(generalCard.element());

        TabsPanel tabsPanel = TabsPanel.create()
                .setId("tabs-panel")
                .setBackgroundColor(Color.WHITE)
                .setColor(Color.RED_DARKEN_3);
        
        Tab tabConcerned = Tab.create("Betroffene Themen");
        //tabConcerned.appendChild(avElement);
        
        Tab tabNotConcerned = Tab.create("NICHT BETROFFENE THEMEN");
        //tabNotConcerned.appendChild(grundbuchElement);
        
        Tab tabWithout = Tab.create("NICHT VORHANDENE THEMEN");
        //tabWithout.appendChild(oerebElement);
        
        tabsPanel.appendChild(tabConcerned);
        tabsPanel.appendChild(tabNotConcerned);
        tabsPanel.appendChild(tabWithout);
        container.appendChild(tabsPanel.element());
        
        
        
        loader.stop();
    }
    
    
    @Override
    public HTMLElement element() {
        return root;
    }
}
