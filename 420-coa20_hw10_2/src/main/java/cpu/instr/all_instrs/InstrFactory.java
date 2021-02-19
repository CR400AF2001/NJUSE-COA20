package cpu.instr.all_instrs;

public class InstrFactory {
    private static final String PREFIX = "cpu.instr.all_instrs.";

    public static Instruction getInstr(int opcode) {
        try {
            System.out.println(opcode);
            String className = Opcode.opcodeEntry[opcode].split("_")[0];
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            System.out.println(className);
            Class clazz = Class.forName(PREFIX + className);
            return (Instruction) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("No Such instruction, please add it to the instruction table!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
