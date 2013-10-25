/**
 *  RecipeCrawler - a data crawler for allrecipes.com
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
/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 2013-10-25
 * Time: 13:59
 */
public class RecipeCrawler {

    public static final String recipeURL = "/recipes.aspx?Page=";

    public static void main(String[] args) {
        RecipeCrawler rc = new RecipeCrawler();
    }

    public boolean crawlRecipesByUser(int userID){


        return true;
    }

    public boolean crawlRecipesByRecipe(){
        // todo
        return true;
    }
}
