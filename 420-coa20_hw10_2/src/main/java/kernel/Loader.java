package kernel;

import cpu.CPU_State;
import memory.Disk;
import memory.Memory;

import java.io.*;

//The loader for elf files
public class Loader {
    private static String bitContent;

    //TODO:load elf file to memory, and return the current eip value
    public static int loadExec(String path) throws IOException {
        //eip is 0x0 if there is no segment
        String eip = "00000000000000000000000000000000";
        Memory.PAGE = true;
        Memory.SEGMENT = true;

        readTarget(path);
        Disk.getDisk().write(eip,
                bitContent.length(),
                bitContent.toCharArray());
        MainEntry.memory.alloc_seg_force(0, "00000000000000000000000000000000", 1024, false, "");
        CPU_State.cs.write("0000000000000000");
        CPU_State.eip.write(eip);
        return 0;
    }

    private static void readTarget(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line;
        line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        bitContent = sb.toString();
    }
}
