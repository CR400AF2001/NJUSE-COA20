package cpu.instr.all_instrs;



public class Hlt implements Instruction {

    @Override
    public int exec(String eip, int opcode) {
        if(opcode == 0xF4){
            return -1;
        }
        return 0;
    }
}
