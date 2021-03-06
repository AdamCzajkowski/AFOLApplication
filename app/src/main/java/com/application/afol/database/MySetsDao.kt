package com.application.afol.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.application.afol.models.LegoSet

@Dao
interface MySetsDao {
    @Query("SELECT * FROM LegoSet")
    fun getAll(): LiveData<MutableList<LegoSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(legoSet: LegoSet)

    @Delete
    fun delete(legoSet: LegoSet)
}