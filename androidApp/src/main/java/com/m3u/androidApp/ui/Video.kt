import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.m3u.material.components.Button


@Composable
fun Video() {
    val youtubeVideo = "<iframe style=\"overflow:hidden;height:100%;width:100%\" height=\"100%\" width=\"100%\" src=\"https://www.youtube.com/embed/IDHKlktqts8?si=W43MkGWWZF-GmGPX&autoplay=1\" title=\"Playlist kiritish\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally

    ) {

        var count by remember { mutableStateOf(0) }

        AndroidView(
            modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
            factory = { context ->
                WebView(context).apply {
                    // WebView sozlamalari
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    webViewClient = WebViewClient()
                    var myCount = count;

                    loadData(youtubeVideo, "text/html" , "UTF-8")
                }
            },
            update = { webView ->
                var myCount = count;
                webView.loadData(youtubeVideo, "text/html" , "UTF-8")
            }
        )

        Row {
             Text(text = "Активация учун кулланма:")
             Button(
                 onClick = {count++},
                 text = "Кайта Куриш"
             )
        }
    }
}
