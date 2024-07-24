package com.example.chattingapp.onBoarding.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chattingapp.LoginActivity
import com.example.chattingapp.R


class CFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_c, container, false)

        view.findViewById<TextView>(R.id.btnFinish).setOnClickListener(){
            val i = Intent(context, LoginActivity::class.java)
            startActivity(i)
            requireActivity().finish()
            onBoardFinished()


        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green1)
        }
        requireActivity().window.navigationBarColor = resources.getColor(R.color.green2)
        return view
    }

    private fun onBoardFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}