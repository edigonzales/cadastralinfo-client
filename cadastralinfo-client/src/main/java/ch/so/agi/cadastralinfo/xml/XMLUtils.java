package ch.so.agi.cadastralinfo.xml;

import static elemental2.dom.DomGlobal.console;

import java.util.List;

import org.gwtproject.xml.client.Element;
import org.gwtproject.xml.client.Node;
import org.gwtproject.xml.client.NodeList;

public class XMLUtils {
    public static String getElementValueByPathV2(Element root, String path) {
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
                //console.log(childElement.getNodeName());
                String nodeName = childElement.getNodeName();
                if (nodeName.contains(":"+pathElements[0])) {
                    //console.log("children found");
                    if (pathElementsLength == 1) {
                        //console.log("abbruch");
                        return childElement.getFirstChild().getNodeValue();
                    } else {
                        return getElementValueByPathV2(childElement, path.substring(path.indexOf("/")+1));
                    }
                }
            }
        }
        return null;
    }
    
    
    // TODO: https://www.geeksforgeeks.org/wildcard-character-matching/
    public static String getElementValueByPath(Element root, String path) {
        if (root == null || path == null) return null;
        
        //console.log("root:"+root.getNodeName());
        //console.log("path:"+path);

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        String[] pathElements = path.split("/");
        int pathElementsLength = pathElements.length;
        
        String value = null;
        if (pathElementsLength == 1) {
            value = root.getFirstChild().getNodeValue();
            return value;
        }

        String pathElement1 = pathElements[1];
        
        if (pathElement1.endsWith("*") && pathElement1.length() > 1) {
            pathElement1 = pathElement1.substring(0,pathElement1.length()-1);
        }
        
        NodeList childNodes = root.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                 if (childNodes.item(i).getNodeName().contains(":"+pathElement1)) {
                    Element childElement = (Element) childNodes.item(i);
                    return getElementValueByPath(childElement, path.substring(path.indexOf("/")+1));                    
                }
            }
        }  
        return value;
    }
    
    // Path beginnt mit dem ersten Kindelement.
    public static void getElementsByPathV2(Element root, String path, List<Element> list) {
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
                        return;
                    } else {
                        getElementsByPathV2(childElement, path.substring(path.indexOf("/")+1), list);
                    }
                }
            }
        }
    }
    
    public static void getElementsByPath(Element root, String path, List<Element> list) {
        //console.log("*******************************************");
        if (root == null || path == null) return;

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        //console.log("path: "+path);
        
        String[] pathElements = path.split("/");
        int pathElementsLength = pathElements.length;
        //console.log(pathElementsLength);
        //console.log("0: " + pathElements[0]);
        
        if (pathElementsLength == 1) {
            //console.log("Spezialfall");
            //console.log("Spez. Element name: " + root.getNodeName());
            list.add(root);
            return;
        }
        
        // next path element
        String pathElement = pathElements[1];
        
        NodeList childNodes = root.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                //console.log("childNodeName: " + childNodes.item(i).getNodeName());
                if (childNodes.item(i).getNodeName().contains(":"+pathElement)) {
                    //console.log("pathElement is childnode: " + childNodes.item(i).getNodeName());
                    //console.log("new path: " +path.substring(path.indexOf("/")+1));
                    Element childElement = (Element) childNodes.item(i);
                    getElementsByPath(childElement, path.substring(path.indexOf("/")+1), list);
                }
            }
        }  
        // V1
//        String[] pathElements = path.split("/");
//        int pathElementsLength = pathElements.length;
//        console.log(pathElements.length);
//        console.log(pathElements[pathElements.length-1]);
//        String tagName = pathElements[pathElements.length-1]; 
//        
//        console.log("0: " +pathElements[0]);
//        console.log("1: " +pathElements[1]);
//        console.log("2: " +pathElements[2]);
//        console.log("3: " +pathElements[3]);
//        console.log("4: " +pathElements[4]);
//        console.log("5: " +pathElements[5]);
//        
//        
//        NodeList nodes = root.getElementsByTagName(tagName);
        
//        for (int i=0; i<nodes.getLength(); i++) {
//            console.log("----------------------");
//            Node node = nodes.item(i);
//            int idx=0;
//            while(node.getParentNode() != null && node.getParentNode() instanceof Element) {
//                console.log("diff: " + (pathElementsLength - idx));
//                console.log("path element: " + pathElements[pathElementsLength - idx - 1]);
//                
//                
//                console.log("vorher: " + node.getNodeName());
//                node = (Element) node.getParentNode();
//                console.log("nachher: " + node.getNodeName());
//                
//                idx++;
//            }
//        }

        
    }
}

