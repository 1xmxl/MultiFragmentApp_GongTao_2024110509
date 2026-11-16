package com.example.multifragmentapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.multifragmentapp.model.Profile
import com.example.multifragmentapp.storage.FileStorage
import com.example.multifragmentapp.ui.GameFragment
import com.example.multifragmentapp.ui.ProfileFragment

/**
 * 主 Activity：只负责承载三个 Fragment 并在底部 Tab 之间切换。
 * 顶部标题显示 “姓名+学号”（从本地序列化文件读取，未填写时显示默认标题）。
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val FILE_PROFILE = "profile.bin"
    }

    private lateinit var tvAppTitle: TextView
    private val tabButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvAppTitle = findViewById(R.id.tvAppTitle)
        val btnProfile: Button = findViewById(R.id.btnTabProfile)
        val btnGame2: Button = findViewById(R.id.btnTabGame2)
        val btnGame3: Button = findViewById(R.id.btnTabGame3)
        tabButtons.addAll(listOf(btnProfile, btnGame2, btnGame3))

        // 恢复标题为 “姓名+学号”
        refreshTitle()

        btnProfile.setOnClickListener { switchTab(0) }
        btnGame2.setOnClickListener { switchTab(1) }
        btnGame3.setOnClickListener { switchTab(2) }

        if (savedInstanceState == null) {
            switchTab(0)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTitle()
    }

    /** 从私有文件读出个人信息，设置应用标题 */
    fun refreshTitle() {
        val profile = FileStorage.load<Profile>(this, FILE_PROFILE)
        tvAppTitle.text = if (profile != null && profile.name.isNotBlank()) {
            "${profile.name}${profile.studentId}"
        } else {
            getString(R.string.app_name)
        }
    }

    private fun switchTab(index: Int) {
        tabButtons.forEachIndexed { i, button ->
            button.setBackgroundColor(
                if (i == index) 0xFFE8EAF6.toInt() else 0xFFFFFFFF.toInt()
            )
        }

        val fragment = when (index) {
            0 -> ProfileFragment()
            1 -> GameFragment.newInstance(pairCount = 2)
            else -> GameFragment.newInstance(pairCount = 3)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
