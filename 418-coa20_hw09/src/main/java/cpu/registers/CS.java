package cpu.registers;

import java.util.Arrays;

/**
 * 16-bits代码段寄存器
 */
public class CS extends Register {

	public CS() {
		reg = new char[16];
		Arrays.fill(reg, '0');
	}

}
