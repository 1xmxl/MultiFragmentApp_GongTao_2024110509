package com.example.multifragmentapp.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.multifragmentapp.R
import com.example.multifragmentapp.model.CardMatchingGame
import com.example.multifragmentapp.model.GameRecord
import com.example.multifragmentapp.storage.FileStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 翻牌游戏 Fragment。同一个类通过构造参数支持两种模式：
 * - pairCount = 2：2 张牌翻牌游戏
 * - pairCount = 3：3 张牌翻牌游戏
 *
 * 每局游戏状态（CardMatchingGame，含整副牌、得分、步数）都会序列化保存，
 * 退出程序后重新进入可继续；历史记录也会追加保存并展示。
 */
class GameFragment : Fragment() {

    private var pairCount: Int = 2
    private lateinit var game: CardMatchingGame
    private lateinit var tvTitle: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvMoves: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvHistory: TextView
    private lateinit var cardGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pairCount = arguments?.getInt(ARG_PAIR_COUNT) ?: 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        tvTitle = view.findViewById(R.id.tvGameTitle)
        tvScore = view.findViewById(R.id.tvScore)
        tvMoves = view.findViewById(R.id.tvMoves)
        tvMessage = view.findViewById(R.id.tvMessage)
        tvHistory = view.findViewById(R.id.tvGameHistory)
        cardGrid = view.findViewById(R.id.cardGrid)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle.text = getString(
            if (pairCount == 2) R.string.game2_title else R.string.game3_title
        )

        // 优先从本地文件恢复游戏状态；若文件与当前模式不匹配则重新发牌
        game = FileStorage.load<CardMatchingGame>(requireContext(), gameFileName)
            ?.takeIf { it.pairCount == pairCount }
            ?: CardMatchingGame(pairCount).also { it.newGame() }
        game.normalizeAfterRestore()

        view.findViewById<Button>(R.id.btnNewGame).setOnClickListener {
            game.newGame()
            FileStorage.save(requireContext(), gameFileName, game)
            tvMessage.text = getString(R.string.new_game_hint)
            addRecord(getString(R.string.record_new_game))
            renderCards()
            updateScore()
            renderHistory()
        }

        renderCards()
        updateScore()
        renderHistory()
    }

    /** 动态生成卡牌按钮并绑定到 GridLayout */
    private fun renderCards() {
        cardGrid.removeAllViews()
        val columns = if (pairCount == 2) 2 else 3
        cardGrid.columnCount = columns

        game.cards.forEachIndexed { index, card ->
            val button = Button(requireContext()).apply {
                text = if (card.isFaceUp || card.isMatched) card.label else "?"
                textSize = 20f
                isEnabled = !card.isMatched
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 190
                    columnSpec = GridLayout.spec(index % columns, 1f)
                    rowSpec = GridLayout.spec(index / columns)
                    setMargins(6, 6, 6, 6)
                }
                gravity = Gravity.CENTER
                // 配对成功显示绿色，翻开未配对显示橙色
                setBackgroundColor(
                    when {
                        card.isMatched -> 0xFF2E7D32.toInt()
                        card.isFaceUp -> 0xFFFF9800.toInt()
                        else -> 0xFF3F51B5.toInt()
                    }
                )
                setTextColor(0xFFFFFFFF.toInt())
                setOnClickListener {
                    val message = game.chooseCard(index)
                    tvMessage.text = message
                    FileStorage.save(requireContext(), gameFileName, game)
                    addRecord(message)
                    renderCards()
                    updateScore()
                    renderHistory()

                    if (game.isFinished) {
                        tvMessage.text = "恭喜！全部配对完成，最终得分：${game.score}"
                    }
                }
            }
            cardGrid.addView(button)
        }
    }

    private fun updateScore() {
        tvScore.text = "得分：${game.score}"
        tvMoves.text = "步数：${game.moves}"
    }

    /** 追加历史记录并序列化保存 */
    private fun addRecord(action: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val record = GameRecord(gameName, action, game.score, time)
        val records = FileStorage.load<ArrayList<GameRecord>>(requireContext(), FILE_HISTORY)
            ?: arrayListOf()
        records.add(0, record)
        FileStorage.save(requireContext(), FILE_HISTORY, records)
    }

    private fun renderHistory() {
        val records = FileStorage.load<List<GameRecord>>(requireContext(), FILE_HISTORY)
        if (records.isNullOrEmpty()) {
            tvHistory.text = getString(R.string.no_history)
            return
        }
        tvHistory.text = records.take(6).map { r ->
            "${r.time}  ${r.action}  (得分 ${r.score})"
        }.joinToString("\n")
    }

    private val gameName: String
        get() = if (pairCount == 2) "2 张牌游戏" else "3 张牌游戏"

    /** 两种模式使用不同文件，互不覆盖 */
    private val gameFileName: String
        get() = if (pairCount == 2) FILE_GAME2 else FILE_GAME3

    companion object {
        const val ARG_PAIR_COUNT = "pair_count"
        const val FILE_GAME2 = "game2.bin"
        const val FILE_GAME3 = "game3.bin"
        const val FILE_HISTORY = "game_history.bin"

        fun newInstance(pairCount: Int): GameFragment = GameFragment().apply {
            arguments = Bundle().apply { putInt(ARG_PAIR_COUNT, pairCount) }
        }
    }
}
