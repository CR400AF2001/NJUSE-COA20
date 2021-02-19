package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;

public class Mov implements Instruction {

    private MMU mmu = MMU.getMMU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0xB8){
            int length = 40;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            CPU_State.eax.write(instr.substring(8));
            return length;
        }
        return 0;
    }
}
