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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import work.racka.reluct.android.compose.theme.Dimens

@Composable
internal fun AppNameEntry(
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current,
    contentPadding: Dp = Dimens.SmallPadding.size,
    appName: String,
    icon: Drawable,
    iconSize: Dp = 32.dp,
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
                /*painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(icon).build()
            ),*/
                painter = rememberImagePainter(data = icon),
                contentDescription = appName
            )

            Text(
                modifier = Modifier.weight(1f),
                text = appName,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )

            actions()
        }
    }
}