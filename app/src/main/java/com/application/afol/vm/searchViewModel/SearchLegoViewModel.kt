package com.application.afol.vm.searchViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.afol.models.ApiResultSearch
import com.application.afol.models.LegoSet
import com.application.afol.network.Result
import com.application.afol.repositories.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchLegoViewModel(private val repository: Repository) : ViewModel() {
    private val job = Job()
    private val coroutineContext: CoroutineContext get() = job + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val getSearchSuccess = MutableLiveData<ApiResultSearch>()
    val getSearchError = MutableLiveData<String>()
    val getSearchException = MutableLiveData<java.lang.Exception>()

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }

    fun getLegoSetBySearch(searchQuery: String, page: Int, pageSize: Int) {
        scope.launch {
            when (val searchRespond = repository.getLegoBySearch(searchQuery, page, pageSize)) {
                is Result.Success -> getSearchSuccess.postValue(searchRespond.data)
                is Result.Error -> getSearchError.postValue(searchRespond.error)
                is Result.Exception -> getSearchException.postValue(searchRespond.exception)
            }
        }
    }

    suspend fun getListOfMySets(): LiveData<MutableList<LegoSet>> {
        return withContext(Dispatchers.IO) {
            return@withContext repository.getMySets()
        }
    }

    fun removeFromMySets(legoSet: LegoSet) = scope.launch {
        legoSet.isInFavorite = false
        repository.removeFromMySets(legoSet)
    }

    fun addToMySets(legoSet: LegoSet) = scope.launch {
        legoSet.isInFavorite = true
        repository.addToMySets(legoSet)
    }

    private fun cancelJob() = job.cancel()
}