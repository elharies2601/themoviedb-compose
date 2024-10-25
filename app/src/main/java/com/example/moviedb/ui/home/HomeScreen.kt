package com.example.moviedb.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.moviedb.component.EmptyStateView
import com.example.moviedb.component.ErrorView
import com.example.moviedb.component.ShimmeringEffect
import com.example.moviedb.data.model.UiState
import com.example.moviedb.data.model.genre.GenreResponse
import com.example.moviedb.data.model.movie.MovieRes
import com.example.moviedb.navigation.MovieRoute
import com.example.moviedb.network.ApiUtil
import com.kmpalette.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val genreState by viewModel.genres.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenres.collectAsStateWithLifecycle()
    val movies = viewModel.moviesPaging.collectAsLazyPagingItems()

    LaunchedEffect(selectedGenre) {
        val genreMapping = selectedGenre.map {
            "${it.id}"
        }
        viewModel.fetchMoviesByGenre(
            genreMapping.joinToString(",")
        )
    }

    HomeContent(
        genreState = genreState,
        moviePaging = movies,
        selectedGenre = selectedGenre,
        onSelectFilter = { genre ->
            viewModel.toggleGenre(genre)
        },
        onClick = { movieId ->
            navHostController.navigate(MovieRoute.MovieDetail.createRoute(movieId))
        }
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    genreState: UiState<MutableList<GenreResponse.Genre>>,
    moviePaging: LazyPagingItems<MovieRes>,
    selectedGenre: Set<GenreResponse.Genre>,
    onSelectFilter: (GenreResponse.Genre) -> Unit = {},
    onClick: (Long) -> Unit = {}
) {
    Scaffold(topBar = { TopAppBarMovie(modifier) }) {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            ListMovie(modifier = Modifier.align(Alignment.Center), items = moviePaging) { movieId ->
                onClick(movieId)
            }
            GenreMovieFilter(
                modifier = Modifier.align(Alignment.TopStart),
                uiState = genreState,
                selectedGenre = selectedGenre,
                onSelectFilter = onSelectFilter
            )
        }
    }
}

@Composable
private fun GenreMovieFilter(
    modifier: Modifier = Modifier,
    uiState: UiState<MutableList<GenreResponse.Genre>>,
    selectedGenre: Set<GenreResponse.Genre> = emptySet(),
    onSelectFilter: (GenreResponse.Genre) -> Unit = {}
) {
    when (uiState) {
        is UiState.Loading -> {
            LoadingGenre(modifier.fillMaxWidth())
        }

        is UiState.Success -> {
            LazyRow(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.result) { genre ->
                    val isSelected = selectedGenre.contains(genre)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectFilter(genre) },
                        label = { Text(genre.name) })
                }
            }
        }

        is UiState.Failed -> {
            Text(
                text = "Error: ${uiState.message}",
                color = MaterialTheme.colorScheme.error,
                modifier = modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ListMovie(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<MovieRes>,
    onClick: (Long) -> Unit = {}
) {
    items.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                LoadingMovie(modifier = modifier.padding(top = 72.dp))
            }

            else -> {
                val gridColumn = if (items.itemCount > 0) 2 else 1
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumn),
                    modifier = modifier.padding(top = 72.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (items.itemCount > 0) {
                        items(
                            count = items.itemCount,
                            key = { index -> items[index]?.id ?: index }) { index ->
                            items[index]?.let { item ->
                                ItemMovie(movieRes = item, onClick = { onClick(item.id) })
                            }
                        }
                    } else {
                        item {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                EmptyStateView(message = "No Movie Found")
                            }
                        }
                    }

                    when {
                        loadState.append is LoadState.Loading -> {
                            item {
                                ShimmeringEffect(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .height(290.dp)
                                )
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            val error = (loadState.append as LoadState.Error).error
                            item {
                                ErrorView(message = error.message ?: "Unknown Error") {
                                    items.retry()
                                }
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            item {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)) {
                                    Button(onClick = { items.retry() }) {
                                        Text(text = "Retry")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemMovie(modifier: Modifier = Modifier, movieRes: MovieRes, onClick: () -> Unit = {}) {
    var palette by remember { mutableStateOf<Palette?>(null) }
    val backgroundColor by palette.paletteBackgroundColor()
    val textColor by palette.paletteTextColor()
    val urlImage = ApiUtil.getPosterPath(movieRes.posterPath)

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() },
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth()
//                    .height(240.dp)
                    .aspectRatio(3f/4f),
                imageModel = { urlImage },
                imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
                component = rememberImageComponent {
                    +CrossfadePlugin()
                    +ShimmerPlugin(
                        Shimmer.Resonate(
                            baseColor = Color.Transparent,
                            highlightColor = Color.LightGray,
                        ),
                    )

                    if (!LocalInspectionMode.current) {
                        +PalettePlugin(
                            imageModel = urlImage,
                            useCache = true,
                            paletteLoadedListener = { palette = it },
                        )
                    }
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .height(50.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = movieRes.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
private fun LoadingMovie(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(count = 10) {
            ShimmeringEffect(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

@Composable
private fun LoadingGenre(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = 6) {
            ShimmeringEffect(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .width(100.dp)
                    .height(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarMovie(modifier: Modifier = Modifier) {
    TopAppBar(title = {
        Text(
            text = "MovieDB",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }, colors = TopAppBarDefaults.topAppBarColors().copy(MaterialTheme.colorScheme.primary))
}

@Preview(name = "HomeScreen")
@Composable
private fun PreviewHomeScreen() {
    val moviePaging = flowOf(
        PagingData.from(data = listOf<MovieRes>(), sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false)
        ))
    )
    HomeContent(genreState = UiState.Loading, moviePaging = moviePaging.collectAsLazyPagingItems(), selectedGenre = emptySet())
}