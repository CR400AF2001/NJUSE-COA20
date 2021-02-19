package cpu.alu;

import cpu.CPU_State;
import cpu.registers.EFlag;
import transformer.Transformer;
import util.BinaryIntegers;
import util.IEEE754Float;

import java.util.Arrays;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减乘除
 */
public class ALU {

//    // 模拟寄存器中的进位标志位
//    private String CF = "0";
//
//    // 模拟寄存器中的溢出标志位
//    private String OF = "0";

    private EFlag eflag =  (EFlag) CPU_State.eflag;

    private Transformer transformer = new Transformer();

	/**
	 * 返回两个二进制整数的乘积(结果低位截取后32位)
	 * @param src 32-bits
	 * @param dest 32-bits
	 * @return 32-bits
	 */
	public String mul (String src, String dest){
        int length = 32;  // length为数据长度
        String X = impleDigits(src, length);
        dest = impleDigits(dest, length);
        String negX = oneAdder(negation(X)).substring(1);  //取反加一,去掉第一位溢出位
        String product = getAllZeros(length)+dest;
        int Y1 = 0;
        int Y2 = product.charAt(2*length-1)-'0';
        for(int i=0;i<length;i++){
            switch(Y1-Y2){
                case 1:
                    product = adder(product.substring(0,length), X, '0', length) + product.substring(length);
                    break;
                case -1:
                    product = adder(product.substring(0,length), negX, '0', length) + product.substring(length);
                    break;
            }
            product = product.substring(0,1) + product.substring(0, product.length()-1);  //算数右移
            Y1 = Y2;  //更新两个Y
            Y2 = product.charAt(2*length-1)-'0';
        }
        String higher = product.substring(0, length);
        String lower = product.substring(length);  // 直接截断高32位，取低32位作为结果返回
        eflag.setOF(false);
        for(char c: higher.toCharArray()){
            if(c == '1'){
                eflag.setOF(true);  // 如果高32位有1判定为溢出
                break;
            }
        }
        return lower;
    }

    /**
     * 返回两个二进制整数的除法结果 operand1 ÷ operand2
     * @param operand1 32-bits
     * @param operand2 32-bits
     * @return 65-bits overflow + quotient + remainder
     */
    public String div(String operand1, String operand2) {
        int length = 64;
        String quotient = "";
        String remainder = "";
        boolean op1Zero = isZero( operand1 );
        boolean op2Zero = isZero( operand2 );

        if(op1Zero && op2Zero){     // If X=0 and X=0: NaN
            return BinaryIntegers.NaN;
        }

        if(op1Zero && !op2Zero){    // If X=0 and Y≠0: 0
            return "0" + BinaryIntegers.ZERO + BinaryIntegers.ZERO;
        }

        if(!op1Zero && op2Zero){    // If X≠0 and Y=0: exception
            throw new ArithmeticException( );
        }

        if( isZero( operand1.substring( 1 )) && operand1.charAt( 0 )=='1' && isNegativeOne( operand2 ) ){   //   -2^23 / -1  溢出
            return "1" + operand1 + BinaryIntegers.ZERO;
        }

        String product = impleDigits(operand1, length);  //符号扩展
        if(product.charAt(0)==operand2.charAt(0)) product = adder(product.substring(0,length/2),negation(  operand2), '1',length/2)+product.substring(length/2);
        else product = adder(product.substring(0,length/2), operand2,'0', length/2)+product.substring(length/2);
        for(int i=0;i<length/2;i++){
            if(product.charAt(0)==operand2.charAt(0)){
                quotient += "1";
                product = leftShift(product,1);
                product = adder(product.substring(0,length/2),negation(  operand2), '1',length/2)+product.substring(length/2);
            }else{
                quotient += "0";
                product = leftShift(product,1);
                product = adder(product.substring(0,length/2),operand2,'0',length/2)+product.substring(length/2);
            }
        }

        quotient = quotient.substring(1);
        if(product.charAt(0)==operand2.charAt(0)) quotient = quotient + "1";
        else quotient = quotient + "0";
        if(quotient.charAt(0)=='1') quotient = oneAdder(quotient).substring(1);
        remainder = product.substring(0,length/2);
        if(remainder.charAt(0)!=operand1.charAt(0)){
            if(operand1.charAt(0)==operand2.charAt(0)){
                remainder = adder(remainder,operand2,'0',length/2);
            }else{
                remainder = adder(remainder,negation(  operand2),'1', length/2);
            }
        }

        if(isZero(  adder( operand2 , impleDigits(remainder,length/2),'0', length/2))){
            quotient =  add( impleDigits( quotient, length/2), BinaryIntegers.NegativeOne );
            remainder = BinaryIntegers.ZERO;
            return "0" + quotient+remainder;
        }

        return "0" + impleDigits(quotient,length/2) + impleDigits(remainder,length/2);  //溢出情况前面判断了，补齐位数
    }

    /**
     * add one to the operand
     * @param operand the operand
     * @return result after adding, the first position means overflow, and the remains means the result
     */
    private String oneAdder (String operand){
        int len = operand.length();
        StringBuffer temp = new StringBuffer(operand);
        temp = temp.reverse();
        int [] num = new int[len];
        for(int i=0;i<len;i++) num[i] = temp.charAt(i)-'0';  //先转化为反转后对应的int数组
        int bit = 0x0;
        int carry = 0x1;
        char []res = new char[len];
        for(int i=0;i<len;i++){
            bit = num[i] ^ carry;
            carry = num[i] & carry;
            res[i] = (char)('0' + bit);
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return ""+(result.charAt(0)==operand.charAt(0) ? '0' : '1')+result;
    }

    /**
     * get a string of all '0'
     * @param length
     * @return all '0' of length
     */
    private String getAllZeros(int length){
        StringBuffer res = new StringBuffer();
        for(int i=0;i<length;i++) res.append('0');
        return res.toString();
    }

    //add two integer
    public String add(String src, String dest) {
        // add two integer in 2's complement code
        String result = adder(src, dest, '0', 32);  //注意有进位不等于溢出，溢出要另外判断。已经被封装在上一步

        return result;
    }

    //sub two integer
    // dest - src
    public String sub(String src, String dest) {
        return adder(dest, negation(src), '1', 32);  //不能直接取反加一（有可能取反加一溢出），必须这一步改初始carry位‘0’为‘1’。这样写完全模拟ppt上面的做法，但是还是可能取反的时候就溢出，先加一反而不溢出，所以其实还是不完美。注意有进位不等于溢出，溢出要另外判断。
    }

    //signed integer mod
    String imod(String src, String dest) {
        String temp = dest;
        if (src.charAt(0) == '0' && dest.charAt(0) == '0') {
            while (isLarger(temp, src) || temp.equals(src)) {
                temp = sub(src, temp);
            }
        }
        if (src.charAt(0) == '0' && dest.charAt(0) == '1') {
            while (isLarger("00000000000000000000000000000000", temp) || temp.equals("00000000000000000000000000000000")) {
                temp = add(src, temp);
            }
            temp = sub(src, temp);
        }

        if (src.charAt(0) == '1' && dest.charAt(0) == '0') {
            while (isLarger(temp, "00000000000000000000000000000000") || temp.equals("00000000000000000000000000000000")) {
                temp = add(src, temp);
            }
            temp = sub(src, temp);
        }
        if (src.charAt(0) == '1' && dest.charAt(0) == '1') {
            while (isLarger(src, temp) || temp.equals(src)) {
                temp = sub(src, temp);

            }
        }

        return temp;
    }

    public String and(String src, String dest) {
        char[] result = new char[32];
        for (int i = 0; i < src.length(); i++) {
            char srcItem = src.charAt(i);
            char desItem = dest.charAt(i);
            result[i] = (char) (srcItem & desItem);

        }
        StringBuilder resultStr = new StringBuilder(32);
        for (char s : result) {
            resultStr.append(s);
        }
        return resultStr.toString();
    }

    public String or(String src, String dest) {
        char[] result = new char[32];
        for (int i = 0; i < src.length(); i++) {
            char srcItem = src.charAt(i);
            char destItem = dest.charAt(i);
            result[i] = (char) (srcItem | destItem);
        }
        StringBuilder resultStr = new StringBuilder(32);
        for (char s : result) {
            resultStr.append(s);
        }
        return resultStr.toString();
    }

    public String xor(String src, String dest) {
        char[] result = new char[32];
        for (int i = 0; i < src.length(); i++) {
            char srcItem = src.charAt(i);
            char destItem = dest.charAt(i);
            if (srcItem == destItem)
                result[i] = '0';
            else
                result[i] = '1';
        }
        StringBuilder resultStr = new StringBuilder(32);
        for (char s : result) {
            resultStr.append(s);
        }
        return resultStr.toString();
    }

    /**
     * fix bug(2019-10-13):
     * 根据标准i386手册(p384)，所有shift操作的移位操作数仅使用后5位，其他位数直接舍去
     * 对应测试用例已修复(testShift2 testShift6)
     * @param src
     * @param dest
     * @return
     */
    String shl(String src, String dest) {
        return sal(src, dest);
    }

    String shr(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        char[] fill = new char[32];
        Arrays.fill(fill, '0');
        return new String(fill).concat(dest).substring(32 - shift, 64 - shift);
    }

    String sal(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
		char[] fill = new char[32];
		Arrays.fill(fill, '0');
		return dest.substring(shift).concat(new String(fill)).substring(0, 32);
    }

    String sar(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        char[] fill = new char[32];
        Arrays.fill(fill, dest.charAt(0));
        return new String(fill).concat(dest).substring(32 - shift, 64 - shift);
    }

    String rol(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        return dest.substring(shift).concat(dest.substring(0, shift));
    }

    String ror(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        return dest.substring(32 - shift).concat(dest.substring(0, 32 - shift));
    }


    private String adder(String operand1, String operand2, char c, int length) {
        operand1 = impleDigits(operand1, length);
        operand2 = impleDigits(operand2, length);
        String res = carry_adder(operand1, operand2, c, length);
        eflag.setOF(addOverFlow(operand1, operand2, res).equals("1"));
        return res;  //注意有进位不等于溢出，溢出要另外判断
    }

    String serialCarryAdder(String adder1,String adder2){
        StringBuilder result=new StringBuilder();
        char carry='0';
        String output="00";
        for (int i = adder1.length()-1; i >=0 ; i--) {
            carry=output.charAt(0);
            output=fullAdder(adder1.charAt(i),adder2.charAt(i), carry);
            result.append(output.charAt(1));
        }
        // 溢出、进位判断
        eflag.setCF(output.charAt(1) == '1');
        eflag.setOF(((carry-'0')^(output.charAt(1))) == '1');   //Cn和Cn-1取异或

        return result.reverse().toString();
    }

    String carryLookAheadAdder_8(String adder1,String adder2, char carryIn){
        char[] xChars=adder1.toCharArray();
        char[] yChars=adder2.toCharArray();
        int[] x=new int[8];
        int[] y=new int[8];

        for (int i = 7; i >=0 ; i--) {
            x[i]=xChars[7-i]-'0';
            y[i]=yChars[7-i]-'0';
        }

        //1个时间单位
        int[] p=new int[8];
        int[] g=new int[8];
        for (int i = 0; i <8 ; i++) {
            p[i]=x[i]|y[i];
            g[i]=x[i]&y[i];
        }

        //两个时间单位
        int[] c=new int[8];//每一位的carryOut
        for (int i = 0; i <8 ; i++) {
            c[i]=g[i];
            int temp=1;
            for (int j = i ;j >=1 ; j--) {
                temp=temp&p[j];
                c[i]=c[i]|(temp&g[j-1]);
            }
            c[i]=c[i]|(temp&p[0]&carryIn);
        }

        //前三个时间单位里同时进行的异或运算
        int[] xorForXY=new int[8];
        for (int i = 0; i <8 ; i++) {
            xorForXY[i]=x[i]^y[i];
        }

        //第4到第6时间单位
        StringBuilder result=new StringBuilder();
        result.append(xorForXY[0]^(carryIn-'0'));
        for (int i = 1; i <8 ; i++) {
            result.append(xorForXY[i]^c[i-1]);
        }
        result.reverse();
        result.append(c[7]);//最后的carryOut
        return result.toString();
    }

    String connectedCLAAdder(String adder1,String adder2){
        String result="";

        String CLAresult=carryLookAheadAdder_8(adder1.substring(24,32),adder2.substring(24,32),'0');
        result=CLAresult.substring(0,8)+result;

        CLAresult=carryLookAheadAdder_8(adder1.substring(16,24),adder2.substring(16,24),CLAresult.charAt(8));
        result=CLAresult.substring(0,8)+result;

        CLAresult=carryLookAheadAdder_8(adder1.substring(8,16),adder2.substring(8,16),CLAresult.charAt(8));
        result=CLAresult.substring(0,8)+result;

        CLAresult=carryLookAheadAdder_8(adder1.substring(0,8),adder2.substring(0,8),CLAresult.charAt(8));
        result=CLAresult.substring(0,8)+result;

        eflag.setCF(CLAresult.charAt(8) == '1');
        int x=adder1.charAt(0)-'0';
        int y=adder2.charAt(0)-'0';
        int s=result.charAt(0)-'0';
        eflag.setOF(((x&y&~s)|(~x&~y&s)) == 1); //不好找Cn-1，所以计算繁琐一点
        return result;

    }

    /**
     * given a length, make operand to that digits considering the sign
     *
     * @param operand given num
     * @param length  make complete
     * @return completed string
     */
    private String impleDigits(String operand, int length) {
        int len = length - operand.length();
        char imple = operand.charAt(0);
        StringBuffer res = new StringBuffer(new StringBuffer(operand).reverse());
        for (int i = 0; i < len; i++) {
            res = res.append(imple);
        }
        return res.reverse().toString();
    }

    /**
     * test if add given two nums overflow
     *
     * @param operand1 first
     * @param operand2 second
     * @param result   result after the adding
     * @return 1 means overflow, 0 means not
     */
    private String addOverFlow(String operand1, String operand2, String result) {
        int X = operand1.charAt(0) - '0';
        int Y = operand2.charAt(0) - '0';
        int S = result.charAt(0) - '0';
        return "" + ((~X & ~Y & S) | (X & Y & ~S));  //两个操作数符号相同，和结果符号不同，则溢出
    }

    /**
     * add two nums with the length of given length which could be divided by 4, and the result's first bit presents the overflow
     * different from the {@code adder} method, the result's first bit presents whether it generates the carry
     *
     * @param operand1 first
     * @param operand2 second
     * @param c        original carray
     * @param length   given length
     * @return result, and the result's first bit presents the carry
     */
    private String carry_adder(String operand1, String operand2, char c, int length) {
        operand1 = impleDigits(operand1, length);
        operand2 = impleDigits(operand2, length);
        String res = "";
        char carry = c;
        for (int i = length - 1; i >= 0; i--) {  //这里length不一定是4的倍数，采用更加通用的加法算法
            String temp = fullAdder(operand1.charAt(i), operand2.charAt(i), carry);
            carry = temp.charAt(0);
            res = temp.charAt(1) + res;
        }
        eflag.setCF(carry == '1');
        return res;  //注意这个方法里面溢出即有进位
    }

    /**
     * the 2 bits' full adder
     *
     * @param x first char
     * @param y second char
     * @param c carry from the former bit
     * @return result after adding, the first position means the carry to the next and second means the result in this position
     */
    private String fullAdder(char x, char y, char c) {
        int bit = (x - '0') ^ (y - '0') ^ (c - '0');  //三位异或
        int carry = ((x - '0') & (y - '0')) | ((y - '0') & (c - '0')) | ((x - '0') & (c - '0'));  //有两位为1则产生进位
        return "" + carry + bit;  //第一个空串让后面的加法都变为字符串加法
    }

    private boolean isLarger(String a, String b) {
        if (a.charAt(0) == '0' && b.charAt(0) == '1') {
            return true;
        }
        if (a.charAt(0) == '1' && b.charAt(0) == '0') {
            return false;
        }
        for (int i = 1; i < a.length(); i++) {
            int diff = a.charAt(i) - b.charAt(i);
            if (diff > 0) {
                return true;
            } else if (diff < 0) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * convert the string's 0 and 1.
     * e.g 00000 to 11111
     *
     * @param operand string to convert (by default, it is 32 bits long)
     * @return string after converting
     */
    public String negation(String operand) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < operand.length(); i++) {
            result = operand.charAt(i) == '1' ? result.append("0") : result.append("1");
        }
        return result.toString();
    }


    /**
     * check if the given number is 0
     * @param operand given number to be checked
     * @return 1 presents num == 0 and 0 presents num != 0
     */
    private boolean isZero(String operand){
        for(char c : operand.toCharArray()){
            if(c != '0') return false;
        }
        return true;
    }


    /**
     * check if the given number is -1
     * @param operand given number to be checked
     * @return 1 presents num == -1 and 0 presents num != -1
     */
    private boolean isNegativeOne(String operand){
        for(char c : operand.toCharArray()){
            if(c != '1') return false;
        }
        return true;
    }


    /**
     * left shift a num using its string format
     * e.g. "00001001" left shift 2 bits -> "00100100"
     * @param operand to be moved
     * @param n moving nums of bits
     * @return after moving
     */
    public String leftShift (String operand, int n){
        StringBuffer result = new StringBuffer(operand.substring(n));  //保证位数不变
        for(int i=0;i<n;i++){
            result = result.append("0");
        }
        return result.toString();
    }


}
