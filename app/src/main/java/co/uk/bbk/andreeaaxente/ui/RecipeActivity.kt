package co.uk.bbk.andreeaaxente.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.uk.bbk.andreeaaxente.databinding.ActivityRecipePageBinding
import co.uk.bbk.andreeaaxente.model.Recipe
import co.uk.bbk.andreeaaxente.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

//this class will show details for the chosen recipe and
// allows actions like edit and delete to be performed
class RecipePageActivity : AppCompatActivity() {
    companion object { const val EXTRA_RECIPE_ID = "RECIPE_ID" }
    private lateinit var binding: ActivityRecipePageBinding
    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        if (id != -1) {
            lifecycleScope.launch {
                recipe = withContext(Dispatchers.IO) {
                    RecipeRepository.getRecipeById(this@RecipePageActivity, id)
                }
                recipe?.let { bindRecipe(it) } ?: finish()
            }
        }

        binding.previousPageButton.setOnClickListener { finish() }
    }

    //this will populate the ui views with the details of the recipe
    @SuppressLint("SetTextI18n")
    private fun bindRecipe(r: Recipe) {
        binding.recipeName.text = r.name
        binding.cookingTime.text = "${r.cookingTime} min"
        binding.ingredientsDescription.text = r.ingredients
        binding.stepsDescription.text = r.steps
        r.imageUri?.let { binding.recipeImageHeader.setImageURI(it.toUri()) }
    }

    //the ability to edit the chosen recipe by opening AddEditRecipeActivity
    fun onEditRecipeClicked(view: View) {
        recipe?.also {
            Intent(this, AddEditRecipeActivity::class.java).apply {
                putExtra(AddEditRecipeActivity.EXTRA_RECIPE_ID, it.id)
                putExtra(AddEditRecipeActivity.EXTRA_CATEGORY_NAME, it.category)
                startActivity(this)
            }
        }
    }

    //the ability to delete the chosen recipe and finish the activity
    fun onDeleteRecipeClicked(view: View) {
        recipe?.let {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) { RecipeRepository.deleteRecipe(this@RecipePageActivity, it) }
                finish()
            }
        }
    }
}
