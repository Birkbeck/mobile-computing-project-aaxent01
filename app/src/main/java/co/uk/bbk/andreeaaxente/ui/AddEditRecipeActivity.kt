package co.uk.bbk.andreeaaxente.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.uk.bbk.andreeaaxente.R
import co.uk.bbk.andreeaaxente.databinding.ActivityAddEditRecipePageBinding
import co.uk.bbk.andreeaaxente.model.Recipe
import co.uk.bbk.andreeaaxente.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditRecipePageBinding
    private var selectedImageUri: Uri? = null
    private var existingRecipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRecipePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //category dropdown setup from resources
        val categories = resources.getStringArray(R.array.recipe_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.chooseCategory.setAdapter(adapter)

        //pre fill a category if it was launched with it
        intent.getStringExtra(CategoryActivity.EXTRA_CATEGORY_NAME)
            ?.let { binding.chooseCategory.setText(it, false) }

        //load the recipe for editing after the recipe id is passed
        val recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        if (recipeId != -1) {
            lifecycleScope.launch {
                existingRecipe = withContext(Dispatchers.IO) {
                    RecipeRepository.getRecipeById(this@AddEditRecipeActivity, recipeId)
                }
                //if a recipe is found fill the ui fields
                existingRecipe?.let { r ->
                    binding.inputRecipeName.setText(r.name)
                    binding.inputCookingTime.setText(r.cookingTime.toString())
                    binding.inputIngredients.setText(r.ingredients)
                    binding.inputSteps.setText(r.steps)
                    binding.chooseCategory.setText(r.category, false)
                    r.imageUri?.let {
                        selectedImageUri = Uri.parse(it)
                        binding.recipeImageHeader.setImageURI(selectedImageUri)
                        binding.addImageLabel.visibility = View.GONE
                    }
                }
            }
        }

        //when image header is clicked the user can pick a new image for the recipe
        binding.imageHeaderContainer.setOnClickListener {
            openImagePicker()
        }
    }

    //during add recipe activity if the cancel button is clicked close the activity without saving
    fun onCancelRecipeClicked(view: View) {
        finish()
    }

    //during add recipe activity if the save button is clicked save all the inputted data,
    //save it in the repository and close the activity
    fun onSaveRecipeClicked(view: View) {
        val name        = binding.inputRecipeName.text.toString().trim()
        val time        = binding.inputCookingTime.text.toString().toIntOrNull() ?: 0
        val ingredients = binding.inputIngredients.text.toString().trim()
        val steps       = binding.inputSteps.text.toString().trim()
        val category    = binding.chooseCategory.text.toString()
        val imageUriStr = selectedImageUri?.toString()

        //this is used to create a new recipe object
        //if the edit button is clicked the existing recipe id will be kept
        val toSave = Recipe(
            id          = existingRecipe?.id ?: 0,
            name        = name,
            cookingTime = time,
            ingredients = ingredients,
            steps       = steps,
            imageUri    = imageUriStr,
            category    = category
        )

        //data is saved off the main thread and the activity is finished
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (existingRecipe == null) {
                    RecipeRepository.addRecipe(this@AddEditRecipeActivity, toSave)
                } else {
                    RecipeRepository.updateRecipe(this@AddEditRecipeActivity, toSave)
                }
            }
            finish()
        }
    }

    private fun openImagePicker() {
        //TODO: image selection to implement
    }

    companion object {
        const val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
        const val EXTRA_CATEGORY_NAME = "EXTRA_CATEGORY_NAME"
    }
}
