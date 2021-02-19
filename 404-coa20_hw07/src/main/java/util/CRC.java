package util;

import cpu.alu.ALU;
import transformer.Transformer;

/**
 * @CreateTime: 2020-11-23 22:13
 */
public class CRC {
    private static ALU alu = new ALU();
    private static Transformer transformer = new Transformer();

    /**
     * CRC计算器
     * @param data 数据流
     * @param polynomial 多项式
     * @return CheckCode
     */
    public static char[] Calculate(char[] data, String polynomial) {
        String string = String.valueOf(data);
        int length = string.length();
        int polynomialLength = polynomial.length();
        for(int i = 0; i < polynomialLength - 1; ++i){
            string += "0";
        }
        for(int i = 0; i < length; ++i){
            if(string.charAt(i) == '1'){
                if(i == 0){
                    string = alu.xor(string.substring(0, polynomialLength), polynomial) + string.substring(polynomialLength);
                }
                else if(i == length - 1){
                    string = string.substring(0, length - 1) + alu.xor(string.substring(length - 1), polynomial);
                }
                else{
                    string = string.substring(0, i) + alu.xor(string.substring(i, i + polynomialLength), polynomial) + string.substring(i + polynomialLength);
                }
            }
        }
        //TODO
        return string.substring(length).toCharArray();
    }

    /**
     * CRC校验器
     * @param data 接收方接受的数据流
     * @param polynomial 多项式
     * @param CheckCode CheckCode
     * @return 余数
     */
    public static char[] Check(char[] data, String polynomial, char[] CheckCode){
        String string = String.valueOf(data);
        int length = string.length();
        int polynomialLength = polynomial.length();
        string = string + String.valueOf(CheckCode);
        for(int i = 0; i < length; ++i){
            if(string.charAt(i) == '1'){
                if(i == 0){
                    string = alu.xor(string.substring(0, polynomialLength), polynomial) + string.substring(polynomialLength);
                }
                else if(i == length - 1){
                    string = string.substring(0, length - 1) + alu.xor(string.substring(length - 1), polynomial);
                }
                else{
                    string = string.substring(0, i) + alu.xor(string.substring(i, i + polynomialLength), polynomial) + string.substring(i + polynomialLength);
                }
            }
        }
        //TODO
        return string.substring(length).toCharArray();
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

}
