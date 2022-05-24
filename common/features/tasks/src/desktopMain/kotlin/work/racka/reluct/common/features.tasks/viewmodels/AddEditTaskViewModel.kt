package work.racka.reluct.common.features.tasks.viewmodels

import kotlinx.coroutines.CoroutineScope
import work.racka.reluct.common.data.usecases.tasks.ModifyTaskUseCase
import work.racka.reluct.common.features.tasks.add_edit_task.AddEditTask
import work.racka.reluct.common.features.tasks.add_edit_task.AddEditTaskImpl

actual class AddEditTaskViewModel internal constructor(
    modifyTasksUseCase: ModifyTaskUseCase,
    taskId: String?,
    scope: CoroutineScope,
) {
    actual val host: AddEditTask by lazy {
        AddEditTaskImpl(
            modifyTaskUseCase = modifyTasksUseCase,
            taskId = taskId,
            scope = scope
        )
    }
}