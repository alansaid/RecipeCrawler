package net.recommenders.recipecrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 2013-10-21
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class CookCollector {
    private final static Logger logger = LoggerFactory.getLogger(CookCollector.class);

    public static final String cooksURL = "http://allrecipes.com/cooks/cooks.aspx?Page=";

    public static void main(String args[]){
        CookCollector collector = new CookCollector();
        collector.collectCooks();
    }

    public boolean collectCooks() {
        //. = class
        //# = id

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

        String baseURL = "http://allrecipes.com/cook/";
        int len = baseURL.length();
        Document doc = null;

        for(int page = 1; page <= 8585; page++){
            StringBuffer userIDs = new StringBuffer();
            double randomSleepTime = Math.random() * 5678 + 1876;
            try {
                Thread.sleep((int)randomSleepTime);
                doc = Jsoup.connect(cooksURL+page).userAgent(userAgents.get((int)Math.random()*userAgents.size())).timeout(10000).get();
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
                } catch (NullPointerException e) {
                    logger.error(e.getMessage());
                    System.out.println("ERROR: wrong identifier");
                }
            }
            if(userIDs.length()> 1000){
                writeUsers(userIDs);
                userIDs.setLength(0);
                System.out.println("on page: " + page);
            }
        }
        return true;
    }

    public boolean writeUsers(StringBuffer userIDs){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter("users.csv", true));
            out.write(userIDs.toString());
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