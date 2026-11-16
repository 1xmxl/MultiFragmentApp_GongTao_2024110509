package com.example.multifragmentapp.model

import java.io.Serializable

/**
 * 一张牌。CardMatchingGame 中牌堆由若干张牌组成。
 * 实现 Serializable，使整副牌可以随游戏状态一起写入文件。
 */
data class Card(
    val suit: String,     // 花色
    val rank: String,     // 点数
    var isFaceUp: Boolean = false,   // 是否正面朝上
    var isMatched: Boolean = false   // 是否已配对成功
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 1L
    }

    /** 牌面文本，例如 “♥A” */
    val label: String
        get() = "$suit$rank"

    /** 翻转牌面 */
    fun flip() {
        isFaceUp = !isFaceUp
    }
}
