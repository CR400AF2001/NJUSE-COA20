package cpu.registers;

import transformer.Transformer;

/**
 * 指令寄存器
 */
public class EIP extends Register {

	public void plus(int len) {
		Transformer t = new Transformer();
		write(t.intToBinary(String.valueOf(Integer.parseInt(t.binaryToInt(read())) + len)));
	}

}
