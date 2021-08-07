package ch.so.agi.cadastralinfo.xml;

import static elemental2.dom.DomGlobal.console;

import java.util.List;

import org.gwtproject.xml.client.Element;
import org.gwtproject.xml.client.Node;
import org.gwtproject.xml.client.NodeList;

public class XMLUtils {
    public static String getElementValueByPath(Element root, String path, String ret) {
        if (root == null || path == null) return null;
        
        //console.log("root:"+root.getNodeName());
        //console.log("path:"+path);

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        String[] pathElements = path.split("/");
        int pathElementsLength = pathElements.length;

        String pathElement = pathElements[0];
        if (pathElement.endsWith("*") && pathElement.length() > 1) {
            pathElement = pathElement.substring(0,pathElement.length()-1);
        }
        
        NodeList childNodes = root.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element childElement = (Element) childNodes.item(i);
                String nodeName = childElement.getNodeName();
                if (nodeName.contains(":"+pathElement)) {
                    if (pathElementsLength == 1) {
                        return childElement.getFirstChild().getNodeValue();
                    } else {
                        return getElementValueByPath(childElement, path.substring(path.indexOf("/")+1), ret);
                    }
                }
            }
        }
        return ret;
    }
    
    
    // TODO: https://www.geeksforgeeks.org/wildcard-character-matching/
    public static String getElementValueByPath(Element root, String path) {
        return getElementValueByPath(root, path, null);
//        if (root == null || path == null) return null;
//        
//        //console.log("root:"+root.getNodeName());
//        //console.log("path:"+path);
//
//        if (path.startsWith("/")) {
//            path = path.substring(1);
//        }
//        
//        String[] pathElements = path.split("/");
//        int pathElementsLength = pathElements.length;
//
//        String pathElement = pathElements[0];
//        if (pathElement.endsWith("*") && pathElement.length() > 1) {
//            pathElement = pathElement.substring(0,pathElement.length()-1);
//        }
//        
//        NodeList childNodes = root.getChildNodes();
//        for (int i=0; i<childNodes.getLength(); i++) {
//            if (childNodes.item(i) instanceof Element) {
//                Element childElement = (Element) childNodes.item(i);
//                //console.log(childElement.getNodeName());
//                String nodeName = childElement.getNodeName();
//                if (nodeName.contains(":"+pathElement)) {
//                    //console.log("children found");
//                    if (pathElementsLength == 1) {
//                        //console.log("abbruch");
//                        return childElement.getFirstChild().getNodeValue();
//                    } else {
//                        return getElementValueByPathV2(childElement, path.substring(path.indexOf("/")+1));
//                    }
//                }
//            }
//        }
//        return null;
    }
    
    // Path beginnt mit dem ersten Kindelement.
    public static void getElementsByPath(Element root, String path, List<Element> list) {
        //console.log("root:"+root.getNodeName());
        //console.log("path:"+path);
        
        if (root == null || path == null) return;

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        String[] pathElements = path.split("/");
        int pathElementsLength = pathElements.length;
        //console.log(pathElementsLength);

        NodeList childNodes = root.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element childElement = (Element) childNodes.item(i);
                //console.log(childElement.getNodeName());
                String nodeName = childElement.getNodeName();
                if (nodeName.contains(":"+pathElements[0])) {
                    //console.log("children found");
                    if (pathElementsLength == 1) {
                        //console.log("abbruch");
                        list.add(childElement);
                    } else {
                        getElementsByPath(childElement, path.substring(path.indexOf("/")+1), list);
                    }
                }
            }
        }
        return;
    }
}

