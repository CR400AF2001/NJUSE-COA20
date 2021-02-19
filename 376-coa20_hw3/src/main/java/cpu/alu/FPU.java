package cpu.alu;

import transformer.Transformer;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    private Transformer transformer = new Transformer();

    /**
     * compute the float add of (a + b)
     **/
    String add(String a,String b){
        if(a == null && b == null){
            return null;
        }
        if(a == null){
            return b;
        }
        if(b == null){
            return a;
        }
        if(a.charAt(0) != b.charAt(0)){
            if(a.charAt(0) == '1'){
                return sub(b, "0" + a.substring(1));
            }
            else if(b.charAt(0) == '1'){
                return sub(a, "0" + b.substring(1));
            }
        }
        if(a.substring(1).equals("0000000000000000000000000000000") && b.substring(1).equals("0000000000000000000000000000000")){
            return "0000000000000000000000000000000";
        }
        if(a.substring(1).equals("0000000000000000000000000000000")){
            return b;
        }
        if(b.substring(1).equals("0000000000000000000000000000000")){
            return a;
        }
        if(a.substring(1,9).equals("11111111")){
            return a;
        }
        if(b.substring(1,9).equals("11111111")){
            return b;
        }
        int a_exp = Integer.parseUnsignedInt(transformer.binaryToInt(a.substring(1,9)));
        int b_exp = Integer.parseUnsignedInt(transformer.binaryToInt(b.substring(1,9)));
        String a_frac;
        String b_frac;
        if(a_exp > b_exp){
            a_frac = b.substring(9);
            b_frac = a.substring(9);
            int temp = a_exp;
            a_exp = b_exp;
            b_exp = temp;
        }
        else{
            a_frac = a.substring(9);
            b_frac = b.substring(9);
        }
        int shift = (b_exp == 0 ? b_exp + 1 : b_exp) - (a_exp == 0 ? a_exp + 1 : a_exp);
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
        while(shift != 0){
            a_frac = "0" + a_frac.substring(0, a_frac.length() - 1);
            shift--;
        }
        OF = "0";
        CF = "0";
        String res_frac = intAdd(a_frac, b_frac);
        res_frac = CF + res_frac;
		return internal_normalize(a.substring(0,1), b_exp, res_frac);
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a,String b){
        if(a == null && b == null){
            return null;
        }
        if(a == null){
            return b;
        }
        if(b == null){
            return a;
        }
        if(a.charAt(0) != b.charAt(0)){
            return add(a, a.substring(0, 1) + b.substring(1));
        }
        if(a.substring(1).equals("0000000000000000000000000000000") && b.substring(1).equals("0000000000000000000000000000000")){
            return "0000000000000000000000000000000";
        }
        if(a.substring(1).equals("0000000000000000000000000000000")){
            return b;
        }
        if(b.substring(1).equals("0000000000000000000000000000000")){
            return a;
        }
        if(a.substring(1,9).equals("11111111")){
            return a;
        }
        if(b.substring(1,9).equals("11111111")){
            return b;
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
        if(a_exp > b_exp){
            int shift = (a_exp == 0 ? a_exp + 1 : a_exp) - (b_exp == 0 ? b_exp + 1 : b_exp);
            while(shift != 0){
                b_frac = "0" + b_frac.substring(0, b_frac.length() - 1);
                shift--;
            }
        }
        else{
            int shift = (b_exp == 0 ? b_exp + 1 : b_exp) - (a_exp == 0 ? a_exp + 1 : a_exp);
            while(shift != 0){
                a_frac = "0" + a_frac.substring(0, a_frac.length() - 1);
                shift--;
            }
        }

        OF = "0";
        CF = "0";
        String res_frac;
        String sign = b.substring(0,1);
        if(largeOrEqual(a_frac,b_frac)){
            res_frac = intSub(b_frac, a_frac);
        }
        else{
            res_frac = intSub(a_frac, b_frac);
            if(sign.equals("1")){
                sign = "0";
            }
            else{
                sign = "1";
            }
        }
        res_frac = "0" + res_frac;
        return internal_normalize(sign, Math.max(a_exp, b_exp), res_frac);
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

    String intAdd(String src, String dest) {
        StringBuilder result = new StringBuilder();
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
        if(src.charAt(0) == dest.charAt(0) && result.charAt(0) != dest.charAt(0)){
            OF = "1";
        }
        else{
            OF = "0";
        }
        return result.toString();
    }

    String intSub(String src, String dest) {
        StringBuilder result = new StringBuilder();
        int a = 0;
        int b = 0;
        int cf = 0;
        for(int i = dest.length() - 1; i >= 0; --i){
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


}
