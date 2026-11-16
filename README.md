# 多 Fragment 应用（实验六）

基于实验五改造的 Android Kotlin 工程，包含三个 Fragment 与对象序列化文件存储。

## 一、功能结构

| 模块 | 说明 |
|---|---|
| `MainActivity` | 底部 Tab 承载并切换三个 Fragment；顶部标题显示“姓名+学号” |
| `ProfileFragment` | 个人信息：姓名、学号、专业、电话；保存时用 `ObjectOutputStream` 写入私有文件，重进程序自动回填 |
| `GameFragment(pairCount=2)` | 2 张牌翻牌游戏：翻开 2 张牌，点数相同则配对成功 |
| `GameFragment(pairCount=3)` | 3 张牌翻牌游戏：翻开 3 张牌，点数相同则配对成功 |

数据持久化（均在 `app/src/main/java/com/example/multifragmentapp/storage/FileStorage.kt`）：

- 写：`context.openFileOutput(fileName, Context.MODE_PRIVATE)` + `ObjectOutputStream.writeObject()`
- 读：`context.openFileInput(fileName)` + `ObjectInputStream.readObject()`
- 涉及对象：`Profile`、`Card`、`CardMatchingGame`、`GameRecord` 均实现 `java.io.Serializable`
- 两类游戏状态分别保存为 `game2.bin` / `game3.bin`，个人信息 `profile.bin`，历史 `profile_history.bin` / `game_history.bin`

## 二、提交前必须做的 3 件事

1. **填写姓名学号**：运行 App 后进入“个人信息”Tab，填写姓名和学号并保存。顶部标题会自动变成“姓名+学号”，满足提交要求 (1)。
   - 若想改默认标题，编辑 `app/src/main/res/values/strings.xml` 中的 `app_name`。
2. **填写 Gitee 地址**：打开同目录的 `实验六_多Fragment应用_实验报告.docx`，把封面“Gitee 仓库地址”占位文字替换为你的真实地址。
3. **修改应用标识（可选）**：把 `app/build.gradle.kts` 中 `applicationId` 的示例部分改成自己的学号，避免与同学安装包冲突。

## 三、Gitee 分支与推送

本仓库已按实验要求建好三个分支：

- `Main`：基础工程
- `Dev`：开发分支
- `Frag`：**本次作业最终代码**（当前工作分支）

在 Android Studio 中连上你的 Gitee 账号后，执行：

```bash
# 1）若 Gitee 上已有实验四/五的仓库，先克隆或添加远程：
git remote add origin https://gitee.com/<你的用户名>/<实验四或五的仓库名>.git

# 2）推送三个分支（保持与实验四/五同一地址，只是分支不同）
git push origin Main
git push origin Dev
git push origin Frag

# 3）若首次推送 Main 被拒绝（远程已有 Main），改用：
git push origin Main:Main --force-with-lease   # 仅在确认要覆盖时使用
# 或者只推送 Dev 和 Frag：
git push origin Dev
git push origin Frag
```

推送后在 Gitee 仓库的“分支”页面应能看到 `Main`、`Dev`、`Frag` 三个分支；在代码页面切换到 `Frag` 分支即可看到本次作业源码。

## 四、截图与实验报告

1. 用 Android Studio 的模拟器或真机运行 App，分别截图：
   - 个人信息页面（填写并保存后）
   - 2 张牌游戏页面（翻牌过程 / 配对成功）
   - 3 张牌游戏页面
   - **退出程序后重新进入**，展示个人信息与游戏状态被恢复（这是“序列化存储”的关键证据）
2. 把这些截图插入 `实验六_多Fragment应用_实验报告.docx` 中“运行界面截图”一节，并在“核心代码说明”一节附上主要代码（工程里已列出关键文件与行号）。

## 五、编译环境

- Android Studio Ladybug 或更新版本
- JDK 17
- Android SDK 34（compileSdk 34 / minSdk 23 / targetSdk 34）
- Gradle 8.7（由 `gradle/wrapper/gradle-wrapper.properties` 指定，首次打开自动下载）
