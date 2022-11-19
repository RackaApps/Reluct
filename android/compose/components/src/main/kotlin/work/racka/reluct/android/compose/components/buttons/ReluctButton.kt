package work.racka.reluct.android.compose.components.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import work.racka.reluct.compose.common.theme.Dimens

@Composable
fun ReluctButton(
    buttonText: String,
    icon: ImageVector?,
    onButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    buttonTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = CircleShape,
    enabled: Boolean = true,
) {
    Button(
        onClick = onButtonClicked,
        shape = shape,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor
        ),
        modifier = modifier,
        enabled = enabled
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocalContentColor.current
            )
            Spacer(
                modifier = Modifier
                    .width(Dimens.SmallPadding.size),
            )
        }
        Text(
            text = buttonText,
            modifier = Modifier
                .padding(
                    vertical = Dimens.SmallPadding.size
                ),
            color = LocalContentColor.current,
            style = buttonTextStyle,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
