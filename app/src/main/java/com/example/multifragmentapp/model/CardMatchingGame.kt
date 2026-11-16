package com.example.multifragmentapp.model

import kotlin.random.Random
import java.io.Serializable

/**
 * 翻牌配对游戏的核心逻辑（参考实验 PDF 中的 CardMatchingGame.kt）。
 *
 * - pairCount：牌的对数。2 张牌游戏传 2，3 张牌游戏传 3。
 * - 每次同时翻开 “pairCount” 张牌；若点数（rank）相同则配对成功并加分，
 *   否则扣分并自动盖回。全部配对成功即游戏结束。
 *
 * 整个类实现 Serializable，可以直接被 ObjectOutputStream 写入私有文件，
 * 再次进入程序时用 ObjectInputStream 读出，实现“退出后重开可恢复”。
 */
class CardMatchingGame(
    val pairCount: Int = 2
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 2L
        private val SUITS = listOf("♠", "♥", "♣", "♦")
        private val RANKS = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    }

    /** 当前牌堆（每局重新生成） */
    var cards: List<Card> = emptyList()
        private set

    /** 得分 */
    var score: Int = 0
        private set

    /** 已操作步数 */
    var moves: Int = 0
        private set

    /** 已经翻开、等待判定是否配对的牌的下标 */
    private val flippedIndexes = mutableListOf<Int>()

    /** 游戏是否结束 */
    val isFinished: Boolean
        get() = cards.isNotEmpty() && cards.all { it.isMatched }

    /** 本局是否需要等待更多牌翻开（用于提示文案） */
    val selectedCount: Int
        get() = flippedIndexes.size

    init {
        newGame()
    }

    /** 重新发牌：生成 pairCount 对点数相同的牌并洗牌 */
    fun newGame() {
        score = 0
        moves = 0
        flippedIndexes.clear()

        val ranks = RANKS.shuffled(Random(System.currentTimeMillis())).take(pairCount)
        val deck = mutableListOf<Card>()
        ranks.forEach { rank ->
            repeat(2) { i ->
                deck.add(Card(suit = SUITS[i % SUITS.size], rank = rank))
            }
        }
        cards = deck.shuffled(Random(System.currentTimeMillis()))
    }

    /**
     * 选择一张牌，返回一条用于界面提示的中文消息。
     */
    fun chooseCard(index: Int): String {
        if (index !in cards.indices) return "无效的牌"

        // 若上一轮未配对成功，此时先把那些牌盖回，再翻新的牌
        if (cards.any { it.isFaceUp && !it.isMatched }) {
            hideUnmatched()
            flippedIndexes.clear()
        }

        val card = cards[index]
        if (card.isMatched || card.isFaceUp) return "这张牌已经翻开，请选择其它牌"

        card.isFaceUp = true
        flippedIndexes.add(index)
        moves++
        saveToHistory()

        if (flippedIndexes.size < pairCount) {
            return "已翻开 ${flippedIndexes.size}/$pairCount 张，继续翻牌"
        }

        // 判定本轮翻开的牌是否点数相同
        val chosen = flippedIndexes.map { cards[it] }
        val matched = chosen.all { it.rank == chosen.first().rank }

        return if (matched) {
            chosen.forEach { it.isMatched = true }
            score += 10
            flippedIndexes.clear()
            "配对成功！+10 分"
        } else {
            score -= 1
            flippedIndexes.clear()
            "点数不同，-1 分；点选其它牌时会自动盖回"
        }
    }

    /** 盖回所有未成功配对的牌（在用户点击“下一轮”时调用） */
    fun hideUnmatched() {
        cards.forEach { card ->
            if (!card.isMatched) card.isFaceUp = false
        }
    }

    /** 保存历史记录：由仓库层调用，此处仅声明接口，保持模型纯粹 */
    private fun saveToHistory() {
        // 历史记录由 GameRepository 在每次操作后统一持久化
    }

    /** 恢复时校正临时状态：把没有配对的牌盖回 */
    fun normalizeAfterRestore() {
        flippedIndexes.clear()
        hideUnmatched()
    }
}
