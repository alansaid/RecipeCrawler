/**
 *  RecipesCrawler - a data crawler for allrecipes.com
 *
 *         Copyright (C) 2013  Alan Said
 *
 *  This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU General Public License as published by
 *         the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *         along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.recommenders.allrecipescrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 2013-10-21
 * Time: 14:49
 */
public class CookCollector extends AbstractCrawler {
    private final static Logger logger = LoggerFactory.getLogger(CookCollector.class);

    public static final String cooksURL = "http://allrecipes.com/cooks/cooks.aspx?Page=";

    public static void main(String args[]){
        CookCollector collector = new CookCollector();
        collector.collectCooks();
    }

    public boolean collectCooks() {
        //. = class
        //# = id

        String baseURL = "http://allrecipes.com/cook/";
        int len = baseURL.length();
        Document doc = null;
        System.out.println("Starting user crawl");
        int userCounter = 0;
        StringBuffer userIDs = new StringBuffer();
        for(int page = 1; page <= 8585; page++){
            double randomSleepTime = Math.random() * 5678 + 3876;
            try {
                Thread.sleep((int)randomSleepTime);
                doc = Jsoup.connect(cooksURL+page).userAgent(USER_AGENTS.get((int)Math.random()*USER_AGENTS.size())).timeout(100000).get();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            catch (InterruptedException ie){
                logger.error(ie.getMessage());
                ie.printStackTrace();
            }
            for (int i = 1; i<=39; i=i+2){
                String id;
                if(i <10)
                    id = "0" + i;
                else
                    id = String.valueOf(i);
                String userIdentifier = "a#ctl00_CenterColumnPlaceHolder_CookPageNav_ProfileList_rptCookProfileList_ctl"+id+"_Cook_lnkUserName";
                try {
//                    content = doc.select(nameDiv).first().text();
                    Element link = doc.select(userIdentifier).first();
                    String url = link.attr("href");
                    String userID = url.substring(len, url.lastIndexOf('/'));
                    userIDs.append(userID+"\n");
                    userCounter++;
                } catch (NullPointerException e) {
                    logger.error(e.getMessage());
                    System.out.println("ERROR: wrong identifier");
                }
            }
            if(userCounter > 100){
                writeUsers(userIDs);
                userIDs.setLength(0);
                userCounter = 0;
                System.out.print("\rCrawling page: " + page);
                System.out.flush();
            }
            if(page == 8585 && userIDs.length() != 0){
                writeUsers(userIDs);
                System.out.println("Final users crawled");
            }
        }

        return true;
    }

    public boolean writeUsers(StringBuffer userIDs){
        return writeData(userIDs, "users.csv");
    }
}