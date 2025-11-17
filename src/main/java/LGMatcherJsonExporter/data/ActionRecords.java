package LGMatcherJsonExporter.data;

public class ActionRecords {

    public ActionRecord insert = new ActionRecord();
    public ActionRecord delete = new ActionRecord();
    public ActionRecord update = new ActionRecord();
    public ActionRecord move   = new ActionRecord();

    @Override
    public String toString() {
        return "ActionRecords {\n" +
                "INSERT=" + insert + ",\n" +
                "DELETE=" + delete + ",\n" +
                "UPDATE=" + update + ",\n" +
                "MOVE=" + move + "\n" +
                "}";
    }
}
