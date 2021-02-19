package cpu.registers;

import java.util.Arrays;

public class SS extends Register  {
    public SS() {
        reg = new char[16];
        Arrays.fill(reg, '0');
    }
}
