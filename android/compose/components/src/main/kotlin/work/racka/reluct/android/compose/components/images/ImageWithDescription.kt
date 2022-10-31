package work.racka.reluct.android.compose.components.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.compose.theme.Dimens

@Composable
fun ImageWithDescription(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    imageSize: Dp = 148.dp,
    description: String?,
    descriptionTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.MediumPadding.size)
    ) {
        Image(
            modifier = Modifier.size(imageSize),
            imageVector = imageVector,
            contentDescription = description
        )
        description?.let {
            Text(
                text = it,
                style = descriptionTextStyle,
                color = LocalContentColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ImageWithDescription(
    modifier: Modifier = Modifier,
    painter: Painter,
    imageSize: Dp = 148.dp,
    description: String?,
    descriptionTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.MediumPadding.size)
    ) {
        Image(
            modifier = Modifier.size(imageSize),
            painter = painter,
            contentDescription = description
        )
        description?.let {
            Text(
                text = it,
                style = descriptionTextStyle,
                color = LocalContentColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
