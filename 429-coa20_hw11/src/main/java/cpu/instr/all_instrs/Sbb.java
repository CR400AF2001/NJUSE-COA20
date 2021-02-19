package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;

public class Sbb implements Instruction {

    private MMU mmu = MMU.getMMU();
    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x1D){
            int length = 40;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            boolean CF = ((EFlag) CPU_State.eflag).getCF();
            CPU_State.eax.write(alu.sub(instr.substring(8), CPU_State.eax.read()));
            if(CF){
                CPU_State.eax.write(alu.sub("00000000000000000000000000000001", CPU_State.eax.read()));
            }
            return length;
        }
        return 0;
    }
}
