package co.uk.bbk.andreeaaxente.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.uk.bbk.andreeaaxente.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //accessing the layout views with the help of ViewBinding instance
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // with the help setOnClickListener each respective category is opened on click
        binding.breakfastButton.setOnClickListener { openCategory("Breakfast") }
        binding.brunchButton   .setOnClickListener { openCategory("Brunch")   }
        binding.lunchButton    .setOnClickListener { openCategory("Lunch")    }
        binding.dinnerButton   .setOnClickListener { openCategory("Dinner")   }
        binding.dessertsButton .setOnClickListener { openCategory("Desserts") }
        binding.otherButton    .setOnClickListener { openCategory("Other")    }
        binding.allRecipesButton.setOnClickListener { openCategory("All")     }

        // setOnClickListener helps open AddEditRecipeActivity when add recipe button is clicked
        binding.addRecipeButton.setOnClickListener {
            startActivity(Intent(this, AddEditRecipeActivity::class.java))
        }
    }
    //this function will start CategoryActivity with the selected category
    private fun openCategory(cat: String) {
        Intent(this, CategoryActivity::class.java).also {
            it.putExtra(CategoryActivity.EXTRA_CATEGORY_NAME, cat)
            startActivity(it)
        }
    }
}
