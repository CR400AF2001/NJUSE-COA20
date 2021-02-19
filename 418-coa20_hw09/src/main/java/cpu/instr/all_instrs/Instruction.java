package cpu.instr.all_instrs;

public interface Instruction {

    int exec(String eip, int opcode);

}
