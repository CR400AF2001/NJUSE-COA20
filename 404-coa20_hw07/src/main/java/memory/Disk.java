package memory;



import transformer.Transformer;
import util.CRC;

/**
 * 磁盘抽象类，磁盘大小为64M
 */
public class Disk {

    public static int DISK_SIZE_B = 64 * 1024 * 1024;      // 磁盘大小 64 MB

    private static Disk diskInstance = new Disk();

    /**
     * 请勿修改下列属性，至少不要修改一个扇区的大小，如果要修改请保证磁盘的大小为64MB
     */
    public static final int CYLINDER_NUM = 8;
    public static final int TRACK_PRE_PLATTER = 16;
    public static final int SECTOR_PRE_TRACK = 128;
    public static final int BYTE_PRE_SECTOR = 512;
    public static final int PLATTER_PRE_CYLINDER = 8;

    public static final String POLYNOMIAL = "11000000000100001";
    public disk_head DISK_HEAD = new disk_head();

    RealDisk disk = new RealDisk();


    public static Transformer transformer = new Transformer();

    /**
     * 初始化
     */
    private Disk() { }

    public static Disk getDisk() {
        return diskInstance;
    }

    /**
     * 读磁盘
     * @param eip
     * @param len
     * @return
     */
    public char[] read(String eip, int len) {
        char[] readData = new char[len];
        int intEip = Integer.parseInt(transformer.binaryToInt(eip));
        DISK_HEAD.Seek(intEip);
        int i = 0;
        char[] data = disk.getData(DISK_HEAD);
        while(len > i){
            readData[i++] = data[DISK_HEAD.point++];
            if(len == i){
                break;
            }
            if (DISK_HEAD.point == BYTE_PRE_SECTOR) {
                DISK_HEAD.adjust();
                data = disk.getData(DISK_HEAD);
            }
        }
        //TODO
        return readData;
    }

    /**
     * 写磁盘
     * @param eip
     * @param len
     * @param data
     */
    public void write(String eip, int len, char[] data) {
        int intEip = Integer.parseInt(transformer.binaryToInt("0" + eip));
        write(intEip, len, data);
        //TODO
    }

    /**
     * 写磁盘（地址为Integer型）
     * 测试会调用该方法
     * @param eip
     * @param len
     * @param data
     */
    public void write(int eip, int len, char[] data) {
        DISK_HEAD.Seek(eip);
        int i = 0;
        while(len > i){
            disk.setData(DISK_HEAD, DISK_HEAD.point, data[i]);
            DISK_HEAD.point++;
            i++;
            if(len == i){
                break;
            }
            if (DISK_HEAD.point == BYTE_PRE_SECTOR) {
                char[] crc = ToByteStream(CRC.Calculate(ToBitStream(disk.getData(DISK_HEAD)), POLYNOMIAL));
                disk.setCRC(DISK_HEAD, crc);
                DISK_HEAD.adjust();
            }
        }
        char[] crc = ToByteStream(CRC.Calculate(ToBitStream(disk.getData(DISK_HEAD)), POLYNOMIAL));
        disk.setCRC(DISK_HEAD, crc);
        //TODO
    }

    /**
     * 该方法仅用于测试
     */
    public char[] getCRC() {
        return disk.getCRC(DISK_HEAD);
    }

    /**
     * 磁头
     */
    private class disk_head {
        int cylinder;
        int platter;
        int track;
        int sector;
        int point;

        /**
         * 调整磁头的位置
         */
        public void adjust() {
            if (point == BYTE_PRE_SECTOR) {
                point = 0;
                sector++;
            }
            if (sector == SECTOR_PRE_TRACK) {
                sector = 0;
                track++;
            }
            if (track == TRACK_PRE_PLATTER) {
                track = 0;
                platter++;
            }
            if (platter == PLATTER_PRE_CYLINDER) {
                platter = 0;
                cylinder++;
            }
            if (cylinder == CYLINDER_NUM) {
                cylinder = 0;
            }
        }

        /**
         * 磁头回到起点
         */
        public void Init() {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            cylinder = 0;
            track = 0;
            sector = 0;
            point = 0;
            platter = 0;
        }

        /**
         * 将磁头移动到目标位置
         * @param start
         */
        public void Seek(int start) {
//            try {
//                Thread.sleep(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            for (int i = cylinder; i < CYLINDER_NUM; i++) {
                for (int t = platter; t < PLATTER_PRE_CYLINDER; t++) {
                    for (int j = track; j < TRACK_PRE_PLATTER; j++) {
                        for (int z = sector; z < SECTOR_PRE_TRACK; z++) {
                            for (int k = point; k < BYTE_PRE_SECTOR; k++) {
                                if ((i * PLATTER_PRE_CYLINDER * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + t * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + j * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + z * BYTE_PRE_SECTOR + k) == start) {
                                    cylinder = i;
                                    track = j;
                                    sector = z;
                                    point = k;
                                    platter = t;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            Init();
            Seek(start);
        }

        @Override
        public String toString() {
            return "The Head Of Disk Is In\n" +
                    "platter:\t" + cylinder + "\n" +
                    "track:\t\t" + track + "\n" +
                    "sector:\t\t" + sector + "\n" +
                    "point:\t\t" + point;
        }
    }

    /**
     * 600 Bytes/Sector
     */
    private class Sector {
        char[] gap1 = new char[17];
        IDField idField = new IDField();
        char[] gap2 = new char[41];
        DataField dataField = new DataField();
        char[] gap3 = new char[20];
    }

    /**
     * 7 Bytes/IDField
     */
    private class IDField {
        char SynchByte;
        char[] Track = new char[2];
        char Head;
        char sector;
        char[] CRC = new char[2];
    }

    /**
     * 515 Bytes/DataField
     */
    private class DataField {
        char SynchByte;
        char[] Data = new char[512];
        char[] CRC = new char[2];
    }

    /**
     * 128 sectors pre track
     */
    private class Track {
        Sector[] sectors = new Sector[SECTOR_PRE_TRACK];

        Track() {
            for (int i = 0; i < SECTOR_PRE_TRACK; i++) sectors[i] = new Sector();
        }
    }


    /**
     * 32 tracks pre platter
     */
    private class Platter {
        Track[] tracks = new Track[TRACK_PRE_PLATTER];

        Platter() {
            for (int i = 0; i < TRACK_PRE_PLATTER; i++) tracks[i] = new Track();
        }
    }

    /**
     * 8 platter pre Cylinder
     */
    private class Cylinder {
        Platter[] platters = new Platter[PLATTER_PRE_CYLINDER];

        Cylinder() {
            for (int i = 0; i < PLATTER_PRE_CYLINDER; i++) platters[i] = new Platter();
        }
    }


    private class RealDisk {
        Cylinder[] cylinders = new Cylinder[CYLINDER_NUM];

        public RealDisk() {
            for (int i = 0; i < CYLINDER_NUM; i++) cylinders[i] = new Cylinder();
        }

        public char[] getData(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data;
        }

        public void setData(disk_head d, int point, char data) {
            cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data[point] = data;
        }

        public char[] getCRC(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC;
        }
        public void setCRC(disk_head d, char[] crc) {
            cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC[0] = crc[0];
            cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC[1] = crc[1];
        }
    }

    /**
     * 将Byte流转换成Bit流
     * @param data
     * @return
     */
    public static char[] ToBitStream(char[] data) {
        char[] t = new char[data.length * 8];
        for(int i = 0; i < data.length; ++i){
            int temp = (int) data[i];
            String tempString = transformer.intToBinary(String.valueOf(temp)).substring(24);
            for(int j = 0; j < 8; ++j){
                t[8 * i + j] = tempString.charAt(j);
            }
        }
        //TODO
        return t;
    }

    /**
     * 将Bit流转换为Byte流
     * @param data
     * @return
     */
    public static char[] ToByteStream(char[] data) {
        char[] t = new char[data.length / 8];
        for(int i = 0; i < data.length; i = i + 8){
            String tempString = "";
            for(int j = i; j < i + 8; ++j){
                tempString += data[j];
            }
            int temp = Integer.parseInt(transformer.binaryToInt(tempString));
            t[i / 8] = (char) temp;
        }
        //TODO
        return t;
    }

    /**
     * 这个方法仅供测试，请勿修改
     * @param eip
     * @param len
     * @return
     */
    public char[] readTest(String eip, int len){
        char[] data = read(eip, len);
        System.out.print(data);
        return data;
    }


}
