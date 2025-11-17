package LGMatcherJsonExporter.util;

import java.util.List;
import java.util.stream.Collectors;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

import LGMatcherJsonExporter.data.ActionRecord;
import LGMatcherJsonExporter.data.ActionRecords;
import LGMatcherJsonExporter.data.RangeRecord;

public class ActionConverter {
	
	public static ActionRecords makeActionRecords(final List<Action> actions, final MappingStore mapping) {

	    ActionRecords allRecords = new ActionRecords();

	    for (Action i : actions) {

	        ActionRecord.ActionType type = null;
	        int srcPos = -1, srcEnd = -1;
	        int dstPos = -1, dstEnd = -1;

	        // ---------------------------------
	        // Move
	        // ---------------------------------
	        if (i instanceof com.github.gumtreediff.actions.model.Move) {
	            type = ActionRecord.ActionType.MOVE;
	            ITree srcNode = i.getNode();
	            ITree dstNode = mapping.getDst(srcNode);

	            srcPos = srcNode.getPos();
	            srcEnd = srcNode.getEndPos();
	            if (dstNode != null) {
	                dstPos = dstNode.getPos();
	                dstEnd = dstNode.getEndPos();
	            }

	        // ---------------------------------
	        // Insert
	        // ---------------------------------
	        } else if (i instanceof com.github.gumtreediff.actions.model.Insert) {
	            type = ActionRecord.ActionType.INSERT;
	            ITree dstNode = i.getNode();
	            dstPos = dstNode.getPos();
	            dstEnd = dstNode.getEndPos();
	            srcPos = -1;
	            srcEnd = -1;

	        // ---------------------------------
	        // Delete
	        // ---------------------------------
	        } else if (i instanceof com.github.gumtreediff.actions.model.Delete) {
	            type = ActionRecord.ActionType.DELETE;
	            ITree srcNode = i.getNode();
	            srcPos = srcNode.getPos();
	            srcEnd = srcNode.getEndPos();
	            dstPos = -1;
	            dstEnd = -1;

	        // ---------------------------------
	        // Update
	        // ---------------------------------
	        } else if (i instanceof com.github.gumtreediff.actions.model.Update) {
	            type = ActionRecord.ActionType.UPDATE;
	            ITree srcNode = i.getNode();
	            ITree dstNode = mapping.getDst(srcNode);

	            int[] srcRange = getTightRange(srcNode);
	            srcPos = srcRange[0];
	            srcEnd = srcRange[1];

	            if (dstNode != null) {
	                int[] dstRange = getTightRange(dstNode);
	                dstPos = dstRange[0];
	                dstEnd = dstRange[1];
	            } else {
	                dstPos = -1;
	                dstEnd = -1;
	            }
	        }

	        if (type != null) {
	            RangeRecord range = new RangeRecord(srcPos, srcEnd, dstPos, dstEnd);

	            switch (type) {
	                case MOVE:
	                    allRecords.addMoveRange(range);
	                    break;
	                case INSERT:
	                    allRecords.addInsertRange(range);
	                    break;
	                case DELETE:
	                    allRecords.addDeleteRange(range);
	                    break;
	                case UPDATE:
	                    allRecords.addUpdateRange(range);
	                    break;
	            }
	        }
	    }

	    return allRecords;
	}
	
	private static int[] getTightRange(ITree node) {
		int pStart = node.getPos();
	    int pEnd   = node.getEndPos();

	    List<ITree> children = node.getChildren();

	    // 子が無ければ親の範囲をそのまま返す
	    if (children.isEmpty()) {
	        return new int[]{pStart, pEnd};
	    }

	    // 子ノードの範囲を (start,end) の形で抽出
	    List<int[]> ranges = children.stream()
	        .map(c -> new int[]{c.getPos(), c.getEndPos()})
	        .sorted((a,b) -> Integer.compare(a[0], b[0]))
	        .collect(Collectors.toList());

	    // 親範囲の "未覆われ部分"（差集合）を探索
	    int current = pStart;

	    for (int[] r : ranges) {
	        int cStart = r[0];
	        int cEnd   = r[1];

	        // 親範囲内でギャップを発見
	        if (current < cStart) {
	            int gapStart = current;
	            int gapEnd   = cStart - 1;

	            // Update の差分は 1つだけ返せば良い
	            return new int[]{gapStart, gapEnd};
	        }

	        // 子範囲の後ろに進める
	        current = Math.max(current, cEnd + 1);
	    }

	    // 最後の子の後ろにもギャップがある場合
	    if (current <= pEnd) {
	        return new int[]{current, pEnd};
	    }

	    // ギャップが無ければ親範囲全部
	    return new int[]{pStart, pEnd};
	}
}
