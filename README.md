# MyDorm - 宿舍管理应用

一个功能丰富的原生 Android 宿舍管理应用，使用 Kotlin + SQLite 开发。

## 项目简介

这是一个专为学生宿舍设计的综合管理应用，提供值日管理、数据同步、生活小工具等多种功能，帮助宿舍成员更高效地管理日常事务。

## 功能特性

### 📅 值日管理
- 查看今日值日人员
- 日历视图查看整月值日安排
- 调休功能
- 补值日功能
- 交换值日功能
- 值日历史记录
- 值日完成标记与时间记录
- 值日评价系统（1-5星打分+备注）

### 🏠 宿舍信息
- 自定义宿舍名称
- 自定义宿舍人数
- 室友信息管理
- 选择自己身份（所有工具以该用户为主）

### 📊 统计与排行榜
- 值日完成率统计
- 室友值日统计
- 值日排行榜（支持点赞）
- 历史记录查看

### 🔄 数据同步
- QR 码数据导出/导入
- 局域网 HTTP 服务器同步
- 完整数据备份与恢复
- 支持多设备数据同步

### 🔔 提醒功能
- 值日提醒
- 自定义提醒时间
- 提前提醒（15分钟/30分钟/1小时/2小时/1天）
- 重复提醒
- 语音提醒

### 🎨 外观设置
- 深色/浅色主题切换
- 系统主题跟随
- 自定义主题颜色
- 启动动画开关

### 🔧 生活小工具
- 📝 室友备忘录（可关联特定室友）
- 🎲 随机抽人器
- 💰 费用分摊计算器
- 🛒 宿舍购物清单
- 🎂 室友生日提醒
- 🗳️ 宿舍投票器

### 📖 其他功能
- 侧边栏导航
- 关于页面（显示应用信息、技术栈、开源协议）
- MIT 开源协议

## 技术栈

- **语言**：Kotlin
- **最低 SDK**：API 24 (Android 7.0)
- **目标 SDK**：API 36
- **数据库**：SQLite
- **架构**：MVVM 思想
- **UI 框架**：原生 Android View + Material Design
- **数据同步**：NanoHTTPD（局域网服务器）+ ZXing（QR 码）
- **JSON 处理**：Gson
- **主题**：支持深色/浅色模式

## 如何使用

### 1. 在 Android Studio 中打开项目
1. 启动 Android Studio
2. 选择 "Open an Existing Project"
3. 选择 `DormitoryManager` 文件夹
4. 等待 Gradle 同步完成

### 2. 运行应用
1. 连接 Android 设备或启动模拟器
2. 点击 Android Studio 中的运行按钮（绿色三角形）

### 3. 构建 APK
```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本（需要配置签名）
./gradlew assembleRelease
```

## 项目结构

```
DormitoryManager/
├── app/
│   ├── src/main/
│   │   ├── java/com/dormitorymanager/
│   │   │   ├── MainActivity.kt              # 主界面
│   │   │   ├── DatabaseHelper.kt            # 数据库帮助类
│   │   │   ├── PreferencesHelper.kt         # 配置管理
│   │   │   ├── ThemeManager.kt              # 主题管理
│   │   │   ├── AlarmManagerHelper.kt        # 闹钟管理
│   │   │   ├── ReminderReceiver.kt          # 提醒接收
│   │   │   ├── Student.kt                   # 学生数据模型
│   │   │   ├── Duty.kt                      # 值日数据模型
│   │   │   ├── BackupData.kt                # 备份数据模型
│   │   │   ├── Memo.kt                      # 备忘录数据模型
│   │   │   ├── ShoppingItem.kt              # 购物清单项数据模型
│   │   │   ├── Birthday.kt                  # 生日数据模型
│   │   │   ├── Vote.kt                      # 投票数据模型
│   │   │   ├── CalendarActivity.kt          # 日历页面
│   │   │   ├── HistoryActivity.kt           # 历史记录页面
│   │   │   ├── StatisticsActivity.kt        # 统计页面
│   │   │   ├── LeaderboardActivity.kt       # 排行榜页面
│   │   │   ├── SettingsActivity.kt          # 设置页面
│   │   │   ├── ToolsActivity.kt             # 小工具页面
│   │   │   ├── MemoActivity.kt              # 备忘录页面
│   │   │   ├── RandomPickerActivity.kt      # 随机抽人器页面
│   │   │   ├── ExpenseSplitActivity.kt      # 费用分摊计算器页面
│   │   │   ├── ShoppingListActivity.kt      # 购物清单页面
│   │   │   ├── BirthdayActivity.kt          # 生日提醒页面
│   │   │   ├── VotingActivity.kt            # 投票器页面
│   │   │   ├── SyncActivity.kt              # 数据同步页面
│   │   │   ├── AboutActivity.kt             # 关于页面
│   │   │   └── ...                          # 其他 Activity
│   │   ├── res/
│   │   │   ├── layout/                      # 布局文件
│   │   │   ├── values/                      # 资源文件
│   │   │   ├── values-night/                # 深色模式资源
│   │   │   ├── drawable/                    # 图片资源
│   │   │   └── mipmap-*/                   # 应用图标
│   │   └── AndroidManifest.xml              # 应用清单
│   └── build.gradle                          # 应用级 Gradle 配置
├── gradle/
│   └── wrapper/
├── build.gradle                              # 项目级 Gradle 配置
├── settings.gradle                           # Gradle 设置
├── LICENSE                                   # MIT 开源协议
└── README.md                                 # 项目说明
```

## 数据库表结构

- `students` - 室友信息表
- `duties` - 值日安排表
- `duty_records` - 值日记录表
- `memos` - 备忘录表
- `shopping_list` - 购物清单表
- `birthdays` - 生日提醒表
- `votes` - 投票表
- `vote_options` - 投票选项表

## 开源协议

本项目采用 MIT 开源协议，详见 [LICENSE](LICENSE) 文件。

## 作者

sykesP42

## 贡献

欢迎提交 Issue 和 Pull Request！

## 注意事项

- 首次打开项目时，Android Studio 会自动下载 Gradle 和依赖
- 确保已安装 Android SDK 36
- 数据同步功能需要设备在同一局域网内
