package com.dormitorymanager

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.time.LocalDate

class SplashActivity : AppCompatActivity() {

    private lateinit var clRoot: ConstraintLayout
    private lateinit var ivLogo: ImageView
    private lateinit var llLogoContainer: LinearLayout
    private lateinit var llTextContainer: LinearLayout
    private lateinit var tvDormName: TextView
    private lateinit var tvDutyLabel: TextView
    private lateinit var vTopDivider: View
    private lateinit var vBottomDivider: View
    private lateinit var llDutyInfo: LinearLayout
    private lateinit var tvDutyPreviewName: TextView
    private lateinit var vLeftLine: View
    private lateinit var vRightLine: View
    private lateinit var prefs: PreferencesHelper
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        prefs = PreferencesHelper(this)
        db = DatabaseHelper(this)
        
        if (!prefs.splashAnimation) {
            navigateToMainDirectly()
            return
        }
        
        initViews()
        setInitialData()
        startAnimations()
    }

    private fun initViews() {
        clRoot = findViewById(R.id.clRoot)
        ivLogo = findViewById(R.id.ivLogo)
        llLogoContainer = findViewById(R.id.llLogoContainer)
        llTextContainer = findViewById(R.id.llTextContainer)
        tvDormName = findViewById(R.id.tvDormName)
        tvDutyLabel = findViewById(R.id.tvDutyLabel)
        vTopDivider = findViewById(R.id.vTopDivider)
        vBottomDivider = findViewById(R.id.vBottomDivider)
        llDutyInfo = findViewById(R.id.llDutyInfo)
        tvDutyPreviewName = findViewById(R.id.tvDutyPreviewName)
        vLeftLine = findViewById(R.id.vLeftLine)
        vRightLine = findViewById(R.id.vRightLine)
    }

    private fun setInitialData() {
        tvDormName.text = prefs.dormitoryName
        
        val todayStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        val (dutyStudent, _) = db.getDutyByDate(todayStr)
        tvDutyPreviewName.text = dutyStudent?.name ?: "未分配"
    }

    private fun startAnimations() {
        ivLogo.scaleX = 0f
        ivLogo.scaleY = 0f
        ivLogo.alpha = 0f
        llTextContainer.alpha = 0f
        llTextContainer.translationY = 40f
        tvDormName.alpha = 0f
        tvDutyLabel.alpha = 0f
        vTopDivider.alpha = 0f
        vTopDivider.scaleX = 0f
        vBottomDivider.alpha = 0f
        vBottomDivider.scaleX = 0f
        llDutyInfo.alpha = 0f
        llDutyInfo.translationY = 20f
        vLeftLine.scaleY = 0f
        vRightLine.scaleY = 0f

        val leftLineAnimator = ValueAnimator.ofFloat(0f, 1f)
        leftLineAnimator.duration = 600
        leftLineAnimator.interpolator = DecelerateInterpolator()
        leftLineAnimator.addUpdateListener { anim ->
            vLeftLine.scaleY = anim.animatedValue as Float
        }

        val rightLineAnimator = ValueAnimator.ofFloat(0f, 1f)
        rightLineAnimator.duration = 600
        rightLineAnimator.startDelay = 100
        rightLineAnimator.interpolator = DecelerateInterpolator()
        rightLineAnimator.addUpdateListener { anim ->
            vRightLine.scaleY = anim.animatedValue as Float
        }

        val logoScaleXAnimator = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0f, 1.2f, 1f)
        logoScaleXAnimator.duration = 700
        logoScaleXAnimator.startDelay = 300
        logoScaleXAnimator.interpolator = OvershootInterpolator(1.3f)

        val logoScaleYAnimator = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0f, 1.2f, 1f)
        logoScaleYAnimator.duration = 700
        logoScaleYAnimator.startDelay = 300
        logoScaleYAnimator.interpolator = OvershootInterpolator(1.3f)

        val logoAlphaAnimator = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f)
        logoAlphaAnimator.duration = 300
        logoAlphaAnimator.startDelay = 300

        val textContainerAnimator = ObjectAnimator.ofFloat(llTextContainer, "alpha", 0f, 1f)
        textContainerAnimator.duration = 400
        textContainerAnimator.startDelay = 700

        val textContainerTranslateAnimator = ObjectAnimator.ofFloat(llTextContainer, "translationY", 40f, 0f)
        textContainerTranslateAnimator.duration = 500
        textContainerTranslateAnimator.startDelay = 700
        textContainerTranslateAnimator.interpolator = DecelerateInterpolator()

        val topDividerScaleAnimator = ObjectAnimator.ofFloat(vTopDivider, "scaleX", 0f, 1f)
        topDividerScaleAnimator.duration = 400
        topDividerScaleAnimator.startDelay = 900
        topDividerScaleAnimator.interpolator = AccelerateDecelerateInterpolator()

        val topDividerAlphaAnimator = ObjectAnimator.ofFloat(vTopDivider, "alpha", 0f, 1f)
        topDividerAlphaAnimator.duration = 200
        topDividerAlphaAnimator.startDelay = 900

        val dormNameAnimator = ObjectAnimator.ofFloat(tvDormName, "alpha", 0f, 1f)
        dormNameAnimator.duration = 400
        dormNameAnimator.startDelay = 1000

        val dutyLabelAnimator = ObjectAnimator.ofFloat(tvDutyLabel, "alpha", 0f, 1f)
        dutyLabelAnimator.duration = 400
        dutyLabelAnimator.startDelay = 1150

        val bottomDividerScaleAnimator = ObjectAnimator.ofFloat(vBottomDivider, "scaleX", 0f, 1f)
        bottomDividerScaleAnimator.duration = 400
        bottomDividerScaleAnimator.startDelay = 1300
        bottomDividerScaleAnimator.interpolator = AccelerateDecelerateInterpolator()

        val bottomDividerAlphaAnimator = ObjectAnimator.ofFloat(vBottomDivider, "alpha", 0f, 1f)
        bottomDividerAlphaAnimator.duration = 200
        bottomDividerAlphaAnimator.startDelay = 1300

        val dutyInfoAnimator = ObjectAnimator.ofFloat(llDutyInfo, "alpha", 0f, 1f)
        dutyInfoAnimator.duration = 400
        dutyInfoAnimator.startDelay = 1450

        val dutyInfoTranslateAnimator = ObjectAnimator.ofFloat(llDutyInfo, "translationY", 20f, 0f)
        dutyInfoTranslateAnimator.duration = 400
        dutyInfoTranslateAnimator.startDelay = 1450
        dutyInfoTranslateAnimator.interpolator = DecelerateInterpolator()

        leftLineAnimator.start()
        rightLineAnimator.start()
        logoScaleXAnimator.start()
        logoScaleYAnimator.start()
        logoAlphaAnimator.start()
        textContainerAnimator.start()
        textContainerTranslateAnimator.start()
        topDividerScaleAnimator.start()
        topDividerAlphaAnimator.start()
        dormNameAnimator.start()
        dutyLabelAnimator.start()
        bottomDividerScaleAnimator.start()
        bottomDividerAlphaAnimator.start()
        dutyInfoAnimator.start()
        dutyInfoTranslateAnimator.start()

        ivLogo.postDelayed({
            navigateToMain()
        }, 2200)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        
        val fadeOut = ObjectAnimator.ofFloat(clRoot, "alpha", 1f, 0f)
        fadeOut.duration = 250
        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
            }
        })
        fadeOut.start()
    }

    private fun navigateToMainDirectly() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
