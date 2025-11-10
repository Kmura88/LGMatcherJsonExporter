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
	
	private static final MappingStore mapping = new MappingStore();

	public static void main(final String[] args) throws IOException {
		final App app = new App();
		
		final List<Action> actions = app.calculateEditScript(args[0], args[1]);
		
		app.SaveDiff(actions);
	}

	public App() {
		Run.initGenerators();
	}
  
	public void SaveDiff(final List<Action> actions) throws IOException {
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
				
				actionJson.put("src_pos", srcNode.getPos());
				actionJson.put("src_end", srcNode.getEndPos());
				if (dstNode != null) {
					actionJson.put("dst_pos", dstNode.getPos());
					actionJson.put("dst_end", dstNode.getEndPos());
				} else {
					actionJson.put("dst_pos", JSONObject.NULL);
					actionJson.put("dst_end", JSONObject.NULL);
				}
			}
			
			// 完成したJSONオブジェクトを配列に追加
			allActionsArray.put(actionJson);
		}
	// --- 3. JSONファイルへの書き出し ---
		try (FileWriter file = new FileWriter("GumTreedata.json")) {
			file.write(allActionsArray.toString(4));
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Successfully wrote action data to GumTreedata.json");
	}

	public List<Action> calculateEditScript(final String src, final String dst) throws IOException {
		final ITree srcTree = Generators.getInstance()
			.getTree(src)
			.getRoot();
		final ITree dstTree = Generators.getInstance()
			.getTree(dst)
			.getRoot();

		final String srcContent = readAll(src);
		final String dstContent = readAll(dst);
		

		final Matcher matcher = LGMatcher.create(srcContent, dstContent, srcTree, dstTree, mapping);
		matcher.match();

		final ActionGenerator generator = new ActionGenerator(srcTree, dstTree, mapping);
		generator.generate();

		return generator.getActions();
  	}

	private String readAll(final String path) throws IOException {
		return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
			.collect(Collectors.joining(System.getProperty("line.separator")));
	}
}