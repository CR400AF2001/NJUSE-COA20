package cpu.registers;

import java.util.Arrays;

/**
 * 默认32位寄存器，注意段寄存器只有16位
 */
public abstract class Register {

    protected char[] reg;

    public Register() {
        reg = new char[32];
        Arrays.fill(reg, '0');
    }

    public String read() {
        return new String(reg);
    }

    public void write(String v) {
        reg = v.toCharArray();
    }

}
