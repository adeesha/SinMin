/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import webcrawler.BasicCrawlController;
import webcrawler.SQLCommunicator;

public class HtmlContentHandlerDinamina extends ContentHandler {

    private final int MAX_ANCHOR_LENGTH = 100;

    private enum Element {

        A, AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY, P, SPAN, HTML, FONT, H2, H1
    }

    private static class HtmlFactory {

        private static Map<String, Element> name2Element;

        static {
            name2Element = new HashMap<>();
            for (Element element : Element.values()) {
                name2Element.put(element.toString().toLowerCase(), element);
            }
        }

        public static Element getElement(String name) {
            return name2Element.get(name);
        }
    }
    private String base;
    private String metaRefresh;
    private String metaLocation;
    private boolean isWithinBodyElement;
    private StringBuilder bodyText;
    private StringBuilder databaseAuthor;
    private StringBuilder databaseDate;
    private StringBuilder databaseTopic;
    private StringBuilder databaseContent;
    private boolean anchorFlag = false;
    private StringBuilder anchorText = new StringBuilder();
    private boolean isEntryStarted;
    private boolean isParagraphStarted;
    private boolean isDateAndAuthorDiscovered;
    private boolean isTopicDiscovered;
    private boolean isDatabasesendOK;
    String year = null;
    String month = null;
    String date = null;
    String url = null;

    public HtmlContentHandlerDinamina(String year, String month, String date, String url) {

        this.year = year;
        this.month = month;
        this.date = date;
        this.url = url;

        isEntryStarted = false;
        isWithinBodyElement = false;
        isParagraphStarted = false;
        isDateAndAuthorDiscovered = false;
        isTopicDiscovered = false;

        isDatabasesendOK = false;

        bodyText = new StringBuilder();
        databaseAuthor = new StringBuilder();
        databaseContent = new StringBuilder();
        databaseDate = new StringBuilder();
        databaseTopic = new StringBuilder();


    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Element element = HtmlFactory.getElement(localName);
        // modified by adeesha. 


        if (element == Element.H2) {
            isTopicDiscovered = true;
            isDatabasesendOK = true;
            isEntryStarted = true;
            return;
        }
        if (element == Element.H1) {
            isTopicDiscovered = true;
            isDatabasesendOK = true;
            isEntryStarted = true;
            return;
        }

//        if (element == Element.META) {
//            String name = attributes.getValue("name");
//            String content = attributes.getValue("content");
//
//            if ("description".equals(name) && "".equals(content)) {
//                System.out.println("not a valid URL");
//                return;
//            }
//            if ("description".equals(name) && !"".equals(content)) {
//                System.out.println("valid URL");
//                isDatabasesendOK = true;
//
//            }
//
//
//        }





        if (isEntryStarted) {
            if (element == Element.P) {
                isParagraphStarted = true;
                String pclass = attributes.getValue("class");
                if (pclass != null) {
                    isParagraphStarted = false;
                    if (pclass.contains("byline")) {
                    }



                }


                // String pClass = attributes.getValue("");
                // System.out.println("sff");
            }

        }
        if (isEntryStarted && isParagraphStarted) {
            if (element != Element.P && element != Element.IMG) {
                isParagraphStarted = false;
                isEntryStarted = false;
                // String pClass = attributes.getValue("");
                // System.out.println("sff");
            }

        }





        if (element == Element.P) {



            String pclass = attributes.getValue("class");
            if (pclass != null) {
                if (pclass.contains("byline")) {
                    isDateAndAuthorDiscovered = true;

                }



            }

            return;
        }

















        if (element == Element.BODY) {
            isWithinBodyElement = true;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Element element = HtmlFactory.getElement(localName);





        if (element == Element.BODY) {
            isWithinBodyElement = false;
            databaseContent = databaseContent.replace(0, databaseAuthor.length() + 1, "");
            String toDatabe[] = new String[4];
            toDatabe[0] = databaseAuthor.toString().replaceAll("\\s+", " ");
            toDatabe[1] = databaseDate.toString().replaceAll("\\s+", " ");
            toDatabe[2] = databaseTopic.toString().replaceAll("\\s+", " ");
            toDatabe[3] = databaseContent.toString().replaceAll("\\s+", " ");
            if (toDatabe[3].length() > 0) {

                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter("./Dinamina.xml", true));
                    writer.write("<post>\n");
                    writer.write("<link>");
                    writer.write(this.url);
                    writer.write("</link>\n");
                    writer.write("<topic>");
                    writer.write(toDatabe[2]);
                    writer.write("</topic>\n");
                    writer.write("<date>");
                    writer.write(toDatabe[1]);
                    writer.write("</date>\n");
                    writer.write("<author>");
                    writer.write(toDatabe[0]);
                    writer.write("</author>\n");
                    writer.write("<content>");
                    writer.write(toDatabe[3]);
                    writer.write("</content>\n");
                    writer.write("</post>\n");

                    writer.flush();
                    writer.close();

                } catch (IOException ex) {
                    Logger.getLogger(HtmlContentHandlerLankaDeepa.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        // modified by Adeesha	


        if (isTopicDiscovered) {
            isTopicDiscovered = false;
            BufferedWriter writer = null;
            try {
                databaseTopic.append(ch, start, length);
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));
                writer.write("Topic   : ");
                writer.write(new String(ch, start, length));

                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerDinamina.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (isDateAndAuthorDiscovered) {
            isDateAndAuthorDiscovered = false;

            String tempauthoranddate = new String(ch);


            databaseDate.append(year + "." + month + "." + date);
            databaseAuthor.append(tempauthoranddate, start, length);
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));
                writer.write("Date and Author   : ");
                writer.write(new String(ch, start, length));

                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerDinamina.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (isParagraphStarted) {
            databaseContent.append(ch, start, length);
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));

                writer.write(new String(ch, start, length));

                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerDinamina.class.getName()).log(Level.SEVERE, null, ex);
            }

            //isParagraphStarted=false;
        }

        // end of modification


        if (isWithinBodyElement) {
            bodyText.append(ch, start, length);

            if (anchorFlag) {
                anchorText.append(new String(ch, start, length));
            }
        }
    }

    @Override
    public String getBodyText() {
        return bodyText.toString();
    }

    @Override
    public String getBaseUrl() {
        return base;
    }
}
