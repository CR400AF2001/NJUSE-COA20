package util;

import cpu.alu.ALU;

/**
 * @CreateTime: 2020-11-23 22:13
 */
public class CRC {

    /**
     * CRC计算器
     * @param data 数据流
     * @param polynomial 多项式
     * @return CheckCode
     */
    public static char[] Calculate(char[] data, String polynomial) {
        int r = polynomial.length();
        String temp = new String(data);
        int i;
        for (i = 0; i < r - 1; i++) temp = temp + "0";
        return CRCHelper(temp.toCharArray(), polynomial);
    }

    /**
     * CRC校验器
     * @param data 接收方接受的数据流
     * @param polynomial 多项式
     * @param CheckCode CheckCode
     * @return 余数
     */
    public static char[] Check(char[] data, String polynomial, char[] CheckCode){
        return CRCHelper((new String(data) + new String(CheckCode)).toCharArray(), polynomial);
    }

    public static char[] CRCHelper(char[] data, String polynomial){
        int r = polynomial.length();
        String temp = new String(data);
        int i;
        String quotient = "";
        ALU alu = new ALU();
        int left = 0;
        for (i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) == '1') {
                left = i;
                break;
            }
        }
        String t = alu.xor(temp.substring(left, r + left), polynomial).substring(0, r);
        for (i = 0; i < t.length(); i++) {
            if (t.charAt(i) != '0') break;
        }
        t = t.substring(i);
        left += r;
        quotient += "1";
        while (i > 1) {
            i--;
            quotient += "0";
        }
        while (left < temp.length()) {
            while (t.length() < r) {
                t += temp.charAt(left);
                left++;
                if (left >= temp.length()) {
                    break;
                }
            }
            if (left >= temp.length() && r != t.length()) {
                break;
            }
            t = alu.xor(t, polynomial).substring(0, r);
            for (i = 0; i < t.length(); i++) {
                if (t.charAt(i) != '0') break;
            }
            t = t.substring(i);
            if (left >= temp.length()) break;
            quotient += "1";
            while (i > 1) {
                i--;
                quotient += "0";
            }
        }
        while (t.length() < r - 1) {
            t = "0" + t;
        }
        return t.toCharArray();

    }
    /**
     * 这个方法仅用于测试，请勿修改
     * @param data
     * @param polynomial
     */
    public static void CalculateTest(char[] data, String polynomial){
        System.out.print(Calculate(data, polynomial));
    }
    /**
     * 这个方法仅用于测试，请勿修改
     * @param data
     * @param polynomial
     */
    public static void CheckTest(char[] data, String polynomial, char[] CheckCode){
        System.out.print(Check(data, polynomial, CheckCode));
    }

    public static void main(String[] args) {
    }
}
