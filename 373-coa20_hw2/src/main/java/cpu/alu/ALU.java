package cpu.alu;

import transformer.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减与逻辑运算
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    //add two integer
    String add(String src, String dest) {
        StringBuilder result = new StringBuilder();
        for(int i = 31; i >= 0; --i){
            int sum = (src.charAt(i) - '0') + (dest.charAt(i) - '0') + (CF.charAt(0) - '0');
            if (sum == 3) {
                result.append("1");
                CF = "1";
            }
            else if (sum == 2) {
                result.append("0");
                CF = "1";
            }
            else if (sum == 1) {
                result.append("1");
                CF = "0";
            }
            else if (sum == 0) {
                result.append("0");
                CF = "0";
            }
        }
        result.reverse();
        if(src.charAt(0) == dest.charAt(0) && result.charAt(0) != dest.charAt(0)){
            OF = "1";
        }
        else{
            OF = "0";
        }
        return result.toString();
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        StringBuilder result = new StringBuilder();
        int a = 0;
        int b = 0;
        int cf = 0;
        for(int i = 31; i >= 0; --i){
            int sum = (dest.charAt(i) - '0') - (src.charAt(i) - '0') - (CF.charAt(0) - '0');
            if (sum == 1) {
                result.append("1");
                CF = "0";
            }
            else if (sum == 0) {
                result.append("0");
                CF = "0";
            }
            else if (sum == -1) {
                result.append("1");
                CF = "1";
            }
            else if (sum == -2) {
                result.append("0");
                CF = "1";
            }
        }
        result.reverse();
        if(src.charAt(0) != dest.charAt(0) && result.charAt(0) != dest.charAt(0)){
            OF = "1";
        }
        else{
            OF = "0";
        }
        return result.toString();
	}

    String and(String src, String dest) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < 32; ++i){
            if(src.charAt(i) == '1' && dest.charAt(i) == '1'){
                result.append("1");
            }
            else{
                result.append("0");
            }
        }
        return result.toString();
    }

    String or(String src, String dest) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < 32; ++i){
            if(src.charAt(i) == '1' || dest.charAt(i) == '1'){
                result.append("1");
            }
            else{
                result.append("0");
            }
        }
        return result.toString();
    }

    String xor(String src, String dest) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < 32; ++i){
            if(src.charAt(i) == dest.charAt(i)){
                result.append("0");
            }
            else{
                result.append("1");
            }
        }
        return result.toString();
    }

}
