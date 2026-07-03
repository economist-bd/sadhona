package com.example.data.repository

import com.example.data.database.AdviceDao
import com.example.data.database.AdviceEntity
import com.example.data.database.CommentEntity
import kotlinx.coroutines.flow.Flow

class AdviceRepository(private val adviceDao: AdviceDao) {
    val allAdvices: Flow<List<AdviceEntity>> = adviceDao.getAllAdvices()
    val reactedAdvices: Flow<List<AdviceEntity>> = adviceDao.getReactedAdvices()

    fun getAdvicesByCategory(category: String): Flow<List<AdviceEntity>> {
        return adviceDao.getAdvicesByCategory(category)
    }

    suspend fun getAdviceById(id: Int): AdviceEntity? {
        return adviceDao.getAdviceById(id)
    }

    suspend fun insertAdvice(advice: AdviceEntity): Long {
        return adviceDao.insertAdvice(advice)
    }

    suspend fun updateAdvice(advice: AdviceEntity) {
        adviceDao.updateAdvice(advice)
    }

    suspend fun deleteAdvice(advice: AdviceEntity) {
        adviceDao.deleteAdvice(advice)
    }

    suspend fun insertComment(comment: CommentEntity) {
        adviceDao.insertComment(comment)
    }

    fun getCommentsForAdvice(adviceId: Int): Flow<List<CommentEntity>> {
        return adviceDao.getCommentsForAdvice(adviceId)
    }

    suspend fun deleteComment(comment: CommentEntity) {
        adviceDao.deleteComment(comment)
    }
}
