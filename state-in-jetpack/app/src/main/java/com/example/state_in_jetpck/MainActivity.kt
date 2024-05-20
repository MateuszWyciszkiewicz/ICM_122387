package com.example.state_in_jetpck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.state_in_jetpck.ui.theme.StateinjetpckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateinjetpckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MoviesListScreen()
                }
            }
        }
    }
}

@Composable
fun MoviesListScreen(
    modifier: Modifier = Modifier,
    movieListViewModel: MovieListViewModel = viewModel()
) {
    var movieName by remember { mutableStateOf("new movie") }
    Column(modifier = modifier) {
        TextField(
            value = movieName,
            onValueChange = { newValue ->
                movieName = newValue
            },
            label = { Text("Enter Movie Name") },
            modifier = Modifier.padding(top = 8.dp).align(alignment = Alignment.CenterHorizontally)
        )

        Button(onClick = {movieListViewModel.addMovie(movieName)}, Modifier.padding(top = 8.dp).align(alignment = Alignment.CenterHorizontally)) {
            Text("Add Movie")
        }
        MovieList(
            list = movieListViewModel.movies,
            onCheckedTask = { task, checked ->
                movieListViewModel.changeTaskChecked(task, checked)
            },
            onCloseTask = { task ->
                movieListViewModel.remove(task)
            }
        )
    }
}

class Movie(
    val id: Int,
    val label: String,
    initialChecked: Boolean = false
) {
    var checked: Boolean by mutableStateOf(initialChecked)
}

@Composable
fun MovieItem(
    movieName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = movieName
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}


class MovieListViewModel : ViewModel() {
    private val _movies = getMoviesList().toMutableStateList()
    val movies: List<Movie>
        get() = _movies

    fun remove(item: Movie) {
        _movies.remove(item)
    }

    fun changeTaskChecked(item: Movie, checked: Boolean) =
        movies.find { it.id == item.id }?.let { task ->
            task.checked = checked
        }
    fun addMovie(label: String) {
        val id = _movies.size +1
        _movies.add(Movie(id, label))
    }
    private fun getMoviesList() = List(5) { i -> Movie(i, "Movie #$i") }
}



@Composable
fun MovieList(
    list: List<Movie>,
    onCheckedTask: (Movie, Boolean) -> Unit,
    onCloseTask: (Movie) -> Unit,

    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = list,
            key = { movie -> movie.id }
        ) { movie ->
            MovieItem(
                movieName = movie.label,
                checked = movie.checked,
                onCheckedChange = { checked -> onCheckedTask(movie, checked) },
                onClose = { onCloseTask(movie) }
            )
        }
    }
}