package com.magic.loader.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.magic.loader.data.model.Game
import com.magic.loader.data.repository.GameRepository
import com.magic.loader.data.repository.GameWithStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val games: List<GameWithStatus> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val gameRepository: GameRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val games = gameRepository.getGamesWithStatus(getApplication())
                _uiState.value = HomeUiState(games = games, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load games"
                )
            }
        }
    }

    fun getGameByName(name: String): Game? {
        return _uiState.value.games.find { it.game.name == name }?.game
    }

    fun refresh() {
        loadGames()
    }
}
