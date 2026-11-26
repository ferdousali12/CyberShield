package com.example.cybershield.ui.quiz_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cybershield.data.model.QuizQuestion
import com.example.cybershield.data.repository.QuizRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val isAnswerSubmitted: Boolean = false,
    val isQuizFinished: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val percentageScore: Int = 0,
    val pointsEarned: Int = 0
)

class QuizViewModel(
    private val repository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        startQuiz()
    }

    fun startQuiz() {
        val allQuestions = repository.getQuestions()
        // Exactly 5 questions per attempt
        val selectedQuestions = allQuestions.shuffled().take(5)
        _uiState.update {
            QuizUiState(
                questions = selectedQuestions,
                currentIndex = 0,
                selectedAnswers = emptyMap(),
                isAnswerSubmitted = false,
                isQuizFinished = false
            )
        }
    }

    fun selectOption(optionIndex: Int) {
        if (_uiState.value.isAnswerSubmitted) return
        val currentQIndex = _uiState.value.currentIndex
        _uiState.update {
            it.copy(selectedAnswers = it.selectedAnswers + (currentQIndex to optionIndex))
        }
    }

    fun submitAnswer() {
        _uiState.update { it.copy(isAnswerSubmitted = true) }
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex < state.questions.size) {
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    isAnswerSubmitted = false
                )
            }
        } else {
            var correct = 0
            val selectedAnswersMap = mutableMapOf<String, Int>()
            val correctAnswersMap = mutableMapOf<String, Int>()
            val questionIds = mutableListOf<Int>()

            state.questions.forEachIndexed { index, question ->
                questionIds.add(question.id)
                correctAnswersMap[question.id.toString()] = question.correctAnswerIndex
                val selected = state.selectedAnswers[index]
                if (selected != null) {
                    selectedAnswersMap[question.id.toString()] = selected
                    if (selected == question.correctAnswerIndex) {
                        correct++
                    }
                }
            }

            val incorrect = state.questions.size - correct
            val percentage = (correct * 100) / state.questions.size // e.g. 4/5 = 80%
            val points = correct * 20 // 20 points per correct answer

            _uiState.update {
                it.copy(
                    isQuizFinished = true,
                    correctCount = correct,
                    incorrectCount = incorrect,
                    percentageScore = percentage,
                    pointsEarned = points
                )
            }

            // Save to Firestore
            viewModelScope.launch {
                try {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        val db = FirebaseFirestore.getInstance()
                        val resultId = db.collection("users").document(uid).collection("quizResults").document().id
                        val resultData = hashMapOf(
                            "resultId" to resultId,
                            "score" to correct,
                            "totalQuestions" to state.questions.size,
                            "percentage" to percentage,
                            "points" to points,
                            "questionIds" to questionIds,
                            "completedAt" to System.currentTimeMillis()
                        )
                        db.collection("users").document(uid).collection("quizResults").document(resultId).set(resultData).await()
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
