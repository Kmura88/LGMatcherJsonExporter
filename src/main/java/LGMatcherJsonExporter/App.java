package LGMatcherJsonExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.kusumotolab.lgmatcher.LGMatcher;



public class App {
	
	private static final String OPTION_LGM = "-LGM";
	private static final String OPTION_M   = "-M";
	private static String JsonPath = "GumTreedata.json";
	private static String srcPath;
	private static String dstPath;

	public static void main(final String[] args) throws IOException {
		final App app = new App();
		// 引数の数の確認
		
		if(args.length<4) {
			System.out.println("Syntax Error");
			return;
		}
		
		//引数の値の確認
		if(!(args[0].equals(OPTION_LGM) || args[0].equals(OPTION_M))) {
			System.out.println("Syntax Error");
			return;
		}
		app.run(args);
	}

	public App() {
		Run.initGenerators();
	}
	
	public void run(final String[] args){
		
		//1. 引数の格納
		JsonPath = args[1];
		srcPath = args[2];
		dstPath = args[3];
		ITree srcTree = null;
		ITree dstTree = null;
		try {
			srcTree = getITree(srcPath);
			dstTree = getITree(dstPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 2.MappingとActionの計算
		List<Action> actions = null;
		MappingStore mapping = null;
		
		
		if(args[0].equals(OPTION_LGM)){
			//LGMatcherを使ってmapping計算
			
			String srcContent="";
			String dstContent="";
			
			try {
				srcContent = readAll(srcPath);
				dstContent = readAll(dstPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			mapping = calculateMapping_LGMatcher(srcTree, dstTree, srcContent, dstContent);
			
		}else if(args[0].equals(OPTION_M)) {
			//既存のMatcherでmapping計算
			mapping = calculateMapping_orginal(srcTree, dstTree);
			
		}
		
		actions = calculateEditScript(srcTree, dstTree, mapping);
		
		//3. json出力
		SaveJsonDiff(actions, mapping);
	}
  
	public void SaveJsonDiff(final List<Action> actions, final MappingStore mapping){
	  	JSONArray allActionsArray = new JSONArray();
	  
	  	for (Action i : actions) {
			JSONObject actionJson = new JSONObject();

			// ---------------------------------
			// Move (Insertより先にチェック)
			// ---------------------------------
			if (i instanceof com.github.gumtreediff.actions.model.Move) {
				actionJson.put("actionType", "Move");
				ITree srcNode = i.getNode();
				ITree dstNode = mapping.getDst(srcNode);
				
				actionJson.put("src_pos", srcNode.getPos());
				actionJson.put("src_end", srcNode.getEndPos());
				if (dstNode != null) {
					actionJson.put("dst_pos", dstNode.getPos());
					actionJson.put("dst_end", dstNode.getEndPos());
				} else {
					actionJson.put("dst_pos", JSONObject.NULL);
					actionJson.put("dst_end", JSONObject.NULL);
				}

			// ---------------------------------
			// Insert (Add)
			// ---------------------------------
			} else if (i instanceof com.github.gumtreediff.actions.model.Insert) {
				actionJson.put("actionType", "Insert");
				ITree dstNode = i.getNode();

				actionJson.put("src_pos", JSONObject.NULL);
				actionJson.put("src_end", JSONObject.NULL);
				actionJson.put("dst_pos", dstNode.getPos());
				actionJson.put("dst_end", dstNode.getEndPos());

			// ---------------------------------
			// Delete
			// ---------------------------------
			} else if (i instanceof com.github.gumtreediff.actions.model.Delete) {
				actionJson.put("actionType", "Delete");
				ITree srcNode = i.getNode();

				actionJson.put("src_pos", srcNode.getPos());
				actionJson.put("src_end", srcNode.getEndPos());
				actionJson.put("dst_pos", JSONObject.NULL);
				actionJson.put("dst_end", JSONObject.NULL);

			// ---------------------------------
			// Update
			// ---------------------------------
			} else if (i instanceof com.github.gumtreediff.actions.model.Update) {
				actionJson.put("actionType", "Update");
				ITree srcNode = i.getNode();
				ITree dstNode = mapping.getDst(srcNode);
				
				int[] srcRange = getTightRange(srcNode);
				actionJson.put("src_pos", srcRange[0]);
				actionJson.put("src_end", srcRange[1]);
				if (dstNode != null) {
					int[] dstRange = getTightRange(dstNode);
					actionJson.put("dst_pos", dstRange[0]);
					actionJson.put("dst_end", dstRange[1]);
				} else {
					actionJson.put("dst_pos", JSONObject.NULL);
					actionJson.put("dst_end", JSONObject.NULL);
				}
				
			}
			
			
			// 完成したJSONオブジェクトを配列に追加
			allActionsArray.put(actionJson);
		}
	// --- 3. JSONファイルへの書き出し ---
		try (FileWriter file = new FileWriter(JsonPath)) {
			file.write(allActionsArray.toString(4));
			file.flush();
			System.out.println("Successfully wrote action data to .json");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// mappingとITreeからActionを計算
	public List<Action> calculateEditScript(final ITree srcTree, final ITree dstTree, final MappingStore mapping){
		final ActionGenerator generator = new ActionGenerator(srcTree, dstTree, mapping);
		generator.generate();
		return generator.getActions();
  	}
	
	// 既存のMatcherで計算したmappingを返すメソッド
	public MappingStore calculateMapping_orginal(final ITree srcTree, final ITree dstTree){
		final Matcher matcher = com.github.gumtreediff.matchers.Matchers
	            .getInstance()
	            .getMatcher(srcTree, dstTree);
		matcher.match();
		return matcher.getMappings();
	}
	
	// LGMatcherで計算したmappingを返すメソッド
	public MappingStore calculateMapping_LGMatcher(final ITree srcTree, final ITree dstTree, final String srcContent,
													final String dstContent) {
		MappingStore mapping = new MappingStore();
		final Matcher matcher = LGMatcher.create(srcContent, dstContent, srcTree, dstTree, mapping);
		matcher.match();
		return mapping;
	}
	
	private static ITree getITree(final String src) throws IOException {
		return Generators.getInstance().getTree(src).getRoot();
	}

	private String readAll(final String path) throws IOException {
		return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
			.collect(Collectors.joining(System.getProperty("line.separator")));
	}
	
	private int[] getTightRange(ITree node) {
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