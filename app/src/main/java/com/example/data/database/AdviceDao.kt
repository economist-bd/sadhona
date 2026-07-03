package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdviceDao {
    @Query("SELECT * FROM advices ORDER BY timestamp DESC")
    fun getAllAdvices(): Flow<List<AdviceEntity>>

    @Query("SELECT * FROM advices WHERE category = :category ORDER BY timestamp DESC")
    fun getAdvicesByCategory(category: String): Flow<List<AdviceEntity>>

    @Query("SELECT * FROM advices WHERE liked = 1 OR thumbed = 1 OR clapped = 1 OR mindblown = 1 ORDER BY timestamp DESC")
    fun getReactedAdvices(): Flow<List<AdviceEntity>>

    @Query("SELECT * FROM advices WHERE id = :id")
    suspend fun getAdviceById(id: Int): AdviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvice(advice: AdviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAdvices(advices: List<AdviceEntity>)

    @Update
    suspend fun updateAdvice(advice: AdviceEntity)

    @Delete
    suspend fun deleteAdvice(advice: AdviceEntity)

    // Comments queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE adviceId = :adviceId ORDER BY timestamp DESC")
    fun getCommentsForAdvice(adviceId: Int): Flow<List<CommentEntity>>

    @Delete
    suspend fun deleteComment(comment: CommentEntity)
}
