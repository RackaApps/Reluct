package work.racka.reluct.common.database.di

import org.koin.core.KoinApplication
import org.koin.dsl.module
import work.racka.reluct.common.database.dao.goals.GoalsDao
import work.racka.reluct.common.database.dao.goals.GoalsDaoImpl
import work.racka.reluct.common.database.dao.screentime.LimitsDao
import work.racka.reluct.common.database.dao.screentime.LimitsDaoImpl
import work.racka.reluct.common.database.dao.tasks.TasksDao
import work.racka.reluct.common.database.dao.tasks.TasksDaoImpl

object Database {

    fun KoinApplication.databaseModules() =
        this.apply {
            modules(
                commonModule(),
                Platform.platformDatabaseModule()
            )
        }

    private fun commonModule() = module {
        single<TasksDao> {
            TasksDaoImpl(
                dispatcher = CoroutineDispatchers.backgroundDispatcher,
                databaseWrapper = get()
            )
        }

        single<LimitsDao> {
            LimitsDaoImpl(
                dispatcher = CoroutineDispatchers.backgroundDispatcher,
                databaseWrapper = get()
            )
        }

        single<GoalsDao> {
            GoalsDaoImpl(
                dispatcher = CoroutineDispatchers.backgroundDispatcher,
                databaseWrapper = get()
            )
        }
    }
}
