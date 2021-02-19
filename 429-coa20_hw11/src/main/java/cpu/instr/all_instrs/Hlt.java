package cpu.instr.all_instrs;


import cpu.CPU_State;
import cpu.MMU;

public class Hlt implements Instruction {

    private MMU mmu = MMU.getMMU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0xF4){
            int length = 8;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            toBinaryStr(instr);
            return -1;
        }
        return 0;
    }
}
