package sam.projects.movisamapp.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): MultiSearchResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): MovieDetails

    @GET("tv/{id}")
    suspend fun getTvDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): TvDetails
}

data class MultiSearchResponse(
    val results: List<SearchResult>
)

data class SearchResult(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String,
    val poster_path: String?,
    val media_type: String
)

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String,
    val vote_average: Double
)

data class TvDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: String?,
    val first_air_date: String,
    val vote_average: Double
)