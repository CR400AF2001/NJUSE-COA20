package cpu.alu;

import transformer.Transformer;
import util.BinaryIntegers;
import util.IEEE754Float;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    private Transformer transformer = new Transformer();
    /**
     * compute the float mul of a * b
     */
    String mul(String a, String b) {
        if(a.equals(IEEE754Float.NaN) || a.matches(IEEE754Float.NaN) || b.equals(IEEE754Float.NaN) || b.matches(IEEE754Float.NaN)){
            return IEEE754Float.NaN;
        }
        if((a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO)) && (b.equals(IEEE754Float.P_INF) || b.equals(IEEE754Float.N_INF))){
            return IEEE754Float.NaN;
        }
        if((b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO)) && (a.equals(IEEE754Float.P_INF) || a.equals(IEEE754Float.N_INF))){
            return IEEE754Float.NaN;
        }
        if((a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO)) && a.charAt(0) == b.charAt(0)){
            return IEEE754Float.P_ZERO;
        }
        if((a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO)) && a.charAt(0) != b.charAt(0)){
            return IEEE754Float.N_ZERO;
        }
        if((b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO)) && a.charAt(0) == b.charAt(0)){
            return IEEE754Float.P_ZERO;
        }
        if((b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO)) && a.charAt(0) != b.charAt(0)){
            return IEEE754Float.N_ZERO;
        }
        if((a.equals(IEEE754Float.P_INF) || a.equals(IEEE754Float.N_INF)) && a.charAt(0) == b.charAt(0)){
            return IEEE754Float.P_INF;
        }
        if((a.equals(IEEE754Float.P_INF) || a.equals(IEEE754Float.N_INF)) && a.charAt(0) != b.charAt(0)){
            return IEEE754Float.N_INF;
        }
        if((b.equals(IEEE754Float.P_INF) || b.equals(IEEE754Float.N_INF)) && a.charAt(0) == b.charAt(0)){
            return IEEE754Float.P_INF;
        }
        if((b.equals(IEEE754Float.P_INF) || b.equals(IEEE754Float.N_INF)) && a.charAt(0) != b.charAt(0)){
            return IEEE754Float.N_INF;
        }
        int a_exp = Integer.parseUnsignedInt(transformer.binaryToInt(a.substring(1,9)));
        int b_exp = Integer.parseUnsignedInt(transformer.binaryToInt(b.substring(1,9)));
        String a_frac = a.substring(9);
        String b_frac = b.substring(9);
        if(a_exp != 0){
            a_frac = "01" + a_frac;
        }
        else{
            a_frac = "00" + a_frac;
        }
        if(b_exp != 0){
            b_frac = "01" + b_frac;
        }
        else{
            b_frac = "00" + b_frac;
        }

        if (a_exp == 0)
            a_exp++;
        if (b_exp == 0)
            b_exp++;

        int exp = a_exp + b_exp - 127 + 2;
        if(exp >= 255){
            if(a.charAt(0) == b.charAt(0)){
                return IEEE754Float.P_INF;
            }
            if(a.charAt(0) != b.charAt(0)){
                return IEEE754Float.N_INF;
            }
        }
        if(exp < 0){
            return IEEE754Float.P_ZERO;
        }

        String res_frac = intMul(a_frac, b_frac).substring(0,29);

        String sign;
        if(a.charAt(0) == b.charAt(0)){
            sign = "0";
        }
        else{
            sign = "1";
        }

        return internal_normalize(sign, exp, res_frac);
    }

    /**
     * compute the float mul of a / b
     */
    String div(String a, String b) {
        if(a.equals(IEEE754Float.NaN) || a.matches(IEEE754Float.NaN) || b.equals(IEEE754Float.NaN) || b.matches(IEEE754Float.NaN)){
            return IEEE754Float.NaN;
        }
        if((a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO)) && !(b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO))){
            if(a.charAt(0) == b.charAt(0)){
                return IEEE754Float.P_ZERO;
            }
            else{
                return IEEE754Float.N_ZERO;
            }
        }
        if(!(a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO)) && (b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO))){
            throw new java.lang.ArithmeticException();
        }
        if((a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO)) && (b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO))){
            return IEEE754Float.NaN;
        }
        if((a.equals(IEEE754Float.P_INF) || a.equals(IEEE754Float.N_INF)) && (b.equals(IEEE754Float.P_INF) || b.equals(IEEE754Float.N_INF))){
            return IEEE754Float.NaN;
        }
        if(a.equals(IEEE754Float.P_INF) || a.equals(IEEE754Float.N_INF)){
            if(a.charAt(0) == b.charAt(0)){
                return IEEE754Float.P_INF;
            }
            else{
                return IEEE754Float.N_INF;
            }
        }
        if(b.equals(IEEE754Float.P_INF) || b.equals(IEEE754Float.N_INF)){
            if(a.charAt(0) == b.charAt(0)){
                return IEEE754Float.P_ZERO;
            }
            else{
                return IEEE754Float.N_ZERO;
            }
        }
        int a_exp = Integer.parseUnsignedInt(transformer.binaryToInt(a.substring(1,9)));
        int b_exp = Integer.parseUnsignedInt(transformer.binaryToInt(b.substring(1,9)));
        String a_frac = a.substring(9);
        String b_frac = b.substring(9);
        if(a_exp != 0){
            a_frac = "1" + a_frac + "0000";
        }
        else{
            a_frac = "0" + a_frac + "0000";
        }
        if(b_exp != 0){
            b_frac = "1" + b_frac + "0000";
        }
        else{
            b_frac = "0" + b_frac + "0000";
        }

        if (a_exp == 0)
            a_exp++;
        if (b_exp == 0)
            b_exp++;

        int exp = a_exp - b_exp + 127 + 2;
        if(exp >= 255){
            if(a.charAt(0) == b.charAt(0)){
                return IEEE754Float.P_INF;
            }
            if(a.charAt(0) != b.charAt(0)){
                return IEEE754Float.N_INF;
            }
        }
        if(exp < 0){
            return IEEE754Float.P_ZERO;
        }

        String res_frac = intDiv(a_frac, b_frac);

        String sign;
        if(a.charAt(0) == b.charAt(0)){
            sign = "0";
        }
        else{
            sign = "1";
        }

        return internal_normalize_long(sign, exp, res_frac);
    }


    public static void main(String[] args) {
    }


    public String intMul (String src, String dest){
        if(src.equals(BinaryIntegers.ZERO) || dest.equals(BinaryIntegers.ZERO)){
            return BinaryIntegers.ZERO;
        }
        String x = dest + "00000000000000000000000000000";
        String _x = reverse(dest) + "00000000000000000000000000000";
        String y = "0000000000000000000000000000" + src + "0";
        for(int i = 0; i < src.length(); ++i){
            if(y.substring(y.length() - 2).equals("10")){
                y = intAdd(y, _x);
            }
            else if(y.substring(y.length() - 2).equals("01")){
                y = intAdd(y, x);
            }
            y = y.substring(0, 1) + y.substring(0, y.length() - 1);
        }
        return y;
    }

    public String intDiv(String operand1, String operand2) {
        String quotient = "00000000000000000000000000000";
        String divisor = "0" + operand2;
        String remainder = "0" + operand1;
        for(int i = 0; i < operand1.length(); ++i){
            if(largeOrEqual(remainder,divisor)){
                quotient = quotient.substring(1) + "1";
                remainder = intAdd(remainder, reverse(divisor));
            }
            else{
                quotient = quotient.substring(1) + "0";
            }
            remainder = remainder.substring(1) + quotient.substring(0, 1);
        }
        return "0" + quotient.substring(0,quotient.length() - 1) + remainder.substring(0,quotient.length() - 1);
    }

    String intAdd(String src, String dest) {
        StringBuilder result = new StringBuilder();
        String CF = "0";
        for(int i = dest.length() - 1; i >= 0; --i){
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
        return result.toString();
    }

    String reverse(String s) {
        char[] resultChar = s.toCharArray();
        boolean flag = false;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (!flag) {
                if (resultChar[i] == '1') {
                    flag = true;
                }
            }
            else {
                if (resultChar[i] == '1') {

                    resultChar[i] = '0';

                } else {
                    resultChar[i] = '1';
                }
            }
        }
        return String.valueOf(resultChar);
    }


    boolean largeOrEqual(String a, String b) {
        for (int i = 0; i < a.length(); i++) {
            int num = a.charAt(i) - b.charAt(i);
            if (num > 0) {
                return true;
            }
            else if (num < 0) {
                return false;
            }
        }
        return true;
    }

    String internal_normalize(String sign, int exp, String sig_grs)
    {


        if (largeOrEqual(sig_grs, "10000000000000000000000000000") || exp < 0)
        {
            while ((largeOrEqual(sig_grs, "10000000000000000000000000000") && exp < 0xff) || (exp < 0))
            {

                sig_grs = "0" + sig_grs.substring(0, sig_grs.length() - 1);
                exp++;

            }

            if (exp >= 0xff)
            {
                return sign + "1111111100000000000000000000000";
            }
            if (exp == 0)
            {
                sig_grs = "0" + sig_grs.substring(0, sig_grs.length() - 1);
            }
            if (exp < 0)
            {
                return "00000000000000000000000000000000";
            }
        }
        else if (!largeOrEqual(sig_grs, "01000000000000000000000000000") && exp > 0)
        {
            while (!largeOrEqual(sig_grs, "01000000000000000000000000000") && exp > 0)
            {
                sig_grs = sig_grs.substring(1) + "0";
                exp--;
            }
            if (exp == 0)
            {
                sig_grs = "0" + sig_grs.substring(0, sig_grs.length() - 1);
            }
        }
        else if (exp == 0 && sig_grs.charAt(1) == '1')
        {
            exp++;
        }

        String expString = transformer.intToBinary(String.valueOf(exp));
        return sign + expString.substring(expString.length() - 8) + sig_grs.substring(2,25);
    }

    String internal_normalize_long(String sign, int exp, String sig_grs)
    {
        if (!largeOrEqual(sig_grs, "100000000000000000000000000000000000000000000000000000000") && exp > 0)
        {
            while (!largeOrEqual(sig_grs, "100000000000000000000000000000000000000000000000000000000") && exp > 0)
            {
                sig_grs = sig_grs.substring(1) + "0";
                exp--;
            }
            if (exp == 0)
            {
                sig_grs = "0" + sig_grs.substring(0, sig_grs.length() - 1);
            }
        }
        else if (exp == 0 && sig_grs.charAt(1) == '1')
        {
            exp++;
        }

        String expString = transformer.intToBinary(String.valueOf(exp));
        return sign + expString.substring(expString.length() - 8) + sig_grs.substring(1,24);
    }

}
