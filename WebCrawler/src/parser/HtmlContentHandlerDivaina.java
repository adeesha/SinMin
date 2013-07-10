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

public class HtmlContentHandlerDivaina extends ContentHandler {

    private final int MAX_ANCHOR_LENGTH = 100;

    private enum Element {

        A, AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY, P, SPAN, HTML, FONT, BR
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
    private boolean isParagraphDiscovered;
    private boolean isDateAndAuthorDiscovered;
    private boolean isWithinAuthor;
    private boolean isTopicDiscovered;
    private boolean isWithinElement;
    private boolean isDatabasesendOK;
    private boolean startCounting;
    private int brCount;

    public HtmlContentHandlerDivaina() {
        isEntryStarted = false;
        isWithinBodyElement = false;
        isParagraphStarted = false;
        isDateAndAuthorDiscovered = false;
        isTopicDiscovered = false;
        isWithinElement = false;
        isDatabasesendOK = false;
        startCounting = false;
        isWithinAuthor = false;
        isParagraphDiscovered = false;
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

        //System.out.println("Start Element");


        if (element == Element.META) {
            String name = attributes.getValue("name");
            String content = attributes.getValue("content");

            if ("description".equals(name) && "".equals(content)) {
                System.out.println("not a valid URL");
                return;
            }
            if ("description".equals(name) && !"".equals(content)) {
                System.out.println("valid URL");
                isDatabasesendOK = true;

            }
        }
        isDatabasesendOK = true;




        /*if (isEntryStarted) {
         if (element == Element.P) {
         isParagraphStarted = true;
         isWithinElement = true;
         // String pClass = attributes.getValue("");
         // System.out.println("sff");
         }

         }
         if (isEntryStarted && isParagraphStarted) {
         if (element != Element.P) {
         isParagraphStarted = false;
         isEntryStarted = false;
         // String pClass = attributes.getValue("");
         // System.out.println("sff");
         }

         }*/


        //System.out.println("Element ="+ element);

        if (element == Element.FONT) {

            String tagName = attributes.getQName(0);
            String size = attributes.getValue("size");
            String color = attributes.getValue("color");

            //System.out.println("Name = " + tagName + " " + size + " " + color);
            if (size != null && color != null) {
                //System.out.println("Name = " + tagName + " " + size + " " + color);
                if ((size.equals("4") && color.equals("#003300")) || (size.equals("5") && color.equals("#003300"))) {
                    isTopicDiscovered = true;
                    isWithinElement = true;
                    //localTopicDiscovered = true;
                }
            }

            if (size != null && color == null) {
                if (size.equals("4")) {
                    brCount = 0;
                    startCounting = true;
                    isWithinAuthor = true;
                }
            }


            return;
        }


        /// end of modification





        if (element == Element.A || element == Element.AREA || element == Element.LINK) {

            String href = attributes.getValue("href");
            if (href != null) {
                anchorFlag = true;

            }
            return;
        }



        if (element == Element.BASE) {
            if (base != null) { // We only consider the first occurrence of the
                // Base element.
                String href = attributes.getValue("href");
                if (href != null) {
                    base = href;
                }
            }
            return;
        }

        if (element == Element.META) {
            String equiv = attributes.getValue("http-equiv");
            String content = attributes.getValue("content");
            if (equiv != null && content != null) {
                equiv = equiv.toLowerCase();

                // http-equiv="refresh" content="0;URL=http://foo.bar/..."
                if (equiv.equals("refresh") && (metaRefresh == null)) {
                    int pos = content.toLowerCase().indexOf("url=");
                    if (pos != -1) {
                        metaRefresh = content.substring(pos + 4);
                    }

                }

                // http-equiv="location" content="http://foo.bar/..."
                if (equiv.equals("location") && (metaLocation == null)) {
                    metaLocation = content;

                }
            }
            return;
        }

        /*if (element == Element.BODY) {
         isWithinBodyElement = true;
         }*/

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Element element = HtmlFactory.getElement(localName);

        if (element == Element.P) {
            isParagraphStarted = false;
        }

        if (element == Element.BR) {
            if (startCounting) {
                //System.out.println("BR");
                brCount++;

                if ((brCount == 2 || brCount == 3) && isWithinAuthor) {
                    isWithinAuthor = false;
                    isDateAndAuthorDiscovered = true;
                    isParagraphStarted = true;
                }
            }
        }

        /* if(element == Element.FONT && isParagraphStarted && brCount >3){
         isParagraphStarted = false;
         isParagraphDiscovered = true;
         }*/


        //System.out.println("End Element");
        if (isWithinElement) {
            isWithinElement = false;
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));
                writer.newLine();
                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerLankaDeepa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /*if (element == Element.A || element == Element.AREA || element == Element.LINK) {
         anchorFlag = false;

         }(*/
        // comment for commit 2013.04.26
        if (element == Element.BODY) {
            isWithinBodyElement = false;
            if (isDatabasesendOK) {
                databaseContent = databaseContent.replace(0, databaseAuthor.length() + 1, "");
                String toDatabe[] = new String[4];
                toDatabe[0] = databaseAuthor.toString().replaceAll("\\s+", " ");
                toDatabe[1] = databaseDate.toString().replaceAll("\\s+", " ");
                toDatabe[2] = databaseTopic.toString().replaceAll("\\s+", " ");
                toDatabe[3] = databaseContent.toString().replaceAll("\\s+", " ");

                if (!(toDatabe[0].equals(" ") && !toDatabe[2].equals(" ") && !toDatabe[3].equals(" ")) && !(toDatabe[0].length()==0 && toDatabe[2].length()==0 && toDatabe[3].length()==0)) {
                    System.out.println("Author =" + toDatabe[0]);
                    System.out.println("Date =" + toDatabe[1]);
                    System.out.println("Topic =" + toDatabe[2]);
                    System.out.println("Contet =" + toDatabe[3]);
                    //SQLCommunicator.InsertInToTable("lankadeepa", toDatabe);
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
                char[] space = new char[1];
                space[0] = ' ';
                databaseTopic.append(space, 0, 1);
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));
                writer.write("Topic   : ");
                writer.write(new String(ch, start, length));

                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerLankaDeepa.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Topic   : " + databaseTopic);
        }
        if (isDateAndAuthorDiscovered) {
            isDateAndAuthorDiscovered = false;

            //String tempauthoranddate = new String(ch);
            //System.out.println(new String(ch, start, length));
            databaseAuthor.append(new String(ch, start, length));
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));
                writer.write("Author   : ");
                writer.write(new String(ch, start, length));

                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerLankaDeepa.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Date and Author   : " + databaseAuthor);

        }

        if (isParagraphStarted) {
            isParagraphDiscovered = false;
            //String tempauthoranddate = new String(ch);
            //System.out.print(new String(ch, start, length));
            databaseContent.append(new String(ch, start, length));

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("./output1.txt", true));

                writer.write(new String(ch, start, length));

                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(HtmlContentHandlerLankaDeepa.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Content   : " + databaseContent);
            //isParagraphStarted=false;
        }

        // end of modification


        /*if (isWithinBodyElement) {
         bodyText.append(ch, start, length);

         if (anchorFlag) {
         anchorText.append(new String(ch, start, length));
         }
         }*/
    }

    @Override
    public String getBodyText() {
        return bodyText.toString();
    }

    /**
     *
     * @return
     */
    @Override
    public String getBaseUrl() {
        return base;
    }
}
