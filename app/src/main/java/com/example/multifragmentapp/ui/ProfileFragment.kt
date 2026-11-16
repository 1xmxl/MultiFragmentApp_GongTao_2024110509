package com.example.multifragmentapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.multifragmentapp.MainActivity
import com.example.multifragmentapp.R
import com.example.multifragmentapp.model.Profile
import com.example.multifragmentapp.storage.FileStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 个人信息 Fragment。
 * 进入时从私有文件反序列化并回填；点击保存后序列化写入文件，
 * 同时把每次保存作为一条历史记录持久化。
 */
class ProfileFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etMajor: EditText
    private lateinit var etPhone: EditText
    private lateinit var tvStatus: TextView
    private lateinit var tvHistory: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        etName = view.findViewById(R.id.etName)
        etStudentId = view.findViewById(R.id.etStudentId)
        etMajor = view.findViewById(R.id.etMajor)
        etPhone = view.findViewById(R.id.etPhone)
        tvStatus = view.findViewById(R.id.tvProfileStatus)
        tvHistory = view.findViewById(R.id.tvProfileHistory)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 恢复历史信息；首次运行时预填本人的姓名学号
        val profile = FileStorage.load<Profile>(requireContext(), FILE_PROFILE)
        if (profile != null) {
            etName.setText(profile.name)
            etStudentId.setText(profile.studentId)
            etMajor.setText(profile.major)
            etPhone.setText(profile.phone)
            tvStatus.text = getString(R.string.restored_hint)
        } else {
            etName.setText(R.string.default_name)
            etStudentId.setText(R.string.default_student_id)
        }
        renderHistory()

        view.findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val newProfile = Profile(
                name = etName.text.toString().trim(),
                studentId = etStudentId.text.toString().trim(),
                major = etMajor.text.toString().trim(),
                phone = etPhone.text.toString().trim()
            )
            FileStorage.save(requireContext(), FILE_PROFILE, newProfile)

            // 追加一条历史记录
            val history = FileStorage.load<ArrayList<Profile>>(requireContext(), FILE_PROFILE_HISTORY)
                ?: arrayListOf()
            history.add(0, newProfile)
            FileStorage.save(requireContext(), FILE_PROFILE_HISTORY, history)

            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            tvStatus.text = "已保存到本地文件（$time）"
            renderHistory()

            // 立即刷新 Activity 顶部的“姓名+学号”
            (activity as? MainActivity)?.refreshTitle()
        }
    }

    private fun renderHistory() {
        val history = FileStorage.load<List<Profile>>(requireContext(), FILE_PROFILE_HISTORY)
        if (history.isNullOrEmpty()) {
            tvHistory.text = getString(R.string.no_history)
            return
        }
        tvHistory.text = history.take(5).mapIndexed { i, p ->
            "${i + 1}. ${p.name} / ${p.studentId} / ${p.major}"
        }.joinToString("\n")
    }

    companion object {
        const val FILE_PROFILE = "profile.bin"
        const val FILE_PROFILE_HISTORY = "profile_history.bin"
    }
}
