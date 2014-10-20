
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
public class RecipesCrawler extends AbstractCrawler {

    private final static Logger logger = LoggerFactory.getLogger(RecipesCrawler.class);
    public static final String recipeURL = "/recipes.aspx?Page=";
    public static final String baseURL = "http://allrecipes.com/cook/";
    private StringBuffer recipesBuffer = new StringBuffer();

    public static void main(String[] args) {
        System.out.println("args[0] = fromLine \t (args[1] = toLine)");
        System.out.println("When no arguments given, all of hasRecipes.csv is read");
        int fromLine = 0;
        int toLine = 0;
        if (args.length != 0) {
            try {
                fromLine = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.info(e.getMessage());
                System.out.println("Start from line 0");
            }
            try {
                toLine = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.info(e.getMessage());
                System.out.println("Read to EOF");
            }
            System.out.println("Reading from line " + fromLine + " to line " + toLine);
        }
        RecipesCrawler rc = new RecipesCrawler();
        int user1 = 169147;
        int user2 = 15278401;
        int user3 = 10160078;
        int user4 = 13587363;
//        rc.crawlRecipesByUser(user4);
        rc.crawlRecipesByUserFromFile(fromLine, toLine);
    }

    public boolean crawlRecipesByUserFromFile(int from, int to) {
        int counter = 0;
        int bufferCounter = 0;
        Scanner in = null;
        try {
            in = new Scanner(new File("hasRecipes.csv"));
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
            int userID = Integer.parseInt(in.nextLine());
            System.out.print("\rCrawling line: " + counter + "\t profileID: " + userID);
            System.out.flush();

            crawlRecipesByUser(userID);
            if (bufferCounter == 20) {
                writeRecipes(recipesBuffer);
                recipesBuffer.setLength(0);

                bufferCounter = 0;
            }
            counter++;
            bufferCounter++;
        }
        System.out.println("\n");
        System.out.println("Crawled: " + (counter - from) + " cooks");
        if (bufferCounter > 0) {
            writeRecipes(recipesBuffer);
        }
        return true;
    }

    public boolean crawlRecipesByUser(int userID) {
        String recipesURL = baseURL + userID + recipeURL;
        Document doc = null;
        double randomSleepTime = Math.random() * 1278 + 576;

        try {
            Thread.sleep((int) randomSleepTime);
            doc = Jsoup.connect(recipesURL).userAgent(USER_AGENTS.get((int) Math.random() * USER_AGENTS.size())).timeout(100000).get();
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
                    doc = Jsoup.connect(recipesURL + i).userAgent(USER_AGENTS.get((int) Math.random() * USER_AGENTS.size())).timeout(100000).get();
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
                String user = userID + "\t" + url + "\t" + type + "\t" + overall + "\t" + personal + "\t" + date + "\t" + System.currentTimeMillis();
                recipesBuffer.append(user + "\n");
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
