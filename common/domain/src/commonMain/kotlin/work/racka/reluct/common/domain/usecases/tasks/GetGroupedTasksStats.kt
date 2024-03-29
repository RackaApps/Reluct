package work.racka.reluct.common.domain.usecases.tasks

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow
import work.racka.reluct.common.model.domain.tasks.DailyTasksStats
import work.racka.reluct.common.model.util.time.Week

interface GetGroupedTasksStats {
    fun dailyTasks(weekOffset: Int = 0, dayIsoNumber: Int): Flow<DailyTasksStats>
    fun weeklyTasks(weekOffset: Int = 0): Flow<ImmutableMap<Week, DailyTasksStats>>
    fun timeRangeTasks(timeRangeMillis: LongRange): Flow<DailyTasksStats>
}
