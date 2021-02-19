package cpu.alu;

import transformer.Transformer;

public class NBCDU {

	// 模拟寄存器中的进位标志位
	private String CF = "0";

	// 模拟寄存器中的溢出标志位
	private String OF = "0";

	/**
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return a + b
	 */
	String add(String a, String b) {
		if(!a.substring(0,4).equals(b.substring(0,4))){
			return sub(a.substring(0,4) + b.substring(4), a);
		}
		String result = "";
		String adder = "0110";
		for(int i = 28; i >= 4; i -= 4){
			String temp = "";
			for(int j = 3; j >= 0; j--){
				int sum = (a.charAt(i + j) - '0') + (b.charAt(i + j) - '0') + (CF.charAt(0) - '0');
				if (sum == 3) {
					temp = "1" + temp;
					CF = "1";
				}
				else if (sum == 2) {
					temp = "0" + temp;
					CF = "1";
				}
				else if (sum == 1) {
					temp = "1" + temp;
					CF = "0";
				}
				else if (sum == 0) {
					temp = "0" + temp;
					CF = "0";
				}
			}
			if(CF.equals("1") || (temp.charAt(0) == '1' && (temp.charAt(1) == '1' || temp.charAt(2) == '1'))){
				CF = "0";
				String newtemp = "";
				for(int k = 3; k >= 0; k--){
					int sum = (temp.charAt(k) - '0') + (adder.charAt(k) - '0') + (CF.charAt(0) - '0');
					if (sum == 3) {
						newtemp = "1" + newtemp;
						CF = "1";
					}
					else if (sum == 2) {
						newtemp = "0" + newtemp;
						CF = "1";
					}
					else if (sum == 1) {
						newtemp = "1" + newtemp;
						CF = "0";
					}
					else if (sum == 0) {
						newtemp = "0" + newtemp;
						CF = "0";
					}
				}
				CF = "1";
				result = newtemp + result;
			}
			else{
				result = temp + result;
			}
		}
		if(CF.equals("1")){
			OF = "1";
		}
		return a.substring(0,4) + result;
	}

	/***
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return b - a
	 */
	String sub(String a, String b) {
		String result = "";
		String adder = "0110";
		if(!a.substring(0,4).equals(b.substring(0,4))){
			return add(b.substring(0,4) + a.substring(4), b);
		}
		String sign = b.substring(0,4);
		for(int i = 4; i < 32; ++i){
			if(a.charAt(i) == '1' && b.charAt(i) == '0'){
				String temp = a;
				a = b;
				b = temp;
				if(sign.equals("1100")){
					sign = "1101";
				}
				else{
					sign = "1100";
				}
				break;
			}
			else if(a.charAt(i) == '0' && b.charAt(i) == '1'){
				break;
			}
			if(i == 31){
				return "11000000000000000000000000000000";
			}
		}
		for(int i = 28; i >= 4; i -= 4){
			String temp = "";
			for(int j = 3; j >= 0; j--){
				int sum = (b.charAt(i + j) - '0') - (a.charAt(i + j) - '0') - (CF.charAt(0) - '0');
				if (sum == 1) {
					temp = "1" + temp;
					CF = "0";
				}
				else if (sum == 0) {
					temp = "0" + temp;
					CF = "0";
				}
				else if (sum == -1) {
					temp = "1" + temp;
					CF = "1";
				}
				else if (sum == -2) {
					temp = "0" + temp;
					CF = "1";
				}
			}
			if(CF.equals("1") || (temp.charAt(0) == '1' && (temp.charAt(1) == '1' || temp.charAt(2) == '1'))){
				CF = "0";
				String newtemp = "";
				for(int k = 3; k >= 0; k--){
					int sum = (temp.charAt(k) - '0') - (adder.charAt(k) - '0') - (CF.charAt(0) - '0');
					if (sum == 1) {
						newtemp = "1" + newtemp;
						CF = "0";
					}
					else if (sum == 0) {
						newtemp = "0" + newtemp;
						CF = "0";
					}
					else if (sum == -1) {
						newtemp = "1" + newtemp;
						CF = "1";
					}
					else if (sum == -2) {
						newtemp = "0" + newtemp;
						CF = "1";
					}
				}
				CF = "1";
				result = newtemp + result;
			}
			else{
				result = temp + result;
			}
		}
		OF = "0";
		return sign + result;
	}

}
