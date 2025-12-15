package com.example.campingscv

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CampingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg camping: Camping)

    @Delete
    fun deleteCamping(camping: Camping)

    @Query("SELECT * FROM tabla_favoritos")
    fun getAllCampings(): List<Camping>

    @Query("SELECT * FROM tabla_favoritos WHERE id_camping = :id")
    fun getCampingById(id: Int): Camping?

}