package com.example.chattingapp.onBoarding.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.chattingapp.R


class AFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_a, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.findViewById<TextView>(R.id.nextBtn).setOnClickListener(){
            viewPager?.currentItem =1
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green1)
        }
        requireActivity().window.navigationBarColor = resources.getColor(R.color.green2)
        return view
    }
}