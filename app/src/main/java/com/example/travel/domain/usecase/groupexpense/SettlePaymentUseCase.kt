package com.example.travel.domain.usecase.groupexpense

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GroupExpenseRepository
import javax.inject.Inject

class SettlePaymentUseCase @Inject constructor(
    private val groupExpenseRepository: GroupExpenseRepository
) {
    suspend operator fun invoke(settlementId: String, isSettled: Boolean): Resource<Boolean> {
        return groupExpenseRepository.settlePayment(settlementId, isSettled)
    }
}
