package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.*
import java.util.*

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    @Embedded(prefix = "author_")
    val author: Author,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val poster: String,
    val date: Date,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)

data class Author (
    @ColumnInfo(name = "user_id")
    val userId: String,
    val avatar: String?,
    val name: String
)

@DatabaseView(
    """
        SELECT id, date, author_name AS author, author_avatar, title, description, poster,
        counts.likes AS like_count, counts.comments AS comment_count, counts.read_duration AS read_duration,
        "0" AS category_id, "0" AS category, "" AS category_icon, 0 AS is_bookmark
        FROM articles AS article
        INNER JOIN article_counts AS counts ON counts.article_id = id
    """
)
data class ArticleItem(
    val id: String,
    val date: Date = Date(),
    val author: String,
    @ColumnInfo(name = "author_avatar")
    val authorAvatar: String?,
    val title: String,
    val description: String,
    val poster: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String = "0",
    val category: String = "0",
    @ColumnInfo(name = "category_icon")
    val categoryIcon: String = "",
    @ColumnInfo(name = "like_count")
    val likeCount: Int = 0,
    @ColumnInfo(name = "comment_count")
    val commentCount: Int = 0,
    @ColumnInfo(name = "read_duration")
    val readDuration: Int = 0,
    @ColumnInfo(name = "is_bookmark")
    val isBookmark: Boolean = false
)