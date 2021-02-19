package cpu.alu;

import transformer.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 取模、逻辑/算术/循环左右移
 */
public class ALU {

    private Transformer transformer = new Transformer();

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    //signed integer mod
    String imod(String src, String dest) {

        String temp = dest;
        if (src.charAt(0) == '0' && dest.charAt(0) == '0') {
            while (largeOrEqual(temp, src)) {
                temp = sub(src, temp);
            }
        }
        else if (src.charAt(0) == '1' && dest.charAt(0) == '1') {
            while (largeOrEqual(src, temp)) {
                temp = sub(src, temp);
            }
        }
        else if (src.charAt(0) == '0' && dest.charAt(0) == '1') {
            while (largeOrEqual("00000000000000000000000000000000", temp)) {
                temp = add(src, temp);
            }
            temp = sub(src, temp);
        }
        else if (src.charAt(0) == '1' && dest.charAt(0) == '0') {
            while (largeOrEqual(temp, "00000000000000000000000000000000")) {
                temp = add(src, temp);
            }
            temp = sub(src, temp);
        }
        return temp;
    }

    String shl(String src, String dest) {
        int shift = Integer.parseInt(transformer.binaryToInt("000000000000000000000000000" + src.substring(src.length()-5)));
        int temp = shift;
        while(shift != 0){
            CF = dest.substring(0, 1);
            dest = dest.substring(1) + "0";
            shift--;
        }
        if(temp == 1){
            if(!dest.substring(0, 1).equals(CF)){
                OF = "1";
            }
            else{
                OF = "0";
            }
        }
		return dest;
    }

    String shr(String src, String dest) {
        int shift = Integer.parseInt(transformer.binaryToInt("000000000000000000000000000" + src.substring(src.length()-5)));
        int temp = shift;
        while(shift != 0){
            CF = dest.substring(dest.length() - 1);
            dest = "0" + dest.substring(0, dest.length() - 1);
            shift--;
        }
        if(temp == 1){
            OF = dest.substring(0, 1);
        }
        return dest;
    }

    String sal(String src, String dest) {
		return shl(src, dest);
    }

    String sar(String src, String dest) {
        int shift = Integer.parseInt(transformer.binaryToInt("000000000000000000000000000" + src.substring(src.length()-5)));
        int temp = shift;
        while(shift != 0){
            CF = dest.substring(dest.length() - 1);
            dest = dest.substring(0, 1) + dest.substring(0, dest.length() - 1);
            shift--;
        }
        if(temp == 1){
            OF = "0";
        }
        return dest;
    }

    String rol(String src, String dest) {
        int shift = Integer.parseInt(transformer.binaryToInt("000000000000000000000000000" + src.substring(src.length()-5)));
		return dest.substring(shift) + dest.substring(0, shift);
    }

    String ror(String src, String dest) {
        int shift = Integer.parseInt(transformer.binaryToInt("000000000000000000000000000" + src.substring(src.length()-5)));
		return dest.substring(dest.length() - shift) + dest.substring(0, dest.length() - shift);
    }



    String add(String src, String dest) {
        CF = "0";
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
        CF = "0";
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

    boolean largeOrEqual(String a, String b) {
        if (a.charAt(0) == '0' && b.charAt(0) == '1') {
            return true;
        }
        if (a.charAt(0) == '1' && b.charAt(0) == '0') {
            return false;
        }
        else{
            for (int i = 1; i < a.length(); i++) {
                int num = a.charAt(i) - b.charAt(i);
                if (num > 0) {
                    return true;
                }
                else if (num < 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
