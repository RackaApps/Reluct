package work.racka.reluct.android.screens.screentime.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import work.racka.reluct.compose.common.theme.Dimens

@Composable
internal fun AppNameEntry(
    appName: String,
    icon: Drawable,
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current,
    contentPadding: Dp = Dimens.SmallPadding.size,
    iconSize: Dp = 32.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MediumPadding.size),
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Image(
                modifier = Modifier.size(iconSize),
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(icon).build()
                ),
                contentDescription = appName
            )

            Text(
                modifier = Modifier.weight(1f),
                text = appName,
                style = textStyle,
                color = contentColor
            )

            actions()
        }
    }
}
