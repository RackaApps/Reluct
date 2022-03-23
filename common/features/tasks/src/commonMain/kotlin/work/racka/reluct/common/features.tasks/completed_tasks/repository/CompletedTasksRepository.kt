package work.racka.reluct.common.features.tasks.completed_tasks.repository

import kotlinx.coroutines.flow.Flow
import work.racka.reluct.common.model.domain.tasks.Task

interface CompletedTasksRepository {
    suspend fun getTasks(): Flow<List<Task>>
    suspend fun toggleTaskDone(taskId: Long, isDone: Boolean)
}