package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;

import static kernel.MainEntry.eflag;

public class Cmp implements Instruction {

    private MMU mmu = MMU.getMMU();
    private ALU alu = new ALU();

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0x3D){
            int length = 40;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            if(alu.sub(instr.substring(8), CPU_State.eax.read()).equals("00000000000000000000000000000000")){
                ((EFlag) CPU_State.eflag).setZF(true);
            }
            else{
                ((EFlag) CPU_State.eflag).setZF(false);
            }
            return length;
        }
        else if(opcode == 0x39){
            int length = 16;
            String instr = String.valueOf(mmu.read(CPU_State.cs.read() + eip, length));
            if(instr.substring(8).equals("11001000")){
                if(alu.sub(CPU_State.ecx.read(), CPU_State.eax.read()).equals("00000000000000000000000000000000")){
                    ((EFlag) CPU_State.eflag).setZF(true);
                }
                else{
                    ((EFlag) CPU_State.eflag).setZF(false);
                }
                if(alu.sub(CPU_State.ecx.read(), CPU_State.eax.read()).charAt(0) == '1'){
                    ((EFlag) CPU_State.eflag).setSF(true);
                }
                else{
                    ((EFlag) CPU_State.eflag).setSF(false);
                }
                toBinaryStr(instr);
                return length;
            }
        }
        return 0;
    }
}
