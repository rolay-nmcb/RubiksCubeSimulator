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
├─ moves/                         // 定义与解析基本移动的包
│  ├─ Moves.java                  // 基本移动及其在立方体上的执行逻辑
│  └─ MoveParser.java             // 文本步骤解析为 Move 对象序列
├─ solutions/                     // 算法求解包
│  ├─ CubeStringConverter.java    // facelets 字符串与 Cube 对象互转
│  ├─ Cross.java                  // 分层法第一步：十字架（Cross）
│  ├─ First2Layers.java           // 分层法第二步：前两层（F2L）
│  ├─ OrientationOfLastLayer.java // 分层法第三步：顶层颜色定向（OLL）
│  ├─ PermutationOfLastLayer.java // 分层法第四步：顶层排列（PLL）
│  ├─ KociembaSolver.java         // Kociemba 两阶段搜索算法实现
│  └─ Solution.java               // 解法整合类，供命令行模式使用
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

- **两阶段搜索算法概述**：

  1. **阶段一（Phase 1）**：将魔方状态约化到 G1 子群（满足所有棱块朝向归零、所有角块朝向归零、上下层棱块处于中部切片——UD slice 位置）。主要坐标量包括：
     - **Twist（角块方向）**：3^7=2187 种可能，存储为 0–2186。
     - **Flip（棱块方向）**：2^11=2048 种可能，存储为 0–2047。
     - **Slice（中部棱块位置）**：从 12 条棱中选出 4 条在 UD slice，C(12,4)=495 种可能。
  2. **阶段二（Phase 2）**：在 G1 基础上，用最少步数将剩余的置换约化到终态。阶段二的坐标量包括：
     - **Corners（角块排列）**：8! = 40320 种。
     - **Edges（棱块排列）**：4!×4!×(剩余4棱) = (可用对称性减少) 。

- **坐标系统与置换表**：

  - 将魔方状态映射到上述坐标，依赖预先离线生成的 **置换表（Move Tables）** 和 **剪枝表（Pruning Tables）**。
  - **Move Tables** 保存单个基本面转对各坐标的转换效果，加速坐标更新。
  - **Pruning Tables** 存储从任意坐标状态到目标坐标的最少转数估计，用于估值函数（heuristic），保证 IDA\* 搜索的最优性。

- **搜索策略**：

  ```text
  function ida_star(start_state):
    for depth = 0 to maxDepth:
      if dfs_phase1(start_state, depth):
        return 拼接(phase1_moves, phase2_moves)

  function dfs_phase1(state, depth_left):
    if (phase1_heuristic(state) > depth_left) return false
    if state in G1:  # twist=0, flip=0, slice目标
      return dfs_phase2(state, maxDepth2)
    for each 允许的基本面转 move:
      next_state = apply(move, state)
      if dfs_phase1(next_state, depth_left - 1):
        record move
        return true
    return false

  function dfs_phase2(state, depth_left):
    if state is solved: return true
    if (phase2_heuristic(state) > depth_left) return false
    for each 允许的 move:
      next_state = apply(move, state)
      if dfs_phase2(next_state, depth_left - 1):
        record move
        return true
    return false
  ```

- **优化与参数**：

  - **最大深度（maxDepth）**：用户可指定阶段一和阶段二的搜索深度上限。
  - **超时控制（timeOut）**：达到设定时间则放弃搜索并返回当前最优解。
  - **剪枝与对称性**：利用魔方映射对称性，减少状态空间，显著提高搜索效率。

- **返回结果**：

  - 合并阶段一和阶段二的移动序列，得到完整解法。
  - 输出格式与分层法一致，可供 `MoveParser` 和 `MyPanel` 使用。

### 11. Solution.java

- 命令行模式下的解法整合类，供 `Cube.java` 调用。
- 根据参数选择算法，生成文本步骤列表并传递给 `MyPanel` 或标准输出。

## 五、运行环境与依赖

- **JDK**：1.8+
- **Maven/Gradle**：可选，用于项目构建
- **Swing**：Java 标准库，无需额外依赖
- **Kociemba 库**：org.kociemba.twophase（可通过 Maven/Gradle 添加依赖以启用 Kociemba 算法模块）
  ```xml
  <!-- 示例：Maven 依赖 -->
  <dependency>
    <groupId>org.kociemba</groupId>
    <artifactId>twophase</artifactId>
    <version>2.1.0</version>
  </dependency>
  ```

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

**IntelliJ IDEA 运行**：

1. 打开 IntelliJ IDEA，选择 **Open** 并定位到项目根目录（包含 `src` 文件夹）。
2. 确保项目 SDK 设置为 **JDK 1.8+**，并将 `src` 目录标记为 **Source Root**。
3. 在 **Project** 工具窗口中，右键单击 `Cube.java`，选择 **Run 'Cube.main()'**。
4. 或者：
   - 点击顶部工具栏的 **Edit Configurations…**。
   - 点击左上角 **+**，选择 **Application**。
   - 配置：
     - **Name**: Cube
     - **Main class**: `Cube`
     - **Use classpath of module**: 选择当前模块
   - 点击 **OK**。
5. 点击 Run 按钮，即可在 IDEA 中直接运行程序；可在 Run Configuration 中添加命令行参数以切换算法或输入面字符串。

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

