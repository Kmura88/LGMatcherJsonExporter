package LGMatcherJsonExporter.util;

import org.json.JSONArray;
import org.json.JSONObject;

import LGMatcherJsonExporter.data.ActionRecord;
import LGMatcherJsonExporter.data.ActionRecords;
import LGMatcherJsonExporter.data.RangeRecord;

public class ActionRecordsJsonExporter {

    public static JSONObject toJson(ActionRecords records) {
        JSONObject root = new JSONObject();

        root.put("INSERT", actionRecordToJson(records.insert));
        root.put("DELETE", actionRecordToJson(records.delete));
        root.put("UPDATE", actionRecordToJson(records.update));
        root.put("MOVE", actionRecordToJson(records.move));
        root.put("MATCH",  actionRecordToJson(records.match));

        return root;
    }

    private static JSONArray actionRecordToJson(ActionRecord record) {
        JSONArray arr = new JSONArray();
        for (RangeRecord range : record.getRanges()) {
            JSONObject obj = new JSONObject();
            obj.put("src_pos", range.src_pos >= 0 ? range.src_pos : JSONObject.NULL);
            obj.put("src_end", range.src_end >= 0 ? range.src_end : JSONObject.NULL);
            obj.put("dst_pos", range.dst_pos >= 0 ? range.dst_pos : JSONObject.NULL);
            obj.put("dst_end", range.dst_end >= 0 ? range.dst_end : JSONObject.NULL);
            arr.put(obj);
        }
        return arr;
    }
}
