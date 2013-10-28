
/*
 * RecipeCrawler - a data crawler for allrecipes.com
 *
 *        Copyright (C) 2013  Alan Said
 *
 * This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *        along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.recommenders.allrecipescrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA. User: alan Date: 2013-10-25 Time: 13:59
 */

/**
 * Class for crawling recipe details from Allrecipes.com.
 */
public class RecipeCrawler extends AbstractCrawler {

    private final static Logger logger = LoggerFactory.getLogger(RecipeCrawler.class);

    /**
     * Main method for RecipeCrawler
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("usage: -Dfile=input_file -Dfrom=start_on_line -Dto=end_on_line)");
        System.out.println("When no arguments given, all of recipeURL.tsv is read");
        int fromLine = 0;
        int toLine = 0;
        try {
            fromLine = Integer.parseInt(System.getProperty("from"));
        } catch (NumberFormatException e) {
            logger.info(e.getMessage());
            System.out.println("Start from line 0");
        }
        try {
            toLine = Integer.parseInt("to");
        } catch (NumberFormatException e) {
            logger.info(e.getMessage());
            System.out.println("Read to EOF");
        }
        System.out.println("Reading from line " + fromLine + " to line " + toLine);
        RecipeCrawler rc = new RecipeCrawler();
        int user1 = 169147;
        int user2 = 15278401;
        int user3 = 10160078;
        int user4 = 13587363;
//        rc.crawlRecipesByUser(user4);
        rc.crawlRecipesFromFile(fromLine, toLine);
    }

    /**
     * Crawl recipe details from URLs in a file.
     * @param from  start at line _from_ in file
     * @param to    end reading file at _to_
     * @return  true if successful
     */
    public boolean crawlRecipesFromFile(int from, int to) {
        int counter = 0;
        int bufferCounter = 0;
        Scanner in = null;
        try {
            in = new Scanner(new File("recipeURL.csv"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        while (in.hasNextLine()) {
            if (counter < from) {
                counter++;
                in.nextLine();
                continue;
            }
            if (to != 0 && counter > to) {
                break;
            }
            String url = in.nextLine();
            System.out.print("\rCrawling line: " + counter + "\t profileID: " + url);
            System.out.flush();

            crawlRecipeByURL(url);
            if (bufferCounter == 20) {
                writeRecipes(dataBuffer);
                dataBuffer.setLength(0);

                bufferCounter = 0;
            }
            counter++;
            bufferCounter++;
        }
        System.out.println("\n");
        System.out.println("Crawled: " + (counter - from) + " cooks");
        if (bufferCounter > 0) {
            writeRecipes(dataBuffer);
        }
        return true;
    }

    public boolean crawlRecipeByURL(String recipeURL) {
        Document doc = null;
        double randomSleepTime = Math.random() * 1278 + 576;

        try {
            Thread.sleep((int) randomSleepTime);
            doc = Jsoup.connect(recipeURL).userAgent(USER_AGENTS.get((int) Math.random() * USER_AGENTS.size())).timeout(100000).get();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            logger.error(ie.getMessage());
            ie.printStackTrace();
        }
        String pageNums = doc.select("div#ctl00_CenterColumnPlaceHolder_RecipePage_pager_corePager > div.page_display").text().replace(",", "");
        int pages = 1;
        if (!pageNums.isEmpty()) {
            pages = (int) Math.ceil(Double.parseDouble(pageNums.substring(pageNums.indexOf("f") + 2, pageNums.indexOf(")")).trim()) / 10);
        }


        for (int i = 1; i <= pages; i++) {
            if (i > 1) {
                try {
                    doc = Jsoup.connect(recipeURL + i).userAgent(USER_AGENTS.get((int) Math.random() * USER_AGENTS.size())).timeout(100000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Element recipe = doc.select("div.recipelistview-container > div.row").first();
            Elements recipes = recipe.siblingElements();
            recipes.add(recipe);
            for (Element rec : recipes) {
                if (rec.hasClass("clear")) {
                    continue;
                }
                String url = rec.select("a.title").first().attr("href").replace("/detail.aspx", "");
                String type = rec.select("li.recipe-list-type").text();
                String overall = rec.select("div.rating-stars").first().select("meta[itemprop=ratingValue]").attr("content");
                if (overall.equals("0")) {
                    overall = "";
                }
                String personal = rec.select("div.rating-stars").last().select("meta[itemprop=ratingValue]").attr("content");
//                if(personal.equals("0"))
//                    personal = "0";
                String date = rec.select("li.recipe-list-added").text();
                String recipeContent ="";// userID + "\t" + url + "\t" + type + "\t" + overall + "\t" + personal + "\t" + date + "\t" + System.currentTimeMillis();
                dataBuffer.append(recipeContent + "\n");
            }
        }
        return true;
    }

    public boolean writeRecipes(StringBuffer input) {
        return writeData(input, "user-recipe.tsv");
    }


    public boolean crawlRecipesByRecipe() {
        // todo
        return true;
    }
}
