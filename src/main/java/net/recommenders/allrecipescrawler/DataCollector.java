package net.recommenders.allrecipescrawler;

/**
 * @author <a href="http://github.com/alansaid">Alan</a>.
 */
public class DataCollector {
    public static void main(String[] args) {
        CookCollector cc = new CookCollector();
        cc.collectCooks(); //writes users.csv
        ProfileCrawler pc = new ProfileCrawler();
        pc.parseProfiles(); //writes hasCooks.csv, hasRecipes.csv, hasReviews.csv, profiles.csv
        RecipeBoxCrawler rbc = new RecipeBoxCrawler();
        rbc.crawlRecipesByUserFromFile(); //writes user-recipe.tsv, recipeURLs.tsv
        RecipeCrawler rc = new RecipeCrawler();
        rc.crawlRecipesFromFile(); //writes recipes.tsv, ingredients.tsv, nutrients.tsv
    }
}
