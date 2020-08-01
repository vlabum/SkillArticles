package ru.skillbranch.skillarticles.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileViewModel

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = ""
        )
    }

    override val layout: Int = R.layout.fragment_profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun setupViews() {
    }


}
