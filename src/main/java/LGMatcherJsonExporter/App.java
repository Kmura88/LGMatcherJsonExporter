package LGMatcherJsonExporter;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import LGMatcherJsonExporter.data.ActionRecords;
import LGMatcherJsonExporter.util.ActionConverter;
import LGMatcherJsonExporter.util.ActionRecordsJsonExporter;


public class App {
	
	private static final String OPTION_LGM = "-LGM";
	private static final String OPTION_M   = "-M";
	private static String jsonPath = "GumTreedata.json";
	private static String srcPath;
	private static String dstPath;
	
	/*
	 * args[0] : -LGM -M
	 * args[1] : JsonPath
	 * args[2] : srcPath
	 * args[3] : dstPath
	 */

	public static void main(final String[] args) throws IOException {
		
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
		
		final App app = new App(args[1], args[2], args[3]);
		
		if(args[0].equals(OPTION_LGM)) {
			app.run(true);
		}else if(args[0].equals(OPTION_M)) {
			app.run(false);
		}
	}

	public App(final String _jsonPath, final String _srcPath, final String _dstPath) {
		jsonPath = _jsonPath;
		srcPath  = _srcPath;
		dstPath  = _dstPath;
	}
	
	public void run(final boolean useLGMatcher){
		GumTreeRunner GTR = new GumTreeRunner(srcPath, dstPath);
		if(!useLGMatcher)GTR.setUseLGMatcher(false);
		GTR.run();
		
		ActionRecords records = ActionConverter.makeActionRecords(GTR.getActions(), GTR.getmapping());
		
		JSONObject json = ActionRecordsJsonExporter.toJson(records);
		
		try (FileWriter writer = new FileWriter(jsonPath)) {
		    writer.write(json.toString(4));
		    System.out.println("Successfully wrote action data to .json");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}