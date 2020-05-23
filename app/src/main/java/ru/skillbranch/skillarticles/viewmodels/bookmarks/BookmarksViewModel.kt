package ru.skillbranch.skillarticles.viewmodels.bookmarks

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class BookmarksViewModel(handle: SavedStateHandle) : ArticlesViewModel(handle) {

    override val listData = Transformations.switchMap(state) {
        when {
            it.isSearch && !it.searchQuery.isNullOrBlank() -> buildPagedList(
                repository.searchArticlesBookmark(
                    it.searchQuery
                )
            )
            else -> buildPagedList(repository.allArticlesBookmark())
        }
    }

}
