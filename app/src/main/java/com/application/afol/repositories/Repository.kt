package com.application.afol.repositories

import com.application.afol.database.MySetsDatabase
import com.application.afol.extension.safeApi
import com.application.afol.models.LegoSet
import com.application.afol.network.LegoApiService

class Repository(
    private val legoApiService: LegoApiService,
    private val mySetsDatabase: MySetsDatabase
) {
    suspend fun getLegoBySearch(searchQuery: String, pageSize: Int, page: Int) =
        safeApi { legoApiService.getLegoBySearchAsync(searchQuery, page, pageSize).await() }

    fun getMySets() = mySetsDatabase.mySetsDao().getAll()

    fun addToMySets(legoSet: LegoSet) = mySetsDatabase.mySetsDao().insert(legoSet)

    fun removeFromMySets(legoSet: LegoSet) = mySetsDatabase.mySetsDao().delete(legoSet)

    suspend fun getBricksFromSet(page: Int, setNumber: String, pageSize: Int) =
        safeApi { legoApiService.getBricksAsync(setNumber, page, pageSize).await() }

    suspend fun getLegoAlternatives(setNumber: String) =
        safeApi { legoApiService.getMOCsAsync(setNumber).await() }

    suspend fun getLegoFromTheme(page: Int, pageSize: Int, themeId: Int) =
        safeApi { legoApiService.getLegoByThemeAsync(page, pageSize, themeId).await() }

    suspend fun getPartBySearch(page: Int, pageSize: Int, searchQuery: String) =
        safeApi { legoApiService.getPartBySearchAsync(page, pageSize, searchQuery).await() }
}