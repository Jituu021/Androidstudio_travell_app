package com.example.travel.domain.usecase.groupexpense

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.GroupExpense
import com.example.travel.domain.repository.GroupExpenseRepository
import javax.inject.Inject

class AddGroupExpenseUseCase @Inject constructor(
    private val groupExpenseRepository: GroupExpenseRepository
) {
    suspend operator fun invoke(expense: GroupExpense): Resource<Boolean> {
        return groupExpenseRepository.addGroupExpense(expense)
    }
}
