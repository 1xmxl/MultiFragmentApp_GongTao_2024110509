package com.example.multifragmentapp.model

import java.io.Serializable

/**
 * 一条游戏历史记录（每次翻牌操作都会追加一条，重开程序后仍可查看）。
 */
data class GameRecord(
    val gameName: String,      // "2 张牌游戏" / "3 张牌游戏"
    val action: String,        // 本次操作说明
    val score: Int,            // 操作后得分
    val time: String           // 操作时间
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 4L
    }
}
