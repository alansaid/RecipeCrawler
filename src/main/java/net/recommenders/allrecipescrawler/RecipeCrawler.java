
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

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA. User: alan Date: 2013-10-25 Time: 13:59
 */

/**
 * Class for crawling recipe details from Allrecipes.com.
 */
public class RecipeCrawler extends AbstractCrawler {

    private final static Logger logger = LoggerFactory.getLogger(RecipeCrawler.class);
    private static String fileName = "recipeURL.tsv";
    public StringBuffer ingredientBuffer = new StringBuffer();

    /**
     * Main method for RecipeCrawler
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("usage: -Dfile=input_file -Dfrom=start_on_line -Dto=end_on_line)");
        System.out.println("When no arguments given, all of "+fileName+" is read");
        int fromLine = 0;
        int  toLine = 0;
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

        try {
            fileName = System.getProperty("file");
        } catch (NullPointerException e){
            System.out.println("Reading from recipeURL.tsv");
        }
        System.out.println("Reading "+ fileName+" from line " + fromLine + " to line " + toLine);
        RecipeCrawler rc = new RecipeCrawler();

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
            in = new Scanner(new File(fileName));
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
            String url = in.nextLine().split("\t")[1];
            System.out.print("\rCrawling line: " + counter + "\t URL: " + url);
            System.out.flush();

            crawlRecipeByURL(url);
            if (bufferCounter == 20) {
                writeRecipes(dataBuffer);
                dataBuffer.setLength(0);
                writeIngredients(ingredientBuffer);
                ingredientBuffer.setLength(0);
                bufferCounter = 0;
            }
            counter++;
            bufferCounter++;
        }
        System.out.println("\n");
        System.out.println("Crawled: " + (counter - from) + " recipes");
        if (bufferCounter > 0) {
            writeRecipes(dataBuffer);
            writeIngredients(ingredientBuffer);
        }
        return true;
    }

    public boolean crawlRecipeByURL(String recipeURL) {
        Document doc = null;
        double randomSleepTime = Math.random() * 1278 + 576;
        try {
            Thread.sleep((int) randomSleepTime);
            doc = Jsoup.connect(recipeURL).userAgent(USER_AGENTS.get((int) Math.random() * USER_AGENTS.size())).timeout(100000).get();

        }
        catch (HttpStatusException he){
            he.printStackTrace();
            dataBuffer.append(recipeURL + "\t" + "404\n");
            return false;
        }catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            logger.error(ie.getMessage());
            ie.printStackTrace();
        }


        String title = doc.select("h1#itemTitle").text();
        String author = doc.select("span#lblSubmitter").text();
        String authorID = doc.select("span#lblSubmitter > a").attr("href");
        if(!authorID.isEmpty())
            authorID = authorID.substring(authorID.lastIndexOf("k")+2, authorID.lastIndexOf("/"));

        String time = null;
        try {
            time = new String(doc.select("span.time").text().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        String servings = doc.select("span#lblYield").text();

        Elements ingredients = doc.select("ul.ingredient-wrap").select("li#liIngredient");

        String ing = "";
        for (Element ingredient : ingredients){
            ing = ingredient.select("span#lblIngName").text() + "\t" + ingredient.attr("data-ingredientid") + "\t" + ingredient.select("span#lblIngAmount").text();
            ingredientBuffer.append(recipeURL + "\t" + ing + "\n");
        }
        dataBuffer.append(recipeURL + "\t" + title + "\t" + servings + "\t" + author + "\t" + authorID + "\t" + time + "\n");

        return true;
    }

    public boolean writeRecipes(StringBuffer input) {
        return writeData(input, "recipes.tsv");
    }

    public boolean writeIngredients(StringBuffer input){
        return writeData(input, "ingredients.tsv");
    }

}
