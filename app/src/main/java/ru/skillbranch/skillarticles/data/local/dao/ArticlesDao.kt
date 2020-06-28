package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem

@Dao
interface ArticlesDao : BaseDao<Article> {

    fun upsert(list: List<Article>) {
        insert(list)
            .mapIndexed { index, recordResult -> if (recordResult == -1L) list[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }

    @Query("""
        SELECT * FROM articles
    """)
    fun findArticles(): List<Article>

    @Query("""
        SELECT * FROM articles
        WHERE id = :id
    """)
    fun findArticlesById(id: String): Article

    @Query("""
        SELECT * FROM articleitem
    """)
    fun findArticleItems(): List<ArticleItem>
}