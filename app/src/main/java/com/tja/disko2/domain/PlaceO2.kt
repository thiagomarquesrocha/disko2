package com.tja.disko2.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@Entity(tableName = "place_items")
@IgnoreExtraProperties
data class PlaceO2(
    @ColumnInfo(name = "address") var address: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "phone") var phone: String,
    @ColumnInfo(name = "photo") var photo: String,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "favorite") var favorite: Int,
    @PrimaryKey(autoGenerate = true) var id: Int
) : Serializable {
    constructor() : this("", "", "", "", "", 0, 0, 0)
}