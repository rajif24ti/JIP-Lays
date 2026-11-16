package com.example.jip_lays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.jip_lays.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment() {

    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE) ?: ""
        val description = arguments?.getString(ARG_DESC) ?: ""
        val imageRes = arguments?.getInt(ARG_IMAGE) ?: R.drawable.ic_onboarding_1

        binding.tvTitle.text = title
        binding.tvDescription.text = description

        // MODUL 10 Requirement: Use Glide library to load image
        Glide.with(this)
            .load(imageRes)
            .into(binding.imgOnboarding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(title: String, description: String, imageRes: Int): OnboardingPageFragment {
            val fragment = OnboardingPageFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESC, description)
                putInt(ARG_IMAGE, imageRes)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
