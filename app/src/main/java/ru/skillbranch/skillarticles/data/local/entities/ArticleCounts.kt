package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "article_counts")
data class ArticleCounts (
    @PrimaryKey
    @ColumnInfo(name = "article_id")
    val articleID: String,
    val likes: Int = 0,
    val comments: Int = 0,
    @ColumnInfo(name = "read_duration")
    val readDuration: Int = 0,
    @ColumnInfo(name = "update_at")
    val updateAt: Date = Date()
)