
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 2013-10-25
 * Time: 13:59
 */
public class RecipesCrawler {
    private final static Logger logger = LoggerFactory.getLogger(RecipesCrawler.class);

    public static final String recipeURL = "/recipes.aspx?Page=";
    public static final String baseURL = "http://allrecipes.com/cook/";
    private StringBuffer recipesBuffer = new StringBuffer();

    public static void main(String[] args) {
        System.out.println("args[0] = fromLine \t (args[1] = toLine)");
        System.out.println("When no arguments given, all of hasRecipes.csv is read");
        int fromLine = 0;
        int toLine = 0;
        if(args.length != 0){
            try{
                fromLine = Integer.parseInt(args[0]);
            }catch (NumberFormatException e){
                logger.info(e.getMessage());
                System.out.println("Start from line 0");
            }
            try{
                toLine = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
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


    public boolean crawlRecipesByUserFromFile(int from, int to){
        int counter = 0;
        int bufferCounter = 0;
        Scanner in = null;
        try {
            in = new Scanner(new File("hasRecipes.csv"));
        } catch (IOException e){
            logger.error(e.getMessage());
        }

        while(in.hasNextLine()){
            if(counter < from){
                counter++;
                in.nextLine();
                continue;
            }
            if(to != 0 && counter > to)
                break;
            int userID = Integer.parseInt(in.nextLine());
            System.out.print("\rCrawling line: " + counter + "\t profileID: " + userID);
            System.out.flush();

            crawlRecipesByUser(userID);
            if (bufferCounter == 20){
                writeRecipes(recipesBuffer);
                recipesBuffer.setLength(0);

                bufferCounter = 0;
            }
            counter ++;
            bufferCounter ++;
        }
        System.out.println("\n");
        System.out.println("Crawled: " + (counter - from) + " cooks");
        if(bufferCounter > 0){
            writeRecipes(recipesBuffer);
        }
        return true;
    }



    public boolean crawlRecipesByUser(int userID){
        String recipesURL = baseURL+userID+recipeURL;
        List<String> userAgents = new ArrayList<String>() {{
            add("Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14");
            add("Mozilla/5.0 (Windows NT 6.0; rv:2.0) Gecko/20100101 Firefox/4.0 Opera 12.14");
            add("Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02");
            add("Opera/9.80 (Windows NT 5.1; U; zh-sg) Presto/2.9.181 Version/12.00");
            add("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; de) Presto/2.9.168 Version/11.52");
            add("Opera/9.80 (X11; Linux x86_64; U; Ubuntu/10.10 (maverick); pl) Presto/2.7.62 Version/11.01");
            add("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1623.0 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1464.0 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:25.0) Gecko/20100101 Firefox/25.0");
            add("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:24.0) Gecko/20100101 Firefox/24.0");
            add("Mozilla/5.0 (Windows NT 6.2; rv:22.0) Gecko/20130405 Firefox/23.0");
            add("Mozilla/5.0 (Windows NT 6.1; rv:6.0) Gecko/20100101 Firefox/19.0");
            add("Mozilla/5.0 (Windows NT 6.1; rv:14.0) Gecko/20100101 Firefox/18.0.1");
            add("Mozilla/5.0 (X11; Linux x86_64; rv:17.0) Gecko/20121202 Firefox/17.0 Iceweasel/17.0.1");
            add("Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0");
            add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
            add("Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US))");
            add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Zune 4.0; InfoPath.3; MS-RTC LM 8; .NET4.0C; .NET4.0E)");
            add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; chromeframe/12.0.742.112)");
            add("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25");
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2");
        }};
        Document doc = null;
        double randomSleepTime = Math.random() * 1278 + 576;

        try {
            Thread.sleep((int)randomSleepTime);
            doc = Jsoup.connect(recipesURL).userAgent(userAgents.get((int)Math.random()*userAgents.size())).timeout(100000).get();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (InterruptedException ie){
            logger.error(ie.getMessage());
            ie.printStackTrace();
        }
        String pageNums = doc.select("div#ctl00_CenterColumnPlaceHolder_RecipePage_pager_corePager > div.page_display").text().replace(",","");
        int pages = 1;
        if(!pageNums.isEmpty())
            pages = (int)Math.ceil(Double.parseDouble(pageNums.substring(pageNums.indexOf("f")+2,pageNums.indexOf(")")).trim()) / 10);


        for(int i = 1; i <= pages; i++){
            if(i > 1){
                try {
                    doc = Jsoup.connect(recipesURL+i).userAgent(userAgents.get((int)Math.random()*userAgents.size())).timeout(100000).get();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            Element recipe = doc.select("div.recipelistview-container > div.row").first();
            Elements recipes = recipe.siblingElements();
            recipes.add(recipe);
            for(Element rec : recipes){
                if(rec.hasClass("clear"))
                    continue;
                String url = rec.select("a.title").first().attr("href").replace("/detail.aspx", "");
                String type = rec.select("li.recipe-list-type").text();
                String overall = rec.select("div.rating-stars").first().select("meta[itemprop=ratingValue]").attr("content");
                if(overall.equals("0"))
                    overall = "";
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

    public boolean writeRecipes(StringBuffer input){
        return writeData(input, "user-recipe.tsv");
    }
    public boolean writeData(StringBuffer input, String filename){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
            out.write(input.toString());
            out.flush();
            out.close();
        }
        catch (IOException e){
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean crawlRecipesByRecipe(){
        // todo
        return true;
    }
}
