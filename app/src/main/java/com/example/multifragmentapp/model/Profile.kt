package com.example.multifragmentapp.model

import java.io.Serializable

/**
 * 个人信息数据类。
 * 实现 Serializable：退出程序后用 ObjectInputStream 恢复。
 */
data class Profile(
    var name: String = "",
    var studentId: String = "",
    var major: String = "",
    var phone: String = ""
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 3L
    }
}
