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
class RoomDbTest {
    private val dbName = "room database test"
    private lateinit var dao: RecipeDao
    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ctx.deleteDatabase(dbName)
    }

    @After
    fun tearDown() {
        ctx.deleteDatabase(dbName)
    }

    @Test
    fun persistenceAcrossRestart() = runBlocking {
        //create the two needed recipes for the test
        val db1 = Room.databaseBuilder(ctx, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .build()
        dao = db1.recipeDao()

        val breakfast = Recipe(
            name        = "Full English Breakfast",
            cookingTime = 20,
            ingredients = "4 Bacon rashers, 4 Mushrooms, 2 Eggs, 150g Beans, 2 Slices bread and butter",
            steps       = "Poach eggs, grill bacon & mushrooms, heat beans, toast bread",
            imageUri    = null,
            category    = "Breakfast"
        )
        val dinner = Recipe(
            name        = "Chicken Curry",
            cookingTime = 50,
            ingredients = "6 chicken thighs, 400g tomatoes, 100g yogurt, 50g ground almonds",
            steps       = "Fry chicken, add tomatoes & yogurt, simmer 30min",
            imageUri    = null,
            category    = "Dinner"
        )

        dao.insert(breakfast)
        dao.insert(dinner)
        db1.close()

        //this will reopen the app and check for the recipes
        val db2 = Room.databaseBuilder(ctx, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .build()
        val dao2 = db2.recipeDao()

        val all = dao2.getAllRecipes()
        assertEquals(2, all.size)
        val names = all.map { it.name }
        assertTrue(names.containsAll(listOf("Full English Breakfast", "Chicken Curry")))

        db2.close()
    }
}