package com.example.campingscv

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Camping::class], version = 1)
abstract class CampingDatabase : RoomDatabase() {
    abstract fun campingDao(): CampingDao
}

