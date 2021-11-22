package com.mvvm.todoapp.ui.deleteallcompleted


import androidx.lifecycle.ViewModel
import com.mvvm.todoapp.data.TaskDao
import com.mvvm.todoapp.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) :ViewModel(){

    fun onConfirmClick(){
        applicationScope.launch {
            taskDao.deleteCompletedTasks()
        }
    }
}