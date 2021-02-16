package com.tja.disko2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tja.disko2.domain.PlaceO2

@Database(entities = [PlaceO2::class], version = 4)
abstract class PlaceO2Database : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {

        @Volatile
        private var INSTANCE: PlaceO2Database? = null

        fun getInstance(context: Context): PlaceO2Database {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(PlaceO2Database::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaceO2Database::class.java,
                    "place02_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }

        }


    }

}