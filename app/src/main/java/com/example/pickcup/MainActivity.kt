package com.example.pickcup

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pickcup.ui.theme.PickCupTheme
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PickCupTheme {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val item=getCup()
                    CupDetailScreen(item)
                }
            }
        }
    }
}
fun getCup():Cup{
    val name="시선 강남점"
    val imgUrl="https://picsum.photos/200/300"
    val videoUrl="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val res=Cup(name,imgUrl,videoUrl)
    return res
}
@Composable
fun CupDetailScreen(detail: Cup,modifier: Modifier= Modifier) {
    Column(modifier = Modifier.padding(16.dp)) {

        Text(text = detail.name, style = MaterialTheme.typography.titleLarge)

        // 1. 사진 표시 (있을 경우에만 표시)
        detail.imageUrl?.let { url ->
            Text(text = "술집 사진", style = MaterialTheme.typography.titleMedium)
            CupImage(imageUrl = detail.imageUrl, modifier = modifier)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 2. 영상 표시 (있을 경우에만 표시)
        detail.videoUrl?.let { url ->
            Text(text = "술집 영상", style = MaterialTheme.typography.titleMedium)
            CupVideoPlayer(videoUrl = url)
        }
    }
}
/**
 * 서버 URL로부터 사진을 로드하고 표시하는 Composable
 *
 * @param imageUrl 서버에서 받은 사진의 URL
 */
@Composable
fun CupImage(imageUrl: String,modifier: Modifier= Modifier) {

    Log.d("TAG", "CupImage: $imageUrl")
    // AsyncImage는 Coil을 사용하여 URL에서 이미지를 비동기로 로드하고 표시합니다.
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl) // 로드할 이미지 URL
            .crossfade(true) // 로딩 시 페이드 인 애니메이션 적용
            .listener(
                onError = { _, result ->
                    // 🚨 로딩 실패 시 출력되는 로그입니다.
                    Log.e("CoilDebug", "이미지 로딩 실패: ${result.throwable?.message}")
                },
                onSuccess = { _, _ ->
                    Log.d("CoilDebug", "이미지 로딩 성공!")
                }
            )
            .build(),
        contentDescription = "술집 사진",
        contentScale = ContentScale.Crop, // 이미지가 컨테이너를 채우도록 자릅니다.
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // 높이 지정
            .padding(vertical = 8.dp)
            .background(Color.Red)

    )
}
/**
 * 서버 URL로부터 영상을 로드하고 재생하는 Composable
 *
 * @param videoUrl 서버에서 받은 영상의 URL
 */
@Composable
fun CupVideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. ExoPlayer 인스턴스 생성 및 기억
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false // 초기 자동 재생 방지
        }
    }

    // 2. Lifecycle 이벤트 감지 및 Player 제어 (필수)
    // 화면을 벗어나거나 앱이 백그라운드로 갈 때 리소스 해제
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_STOP -> exoPlayer.release()
                Lifecycle.Event.ON_START -> exoPlayer.prepare()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // Composable이 화면에서 제거될 때 (DisposableEffect)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    // 3. AndroidView를 사용하여 PlayerView를 Compose에 통합
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true // 재생/정지, 탐색 컨트롤러 사용
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp) // 영상 플레이어 높이 지정
    )
}

