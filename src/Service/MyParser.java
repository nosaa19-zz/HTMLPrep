/*
 * MyParser is CyberNeko
 */
package Service;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author Oviliani
 */
public class MyParser extends DOMParser {
    static final String AUGMENTATIONS
            = "http://cyberneko.org/html/features/augmentations";

    public MyParser() throws SAXNotRecognizedException, SAXNotSupportedException {
        initialize();
    }

    public void setEncoding(String value) {
        try {
            setProperty("http://cyberneko.org/html/properties/default-encoding", value);
        } catch (SAXNotRecognizedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void initialize() {
        this.reset();
        try {
            setFeature("http://xml.org/sax/features/namespaces", false);
            setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags", true);
            setFeature("http://cyberneko.org/html/features/balance-tags", true);
            setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF8");
            //setProperty("http://cyberneko.org/html/properties/default-encoding", "EUC-KR");
            setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
            setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);
        } catch (SAXNotRecognizedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /*
     * Remove whitespace nodes in DOM tree 
     */
    private void removeWhitespaceNode(Node doc) {

        XPathFactory xpathFactory = XPathFactory.newInstance();
        // XPath to find empty text nodes.
        XPathExpression xpathExp;
        try {
            xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);

            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public Document getDocument() {

        Document doc = super.getDocument();
        removeWhitespaceNode(doc);
        return doc;
        
    }

    
}
