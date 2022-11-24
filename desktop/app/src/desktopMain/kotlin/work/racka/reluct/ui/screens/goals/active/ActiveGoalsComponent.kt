package work.racka.reluct.ui.screens.goals.active

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import work.racka.reluct.ui.screens.ComposeRenderer

class ActiveGoalsComponent(
    componentContext: ComponentContext,
    private val onShowDetails: (goalId: String) -> Unit,
    private val onAddGoal: (defaultGoalIndex: Int?) -> Unit,
    private val onExit: () -> Unit
) : ComponentContext by componentContext, ComposeRenderer {

    @Composable
    override fun Render(modifier: Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Active Goals")
        }
    }
}