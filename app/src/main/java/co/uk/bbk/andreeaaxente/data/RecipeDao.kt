package co.uk.bbk.andreeaaxente.data

import androidx.room.*
import co.uk.bbk.andreeaaxente.model.Recipe

//data access object interface detailing the operations of the database for Recipe entities
@Dao
interface RecipeDao {
    //get all recipes present in the database
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): List<Recipe>

    //get all the recipes that have the same category
    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getRecipesByCategory(category: String): List<Recipe>

    //get a recipe using its unique id
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Recipe?

    //add a recipe to the database and in case of a conflict it will replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recipe: Recipe): Long

    //update a present recipe in the database
    @Update
    fun update(recipe: Recipe)

    //delete a present recipe in the database
    @Delete
    fun delete(recipe: Recipe)

    //get all recipes that contain the string specified by the user
    @Query("SELECT * FROM recipes WHERE ingredients LIKE '%' || :ingredient || '%'")
    fun getRecipesByIngredient(ingredient: String): List<Recipe>
}