package com.mvvm.todoapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mvvm.todoapp.data.PreferencesManager
import com.mvvm.todoapp.data.SortOrder
import com.mvvm.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    //val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    //val hideCompleted = MutableStateFlow(false)

    /*
    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ){ query,sortOrder, hideCompleted ->
        Triple(query,sortOrder,hideCompleted)
    }.flatMapLatest {
        taskDao.getTasks(it.first,it.second,it.third)
    }

     */
    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow
    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ){ query,filterpreferences ->
        Pair(query,filterpreferences)
    }.flatMapLatest {(query, filterpreferences) ->
        taskDao.getTasks(query,
            filterpreferences.sortOrder,
            filterpreferences.hideCompleted)
    }

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

    val tasks = tasksFlow.asLiveData()
}

