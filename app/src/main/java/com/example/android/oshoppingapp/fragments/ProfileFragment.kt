package com.example.android.oshoppingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.activities.SettingsActivity
import com.example.android.oshoppingapp.databinding.FragmentProfileBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.utils.GlideLoader

open class ProfileFragment : BaseFragment() ,View.OnClickListener{

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mUserDetails: com.example.android.oshoppingapp.models.User


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding= FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding.llMyProfile.setOnClickListener ( this )
        getUserDetails()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.ll_myProfile -> {
                    val intent = Intent(activity, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user:com.example.android.oshoppingapp.models.User){
        mUserDetails=user
        hideProgressDialog()

        GlideLoader(requireContext()).loadUserPicture(user.image,binding.ivUserPhoto)
        binding.tvName.text="${user.firstName} ${user.lastName}"
    }


}