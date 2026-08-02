package com.example.travel.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.GroupTrip
import com.example.travel.domain.model.TripMember
import com.example.travel.domain.repository.GroupTripRepository
import com.example.travel.domain.usecase.group.CreateGroupTripUseCase
import com.example.travel.domain.usecase.group.InviteMemberUseCase
import com.example.travel.domain.usecase.group.SyncTripChangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupTripUiState(
    val groupTrips: List<GroupTrip> = emptyList(),
    val activeMembers: List<TripMember> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class GroupTripViewModel @Inject constructor(
    private val groupTripRepository: GroupTripRepository,
    private val createGroupTripUseCase: CreateGroupTripUseCase,
    private val inviteMemberUseCase: InviteMemberUseCase,
    private val syncTripChangesUseCase: SyncTripChangesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupTripUiState())
    val uiState: StateFlow<GroupTripUiState> = _uiState.asStateFlow()

    init {
        loadGroupTrips()
    }

    private fun loadGroupTrips() {
        viewModelScope.launch {
            groupTripRepository.getAllGroupTrips().collect { list ->
                _uiState.value = _uiState.value.copy(groupTrips = list)
                if (list.isNotEmpty()) {
                    loadMembersForTrip(list.first().id)
                }
            }
        }
    }

    fun createTrip(title: String, destination: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = createGroupTripUseCase(title, destination, "owner_me")) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    loadGroupTrips()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun inviteMember(tripId: String, name: String, role: String) {
        viewModelScope.launch {
            inviteMemberUseCase(tripId, name, role)
            loadMembersForTrip(tripId)
        }
    }

    private fun loadMembersForTrip(tripId: String) {
        viewModelScope.launch {
            groupTripRepository.getMembersForTrip(tripId).collect { members ->
                _uiState.value = _uiState.value.copy(activeMembers = members)
            }
        }
    }
}
