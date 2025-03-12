package com.demo.userauth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.userauth.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(userPreferences: UserPreferences) : ViewModel() {

    // Properly collect login state as a StateFlow
    val isLoggedIn: StateFlow<Boolean> = userPreferences.getLoginState.stateIn(viewModelScope, SharingStarted.Lazily, true)
}