package cpu.registers;

import java.util.Arrays;

/**
 * 数据段寄存器
 */
public class DS extends Register {

	public DS() {
		reg = new char[16];
		Arrays.fill(reg, '0');
	}

}
