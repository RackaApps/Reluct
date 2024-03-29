package work.racka.reluct.compose.common.components.cards.appUsageEntry

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import work.racka.reluct.common.model.domain.usagestats.AppUsageInfo
import work.racka.reluct.compose.common.components.SharedRes
import work.racka.reluct.compose.common.components.images.painterResource
import work.racka.reluct.compose.common.components.resources.stringResource
import work.racka.reluct.compose.common.components.textfields.texts.EntryHeading
import work.racka.reluct.compose.common.theme.Dimens
import work.racka.reluct.compose.common.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUsageEntry(
    appUsageInfo: AppUsageInfo,
    onEntryClick: () -> Unit,
    onTimeSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    playAnimation: Boolean = false,
) {
    // Scale animation
    val animatedProgress = remember(appUsageInfo) {
        Animatable(initialValue = 0.8f)
    }
    if (playAnimation) {
        LaunchedEffect(key1 = appUsageInfo) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        }
    }

    val animatedModifier = if (playAnimation) {
        modifier
            .graphicsLayer(
                scaleX = animatedProgress.value,
                scaleY = animatedProgress.value
            )
    } else {
        modifier
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = { onEntryClick() },
        modifier = animatedModifier
            .fillMaxWidth()
            .clip(Shapes.large)
    ) {
        AppUsageEntryBase(
            modifier = Modifier
                .padding(Dimens.MediumPadding.size)
                .fillMaxWidth(),
            appUsageInfo = appUsageInfo,
            onTimeSettingsClick = onTimeSettingsClick,
            contentColor = contentColor
        )
    }
}

@Composable
fun AppUsageEntryBase(
    appUsageInfo: AppUsageInfo,
    onTimeSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement
            .spacedBy(Dimens.MediumPadding.size),
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(appUsageInfo.appIcon),
            contentDescription = appUsageInfo.appName
        )

        AppNameAndTimeText(
            modifier = Modifier.weight(1f),
            appName = appUsageInfo.appName,
            timeText = appUsageInfo.formattedTimeInForeground,
            color = contentColor
        )

        IconButton(onClick = onTimeSettingsClick) {
            Icon(
                imageVector = Icons.Rounded.HourglassBottom,
                contentDescription = stringResource(SharedRes.strings.time_limit_settings_icon),
                tint = contentColor
            )
        }
    }
}

/*@Composable
private fun AppUsageTextAndTimerButton(
    modifier: Modifier = Modifier,
    appName: String,
    timeText: String,
    onTimeSettingsClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

    }
}*/

@Composable
private fun AppNameAndTimeText(
    appName: String,
    timeText: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) {
    Column(
        modifier = modifier
    ) {
        EntryHeading(text = appName, color = color)
        Spacer(modifier = Modifier.width(Dimens.SmallPadding.size))
        TimeInfoText(text = timeText, color = color)
    }
}
