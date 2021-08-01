package ch.so.agi.cadastralinfo;

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
import org.gwtproject.i18n.client.NumberFormat;
import org.jboss.elemento.IsElement;

import com.google.gwt.user.client.Window;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;

public class GrundbuchElement implements IsElement<HTMLElement> {
    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");
    private NumberFormat fmtInteger = NumberFormat.getFormat("#,##0");

    private Loader loader;
    private final HTMLElement root;
    private HTMLDivElement container;
    private Card generalCard;
    private Card descriptionCard;
    private Card dominatingCard;
    private Card propertyCard;
    private Card noteCard;
    private Card servitudeCard;
    private Card realBurdenCard;
    private Card pendingCard;

    public GrundbuchElement() {
        root = div().id("gb-element").element();
    }
    
    public void update(String egrid, String grundbuchServiceBaseUrl) {
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
                .setContent("PDF")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#e53935")
                .setBorder("1px #e53935 solid")
                .setPadding("5px 5px 5px 0px;")
                .setMinWidth(px.of(100)).get();
                
        pdfBtn.addClickListener(evt -> {
            Window.open(grundbuchServiceBaseUrl+"/extract/pdf/geometry/"+egrid, "_blank", null);
        });

        container.appendChild(pdfBtn.element());
        container.appendChild(Row.create().css("empty-row-20").element());
        
        generalCard = Card.create("Allgemeine Informationen")
                .setCollapsible()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(generalCard.element());

        descriptionCard = Card.create("Grundstückbeschreibung")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(descriptionCard.element());

        dominatingCard = Card.create("Dominierende Grundstücke")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(dominatingCard.element());

        propertyCard = Card.create("Eigentum")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);        
        container.appendChild(propertyCard.element());

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
        
        loader.stop();
    }
    
    
    @Override
    public HTMLElement element() {
        return root;
    }

}
