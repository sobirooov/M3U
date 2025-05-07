import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun Video() {
    val youtubeVideo = "<iframe style=\"overflow:hidden;height:100%;width:100%\" height=\"100%\" width=\"100%\" src=\"https://www.youtube.com/embed/IDHKlktqts8?si=W43MkGWWZF-GmGPX\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally

    ) {

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
                    // JavaScript ni yoqish (YouTube pleeri uchun zarur)
                    settings.javaScriptEnabled = true
                    // WebView ichidagi navigatsiyani boshqarish
                    webViewClient = WebViewClient()

                    // Video linkini yuklash
                    loadData(youtubeVideo, "text/html" , "UTF-8")
                }
            },
            update = { webView ->
                // Agar videoEmbedUrl o'zgarsa, WebView ni yangilash
                // Bu recompositionda URL o'zgarganda chaqiriladi
                webView.loadData(youtubeVideo, "text/html" , "UTF-8")
            }
        )

        Text(text = "Активация учун кулланма:")
    }
}
