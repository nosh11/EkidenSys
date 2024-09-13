package com.github.nosh11.ekidensys.util;

public class Unico {
    /**
     * Unicode文字列から元の文字列に変換する ("\u3042" -> "あ")
     * @param unicode
     * @return
     */
    public static String convert(String unicode)
    {
        String[] code = unicode.split("\\\\u");
        int[] codePoints = new int[code.length - 1];
        for (int i = 0; i < codePoints.length; i++)
            codePoints[i] = Integer.parseInt(code[i + 1], 16);
        return new String(codePoints, 0, codePoints.length);
    }
}
