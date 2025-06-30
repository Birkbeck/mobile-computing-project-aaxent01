package co.uk.bbk.andreeaaxente.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//this class will store a Recipe entity in the Room database
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    //this will generate a primary key for a recipe automatically
    var id: Int = 0,
    //name of the recipe
    var name: String,
    //cooking time in minutes for the recipe
    var cookingTime: Int,
    //all recipe ingredients
    var ingredients: String,
    //cooking steps
    var steps: String,
    //recipe image
    var imageUri: String? = null,
    //recipe category
    var category: String
)