package com.azhar.infopendakian.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Gunung (
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "nama") var nama: String?,
    @ColumnInfo(name = "image") var imageGunung: String?,
    @ColumnInfo(name = "lokasi") var lokasi: String?,
    @ColumnInfo(name = "lat") var lat: Double?,
    @ColumnInfo(name = "lon") var lon: Double?,
    @ColumnInfo(name = "deskripsi") var deskripsi: String?,
    @ColumnInfo(name = "jalur") var jalurPendakian: String?,
    @ColumnInfo(name = "info") var infoGunung: String?
)