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

public class HelpElement implements IsElement<HTMLElement> {

    private final HTMLElement root;
    private HTMLDivElement container;
    
    public HelpElement() {
        root = div().id("oereb-element").element();
        
        container = div().element();
        container.style.padding = CSSProperties.PaddingUnionType.of("10px"); 
        root.appendChild(container);

        container.appendChild(Row.create().css("empty-row-20").element());
        
        Card helpCard = Card.create("Hilfe")
                .setCollapsible()
                .elevate(Elevation.LEVEL_0);
        container.appendChild(helpCard.element());

        Card contactCard = Card.create("Kontakt")
                .setCollapsible()
                .collapse()
                .elevate(Elevation.LEVEL_0);
        container.appendChild(contactCard.element());

        String helpText = "Um Informationen über ein Grundstück zu erlangen, klicken sie in der Karte auf das gewünschte Grundstück oder suchen sie das Grundstück oder eine Adresse im Suchfeld.";
        helpCard.appendChild(span().textContent(helpText));
    }

    @Override
    public HTMLElement element() {
        return root;
    }
}
