package ru.skillbranch.gameofthrones.ui.slpash

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.android.synthetic.main.fragment_splash.*

import ru.skillbranch.gameofthrones.R

class SplashFragment : Fragment() {
    private lateinit var draw : Drawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       draw = image_animated_dragon.drawable
        if(draw is AnimatedVectorDrawableCompat)
            (draw as AnimatedVectorDrawableCompat).start()
        else if(draw is AnimatedVectorDrawable)
            (draw as AnimatedVectorDrawable).start()
    }

    override fun onPause() {
       super.onPause()
        (draw as AnimatedVectorDrawable).stop()
    }
}
