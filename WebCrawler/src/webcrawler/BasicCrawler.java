/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcrawler;
import crawler.Page;
import crawler.WebCrawler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;



import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;



import org.apache.http.Header;
/**
 *
 * @author adeesha
 */
public class BasicCrawler extends WebCrawler {
     private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
                        + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

        /**
         * You should implement this function to specify whether the given url
         * should be crawled or not (based on your crawling logic).
         */
        @Override
        public boolean shouldVisit(String url) {
                //String href = url.getURL().toLowerCase();
                //return !FILTERS.matcher(href).matches() && href.startsWith("http://www.lankadeepa.lk/");
                return true;
        }

        /**
         * This function is called when a page is fetched and ready to be processed
         * by your program.
         */
        @Override
        public void visit(Page page) {
               
           
                            
                
              
                
        }
}
