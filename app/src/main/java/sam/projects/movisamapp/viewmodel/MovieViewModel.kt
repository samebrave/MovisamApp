package sam.projects.movisamapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sam.projects.movisamapp.data.*
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    var searchResults by mutableStateOf<List<SearchResult>>(emptyList())
        private set

    var selectedItemDetails by mutableStateOf<Any?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun searchMulti(query: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = RetrofitInstance.api.searchMulti(API_KEY, query)
                searchResults = response.results
            } catch (e: Exception) {
                error = "Arama sırasında bir hata oluştu: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun getDetails(id: Int, mediaType: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                selectedItemDetails = when (mediaType) {
                    "movie" -> RetrofitInstance.api.getMovieDetails(id, API_KEY)
                    "tv" -> RetrofitInstance.api.getTvDetails(id, API_KEY)
                    else -> throw IllegalArgumentException("Geçersiz media type: $mediaType")
                }
            } catch (e: Exception) {
                error = "Detaylar alınırken bir hata oluştu: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        private const val API_KEY = "00adaaea20c9f90e32bdfe907a8927a4" // TMDb API anahtarınızı buraya ekleyin
    }
}