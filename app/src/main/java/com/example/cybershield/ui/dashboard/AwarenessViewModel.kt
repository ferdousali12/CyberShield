package com.example.cybershield.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.cybershield.data.model.AwarenessArticle
import com.example.cybershield.data.repository.AwarenessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AwarenessUiState(
    val articles: List<AwarenessArticle> = emptyList(),
    val selectedArticle: AwarenessArticle? = null
)

class AwarenessViewModel(
    private val repository: AwarenessRepository = AwarenessRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AwarenessUiState())
    val uiState: StateFlow<AwarenessUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
    }

    private fun loadArticles() {
        val list = repository.getArticles()
        _uiState.update { it.copy(articles = list) }
    }

    fun selectArticle(id: String) {
        val article = repository.getArticleById(id)
        _uiState.update { it.copy(selectedArticle = article) }
    }
}
