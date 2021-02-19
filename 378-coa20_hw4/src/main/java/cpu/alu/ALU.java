package cpu.alu;

import util.BinaryIntegers;
import util.IEEE754Float;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 乘除
 */
public class ALU {

	// 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";


	/**
	 * 返回两个二进制整数的乘积(结果低位截取后32位)
	 * @param src 32-bits
	 * @param dest 32-bits
	 * @return 32-bits
	 */
	public String mul (String src, String dest){
		if(src.equals(BinaryIntegers.ZERO) || dest.equals(BinaryIntegers.ZERO)){
			return BinaryIntegers.ZERO;
		}
		String x = dest + "000000000000000000000000000000000";
		String _x = reverse(dest) + "000000000000000000000000000000000";
		String y = "00000000000000000000000000000000" + src + "0";
		for(int i = 0; i < src.length(); ++i){
			if(y.substring(y.length() - 2).equals("10")){
				y = add(y, _x);
			}
			else if(y.substring(y.length() - 2).equals("01")){
				y = add(y, x);
			}
			y = y.substring(0, 1) + y.substring(0, y.length() - 1);
		}
		return y.substring(y.length() - 33, y.length() - 1);
    }

    /**
     * 返回两个二进制整数的除法结果 operand1 ÷ operand2
     * @param operand1 32-bits
     * @param operand2 32-bits
     * @return 65-bits overflow + quotient + remainder
     */
    public String div(String operand1, String operand2) {
		if(operand1.equals(BinaryIntegers.ZERO) && !operand2.equals(BinaryIntegers.ZERO)){
			return "0" + BinaryIntegers.ZERO + "00000000000000000000000000000000";
		}
		if(!operand1.equals(BinaryIntegers.ZERO) && operand2.equals(BinaryIntegers.ZERO)){
			throw new java.lang.ArithmeticException();
		}
		if(operand1.equals(BinaryIntegers.ZERO) && operand2.equals(BinaryIntegers.ZERO)){
			return BinaryIntegers.NaN;
		}
		String quotient = operand1;
		String divisor = operand2;
		String remainder;
		if(quotient.charAt(0) == '0'){
			remainder = "00000000000000000000000000000000";
		}
		else{
			remainder = "11111111111111111111111111111111";
		}
		String temp;
		if(operand1.charAt(0) == operand2.charAt(0)){
			divisor = reverse(divisor);
		}
		for(int i = 0; i < operand1.length(); ++i){
			remainder = remainder.substring(1) + quotient.substring(0, 1);
			temp = add(remainder, divisor);
			if(((remainder.charAt(0) == temp.charAt(0)) && !remainder.equals("00000000000000000000000000000000")) || temp.equals("00000000000000000000000000000000")){
				quotient = quotient.substring(1) + "1";
				remainder = temp;
			}
			else{
				quotient = quotient.substring(1) + "0";
			}
		}
		if(operand1.charAt(0) != operand2.charAt(0)){
			quotient = reverse(quotient);
		}
		if(operand1.equals("10000000000000000000000000000000") && operand2.equals("11111111111111111111111111111111")){
			return "11000000000000000000000000000000000000000000000000000000000000000";
		}
        return "0" + quotient + remainder;
    }

    String reverse(String s){
		char[] resultChar = s.toCharArray();
		boolean flag = false;
		for (int i = s.length() - 1; i >= 0; i--){
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

	String add(String src, String dest) {
		StringBuilder result = new StringBuilder();
		CF = "0";
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

}
