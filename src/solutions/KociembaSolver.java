package solutions;

import moves.MoveParser;
import moves.Moves;
import org.kociemba.twophase.Search;
import org.kociemba.twophase.Tools;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class KociembaSolver extends Solution {

    // 假设这些常量与你定义的颜色值一致
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int ORANGE = 3;
    public static final int YELLOW = 4;
    public static final int BLACK = 5;

    public KociembaSolver(JPanel panel, int[][] color) {
        super(panel, color);
    }

    // 主运行函数（供主程序调用）
    public void run() {
        direct();
        Integer steps=0;
        // 获取当前魔方状态并转为 facelet 字符串（URFDLB 顺序）
        String facelets = CubeStringConverter.toKociembaString(Moves.color);

        System.out.println("魔方状态是："+ Arrays.deepToString(Moves.color));

        // 在 solveKociemba 中先验证：
        int msg = Tools.verify(facelets);
        if (msg!=0) {
            System.err.println("非法魔方状态: " + msg);
        }

        // 使用 Kociemba 算法获取解法公式（字符串序列）
        List<String> solution = solveKociemba(facelets);

        // 拼接为完整公式字符串
        String formula = String.join(" ", solution);
        System.out.println("Kociemba 解法：" + formula+'\n'+"一共耗费"+solution.size()+"步解法");
        steps=solution.size();

        // 实际执行操作
        String moves = MoveParser.convertMoves(formula);
        System.out.println("转换后的公式：" + moves);
        Moves.perform(moves, delay);
        stall(300 + delay);
        //弹窗提示消耗多少步数
        JOptionPane.showMessageDialog(null,"消耗步数："+steps);
    }

    /**
     * 按照 Kociemba 官方定义转换 color[6][9] 为 facelet 字符串（如 UUUUUUUUURRRRRRRRR...）
     */
    public static String convertToFaceletString(int[][] color) {
        if (color == null || color.length != 6) {
            throw new IllegalArgumentException("color 必须是 6 个面的二维数组。");
        }
        for (int[] face : color) {
            if (face == null || face.length != 9) {
                throw new IllegalArgumentException("每个面必须有 9 个格子。");
            }
        }

        // 颜色值 → 面标识字母
        char[] colorMap = new char[6];
        colorMap[BLUE]   = 'U';
        colorMap[RED]    = 'R';
        colorMap[GREEN]  = 'F';
        colorMap[ORANGE] = 'D';
        colorMap[YELLOW] = 'L';
        colorMap[BLACK]  = 'B';

        // 魔方面顺序：U, R, F, D, L, B
        int[] faceOrder = {0, 1, 2, 3, 4, 5};

        StringBuilder sb = new StringBuilder(54);
        for (int faceIdx : faceOrder) {
            for (int i = 0; i < 9; i++) {
                int colorVal = color[faceIdx][i];
                sb.append(colorMap[colorVal]);
            }
        }

        return sb.toString();
    }


    // color值（0~5）转为 URFDLB 字母
    private char getColorChar(int val) {
        switch (val) {
            case 0: return 'U'; // 蓝 → 上
            case 1: return 'R'; // 红 → 右
            case 2: return 'F'; // 绿 → 前
            case 3: return 'L'; // 橙 → 左
            case 4: return 'D'; // 黄 → 下
            case 5: return 'B'; // 黑 → 背（此处设为黑=背）
            default: return '?';
        }
    }

    // 假设函数：使用 Kociemba 算法返回字符串步骤
    private List<String> solveKociemba(String facelets) {
        List<String> steps = new ArrayList<>();
        try {
            // 使用官方库返回字符串解法
            System.out.println("魔方状态是"+facelets);
            String solution = Search.solution(facelets, 21, 5000, false);
            System.out.println("解决方案是"+solution);
            // 去掉注释部分（如：// 8 moves）
            if (solution.contains(" ")) {
                String[] parts = solution.split(" ");
                for (String move : parts) {
                    if (move.startsWith("(")) break; // 忽略备注
                    steps.add(move);
                }
            }
        } catch (Exception e) {
            System.err.println("Kociemba 求解失败：" + e.getMessage());
        }
        return steps;
    }

}

