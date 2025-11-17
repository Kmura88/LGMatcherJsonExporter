package LGMatcherJsonExporter.data;

import java.util.ArrayList;
import java.util.List;

public class ActionRecord {

    private List<RangeRecord> ranges;

    public ActionRecord() {
        this.ranges = new ArrayList<>();
    }

    public List<RangeRecord> getRanges() {
        return ranges;
    }

    public void addRange(final RangeRecord range) {
        ranges.add(range);
    }
    
    public void addRange(final int srcPos, final int srcEnd, final int dstPos, final int dstEnd) {
        ranges.add(new RangeRecord(srcPos, srcEnd, dstPos, dstEnd));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ranges=");
        sb.append(ranges);
        sb.append("]");
        return sb.toString();
    }
}