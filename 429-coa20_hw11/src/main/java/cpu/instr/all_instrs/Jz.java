package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;

public class Jz implements Instruction {

    private MMU mmu = MMU.getMMU();
    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x74){
            int length = 16;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            if(((EFlag) CPU_State.eflag).getZF()){
                if(instr.charAt(8) == '1'){
                    CPU_State.eip.write(alu.add("111111111111111111111111" + instr.substring(8), CPU_State.eip.read()));
                }
                else{
                    CPU_State.eip.write(alu.add("000000000000000000000000" + instr.substring(8), CPU_State.eip.read()));
                }
                return 0;
            }
            return length;
        }
        return 0;
    }
}
