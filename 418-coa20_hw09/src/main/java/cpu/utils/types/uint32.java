package cpu.utils.types;

public class uint32 {
    private char []c;

    public uint32(String s) {
        if (s.length() != 32) {
            throw new ClassCastException();
        }
        this.c = s.toCharArray();
    }

    public String getValue() {
        return new String(c);
    }
}
