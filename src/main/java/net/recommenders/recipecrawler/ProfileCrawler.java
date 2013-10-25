package net.recommenders.recipecrawler;

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
import java.util.List;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 2013-10-21
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class ProfileCrawler {
    private final static Logger logger = LoggerFactory.getLogger(ProfileCrawler.class);

    public static final String profileURL = "http://allrecipes.com/cook/";
    public static final String recipeURL = "/recipes.aspx?Page=";
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
        pc.parseProfile(errUser);

//        pc.parseProfiles(fromLine, toLine);
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
        double randomSleepTime = Math.random() * 978 + 176;

        try {
            Thread.sleep((int)randomSleepTime);
            doc = Jsoup.connect(profileURL + userID).userAgent(userAgents.get((int)Math.random()*userAgents.size())).timeout(100000).get();
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
        System.out.println(user);
        System.out.println("likesCooks: " + likesCooks);
        System.out.println("hasRecipes: " + hasRecipeBox);
        System.out.println("hasReviews: " + hasReviewBox);
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
}
