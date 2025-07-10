# Rubik 魔方求解程序说明书

## 一、项目简介

Rubik 魔方求解程序是一个基于 Java 实现的三阶魔方（3x3）自动求解与可视化演示工具。它集成了经典分层法（Cross + F2L + OLL + PLL）、高效 Kociemba 两阶段搜索算法，并提供基于 Swing 的可视化界面，能够在图形面板上实时展示移动步骤及效果。

## 二、主要特性

- **多种求解算法**：支持经典分层法与 Kociemba 两阶段搜索，可根据场景切换。
- **可视化面板**：基于 Swing 的 `MyPanel.java`，展示 3D 风格平铺的立方体效果与动画。
- **模块化设计**：各算法阶段与界面逻辑独立封装，易于维护与扩展。
- **Facelets 字符串解析**：灵活转换输入字符串与内部立方体模型。
- **命令行与图形双入口**：既可在终端调用，也可启动 GUI 窗口演示。

## 三、项目结构

```
src/
├─ Cube.java                      // 程序主入口，解析参数并启动命令行或 GUI
├─ MyPanel.java                   // Swing 面板，实现魔方的可视化绘制与动画
├─ CubeStringConverter.java       // facelets 字符串与 Cube 对象互转
├─ Moves.java                     // 定义基本移动及其在立方体上的执行逻辑
├─ MoveParser.java                // 文本步骤解析为 Move 对象序列
├─ Cross.java                     // 分层法第一步：十字架（Cross）
├─ First2Layers.java              // 分层法第二步：前两层（F2L）
├─ OrientationOfLastLayer.java    // 分层法第三步：顶层颜色定向（OLL）
├─ PermutationOfLastLayer.java    // 分层法第四步：顶层排列（PLL）
├─ KociembaSolver.java            // Kociemba 两阶段搜索算法实现
└─ Solution.java                  // 与 Cube.java 协同的解法整合类，供命令行模式使用
```

## 四、模块说明

### 1. Cube.java

- 程序主入口，负责读取命令行参数：
  - 无参数：启动 GUI 模式，弹出可视化面板。
  - 提供 facelets 字符串：运行命令行求解，将步骤打印到控制台并在面板中演示。
- 初始化 `Cube` 对象并调用相应算法模块。

### 2. MyPanel.java

- 基于 Swing 的 `JPanel` 子类，负责立方体网格绘制与动画。
- 接收移动序列，按帧渲染转动效果，支持暂停/单步/全速演示。

### 3. CubeStringConverter.java

- 将外部输入的 facelets 字符串（U1…B9 顺序）转换成 `Cube` 对象。
- 提供逆向转换以便结果验证与日志输出。

### 4. Moves.java

- 定义各基本面转操作（U, R, F, D, L, B 及其逆和双转）。
- 实现对应的面块索引变换与状态更新。

### 5. MoveParser.java

- 将文本格式解法（如 “R U R' U'”）解析为 `Move` 对象序列。
- 支持连写、后缀（2, '）与空格分隔。

### 6. Cross.java

- 分层法第一步：生成底层十字架的移动序列。
- 利用启发式规则，按顺序插入对应的棱块。

### 7. First2Layers.java

- 分层法第二步：F2L 对角与棱块组合插入。
- 针对 4 对对应位置进行定位与归位操作。

### 8. OrientationOfLastLayer.java

- 分层法第三步：顶层 OLL 识别与公式应用，完成颜色定向。

### 9. PermutationOfLastLayer.java

- 分层法第四步：顶层 PLL 识别与公式应用，完成整顶面排列。

### 10. KociembaSolver.java

- 两阶段搜索：阶段 1 对中心和棱棱归类，阶段 2 完成余下还原。
- 内部使用 IDA\* 搜索与剪枝表，支持最大深度与超时控制。

### 11. Solution.java

- 命令行模式下的解法整合类，供 `Cube.java` 调用。
- 根据参数选择算法，生成文本步骤列表并传递给 `MyPanel` 或标准输出。

## 五、运行环境与依赖

- **JDK 1.8+**
- **Maven/Gradle**（可选，用于项目构建）
- **Swing**（Java 标准库，无需额外依赖）
- **org.kociemba** （Kociemba 算法库，用于两阶段搜索）

## 六、编译与运行

```bash
javac src/*.java
```

```bash
# GUI 模式
java -cp src Cube

# 命令行模式（分层法）
java -cp src Cube <facelets_string>

# 命令行模式（Kociemba）
java -cp src Cube <facelets_string> --method kociemba --maxDepth 20 --timeOut 5
```

## 七、使用示例

```bash
# 默认启动可视化面板
java -cp src Cube

# 打印并演示解法
java -cp src Cube UBRR... --method cross
```

## 八、贡献与许可

- 欢迎提交 issue 与 pull request，扩展更多公式与优化界面。
- 本项目遵循 **MIT 协议**，详见 LICENSE 文件。

