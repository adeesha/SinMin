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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;

import crawler.Configurable;
import crawler.CrawlConfig;
import crawler.Page;
import org.apache.tika.parser.html.HtmlMapper;
import org.xml.sax.helpers.DefaultHandler;




import webcrawler.BasicCrawlController;
import webcrawler.BasicCrawlController.paperE;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
class AllTagMapper implements HtmlMapper {

    @Override
    public String mapSafeElement(String name) {
        return name.toLowerCase();
    }

    @Override
    public boolean isDiscardElement(String name) {
        return false;
    }

    @Override
    public String mapSafeAttribute(String elementName, String attributeName) {
        return attributeName.toLowerCase();
    }
}

public class Parser extends Configurable {

    protected static final Logger logger = Logger.getLogger(Parser.class.getName());
    private HtmlParser htmlParser;
    private ParseContext parseContext;

    public Parser(CrawlConfig config) {
        super(config);
        htmlParser = new HtmlParser();
        parseContext = new ParseContext();
    }

    public boolean parse(Page page, String contextURL,String year, String month, String date, String cat) throws InstantiationException, IllegalAccessException {
     

      

        Metadata metadata = new Metadata();
        parseContext.set(HtmlMapper.class, AllTagMapper.class.newInstance());

        // start modification by Adeesha
        // filter web pages using the name of the newspaper

        ContentHandler contentHandler = null;

        if (BasicCrawlController.paper==paperE.LankadeepaArchives) {
            contentHandler = new HtmlContentHandlerLankaDeepa(contextURL);
        }
        else if(BasicCrawlController.paper==paperE.DivainaArchives) {
            contentHandler = new HtmlContentHandlerDivaina(year,month,date,contextURL);
        }
        else if(BasicCrawlController.paper==paperE.DinaminaArchives) {
            contentHandler = new HtmlContentHandlerDinamina(year, month,date,contextURL);
        }





        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(page.getContentData());
            htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
        } catch (Exception e) {
            logger.error(e.getMessage() + ", while parsing: " + page.getWebURL());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage() + ", while parsing: " + page.getWebURL());
            }
        }

        if (page.getContentCharset() == null) {
            page.setContentCharset(metadata.get("Content-Encoding"));
        }

       


        String baseURL = contentHandler.getBaseUrl();
        if (baseURL != null) {
            contextURL = baseURL;
        }

        int urlCount = 0;
       


       

       
        return true;

    }
}