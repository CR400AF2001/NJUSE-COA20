package cpu.instr.all_instrs;

import program.Log;

public interface Instruction {

    int exec(String eip, int opcode);

    default String toBinaryStr(String logInfo) {
        Log.write(logInfo);
        return logInfo;
    }

}
