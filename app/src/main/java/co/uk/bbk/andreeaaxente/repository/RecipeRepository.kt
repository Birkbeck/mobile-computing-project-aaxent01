package co.uk.bbk.andreeaaxente.repository

import android.content.Context
import co.uk.bbk.andreeaaxente.data.AppDatabase
import co.uk.bbk.andreeaaxente.model.Recipe

//perform CRUD operations on Recipe with the help of Room database
object RecipeRepository {
    private fun dao(context: Context) = AppDatabase.getInstance(context).recipeDao()

    //add new recipe to the database
    suspend fun addRecipe(context: Context, recipe: Recipe) {
        dao(context).insert(recipe)
    }

    //update a recipe already present in the database
    suspend fun updateRecipe(context: Context, recipe: Recipe) {
        dao(context).update(recipe)
    }

    //delete a recipe already present database
    suspend fun deleteRecipe(context: Context, recipe: Recipe) {
        dao(context).delete(recipe)
    }

    //retrieve a single recipe with the help of the unique id
    suspend fun getRecipeById(context: Context, id: Int): Recipe? =
        dao(context).getRecipeById(id)

    //retrieve all existent recipes in the database
    suspend fun getAllRecipes(context: Context): List<Recipe> =
        dao(context).getAllRecipes()

    //retrieve a recipe by the specific category
    suspend fun getRecipesByCategory(context: Context, category: String): List<Recipe> =
        dao(context).getRecipesByCategory(category)
}
