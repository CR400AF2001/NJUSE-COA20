package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;
import memory.Memory;

public class Mov implements Instruction {

    private MMU mmu = MMU.getMMU();
    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0xB8){
            int length = 40;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            CPU_State.eax.write(instr.substring(8));
            toBinaryStr(instr);
            return length;
        }
        else if(opcode == 0x89){
            int length = 48;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            if(instr.substring(8, 16).equals("10000011")){
                String addr = alu.add(instr.substring(16, 48), CPU_State.ebx.read());
                Memory.getMemory().write(addr, 32 , CPU_State.eax.read().toCharArray());
                toBinaryStr(instr);
                return length;
            }
            else if(instr.substring(8, 16).equals("10001011")){
                String addr = alu.add(instr.substring(16, 48), CPU_State.ebx.read());
                Memory.getMemory().write(addr, 32 , CPU_State.ecx.read().toCharArray());
                toBinaryStr(instr);
                return length;
            }
        }
        else if(opcode == 0x8b){
            int length = 48;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            if(instr.substring(8, 16).equals("10000011")){
                String addr = alu.add(instr.substring(16, 48), CPU_State.ebx.read());
                CPU_State.eax.write(String.valueOf(mmu.read(CPU_State.ds.read() + addr, 32)));
                toBinaryStr(instr);
                return length;
            }
            else if(instr.substring(8, 16).equals("10001011")){
                String addr = alu.add(instr.substring(16, 48), CPU_State.ebx.read());
                CPU_State.ecx.write(String.valueOf(mmu.read(CPU_State.ds.read() + addr, 32)));
                toBinaryStr(instr);
                return length;
            }
        }
        else if(opcode == 0xc7){
            int length = 80;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            if(instr.substring(8, 16).equals("10000011")){
                String addr = alu.add(instr.substring(16, 48), CPU_State.ebx.read());
                Memory.getMemory().write(addr, 32 , instr.substring(48, 80).toCharArray());
                toBinaryStr(instr);
                return length;
            }
        }
        return 0;
    }
}
