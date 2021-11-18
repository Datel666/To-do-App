package com.mvvm.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mvvm.todoapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao


    /*
    This class is required to fill our database with some test data
    when its created.
    This method is actually the short way to provide a dependency in case if
    we are not using third party objects from libraries or it doesnt need
    additional configuration like calling methods on it.

    With a provider class we can get dependencies lazy
    This way we avoid a circular dependency where our taskdatabase needs the callback
    and the callback needs taskdatabase.

     */
    class CallBack @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationscope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationscope.launch {
                dao.insert(Task("Call mom"))
                dao.insert(Task("Walk the dog", important = true))
                dao.insert(Task("Call friend's mom"))

            }

        }
    }
}