package com.mvvm.todoapp.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.mvvm.todoapp.R
import com.mvvm.todoapp.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task){

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            edittextTaskName.setText(viewModel.taskName)
            checkboxImportant.isChecked = viewModel.taskImportance
            // to check checkbox without ugly animation while transitioning to addedit fragment
            checkboxImportant.jumpDrawablesToCurrentState()
            textviewDateCreated.isVisible = viewModel.task != null
            textviewDateCreated.text = "Created: ${viewModel.task?.createdDateFormatted}"
        }
    }
}