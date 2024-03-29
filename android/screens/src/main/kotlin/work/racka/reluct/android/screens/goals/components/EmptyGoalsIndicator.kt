package work.racka.reluct.android.screens.goals.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.screens.R
import work.racka.reluct.compose.common.components.SharedRes
import work.racka.reluct.compose.common.components.images.LottieAnimationWithDescription

@Composable
internal fun EmptyGoalsIndicator(
    showAnimationProvider: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    val showAnimation = remember { derivedStateOf(showAnimationProvider) }
    if (showAnimation.value) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimationWithDescription(
                lottieResource = SharedRes.files.no_task_animation,
                imageSize = 200.dp,
                description = stringResource(R.string.no_goals_text),
                descriptionTextStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
