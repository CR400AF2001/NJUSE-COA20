package cpu.instr.decode;

import cpu.MMU;

/**
 * 如果操作数是立即数，直接setVal
 * 如果操作数是地址，则先set到对应的addr，再使用operandRead初始化val
 */
public class Operand {
    private OperandType type;
    private String addr;
    private int sreg;
    private String val;
    private int data_size = 32;
    private MMU mmu = MMU.getMMU();

    public Operand() {
    }

    public OperandType getType() {
        return type;
    }

    public void setType(OperandType type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getSreg() {
        return sreg;
    }

    public void setSreg(int sreg) {
        this.sreg = sreg;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    // read the operand's value
    // put value back to its addr
    public void operandRead() {
        // TODO: this.addr 读取数据
        switch (this.type) {
            case OPR_MEM:
            case OPR_IMM:
            case OPR_REG:
            case OPR_CREG:
            case OPR_SREG:
        }

        // deal with data size
        switch (this.data_size) {
            case 8:
            case 16:
            case 32:
            default:
                System.out.printf("Error: Operand data size = %u\n", this.data_size);
                break;
        }
    }

    // write the operand's value to the addr
    public void operandWrite() {
        // TODO: 写入数据
        switch(this.type) {
            case OPR_MEM:
            case OPR_REG:
            case OPR_IMM:
            case OPR_CREG:
        }
    }

}
