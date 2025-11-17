package LGMatcherJsonExporter.data;

import java.util.ArrayList;
import java.util.List;

public class ActionRecord {

    public static enum ActionType {
        MOVE, INSERT, DELETE, UPDATE
    }

    private ActionType type;
    private List<RangeRecord> ranges;

    public ActionRecord(ActionType type) {
        this.type = type;
        this.ranges = new ArrayList<>();
    }

    public ActionType getType() {
        return type;
    }

    public List<RangeRecord> getRanges() {
        return ranges;
    }

    public void addRange(RangeRecord range) {
        ranges.add(range);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[type=").append(type).append(", ranges=");
        sb.append(ranges);
        sb.append("]");
        return sb.toString();
    }
}