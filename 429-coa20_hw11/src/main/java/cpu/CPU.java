package cpu;

import cpu.alu.ALU;
import cpu.instr.all_instrs.InstrFactory;
import cpu.instr.all_instrs.Instruction;
import cpu.registers.CS;
import transformer.Transformer;

public class CPU {

    Transformer transformer = new Transformer();
    MMU mmu = MMU.getMMU();
    ALU alu = new ALU();


    /**
     * execInstr specific numbers of instructions
     *
     * @param number numbers of instructions
     */
    public int execInstr(long number) {
        // 执行过的指令的总长度
        int totalLen = 0;
        while (number > 0) {
            // TODO 上次作业
            int length = execInstr();
            if(length < 0){
                break;
            }
            totalLen += length;
            number--;
        }
        return totalLen;
    }

    /**
     * execInstr a single instruction according to eip value
     */
    private int execInstr() {
        String eip = CPU_State.eip.read();
        int len = decodeAndExecute(eip);
        CPU_State.eip.write(alu.add(transformer.intToBinary(String.valueOf(len)), CPU_State.eip.read()));
        return len;
    }

    private int decodeAndExecute(String eip) {
        int opcode = instrFetch(eip, 1);
        Instruction instruction = InstrFactory.getInstr(opcode);
        assert instruction != null;

        //exec the target instruction
        int len = instruction.exec(eip, opcode);
        return len;


    }

    /**
     * @param eip
     * @param length opcode的字节数，本作业只使用单字节opcode
     * @return
     */
    private int instrFetch(String eip, int length) {
        // TODO X   FINISHED √
        String addr = ((CS) CPU_State.cs).read() + eip;
        String opcode = String.valueOf(mmu.read(addr, length * 8));
        return Integer.parseInt(transformer.binaryToInt("0" + opcode));
    }

    public void execUntilHlt(){
        // TODO ICC
        while(CPU_State.ICC != 3){
            if(execInstr() == -1){
                CPU_State.ICC = 3;
            }
        }
        CPU_State.ICC = 0;
    }

}

