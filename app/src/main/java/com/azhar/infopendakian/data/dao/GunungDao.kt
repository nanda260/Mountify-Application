package com.azhar.infopendakian.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.azhar.infopendakian.data.entity.Gunung

@Dao
interface GunungDao {

    @Query("SELECT * FROM Gunung")
    fun getAll(): List<Gunung>

    @Query("SELECT * FROM Gunung WHERE lokasi = :lokasi")
    fun getByLokasi(lokasi: String): List<Gunung>

    @Query("SELECT * FROM Gunung WHERE uid IN (:gunungIds)")
    fun loadAllByIds(gunungIds: IntArray): List<Gunung>

    @Insert
    fun insertAll(vararg gunung: Gunung)

    @Delete
    fun delete(gunung: Gunung)
}
