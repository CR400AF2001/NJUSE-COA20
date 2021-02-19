package transformer;


import java.util.Arrays;

//
public class Transformer {
    /**
     * Integer to binaryString
     *
     * @param numStr to be converted
     * @return result
     */
    public String intToBinary(String numStr) {
        StringBuilder result = new StringBuilder();
        int n = Integer.parseInt(numStr);
        if(n == 0){
            return "00000000000000000000000000000000";
        }
        int num = n;
        if(num < 0){
            num = -num;
        }
        int max = 1;
        while(num >= max){
            max *= 2;
        }
        max /= 2;
        while(max > 0){
            if(num >= max){
                result.append("1");
                num -= max;
            }
            else{
                result.append("0");
            }
            max /= 2;
        }
        result = result.reverse();
        for(int i = result.length(); i < 32; i++){
            result.append("0");
        }
        result = result.reverse();
        if(n > 0){
            return result.toString();
        }
        else{
            char[] resultChar = result.toString().toCharArray();
            boolean flag = false;
            for (int i = result.length() - 1; i >= 0; i--){
                if(!flag){
                    if(resultChar[i] == '1'){
                        flag = true;
                    }
                }
                else{
                    if(resultChar[i] == '1'){

                        resultChar[i] = '0';

                    }
                    else{
                        resultChar[i] = '1';
                    }
                }
            }
            return String.valueOf(resultChar);
        }
    }

    /**
     * BinaryString to Integer
     *
     * @param binStr : Binary string in 2's complement
     * @return :result
     */
    public String binaryToInt(String binStr) {
        int result = 0;
        boolean sign = false;
        if(binStr.charAt(0) == '1'){
            sign = true;
            boolean flag = false;
            char[] strChar = binStr.toCharArray();
            for (int i = 31; i >= 0; i--){
                if(!flag){
                    if(strChar[i] == '1'){
                        flag = true;
                    }
                }
                else{
                    if(strChar[i] == '1'){
                        strChar[i] = '0';
                    }
                    else{
                        strChar[i] = '1';
                    }
                }
            }
            binStr = String.valueOf(strChar);
        }
        for(int i = 0; i < 31; ++i){
            if(binStr.charAt(31 - i) == '1'){
                result += Math.pow(2,i);
            }
        }
        if(sign){
            result = -result;
        }
        return String.valueOf(result);
    }

    /**
     * Float true value to binaryString
     * @param floatStr : The string of the float true value
     * */
    public String floatToBinary(String floatStr) {
        float f = Float.parseFloat(floatStr);
        if(Float.isNaN(f)){
            return "NaN";
        }
        else if(Float.isInfinite(f)){
            if(f > 0){
                return "+Inf";
            }
            else{
                return "-Inf";
            }
        }
        else if(f == 0){
            return "00000000000000000000000000000000";
        }
        StringBuilder result = new StringBuilder();
        if(f < 0){
            result.append("1");
            f = -f;
        }
        else{
            result.append("0");
        }
        int num = 127;
        if(f >= (float) 2.0){
            while(f >= (float) 2.0 && num < 254){
                num++;
                f /= 2;
            }
        }
        else if(f < (float) 1.0){
            while(f < (float) 1.0 && num > 0){
                num--;
                f *= 2;
            }
        }
        result.append(intToBinary(String.valueOf(num)).substring(24));
        float time = (float) 0.5;
        if(num != 0){
            f -= 1.0;
            for(int i = 0; i < 23; i++){
                if(f >= time){
                    result.append("1");
                    f -= time;
                }
                else{
                    result.append("0");
                }
                time /= 2;
            }
        }
        else{
            f /= 2;
            for(int i = 0; i < 23; i++){
                if(f >= time){
                    result.append("1");
                    f -= time;
                }
                else{
                    result.append("0");
                }
                time /= 2;
            }
        }
        return result.toString();
    }

    /**
     * Binary code to its float true value
     * */
    public String binaryToFloat(String binStr) {
        if(binStr.substring(1).equals("0000000000000000000000000000000")){
            return "0.0";
        }
        else if(binStr.substring(1).equals("1111111100000000000000000000000")){
            if(binStr.charAt(0) == '0'){
                return "+Inf";
            }
            else{
                return "-Inf";
            }
        }
        else if(binStr.substring(1, 9).equals("11111111")){
            return "NaN";
        }
        double result = 0;
        if(!binStr.substring(1, 9).equals("00000000")){
            result += 1.0;
        }
        double time = 0.5;
        for(int i = 9; i < 32; ++i){
            if(binStr.charAt(i) == '1'){
                result += time;
            }
            time /= 2;
        }
        if(binStr.substring(1, 9).equals("00000000")){
            result *= Math.pow(2, -126);
        }
        else{
            result *= Math.pow(2, Integer.parseInt(binaryToInt("000000000000000000000000" + binStr.substring(1, 9))) - 127);
        }
        if(binStr.charAt(0) == '1'){
            result = -result;
        }
        return String.valueOf(result);
    }

    /**
     * The decimal number to its NBCD code
     * */
    public String decimalToNBCD(String decimal) {
        StringBuilder result = new StringBuilder();
        int n = Integer.parseInt(decimal);
        int num = n;
        if(n == 0){
            return "11000000000000000000000000000000";
        }
        if(n < 0){
            num = -num;
        }
        while(num > 0){
            switch (num % 10){
                case 0:
                    result.append("0000");
                    break;
                case 1:
                    result.append("1000");
                    break;
                case 2:
                    result.append("0100");
                    break;
                case 3:
                    result.append("1100");
                    break;
                case 4:
                    result.append("0010");
                    break;
                case 5:
                    result.append("1010");
                    break;
                case 6:
                    result.append("0110");
                    break;
                case 7:
                    result.append("1110");
                    break;
                case 8:
                    result.append("0001");
                    break;
                case 9:
                    result.append("1001");
                    break;
            }
            num /= 10;
        }
        for(int i = result.length() / 4; i < 7; i++){
            result.append("0000");
        }
        result = result.reverse();
        if(n < 0){
            return "1101" + result.toString();
        }
        else{
            return "1100" + result.toString();
        }
    }

    /**
     * NBCD code to its decimal number
     * */
    public String NBCDToDecimal(String NBCDStr) {
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        int length = NBCDStr.length() / 4;
        for(int i = 0; i < length; ++i){
            String substring = NBCDStr.substring(4 * i, 4 * i + 4);
            if(substring.equals("1100")){
                continue;
            }
            else if(substring.equals("1101")){
                result.append("-");
            }
            else{
                switch (substring){
                    case "0000":
                        if(flag || i == length - 1){
                            result.append("0");
                        }
                        break;
                    case "0001":
                        result.append("1");
                        flag = true;
                        break;
                    case "0010":
                        result.append("2");
                        flag = true;
                        break;
                    case "0011":
                        result.append("3");
                        flag = true;
                        break;
                    case "0100":
                        result.append("4");
                        flag = true;
                        break;
                    case "0101":
                        result.append("5");
                        flag = true;
                        break;
                    case "0110":
                        result.append("6");
                        flag = true;
                        break;
                    case "0111":
                        result.append("7");
                        flag = true;
                        break;
                    case "1000":
                        result.append("8");
                        flag = true;
                        break;
                    case "1001":
                        result.append("9");
                        flag = true;
                        break;
                }
            }
        }
        return result.toString();
    }




}
