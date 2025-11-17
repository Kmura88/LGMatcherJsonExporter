package LGMatcherJsonExporter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.kusumotolab.lgmatcher.LGMatcher;


/*
 * GumTreeの計算の大まかな流れとしては
 * AST木の構築 -> mapping計算 -> Actions計算
 */

public class GumTreeRunner {
	private final String srcPath;
	private final String dstPath;
	private List<Action> actions;
	private MappingStore mapping;
	private boolean useLGMatcher = true;
	
	public GumTreeRunner(final String _srcPath, final String _dstPath) {
		srcPath = _srcPath;
		dstPath = _dstPath;
		Run.initGenerators();
	}
	
	public void run(){
		ITree srcTree = null;
		ITree dstTree = null;
		try {
			srcTree = getITree(srcPath);
			dstTree = getITree(dstPath);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		if(useLGMatcher){ // LGMatcherを使ってmapping計算
			
			String srcContent="";
			String dstContent="";
			
			try {
				srcContent = readAll(srcPath);
				dstContent = readAll(dstPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			mapping = calculateMapping_LGMatcher(srcTree, dstTree, srcContent, dstContent);
			
		}else { //既存のMatcherでmapping計算
			mapping = calculateMapping_orginal(srcTree, dstTree);	
		}
		
		actions = calculateEditScript(srcTree, dstTree, mapping);
	}
	
	public List<Action> getActions(){
		return actions;
	}
	
	public MappingStore getmapping(){
		return mapping;
	}
	
	public void setUseLGMatcher(final boolean bool) {
		useLGMatcher = bool;
	}
	
	/*
	 * mappingとITreeからActionを計算
	 */
	public List<Action> calculateEditScript(final ITree srcTree, final ITree dstTree, final MappingStore mapping){
		final ActionGenerator generator = new ActionGenerator(srcTree, dstTree, mapping);
		generator.generate();
		return generator.getActions();
  	}
	
	/*
	 * 既存のMatcherで計算したmappingを返すメソッド
	 */
	public MappingStore calculateMapping_orginal(final ITree srcTree, final ITree dstTree){
		final Matcher matcher = com.github.gumtreediff.matchers.Matchers
	            .getInstance()
	            .getMatcher(srcTree, dstTree);
		matcher.match();
		return matcher.getMappings();
	}
	
	/*
	 *  LGMatcherで計算したmappingを返すメソッド
	 */
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
	
	private static String readAll(final String path) throws IOException {
		return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
			.collect(Collectors.joining(System.getProperty("line.separator")));
	}
}
