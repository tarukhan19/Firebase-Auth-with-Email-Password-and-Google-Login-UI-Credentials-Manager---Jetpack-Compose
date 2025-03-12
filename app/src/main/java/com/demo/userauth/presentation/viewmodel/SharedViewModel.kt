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

    /*
    👉 This code transforms a Flow<Boolean> (from userPreferences) into a StateFlow<Boolean>,
    ensuring the UI always has access to the latest login state.
    👉 It optimizes resource usage with SharingStarted.Lazily by collecting only when needed,
    reducing unnecessary computations.
    👉 Using viewModelScope, it ensures safe lifecycle management within the ViewModel.
     */
    val isLoggedIn: StateFlow<Boolean> =
        userPreferences.getLoginState.stateIn(viewModelScope, SharingStarted.Lazily, false)
}