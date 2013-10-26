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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 2013-10-21
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class ProfileCrawler extends AbstractCrawler {
    private final static Logger logger = LoggerFactory.getLogger(ProfileCrawler.class);

    public static final String profileURL = "http://allrecipes.com/cook/";
    public static final String reviewURL = "/reviews.aspx?Page=";
    public static final String menuURL = "/menus.aspx?Page=";
    public static final String blogURL = "/blog.aspx?Page=";
    public static final String profileFile = "profiles.csv";
    public static final String hasCooksFile = "hasCooks.csv";
    public static final String hasReviewsFile = "hasReviews.csv";
    public static final String hasRecipesFile = "hasRecipes.csv";
    private StringBuffer profiles= new StringBuffer();
    private StringBuffer recipes = new StringBuffer();
    private StringBuffer cooks = new StringBuffer();
    private StringBuffer reviews = new StringBuffer();

    public static void main(String[] args) {

        System.out.println("args[0] = fromLine \t (args[1] = toLine)");
        System.out.println("When no arguments given, all of users.csv is read");
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
        ProfileCrawler pc = new ProfileCrawler();

        int userID = 18114940;
        int proUserID = 12256565;
        int inactiveUserID = 188406874;
        int emptyUserID = 13944900;
        int errUser = 13482741;
//        pc.parseProfile(errUser);

        pc.parseProfiles(fromLine, toLine);
//        pc.parseProfile(10000003);
    }

    public boolean parseProfiles(int from, int to){
        int counter = 0;
        int bufferCounter = 0;
        Scanner in = null;
        try {
            in = new Scanner(new File("users.csv"));
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
            parseProfile(userID);
            if (bufferCounter == 20){
                writeProfiles(profiles);
                profiles.setLength(0);
                writeCooks(cooks);
                cooks.setLength(0);
                writeRecipes(recipes);
                recipes.setLength(0);
                writeReviews(reviews);
                reviews.setLength(0);
                bufferCounter = 0;
            }
            System.out.print("\rCrawling line: " + counter + "\t profileID: " + userID);
            System.out.flush();
            counter ++;
            bufferCounter ++;
        }
        System.out.println("\n");
        System.out.println("Crawled: " + (counter - from) + " cooks");
        if(bufferCounter > 0){
            writeProfiles(profiles);
            writeCooks(cooks);
            writeRecipes(recipes);
            writeReviews(reviews);
        }
        return true;
    }


    public void parseProfile(int userID){
        Document doc = null;
        double randomSleepTime = Math.random() * 978 + 176;

        try {
            Thread.sleep((int)randomSleepTime);
            doc = Jsoup.connect(profileURL + userID).userAgent(USER_AGENTS.get((int)Math.random()*USER_AGENTS.size())).timeout(100000).get();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (InterruptedException ie){
            logger.error(ie.getMessage());
            ie.printStackTrace();
        }
        boolean proUser = !doc.select("a.proPill35x12").isEmpty();
        String name = doc.select("div#ctl00_CenterColumnPlaceHolder_ucProfileDetails_cooksName_litName").text();
        Element homeTownElem = doc.select("div#ctl00_CenterColumnPlaceHolder_ucProfileDetails_ucCooksLocation_HomeTownArea").first();
        String homeTown = homeTownElem.select("span").text();

        List<Element> elements = homeTownElem.siblingElements().subList(0,5);
        String livingIn = (elements.get(0).text()+" ").split(":")[1].trim();
        String memberSince= (elements.get(1).text()+" ").split(":")[1].trim();
        String cookingLevel = (elements.get(2).text()+" ").split(":")[1].trim();
        String cookingInterests = (elements.get(3).text()+" ").split(":")[1].trim();
        String hobbies = (elements.get(4).text()+" ").split(":")[1].trim();

        String user = userID + "\t" +
                name + "\t" +
                homeTown + "\t" +
                livingIn + "\t" +
                memberSince + "\t" +
                cookingLevel + "\t" +
                cookingInterests + "\t" +
                hobbies + "\t" +
                proUser + "\t" +
                System.currentTimeMillis();

        boolean likesCooks = !doc.select("a#ctl00_RightColumnTopPlaceHolder_ucCooksILike_lnkViewAll").isEmpty();
        boolean hasRecipeBox = !doc.select("div#ctl00_CenterColumnPlaceHolder_divSharedRecipeBoxHeader > div.title").first().text().equals("Recipe Box 0 recipes");
        Element reviewBox = doc.select("div#ctl00_CenterColumnPlaceHolder_ucProfileReviewList_divHeader > div.title").first();
        boolean hasReviewBox = false;
        if(reviewBox != null)
            hasReviewBox = !doc.select("div#ctl00_CenterColumnPlaceHolder_ucProfileReviewList_divHeader > div.title").first().text().equals("Recipe Reviews");
        if(likesCooks)
            cooks.append(userID + "\n");
        if(hasRecipeBox)
            recipes.append(userID + "\n");
        if(hasReviewBox)
            reviews.append(userID + "\n");
        profiles.append(user + "\n");
//        System.out.println(user);
//        System.out.println("likesCooks: " + likesCooks);
//        System.out.println("hasRecipes: " + hasRecipeBox);
//        System.out.println("hasReviews: " + hasReviewBox);
    }


    private boolean writeProfiles(StringBuffer profiles){
        return writeData(profiles, profileFile);
    }
    private boolean writeCooks(StringBuffer cooks){
        return writeData(cooks, hasCooksFile);
    }
    private boolean writeRecipes(StringBuffer recipes){
        return writeData(recipes, hasRecipesFile);
    }
    private boolean writeReviews(StringBuffer reviews){
        return writeData(reviews, hasReviewsFile);
    }


}
