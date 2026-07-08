package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.database.AdviceEntity
import com.example.data.database.CommentEntity
import com.example.data.database.AppDatabase
import com.example.data.repository.AdviceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CoachMessage(
    val sender: String, // "user" or "coach"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AdviceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AdviceRepository
    
    private val _dailyMotivation = MutableStateFlow<AdviceEntity?>(null)
    val dailyMotivation: StateFlow<AdviceEntity?> = _dailyMotivation.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AdviceRepository(database.adviceDao())
        
        // Load daily motivation once data is available
        viewModelScope.launch {
            repository.allAdvices
                .filter { it.isNotEmpty() }
                .firstOrNull()?.let { list ->
                    _dailyMotivation.value = list.randomOrNull()
                }
        }
    }

    fun refreshDailyMotivation() {
        viewModelScope.launch {
            repository.allAdvices
                .filter { it.isNotEmpty() }
                .firstOrNull()?.let { list ->
                    _dailyMotivation.value = list.randomOrNull()
                }
        }
    }

    val categories = listOf(
        "সকল উপদেশ",
        "মনোযোগ ও ফোকাস",
        "সময় ও রুটিন",
        "পরীক্ষার প্রস্তুতি",
        "ডিজিটাল আসক্তি",
        "ব্যর্থতা ও অনুপ্রেরণা",
        "স্বাস্থ্য ও মানসিকতা"
    )

    private val _selectedCategory = MutableStateFlow("সকল উপদেশ")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Reactive Advices Flow filtered by selected category
    val advices: StateFlow<List<AdviceEntity>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == "সকল উপদেশ") {
                repository.allAdvices
            } else {
                repository.getAdvicesByCategory(category)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val reactedAdvices: StateFlow<List<AdviceEntity>> = repository.reactedAdvices
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Comments Management
    private val _currentComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val currentComments: StateFlow<List<CommentEntity>> = _currentComments.asStateFlow()

    private var commentsJob: Job? = null
    private val _activeAdviceId = MutableStateFlow<Int?>(null)
    val activeAdviceId: StateFlow<Int?> = _activeAdviceId.asStateFlow()

    fun selectAdviceForComments(adviceId: Int?) {
        _activeAdviceId.value = adviceId
        if (adviceId != null) {
            commentsJob?.cancel()
            commentsJob = viewModelScope.launch {
                repository.getCommentsForAdvice(adviceId).collect {
                    _currentComments.value = it
                }
            }
        } else {
            commentsJob?.cancel()
            _currentComments.value = emptyList()
        }
    }

    // Advice actions
    fun toggleCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleLike(advice: AdviceEntity) {
        viewModelScope.launch {
            val updated = advice.copy(
                liked = !advice.liked,
                likesCount = if (advice.liked) advice.likesCount - 1 else advice.likesCount + 1
            )
            repository.updateAdvice(updated)
        }
    }

    fun toggleThumb(advice: AdviceEntity) {
        viewModelScope.launch {
            val updated = advice.copy(thumbed = !advice.thumbed)
            repository.updateAdvice(updated)
        }
    }

    fun toggleClap(advice: AdviceEntity) {
        viewModelScope.launch {
            val updated = advice.copy(clapped = !advice.clapped)
            repository.updateAdvice(updated)
        }
    }

    fun toggleMindblown(advice: AdviceEntity) {
        viewModelScope.launch {
            val updated = advice.copy(mindblown = !advice.mindblown)
            repository.updateAdvice(updated)
        }
    }

    fun addCustomAdvice(text: String, category: String) {
        viewModelScope.launch {
            val newAdvice = AdviceEntity(
                text = text,
                category = if (category == "সকল উপদেশ") "মনোযোগ ও ফোকাস" else category,
                author = "স্বয়ং (ছাত্র)",
                isCustom = true
            )
            repository.insertAdvice(newAdvice)
        }
    }

    fun deleteAdvice(advice: AdviceEntity) {
        viewModelScope.launch {
            repository.deleteAdvice(advice)
        }
    }

    fun addComment(adviceId: Int, userName: String, commentText: String) {
        if (commentText.isBlank()) return
        viewModelScope.launch {
            val newComment = CommentEntity(
                adviceId = adviceId,
                userName = userName.trim().ifBlank { "বেনামী শিক্ষার্থী" },
                commentText = commentText.trim()
            )
            repository.insertComment(newComment)
        }
    }

    fun deleteComment(comment: CommentEntity) {
        viewModelScope.launch {
            repository.deleteComment(comment)
        }
    }

    // AI Study Coach State
    private val _coachChatHistory = MutableStateFlow<List<CoachMessage>>(
        listOf(
            CoachMessage(
                sender = "coach",
                text = "স্বাগতম শিক্ষার্থী! আমি তোমার 'সাধনা স্টাডি কোচ'। পড়াশোনায় মনোযোগ বাড়ানো, রুটিন তৈরি, সামাজিক মাধ্যমের আসক্তি কমানো বা যেকোনো পড়াশোনা সংক্রান্ত সমস্যায় আমাকে জিজ্ঞেস করো। আমি তোমাকে প্রয়োজনীয় উপদেশ দেব।"
            )
        )
    )
    val coachChatHistory: StateFlow<List<CoachMessage>> = _coachChatHistory.asStateFlow()

    private val _isGeneratingCoach = MutableStateFlow(false)
    val isGeneratingCoach: StateFlow<Boolean> = _isGeneratingCoach.asStateFlow()

    fun sendCoachMessage(text: String) {
        val userQuery = text.trim()
        if (userQuery.isEmpty()) return

        val userMsg = CoachMessage(sender = "user", text = userQuery)
        _coachChatHistory.value = _coachChatHistory.value + userMsg

        _isGeneratingCoach.value = true
        viewModelScope.launch {
            val response = GeminiApiClient.generateStudyAdvice(userQuery)
            val coachMsg = CoachMessage(sender = "coach", text = response)
            _coachChatHistory.value = _coachChatHistory.value + coachMsg
            _isGeneratingCoach.value = false
        }
    }

    fun clearCoachChat() {
        _coachChatHistory.value = listOf(
            CoachMessage(
                sender = "coach",
                text = "আমি আবার নতুন করে শুরু করতে প্রস্তুত! তোমার যেকোনো পড়াশোনার সমস্যা বা মানসিক চাপ নিয়ে আমার সাথে কথা বলতে পারো।"
            )
        )
    }
}
