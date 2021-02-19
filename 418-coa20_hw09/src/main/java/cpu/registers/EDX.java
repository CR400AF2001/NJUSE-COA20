package cpu.registers;

import transformer.Transformer;

/**
 * 指令寄存器
 */
public class EDX extends Register {

	public void plus(int len) {
		Transformer t = new Transformer();
		write(t.intToBinary(String.valueOf(Integer.parseInt(t.binaryToInt(read())) + len)));
	}

}
