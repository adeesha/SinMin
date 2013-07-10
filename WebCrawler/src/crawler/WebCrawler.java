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
package crawler;

import fetcher.PageFetchResult;
import fetcher.CustomFetchStatus;
import fetcher.PageFetcher;




import parser.Parser;



import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import webcrawler.BasicCrawlController;
import webcrawler.BasicCrawlController.paperE;

/**
 * WebCrawler class in the Runnable class that is executed by each crawler
 * thread.
 *
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class WebCrawler implements Runnable {

    protected static final Logger logger = Logger.getLogger(WebCrawler.class.getName());
    /**
     * The id associated to the crawler thread running this instance
     */
    protected int myId;
    /**
     * The controller instance that has created this crawler thread. This
     * reference to the controller can be used for getting configurations of the
     * current crawl or adding new seeds during runtime.
     */
    protected CrawlController myController;
    /**
     * The thread within which this crawler instance is running.
     */
    private Thread myThread;
    /**
     * The parser that is used by this crawler instance to parse the content of
     * the fetched pages.
     */
    private Parser parser;
    /**
     * The fetcher that is used by this crawler instance to fetch the content of
     * pages from the web.
     */
    private PageFetcher pageFetcher;
    /**
     * The RobotstxtServer instance that is used by this crawler instance to
     * determine whether the crawler is allowed to crawl the content of each
     * page.
     */
    //private RobotstxtServer robotstxtServer;
    /**
     * The DocIDServer that is used by this crawler instance to map each URL to
     * a unique docid.
     */
    //  private DocIDServer docIdServer;
    /**
     * The Frontier object that manages the crawl queue.
     */
    /**
     * Is the current crawler instance waiting for new URLs? This field is
     * mainly used by the controller to detect whether all of the crawler
     * instances are waiting for new URLs and therefore there is no more work
     * and crawling can be stopped.
     */
    private boolean isWaitingForNewURLs;

    /**
     * Initializes the current instance of the crawler
     *
     * @param id the id of this crawler instance
     * @param crawlController the controller that manages this crawling session
     */
    public void init(int id, CrawlController crawlController) {
        this.myId = id;
        this.pageFetcher = crawlController.getPageFetcher();
        //this.robotstxtServer = crawlController.getRobotstxtServer();
        //  this.docIdServer = crawlController.getDocIdServer();

        this.parser = new Parser(crawlController.getConfig());
        this.myController = crawlController;
        this.isWaitingForNewURLs = false;
    }

    /**
     * Get the id of the current crawler instance
     *
     * @return the id of the current crawler instance
     */
    public int getMyId() {
        return myId;
    }

    public CrawlController getMyController() {
        return myController;
    }

    /**
     * This function is called just before starting the crawl by this crawler
     * instance. It can be used for setting up the data structures or
     * initializations needed by this crawler instance.
     */
    public void onStart() {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    }

    /**
     * This function is called just before the termination of the current
     * crawler instance. It can be used for persisting in-memory data or other
     * finalization tasks.
     */
    public void onBeforeExit() {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    }

    /**
     * This function is called once the header of a page is fetched. It can be
     * overwritten by sub-classes to perform custom logic for different status
     * codes. For example, 404 pages can be logged, etc.
     *
     * @param webUrl
     * @param statusCode
     * @param statusDescription
     */
    protected void handlePageStatusCode(String webUrl, int statusCode, String statusDescription) {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    }

    /**
     * This function is called if the content of a url could not be fetched.
     *
     * @param webUrl
     */
    protected void onContentFetchError(String webUrl) {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    }

    /**
     * This function is called if there has been an error in parsing the
     * content.
     *
     * @param webUrl
     */
    protected void onParseError(String webUrl) {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    }

    /**
     * The CrawlController instance that has created this crawler instance will
     * call this function just before terminating this crawler thread. Classes
     * that extend WebCrawler can override this function to pass their local
     * data to their controller. The controller then puts these local data in a
     * List that can then be used for processing the local data of crawlers (if
     * needed).
     */
    public Object getMyLocalData() {
        return null;
    }

    public void run() {
        onStart();
        //while (true) {

        // modified by Adeesha

        // start crawling for lankadeepa archives 
        if (BasicCrawlController.paper==paperE.LankadeepaArchives) {


            for (int i = 110027; i < 1000000; i++) {
                String pageURL = "http://www.lankadeepa.lk/index.php/articles/" + i;
                // String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageURL);

                System.out.println("**********Crawling**********" + pageURL);
                processPage(pageURL, null, null, null,null);

            }
        } // start crawling for divaina archives
        else if (BasicCrawlController.paper==paperE.DivainaArchives) {

            String year = null;
            String month = null;
            String date = null;
            String news = null;
            for (int i = 2012; i < 2014; i++) {

                for (int j = 1; j < 13; j++) {

                    for (int k = 1; k < 32; k++) {
                        for (int p = 1; p < 15; p++) {
                            if (j < 10) {
                                month = "0" + j;

                            }
                            if (k < 10) {
                                date = "0" + k;
                            }
                            if (p < 10) {
                                news = "0" + p;

                            }else{
                                news = ""+p;
                            }
                            year = "" + i;

                            String pageURL = "http://www.divaina.com/" + i + "/" + month + "/" + date + "/news" + news + ".html";


                            System.out.println("**********Crawling**********" + pageURL);
                            processPage(pageURL, year, month, date,null);

                        }
                    }

                }

            }
        } else if (BasicCrawlController.paper==paperE.DinaminaArchives) {


            String year = null;
            String month = null;
            String date = null;
            String news = null;
            String cat = null;
            for (int i = 13; i < 14; i++) {

                for (int j = 1; j < 13; j++) {

                    for (int k = 1; k < 32; k++) {
                        for (int category = 0; category < 6; category++) {
                            for (int p = 1; p < 30; p++) {
                                if (j < 10) {
                                    month = "0" + j;

                                }
                                if (k < 10) {
                                    date = "0" + k;
                                }
                                //if (p < 10) {
                                //news = "0" + p;

                                //}

                                if (category == 0) {
                                    cat = "n";
                                } else if (category == 1) {
                                    cat = "s";
                                } else if (category == 2) {
                                    cat = "f";
                                } else if (category == 3) {
                                    cat = "r";
                                } else if (category == 4) {
                                    cat = "w";
                                }
                                else if (category == 5) {
                                    cat = "e";
                                }

                                String pageURL = "http://www.dinamina.lk/20" + i + "/" + month + "/" + date + "/_art.asp?fn=" + cat + i + month + date + p + "";

                                year = "20" + i;
                                //      http://www.dinamina.lk/2013/05/22/_art.asp?fn=n1305226
                                System.out.println("**********Crawling**********" + pageURL);
                                processPage(pageURL, year, month, date,cat);
                            }

                        }
                    }

                }

            }


        }


        // end of modification
        // }
    }

    /**
     * Classes that extends WebCrawler can overwrite this function to tell the
     * crawler whether the given url should be crawled or not. The following
     * implementation indicates that all urls should be included in the crawl.
     *
     * @param url the url which we are interested to know whether it should be
     * included in the crawl or not.
     * @return if the url should be included in the crawl it returns true,
     * otherwise false is returned.
     */
    public boolean shouldVisit(String url) {
        return true;
    }

    /**
     * Classes that extends WebCrawler can overwrite this function to process
     * the content of the fetched and parsed page.
     *
     * @param page the page object that is just fetched and parsed.
     */
    public void visit(Page page) {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    }

    private void processPage(String curURL, String year, String month, String date,String cat) {
        if (curURL == null) {
            return;
        }
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchHeader(curURL);
            int statusCode = fetchResult.getStatusCode();
            handlePageStatusCode(curURL, statusCode, CustomFetchStatus.getStatusDescription(statusCode));
            if (statusCode != HttpStatus.SC_OK) {
                if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    if (myController.getConfig().isFollowRedirects()) {
                        String movedToUrl = fetchResult.getMovedToUrl();
                        if (movedToUrl == null) {
                            return;
                        }


                        // WebURL webURL = new WebURL();
                        // webURL.setURL(movedToUrl);
                        // webURL.setParentDocid(curURL.getParentDocid());
                        // webURL.setParentUrl(curURL.getParentUrl());
                        // webURL.setDepth(curURL.getDepth());
                        // webURL.setDocid(-1);
                        // webURL.setAnchor(curURL.getAnchor());
                        //modified by Adeesha
//						if (shouldVisit(webURL) && robotstxtServer.allows(webURL)) {
//							webURL.setDocid(docIdServer.getNewDocID(movedToUrl));
                        //  frontier.schedule(webURL);
//						}
                        // modified by Adeesha
                    }
                } else if (fetchResult.getStatusCode() == CustomFetchStatus.PageTooBig) {
                    logger.info("Skipping a page which was bigger than max allowed size: " + curURL);
                }
                return;
            }

            if (!curURL.equals(fetchResult.getFetchedUrl())) {

                curURL = (fetchResult.getFetchedUrl());

            }

            Page page = new Page(curURL);

            if (!fetchResult.fetchContent(page)) {
                onContentFetchError(curURL);
                return;
            }

            if (!parser.parse(page, curURL, year, month, date,cat)) {
                onParseError(curURL);
                return;
            }



        } catch (Exception e) {
            logger.error(e.getMessage() + ", while processing: " + curURL);
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }



    }

    public Thread getThread() {
        return myThread;
    }

    public void setThread(Thread myThread) {
        this.myThread = myThread;
    }

    public boolean isNotWaitingForNewURLs() {
        return !isWaitingForNewURLs;
    }
}
