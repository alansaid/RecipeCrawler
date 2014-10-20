package net.recommenders.allrecipescrawler;

/**
 * @author <a href="http://github.com/alansaid">Alan</a>.
 */
public class CollectData {
    public static void main(String[] args) {
        CookCollector cc = new CookCollector();
        cc.collectCooks();
        ProfileCrawler pc = new ProfileCrawler();
        pc.parseProfiles();
        RecipesCrawler rc = new RecipesCrawler();
        rc.crawlRecipesByUserFromFile();
    }
}
