package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.alu.ALU;
import memory.Memory;

public class Pop implements Instruction {

    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x58){
            int length = 8;
            CPU_State.eax.write(Memory.getMemory().topOfStack(CPU_State.esp.read()));
            CPU_State.esp.write(alu.add("00000000000000000000000000100000", CPU_State.esp.read()));
            return length;
        }
        else if(opcode == 0x59){
            int length = 8;
            CPU_State.ecx.write(Memory.getMemory().topOfStack(CPU_State.esp.read()));
            CPU_State.esp.write(alu.add("00000000000000000000000000100000", CPU_State.esp.read()));
            return length;
        }
        else if(opcode == 0x5A){
            int length = 8;
            CPU_State.edx.write(Memory.getMemory().topOfStack(CPU_State.esp.read()));
            CPU_State.esp.write(alu.add("00000000000000000000000000100000", CPU_State.esp.read()));
            return length;
        }
        return 0;
    }
}
