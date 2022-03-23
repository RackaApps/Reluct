package work.racka.reluct.common.features.tasks.viewmodels

import kotlinx.coroutines.CoroutineScope
import work.racka.reluct.common.features.tasks.completed_tasks.container.CompletedTasksContainerHost
import work.racka.reluct.common.features.tasks.completed_tasks.container.CompletedTasksContainerHostImpl
import work.racka.reluct.common.features.tasks.completed_tasks.repository.CompletedTasksRepository

actual class CompletedTasksViewModel(
    completedTasks: CompletedTasksRepository,
    scope: CoroutineScope
) {
    val host: CompletedTasksContainerHost by lazy {
        CompletedTasksContainerHostImpl(
            completedTasks = completedTasks,
            scope = scope
        )
    }
}