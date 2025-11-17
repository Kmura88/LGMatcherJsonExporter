package LGMatcherJsonExporter.util;

import org.json.JSONArray;
import org.json.JSONObject;

import LGMatcherJsonExporter.data.ActionRecord;
import LGMatcherJsonExporter.data.ActionRecords;
import LGMatcherJsonExporter.data.RangeRecord;

public class ActionRecordsJsonExporter {

    public static JSONObject toJson(ActionRecords records) {
        JSONObject root = new JSONObject();

        root.put("INSERT", actionRecordToJson(records.getInsert()));
        root.put("DELETE", actionRecordToJson(records.getDelete()));
        root.put("UPDATE", actionRecordToJson(records.getUpdate()));
        root.put("MOVE", actionRecordToJson(records.getMove()));

        return root;
    }

    private static JSONArray actionRecordToJson(ActionRecord record) {
        JSONArray arr = new JSONArray();
        for (RangeRecord range : record.getRanges()) {
            JSONObject obj = new JSONObject();
            obj.put("src_pos", range.getSrcPos() >= 0 ? range.getSrcPos() : JSONObject.NULL);
            obj.put("src_end", range.getSrcEnd() >= 0 ? range.getSrcEnd() : JSONObject.NULL);
            obj.put("dst_pos", range.getDstPos() >= 0 ? range.getDstPos() : JSONObject.NULL);
            obj.put("dst_end", range.getDstEnd() >= 0 ? range.getDstEnd() : JSONObject.NULL);
            arr.put(obj);
        }
        return arr;
    }
}
