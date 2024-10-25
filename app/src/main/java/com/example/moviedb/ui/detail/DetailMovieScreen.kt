package com.example.moviedb.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.moviedb.component.EmptyStateView
import com.example.moviedb.component.ErrorView
import com.example.moviedb.component.ShimmeringEffect
import com.example.moviedb.data.model.UiState
import com.example.moviedb.data.model.detail.DetailMovieRes
import com.example.moviedb.data.model.review.Review
import com.example.moviedb.data.model.video.VideosRes
import com.example.moviedb.network.ApiUtil
import com.example.moviedb.ui.home.paletteBackgroundColor
import com.example.moviedb.ui.home.paletteTextColor
import com.kmpalette.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Composable
fun DetailMovieScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: DetailMovieViewModel = hiltViewModel(),
    movieId: Long = 0L
) {
    val detailMovie by viewModel.detail.collectAsStateWithLifecycle()
    val video by viewModel.videoThumb.collectAsStateWithLifecycle()
    val reviewPaging = viewModel.reviews.collectAsLazyPagingItems()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadDetail(movieId)
        viewModel.loadThumbnail(movieId)
        viewModel.fetchReviewsMovie(movieId)
    }

    DetailMovieContent(detailState = detailMovie, videoState = video, reviewPaging = reviewPaging, onBack = {
        navHostController.popBackStack()
    })
}

@Composable
private fun DetailMovieContent(
    modifier: Modifier = Modifier,
    detailState: UiState<DetailMovieRes> = UiState.Idle,
    videoState: UiState<VideosRes> = UiState.Idle,
    reviewPaging: LazyPagingItems<Review>,
    onBack: () -> Unit = {}
) {
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBarDetail {
            onBack()
        }
    }) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (detailState) {
                is UiState.Success -> {
                    DetailHeader(
                        modifier = Modifier.fillMaxWidth(),
                        detailMovie = detailState.result
                    )
                }

                is UiState.Failed -> {

                }

                is UiState.Loading -> {}
            }
            Spacer(modifier = Modifier.height(8.dp))
            DetailMovieTrailer(modifier = Modifier.fillMaxWidth(), uiState = videoState)
            Spacer(modifier = Modifier.height(8.dp))
            when (detailState) {
                is UiState.Success -> {
                    DetailMovieSummary(
                        modifier = Modifier.fillMaxWidth(),
                        detailMovie = detailState.result
                    )
                }

                is UiState.Failed -> {

                }

                is UiState.Loading -> {}
            }
            ListReviews(modifier = Modifier.fillMaxWidth().height(500.dp), items = reviewPaging)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarDetail(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    TopAppBar(modifier = modifier, title = {
        Text(
            "Detail Movie",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp)
        )
    }, navigationIcon = {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "",
            modifier = Modifier.clickable { onBack() })
    }, colors = TopAppBarDefaults.topAppBarColors().copy(MaterialTheme.colorScheme.primary))
}

@Composable
private fun DetailHeader(
    modifier: Modifier = Modifier,
    detailMovie: DetailMovieRes = DetailMovieRes()
) {
    var palette by remember { mutableStateOf<Palette?>(null) }
    val textColor by palette.paletteTextColor()
    val backgroundColor by palette.paletteBackgroundColor()
    val urlImage = ApiUtil.getBackdropPath(detailMovie.backdropPath)
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        CoilImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
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
            },
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = detailMovie.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Release Date: ${detailMovie.releaseDate}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = backgroundColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${detailMovie.voteAverage / 2f}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DetailMovieSummary(
    modifier: Modifier = Modifier,
    detailMovie: DetailMovieRes = DetailMovieRes()
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            "Summary",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = detailMovie.overview,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailMovieTrailer(modifier: Modifier = Modifier, uiState: UiState<VideosRes>) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            "Trailers",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        when (uiState) {
            is UiState.Loading -> {}
            is UiState.Success -> {
                LazyRow(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = uiState.result.results, key = { it.key }) { video ->
                        ThumbnailVideo(video = video)
                    }
                }
            }

            is UiState.Failed -> {}
        }
    }
}

@Composable
private fun ThumbnailVideo(modifier: Modifier = Modifier, video: VideosRes.Video) {
    val context = LocalContext.current
    Box(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clip(RoundedCornerShape(8.dp))
        .clickable {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(ApiUtil.getYoutubeVideoPath(video.key)))
            context.startActivity(intent)
        }) {
        CoilImage(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(4f / 3f)
                .width(150.dp),
//                .height(100.dp),
            loading = {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    ShimmeringEffect()
                }
            },
            imageModel = { ApiUtil.getYoutubeThumbnailPath(video.key) },
            imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
            component = rememberImageComponent {
                +CrossfadePlugin()
                +ShimmerPlugin()
            },
        )
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Icon Play",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(30.dp, 20.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun ListReviews(modifier: Modifier = Modifier, items: LazyPagingItems<Review>) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(16.dp)
    ) {
        Text("Reviews", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        items.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    repeat(5) {
                        ShimmeringEffect(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(
                                    RoundedCornerShape(16.dp)
                                )
                        )
                    }
                }

                else -> {
                    LazyColumn {
                        if (items.itemCount > 0) {
                            items(
                                count = items.itemCount,
                                key = { index -> items[index]?.content ?: hashCode() }) { index ->
                                items[index]?.let { item ->
                                    ItemReview(review = item)
                                }
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    EmptyStateView(message = "No Review Found")
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
}

@Composable
private fun ItemReview(modifier: Modifier = Modifier, review: Review) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                review.author,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                review.content,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                textAlign = TextAlign.Justify,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun PreviewThumbnailVideo() {
    val temp = VideosRes.Video(
        key = "hAAskAiWOH4"
    )
    ThumbnailVideo(video = temp)
}

@Preview
@Composable
private fun PreviewDetailHeader() {
    val temp = DetailMovieRes(
        title = "Title",
        releaseDate = "2024-01-01",
        backdropPath = "/iGdHtZBjpBkmSAgkvUeniIfI8ME.jpg"
    )
    DetailHeader(detailMovie = temp)
}

@Preview(name = "DetailMovieScreen")
@Composable
private fun PreviewDetailMovieScreen() {
    DetailMovieScreen()
}