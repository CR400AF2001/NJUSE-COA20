package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;

public class And implements Instruction {

    private MMU mmu = MMU.getMMU();
    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x25){
            int length = 40;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            CPU_State.eax.write(alu.and(instr.substring(8), CPU_State.eax.read()));
            return length;
        }
        return 0;
    }
}
