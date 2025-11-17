package LGMatcherJsonExporter.data;

public class ActionRecords {

    private ActionRecord insertRecord;
    private ActionRecord deleteRecord;
    private ActionRecord updateRecord;
    private ActionRecord moveRecord;

    // コンストラクタで各フィールドを初期化（空の ActionRecord をセット）
    public ActionRecords() {
        insertRecord = new ActionRecord(ActionRecord.ActionType.INSERT);
        deleteRecord = new ActionRecord(ActionRecord.ActionType.DELETE);
        updateRecord = new ActionRecord(ActionRecord.ActionType.UPDATE);
        moveRecord = new ActionRecord(ActionRecord.ActionType.MOVE);
    }

    // 各 ActionRecord に RangeRecord を追加するメソッド
    public void addInsertRange(RangeRecord range) {
        insertRecord.addRange(range);
    }

    public void addDeleteRange(RangeRecord range) {
        deleteRecord.addRange(range);
    }

    public void addUpdateRange(RangeRecord range) {
        updateRecord.addRange(range);
    }

    public void addMoveRange(RangeRecord range) {
        moveRecord.addRange(range);
    }

    // 各 ActionRecord の取得
    public ActionRecord getInsert() {
        return insertRecord;
    }

    public ActionRecord getDelete() {
        return deleteRecord;
    }

    public ActionRecord getUpdate() {
        return updateRecord;
    }

    public ActionRecord getMove() {
        return moveRecord;
    }

    @Override
    public String toString() {
        return "ActionRecords {\n" +
                "INSERT=" + insertRecord + ",\n" +
                "DELETE=" + deleteRecord + ",\n" +
                "UPDATE=" + updateRecord + ",\n" +
                "MOVE=" + moveRecord + "\n" +
                "}";
    }
}
