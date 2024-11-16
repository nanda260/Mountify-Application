package com.azhar.infopendakian.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.azhar.infopendakian.data.dao.GunungDao
import com.azhar.infopendakian.data.entity.Gunung

@Database(entities = [Gunung::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gunungDao():GunungDao

    companion object{
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase{
            if (instance==null){
                instance = Room.databaseBuilder(context, AppDatabase::class.java, name = "app-database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}