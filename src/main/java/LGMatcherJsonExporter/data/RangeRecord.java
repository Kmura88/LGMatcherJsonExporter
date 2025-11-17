package LGMatcherJsonExporter.data;

public class RangeRecord {
    private int src_pos;
    private int src_end;
    private int dst_pos;
    private int dst_end;

    public RangeRecord(int src_pos, int src_end, int dst_pos, int dst_end) {
        this.src_pos = src_pos;
        this.src_end = src_end;
        this.dst_pos = dst_pos;
        this.dst_end = dst_end;
    }

    public int getSrcPos() {
        return src_pos;
    }

    public int getSrcEnd() {
        return src_end;
    }

    public int getDstPos() {
        return dst_pos;
    }

    public int getDstEnd() {
        return dst_end;
    }

    @Override
    public String toString() {
        return "[src=" + src_pos + "," + src_end + "], dst=[" + dst_pos + "," + dst_end + "]";
    }
}