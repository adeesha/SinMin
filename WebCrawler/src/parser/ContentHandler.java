/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author adeesha
 */
public class ContentHandler extends DefaultHandler{
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
    }
     @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
         
     }
     
    @Override
     public void characters(char ch[], int start, int length) throws SAXException {
         
     }
    
    public String getBodyText() {
        return null;
      
    }

    

    public String getBaseUrl() {
        return null;
       
    }
}
