package co.uk.bbk.andreeaaxente

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.uk.bbk.andreeaaxente.data.AppDatabase
import co.uk.bbk.andreeaaxente.data.RecipeDao
import co.uk.bbk.andreeaaxente.model.Recipe
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class AllOtherTests {
    private lateinit var db: AppDatabase
    private lateinit var dao: RecipeDao

    @Before fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.recipeDao()
    }

    @After fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndGetById() = runBlocking {
        val r = Recipe(
            name        = "Full English Breakfast",
            cookingTime = 25,
            ingredients = "Bacon rashers" + "Mushrooms" + "Eggs" + "Beans",
            steps       = "Poach each egg for 3 minutes, and keep warm" +
                    "Grill the bacon and mushrooms" + "Heat the beans",
            imageUri    = null,
            category    = "Breakfast"
        )

        val newId = dao.insert(r).toInt()
        val loaded = dao.getRecipeById(newId)
        assertNotNull(loaded)
        assertEquals("Full English Breakfast", loaded?.name)
    }

    @Test
    fun updateRecipe() = runBlocking {
        val r = Recipe(
            name        = "Full English Breakfast",
            cookingTime = 20,
            ingredients = "4 Bacon rashers" + " 4 Mushrooms" + "2 Eggs" + "150g Beans" + "2 Slices bread and butter",
            steps       = "Poach eggs for 3 minutes, and keep warm" +
                    "Grill the bacon and mushrooms" + "Heat the beans" + "Toast the bread",
            imageUri    = null,
            category    = "Breakfast"
        )
        val id = dao.insert(r).toInt()

        val updated = dao.getRecipeById(id)!!
            .copy(name = "Full English Breakfast updated version")
        dao.update(updated)

        assertEquals("Full English Breakfast updated version", dao.getRecipeById(id)?.name)
    }

    @Test
    fun deleteRecipe() = runBlocking {
        val r = Recipe(
            name        = "Chicken Curry",
            cookingTime = 50,
            ingredients = "6 chicken thighs" + " 400g tomatoes" + "100g greek yogurt" +
                    "50g ground almonds",
            steps       = "Chop the chicken in pieces and fry for 5 minutes and add it with the " +
                    "tomatoes and yogurt to a pan and cook for 30 minutes",
            imageUri    = null,
            category    = "Dinner"
        )
        val id = dao.insert(r).toInt()
        dao.delete(dao.getRecipeById(id)!!)

        assertNull(dao.getRecipeById(id))
    }

    @Test
    fun getRecipesByCategory() = runBlocking {
        dao.insert(Recipe(
            name        = "Full English Breakfast",
            cookingTime = 20,
            ingredients = "4 Bacon rashers" + " 4 Mushrooms" + "2 Eggs" + "150g Beans" + "2 Slices bread and butter",
            steps       = "Poach eggs for 3 minutes, and keep warm" +
                    "Grill the bacon and mushrooms" + "Heat the beans" + "Toast the bread",
            imageUri    = null,
            category    = "Breakfast"
        ))

        dao.insert(Recipe(
            name        = "Chicken Curry",
            cookingTime = 50,
            ingredients = "6 chicken thighs" + " 400g tomatoes" + "100g greek yogurt" +
                    "50g ground almonds",
            steps       = "Chop the chicken in pieces and fry for 5 minutes and add it with the " +
                    "tomatoes and yogurt to a pan and cook for 30 minutes",
            imageUri    = null,
            category    = "Dinner"
        ))

        val dinnerOnly = dao.getRecipesByCategory("Dinner")
        assertEquals(1, dinnerOnly.size)
        assertEquals("Chicken Curry", dinnerOnly[0].name)
    }

    @Test
    fun getAllRecipes() = runBlocking {
        dao.insert(Recipe(
            name        = "Full English Breakfast",
            cookingTime = 20,
            ingredients = "4 Bacon rashers" + " 4 Mushrooms" + "2 Eggs" + "150g Beans" + "2 Slices bread and butter",
            steps       = "Poach eggs for 3 minutes, and keep warm" +
                    "Grill the bacon and mushrooms" + "Heat the beans" + "Toast the bread",
            imageUri    = null,
            category    = "Breakfast"
        ))

        dao.insert(Recipe(
            name        = "Chicken Curry",
            cookingTime = 50,
            ingredients = "6 chicken thighs" + " 400g tomatoes" + "100g greek yogurt" +
                    "50g ground almonds",
            steps       = "Chop the chicken in pieces and fry for 5 minutes and add it with the " +
                    "tomatoes and yogurt to a pan and cook for 30 minutes",
            imageUri    = null,
            category    = "Dinner"
        ))

        val all = dao.getAllRecipes()
        assertEquals(2, all.size)

        val names = all.map { it.name }
        assertTrue(names.containsAll(listOf("Full English Breakfast", "Chicken Curry")))
    }

    @Test
    fun getRecipesByIngredient_fullMatch() = runBlocking {
        dao.insert(Recipe(
            name        = "Full English Breakfast",
            cookingTime = 20,
            ingredients = "4 Bacon rashers" + " 4 Mushrooms" + "2 Eggs" + "150g Beans" + "2 Slices bread and butter",
            steps       = "Poach eggs for 3 minutes, and keep warm" +
                    "Grill the bacon and mushrooms" + "Heat the beans" + "Toast the bread",
            imageUri    = null,
            category    = "Breakfast"
        ))
        dao.insert(Recipe(
            name        = "Chicken Curry",
            cookingTime = 50,
            ingredients = "6 chicken thighs" + " 400g tomatoes" + "100g greek yogurt" +
                    "50g ground almonds",
            steps       = "Chop the chicken in pieces and fry for 5 minutes and add it with the " +
                    "tomatoes and yogurt to a pan and cook for 30 minutes",
            imageUri    = null,
            category    = "Dinner"
        ))

        val eggs = dao.getRecipesByIngredient("Eggs")
        assertEquals(1, eggs.size)
    }
}