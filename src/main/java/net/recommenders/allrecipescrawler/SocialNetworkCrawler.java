/**
 * RecipeBoxCrawler - a data crawler for allrecipes.com
 *
 * Copyright (C) 2013 Alan Said
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.recommenders.allrecipescrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: alejandro
 */
public class SocialNetworkCrawler extends AbstractCrawler {

    private final static Logger logger = LoggerFactory.getLogger(SocialNetworkCrawler.class);
    public static final String cooksILikeURL = "http://allrecipes.com/my/content/cooksilike.aspx?userID=";

    public static void main(String args[]) throws Exception {
//        args = new String[]{"/ufs/alejandr/recommend2/alex/ar/crawled/all/hasCooks.csv", "/ufs/alejandr/recommend2/alex/ar/crawled/all/user-cooksilike2.csv", "/ufs/alejandr/recommend2/alex/ar/crawled/all/user-cooksilike.csv"};

        Set<String> usersAlreadyProcessed = new HashSet<String>();
        if (args.length > 2) {
            BufferedReader br = new BufferedReader(new FileReader(args[2]));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] toks = line.split("\t");
                usersAlreadyProcessed.add(toks[0]);
            }
            br.close();
        }

        SocialNetworkCrawler collector = new SocialNetworkCrawler();

        PrintStream out = new PrintStream(args[1]);
        collector.collectCooks(new File(args[0]), out, usersAlreadyProcessed);
        out.close();

//        collector.collectCooks("13124128", System.out); // no cooks
//        collector.collectCooks("10983665", System.out); // 55 cooks
    }

    public boolean collectCooks(File f, PrintStream out, Set<String> usersToIgnore) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = null;
        while ((line = br.readLine()) != null) {
            String user = line;
            if (usersToIgnore.contains(user)) {
                continue;
            }
            collectCooks(user, out);
        }
        br.close();
        return true;
    }

    public boolean collectCooks(String user, PrintStream out) {
        return collectCooks(user, null, out);
    }

    public boolean collectCooks(String user, String page, PrintStream out) {
        //. = class
        //# = id
        String baseURL = "http://allrecipes.com/cook/";
//        double randomSleepTime = Math.random() * 5678 + 3876;
        double randomSleepTime = 1234;
        try {
            Thread.sleep((int) randomSleepTime);
            long t = System.currentTimeMillis();
            Document doc = Jsoup.connect(cooksILikeURL + user + (page == null ? "" : "&Page=" + page)).userAgent(USER_AGENTS.get((int) Math.random() * USER_AGENTS.size())).timeout(100000).get();
            Elements divsILike = doc.select("div.cooksilike");
            for (Element divILike : divsILike) {
                Elements usersDiv = divILike.select("div.user_link");
                for (Element userDiv : usersDiv) {
                    Elements ellipsis = userDiv.select("a.ellipsis_inner"); // ellipsis_inner expanded_s
                    String url = ellipsis.attr("href");
                    url = url.replace(baseURL, "").replace("/", "");
                    String name = ellipsis.text();
                    out.println(user + "\t" + name + "\t" + url + "\t" + t);
                }
            }
            // check next page
            Elements pagings = doc.select("div.paging");
            for (Element paging : pagings) {
                Elements aPage = paging.select("a");
                for (Element a : aPage) {
                    if (a.text().startsWith("NEXT")) {
                        String url = a.attr("href");
                        String p = "&Page=";
                        url = url.substring(url.indexOf(p) + p.length());
                        collectCooks(user, url, out);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Problem with user " + user + " in page " + page);
            logger.error(e.getMessage());
            System.out.println("Problem with user " + user + " in page " + page);
            e.printStackTrace();
        } catch (InterruptedException ie) {
            logger.error("Problem with user " + user + " in page " + page);
            logger.error(ie.getMessage());
            System.out.println("Problem with user " + user + " in page " + page);
            ie.printStackTrace();
        }
        return true;
    }
}
