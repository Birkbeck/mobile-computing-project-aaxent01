package co.uk.bbk.andreeaaxente.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.uk.bbk.andreeaaxente.model.Recipe

//this is the Room database class that provides an database instance and its data access object
@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    //gives access to RecipeDao so that database operations can be performed
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        //retrieves a AppDatabase instance and if needed it will create it, also
        // avoids activity contexts leaking by using application context
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipes_db"
                ).build().also { instance = it }
            }
    }
}