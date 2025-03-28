package com.demo.authentication.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.authentication.features.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(userPreferences: UserPreferences) : ViewModel() {

    /*
    👉 _isDataLoaded: A private mutable state flow that initially starts as false.
        isDataLoaded: An immutable StateFlow exposed to the UI.
        This tells the UI when the data is ready so that we can prevent flickering issues when deciding which screen to show.

    👉 userPreferences.getLoginState: A Flow<Boolean> that retrieves the login state from DataStore.
       stateIn(...) converts it into a StateFlow with:
       Scope: viewModelScope (ensures proper lifecycle management)
       SharingStarted.Eagerly: Starts collecting immediately when ViewModel is created.
       Initial Value: false (assumes user is not logged in until DataStore loads)

    👉 Launches a coroutine in viewModelScope to collect getLoginState (login state from DataStore).
       As soon as we receive the first value, _isDataLoaded is set to true, signaling that DataStore has
       completed its initial load.
       This ensures that we don’t switch screens before data is available, preventing flickering issues in UI.
     */
    private val _isDataLoaded = MutableStateFlow(false)  // Track when DataStore is loaded
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = userPreferences.getLoginState
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            userPreferences.getLoginState.first() // Waits for the first emitted value
            _isDataLoaded.value = true  // Only mark as loaded after first value is received
        }
    }
}