package moves;

import java.util.ArrayList;
import java.util.List;

public class MoveParser {
    /**
     * 转换魔方动作字符串：
     * - 单个大写字母 X ⇒ 保持 "X"
     * - X2 ⇒ "X X"
     * - X' ⇒ "x"
     *
     * @param moves 原始动作串，动作之间以空格分隔
     * @return 转换后的动作串，动作之间以空格分隔
     */
    public static String convertMoves(String moves) {
        if (moves == null || moves.isEmpty()) {
            return "";
        }
        String[] tokens = moves.trim().split("\\s+");
        List<String> out = new ArrayList<>();
        System.out.println("一共耗费"+tokens.length+"步还原");
        for (String tok : tokens) {
            if (tok.length() == 1 && tok.charAt(0) >= 'A' && tok.charAt(0) <= 'Z') {
                // 普通大写字母
                out.add(tok);
            } else if (tok.length() == 2 && tok.charAt(1) == '2'
                    && tok.charAt(0) >= 'A' && tok.charAt(0) <= 'Z') {
                // X2 → X X
                char face = tok.charAt(0);
                out.add(String.valueOf(face));
                out.add(String.valueOf(face));
            } else if (tok.length() == 2 && tok.charAt(1) == '\''
                    && tok.charAt(0) >= 'A' && tok.charAt(0) <= 'Z') {
                // X' → x (小写)
                char face = tok.charAt(0);
                out.add(String.valueOf(Character.toLowerCase(face)));
            } else {
                // 非法或其它格式，原样加入（也可抛异常）
                out.add(tok);
            }
        }
        return String.join(" ", out);
    }
}
