package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;

public class Or implements Instruction {

    private MMU mmu = MMU.getMMU();
    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x0D){
            int length = 40;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            CPU_State.eax.write(alu.or(instr.substring(8), CPU_State.eax.read()));
            return length;
        }
        return 0;
    }
}
