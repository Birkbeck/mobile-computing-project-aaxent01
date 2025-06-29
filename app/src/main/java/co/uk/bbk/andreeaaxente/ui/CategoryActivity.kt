package co.uk.bbk.andreeaaxente.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.uk.bbk.andreeaaxente.databinding.ActivityCategoryBinding
import co.uk.bbk.andreeaaxente.model.Recipe
import co.uk.bbk.andreeaaxente.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

//this will display a list of recipes by categories and the possibility of new recipes to be added
class CategoryActivity : AppCompatActivity() {

    companion object { const val EXTRA_CATEGORY_NAME = "CATEGORY_NAME" }

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //gets the chosen category, the default is set to All
        category = intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: "All"
        binding.categoryTitleBar.title = category
        binding.categoryTitleBar.setNavigationOnClickListener { finish() }

        //adds a new recipe inside the chosen category
        binding.addRecipeButton.setOnClickListener {
            Intent(this, AddEditRecipeActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY_NAME, category)
                startActivity(this)
            }
        }
    }

    //when the activity resumes the recipes are loaded
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val recipes = withContext(Dispatchers.IO) {
                if (category == "All") RecipeRepository.getAllRecipes(this@CategoryActivity)
                else RecipeRepository.getRecipesByCategory(this@CategoryActivity, category)
            }
            displayRecipes(recipes)
        }
    }

    //in the ui populate dynamically up to four recipe cards
    private fun displayRecipes(recipes: List<Recipe>) {
        val cardRoots = listOf(
            binding.recipeCard1.root,
            binding.recipeCard2.root,
            binding.recipeCard3.root,
            binding.recipeCard4.root
        )

        cardRoots.forEachIndexed { index, rootView ->
            if (index < recipes.size) {
                rootView.visibility = View.VISIBLE
                val recipe = recipes[index]

                //access inner views of the layout card
                val container = rootView.getChildAt(0) as ViewGroup
                val nameTv = container.getChildAt(0) as TextView
                val imgVw = container.getChildAt(1) as ImageView

                nameTv.text = recipe.name
                recipe.imageUri?.let { imgVw.setImageURI(it.toUri()) }

                //when the a specific card is clicked it navigates to its recipe details
                rootView.setOnClickListener {
                    Intent(this, RecipePageActivity::class.java).apply {
                        putExtra(RecipePageActivity.EXTRA_RECIPE_ID, recipe.id)
                        startActivity(this)
                    }
                }
            } else {
                rootView.visibility = View.GONE
            }
        }
    }
}
