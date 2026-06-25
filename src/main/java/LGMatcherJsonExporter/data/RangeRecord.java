package LGMatcherJsonExporter.data;

public class RangeRecord {
    public int src_pos;
    public int src_end;
    public int dst_pos;
    public int dst_end;

    public RangeRecord(int src_pos, int src_end, int dst_pos, int dst_end) {
        this.src_pos = src_pos;
        this.src_end = src_end;
        this.dst_pos = dst_pos;
        this.dst_end = dst_end;
    }
    
    @Override
    public String toString() {
        return "[src=" + src_pos + "," + src_end + "], dst=[" + dst_pos + "," + dst_end + "]";
    }
}