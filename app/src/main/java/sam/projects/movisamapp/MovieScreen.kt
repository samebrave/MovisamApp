package sam.projects.movisamapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import sam.projects.movisamapp.data.MovieDetails
import sam.projects.movisamapp.data.SearchResult
import sam.projects.movisamapp.data.TvDetails
import sam.projects.movisamapp.viewmodel.MovieViewModel

@Composable
fun MovieDBApp(viewModel: MovieViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(viewModel) { id, mediaType ->
                viewModel.getDetails(id, mediaType)
                navController.navigate("details")
            }
        }
        composable("details") {
            DetailsScreen(viewModel) {
                navController.navigateUp()
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MovieViewModel, onItemClick: (Int, String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Film veya Dizi Ara") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            trailingIcon = {
                IconButton(onClick = { viewModel.searchMulti(searchQuery) }) {
                    Icon(Icons.Default.Search, contentDescription = "Ara")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.searchMulti(searchQuery) })
        )

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        if (viewModel.error != null) {
            Text(
                text = viewModel.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        if (viewModel.searchResults.isEmpty() && !viewModel.isLoading && viewModel.error == null) {
            EmptyState()
        } else {
            LazyColumn {
                items(viewModel.searchResults) { result ->
                    SearchResultItem(result) {
                        onItemClick(result.id, result.media_type)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w185${result.poster_path}",
                contentDescription = "${result.title ?: result.name} poster",
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = result.title ?: result.name ?: "Unknown",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DetailsScreen(viewModel: MovieViewModel, onBackClick: () -> Unit) {
    when (val details = viewModel.selectedItemDetails) {
        is MovieDetails -> MovieDetailsContent(details, onBackClick)
        is TvDetails -> TvDetailsContent(details, onBackClick)
        else -> Text("Detaylar yüklenemedi.")
    }
}

@Composable
fun MovieDetailsContent(movie: MovieDetails, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = "${movie.title} poster",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = movie.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Yayın Tarihi: ${movie.release_date}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Puan: ${movie.vote_average}")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = movie.overview)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackClick) {
            Text("Geri")
        }
    }
}

@Composable
fun TvDetailsContent(tv: TvDetails, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${tv.poster_path}",
            contentDescription = "${tv.name} poster",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = tv.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "İlk Yayın Tarihi: ${tv.first_air_date}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Puan: ${tv.vote_average}")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = tv.overview)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackClick) {
            Text("Geri")
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Arama",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Film veya dizi aramak için yukarıdaki arama çubuğunu kullanın",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}