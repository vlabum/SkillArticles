package ru.skillbranch.skillarticles.viewmodels.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel

class ProfileViewModel(handle: SavedStateHandle) : BaseViewModel<ArticleState>(handle, ArticleState()) {
    // TODO: Implement the ViewModel
}
