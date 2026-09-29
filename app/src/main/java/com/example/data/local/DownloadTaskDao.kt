package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DownloadTask
import com.example.data.model.RenewalLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadTaskDao {

    @Query("SELECT * FROM download_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<DownloadTask>>

    @Query("SELECT * FROM download_tasks WHERE id = :id")
    suspend fun getTaskById(id: String): DownloadTask?

    @Query("SELECT * FROM download_tasks WHERE executionState = :state ORDER BY createdAt DESC")
    fun getTasksByState(state: String): Flow<List<DownloadTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DownloadTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<DownloadTask>)

    @Update
    suspend fun updateTask(task: DownloadTask)

    @Delete
    suspend fun deleteTask(task: DownloadTask)

    @Query("DELETE FROM download_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("UPDATE download_tasks SET currentDownloadUrl = :newUrl, executionState = :newState, lastErrorCode = NULL, lastErrorMessage = NULL WHERE id = :id")
    suspend fun updateTaskUrlAndResume(id: String, newUrl: String, newState: String)

    @Query("UPDATE download_tasks SET executionState = :state, lastErrorCode = :errorCode, lastErrorMessage = :errorMessage WHERE id = :id")
    suspend fun updateTaskState(id: String, state: String, errorCode: Int?, errorMessage: String?)

    @Query("UPDATE download_tasks SET downloadedBytes = :downloaded, totalBytes = :total, downloadSpeedBps = :speed, etaSeconds = :eta WHERE id = :id")
    suspend fun updateProgress(id: String, downloaded: Long, total: Long, speed: Long, eta: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RenewalLogEntry)

    @Query("SELECT * FROM renewal_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<RenewalLogEntry>>

    @Query("SELECT * FROM renewal_logs WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getLogsForTask(taskId: String): Flow<List<RenewalLogEntry>>

    @Query("DELETE FROM renewal_logs")
    suspend fun clearLogs()
}
