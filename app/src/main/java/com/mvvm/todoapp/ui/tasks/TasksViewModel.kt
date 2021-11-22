package com.mvvm.todoapp.ui.tasks

import androidx.lifecycle.*
import com.mvvm.todoapp.ADD_TASK_RESULT_OK
import com.mvvm.todoapp.EDIT_TASK_RESULT_OK
import com.mvvm.todoapp.data.PreferencesManager
import com.mvvm.todoapp.data.SortOrder
import com.mvvm.todoapp.data.Task
import com.mvvm.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {


    //val searchQuery = MutableStateFlow("")
    val searchQuery = state.getLiveData("searchQuery","")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ){ query,filterpreferences ->
        Pair(query,filterpreferences)
    }.flatMapLatest {(query, filterpreferences) ->
        taskDao.getTasks(query,
            filterpreferences.sortOrder,
            filterpreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder){
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun onHideCompletedClick(hideCompleted : Boolean){
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }



    fun onTaskCheckedChanged(task: Task,isChecked: Boolean){
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }
    }

    fun onTaskSwiped(task: Task){
        viewModelScope.launch {
            taskDao.delete(task)
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
        }
    }

    fun onUndoDeleteClick(task: Task){
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun onAddNewTaskClick(){
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
        }
    }

    fun onTaskSelected(task: Task){
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onAddEditResult(result: Int){
        when(result){
            ADD_TASK_RESULT_OK ->{
                showTaskSavedConfirmationMessage("Task added")
            }
            EDIT_TASK_RESULT_OK ->{
                showTaskSavedConfirmationMessage("Task updated")
            }
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String){
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
        }
    }


    sealed class TasksEvent{

        object NavigateToAddTaskScreen : TasksEvent()

        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()

        data class ShowTaskSavedConfirmationMessage(val message:String) :TasksEvent()
    }



}

