package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import memory.Memory;

public class Push implements Instruction {

    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x53){
            int length = 8;
            CPU_State.esp.write(alu.sub("00000000000000000000000000100000", CPU_State.esp.read()));
            Memory.getMemory().pushStack(CPU_State.esp.read(), CPU_State.ebx.read());
            return length;
        }
        return 0;
    }
}
