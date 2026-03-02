# 宿舍值日管理 - 原生 Android 版本

## 项目简介
这是一个使用原生 Android + Kotlin + SQLite 开发的宿舍值日管理应用。

## 功能特性
- 查看今日值日人员
- 室友列表显示
- 调休、换班、补值日功能（开发中）
- 日历视图（开发中）

## 技术栈
- 语言：Kotlin
- 最低 SDK：API 24 (Android 7.0)
- 目标 SDK：API 36
- 数据库：SQLite

## 如何使用

### 1. 在 Android Studio 中打开项目
1. 启动 Android Studio
2. 选择 "Open an Existing Project"
3. 选择 `DormitoryManager` 文件夹
4. 等待 Gradle 同步完成

### 2. 运行应用
1. 连接 Android 设备或启动模拟器
2. 点击 Android Studio 中的运行按钮（绿色三角形）

## 项目结构
```
DormitoryManager/
├── app/
│   ├── src/main/
│   │   ├── java/com/dormitorymanager/
│   │   │   ├── MainActivity.kt          # 主界面
│   │   │   ├── DatabaseHelper.kt        # 数据库帮助类
│   │   │   ├── Student.kt               # 学生数据模型
│   │   │   ├── Duty.kt                  # 值日数据模型
│   │   │   └── StudentAdapter.kt       # 学生列表适配器
│   │   ├── res/
│   │   │   ├── layout/                  # 布局文件
│   │   │   ├── values/                  # 资源文件
│   │   │   └── drawable/                # 图片资源
│   │   └── AndroidManifest.xml          # 应用清单
```

## 注意事项
- 首次打开项目时，Android Studio 会自动下载 Gradle 和依赖
- 确保已安装 Android SDK 36
