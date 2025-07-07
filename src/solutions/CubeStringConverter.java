package solutions;

public class CubeStringConverter {
    // —— 颜色常量 ——
    public static final int BLUE   = 0;
    public static final int RED    = 1;
    public static final int GREEN  = 2;
    public static final int ORANGE = 3;
    public static final int YELLOW = 4;
    public static final int BLACK  = 5;

    // —— 面索引 ——
    // 下面的索引只是数组第一维的 0…5，对应哪一面由你录入时决定
    public static final int FACE_U = 4;
    public static final int FACE_R = 1;
    public static final int FACE_F = 0;
    public static final int FACE_D = 5;
    public static final int FACE_L = 3;
    public static final int FACE_B = 2;

    /**
     * 颜色值 → Kociemba 面字母
     * 按你实际配色：
     *   蓝→F，红→R，绿→B，橙→L，黄→U，黑→D
     */
    private static final char[] C2C = {
            'F', // BLUE=0
            'R', // RED =1
            'B', // GREEN=2
            'L', // ORANGE=3
            'U', // YELLOW=4
            'D'  // BLACK=5
    };

    /**
     * 按 URFDLB（U, R, F, D, L, B）面顺取出每个面的 9 个格子
     * 这样拼出来的字符串就是 U1…U9,R1…R9,F1…F9,D1…D9,L1…L9,B1…B9
     */
    private static final int[] FACE_ORDER = {
            FACE_U, FACE_R, FACE_F, FACE_D, FACE_L, FACE_B
    };

    /**
     * 将 color[6][9] 按 URFDLB 排序，并映射成 54 字符的 Kociemba 输入
     *
     * @param color 每个 color[faceIdx][i] 都是 0…5 之一，
     *              其中 faceIdx=0..5 分别是你录入时的 U,R,F,D,L,B
     * @return 长度 54 的面转字符串
     */
    public static String toKociembaString(int[][] color) {
        if (color.length != 6)
            throw new IllegalArgumentException("必须有 6 个面");
        StringBuilder sb = new StringBuilder(54);
        // URFDLB 顺序遍历
        for (int face : FACE_ORDER) {
            if (color[face].length != 9)
                throw new IllegalArgumentException("color[" + face + "] 长度必须为9");
            if(face!=FACE_D)
            {
                for (int i = 0; i < 9; i++) {
                    int col = color[face][i];
                    if (col < 0 || col >= C2C.length)
                        throw new IllegalArgumentException("非法颜色值: " + col);
                    sb.append(C2C[col]);
                }
            }
            else {
                for(int i=0;i<3;i++)
                {
                    for(int j=2;j>=0;j--)
                    {
                        int col = color[face][i*3+j];
                        if (col < 0 || col >= C2C.length)
                            throw new IllegalArgumentException("非法颜色值: " + col);
                        sb.append(C2C[col]);
                    }
                }
            }

        }
        return sb.toString();
    }
}


