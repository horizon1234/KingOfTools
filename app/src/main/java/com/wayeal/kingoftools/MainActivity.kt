package com.wayeal.kingoftools

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wayeal.kingoftools.fragment.FavorFragment
import com.wayeal.kingoftools.fragment.MainFragment
import com.wayeal.kingoftools.fragment.MineFragment
import com.wayeal.kingoftools.view.MagicTabView
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "MagicTabView"

class MainActivity : AppCompatActivity() {

    private val mainFragment: MainFragment by lazy {
        MainFragment.newInstance(" ", " ")
    }

    private val favorFragment: FavorFragment by lazy {
        FavorFragment.newInstance(" ", " ")
    }

    private val mineFragment: MineFragment by lazy {
        MineFragment.newInstance(" ", " ")
    }

    //fragments
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    //tabViews
    private val tabViewList: ArrayList<MagicTabView> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //添加fragmentList
        fragmentList.add(mainFragment)
        fragmentList.add(favorFragment)
        fragmentList.add(mineFragment)
        //添加tabView
        tabViewList.add(main)
        tabViewList.add(favor)
        tabViewList.add(mine)
        //设置viewPager
        viewPager.offscreenPageLimit = tabViewList.size - 1
        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener(){

            /**
             * 滑动回调，这里的回调很特殊,position总是代表左边的view
             * */
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.i(TAG, "onPageScrolled: position = $position  positionOffset = $positionOffset")
                //左边view进行动画
                tabViewList[position].setPercentage(1 - positionOffset)
                //如果positionOffset非0，那么就代表右边的View可见，也就说明需要对右边的View进行动画
                if (positionOffset > 0){
                    tabViewList[position + 1].setPercentage(positionOffset)
                }
                //如果为0，则表示选中
                if (positionOffset == 0f){
                    for (i in 0 until tabViewList.size){
                        if (i == position){
                            tabViewList[i].setPercentage(1f)
                        }else{
                            tabViewList[i].setPercentage(0f)
                        }
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        //点击tabView
        main.setOnClickListener {
            viewPager.setCurrentItem(0,false)
            updateTab(0)
        }
        favor.setOnClickListener {
            viewPager.setCurrentItem(1,false)
            updateTab(1)
        }
        mine.setOnClickListener {
            viewPager.setCurrentItem(2,false)
            updateTab(2)
        }
    }

    inner class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
        override fun getCount(): Int {
            return tabViewList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

    }

    private fun updateTab(index: Int){
        //如果该item已经选中则再加载一遍动画即可

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 400
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            for (i in 0 until tabViewList.size){
                val tabView = tabViewList[i]
                Log.i(TAG, "updateTab: $i 的percent = ${tabView.getPercent()}")
                if (index == i){
                    tabView.setPercentage(progress)
                }else{
                    if (tabView.getPercent() > 0f){
                        tabView.setPercentage(1-progress)
                    }
                }
            }
        }
        valueAnimator.addListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        valueAnimator.start()
    }


}