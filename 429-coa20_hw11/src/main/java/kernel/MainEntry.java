package kernel;

import cpu.CPU;
import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import memory.Memory;

import java.io.IOException;

public class MainEntry {
    //Compose the components
    public static final CPU cpu = new CPU();
    public static final ALU alu = new ALU();
    public static final MMU mmu = MMU.getMMU();
    public static final Memory memory = Memory.getMemory();
    public static final EFlag eflag = (EFlag) CPU_State.eflag;

    public static void main(String[] args) {
        assert args.length > 0;
        String programmePath = args[0];
        try {
            int eip = Loader.loadExec(programmePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Set up eip value , which will be the first instruction
        cpu.execUntilHlt();
    }
}
