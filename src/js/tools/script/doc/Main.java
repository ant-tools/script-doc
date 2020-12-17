package js.tools.script.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import js.tools.commons.ast.Log;
import js.tools.commons.ast.Scanner;
import js.tools.commons.util.Files;
import js.tools.script.doc.doclet.JsDocRoot;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

public class Main
{
  private static final String JS_EXT = ".js";
  private static final String VERBOSE = "-verbose";
  private static final String SOURCEPATH = "-sourcepath";
  private static final String EXCLUDES = "-excludes";

  public static void main(String[] args)
  {
    try {
      Main main = new Main();
      main.exec(parseConfig(args));
    }
    catch(FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private Config config;
  private Scanner scanner;

  private void exec(Config config) throws FileNotFoundException
  {
    this.config = config;
    this.scanner = new Scanner();
    Log log = this.scanner.getLog();
    JsDocFacade facade = JsDocFacadeProxy.getInstance(this.config, log, new JsDocFacadeImpl());
    Context context = new Context(log, facade);

    this.scanner.bind(AstRoot.class, new RootHandler(context));
    this.scanner.bind(Assignment.class, new AssignmentHandler(context));
    this.scanner.bind(FunctionCall.class, new FunctionCallHandler(context));
    this.scanner.bind(PropertyGet.class, new PropertyGetHandler(context));

    this.scanner.setVerbose(this.config.verbose);
    parseSourceFiles(this.scanner, new File(this.config.sourcepath));

    JsDocRoot root = context.facade.getJsDocRoot();
    root.setOptions(this.config.options);
    root.seal();

    print("--- End source files scanning ---");
    print("--- Invoke Java standard HTML documentation generator ---");
    com.sun.tools.doclets.standard.Standard.start(root);

    // necessary for batch apidoc generation when reuse this generation logic couple times per virtual machine instance
    JsDocRoot.destroy();
  }

  private void parseSourceFiles(Scanner scanner, File dir) throws FileNotFoundException
  {
    if(!dir.isDirectory()) throw new IllegalArgumentException("Need a directory!");
    for(File file : dir.listFiles()) {
      if(file.isDirectory()) {
        parseSourceFiles(scanner, file);
        continue;
      }
      if(!file.getName().endsWith(JS_EXT)) {
        continue;
      }
      if(this.config.excludes.contains(Files.basename(file))) {
        print("Exclude file |%s|.", file);
        continue;
      }
      Reader reader = new FileReader(file);
      try {
        scanner.parse(reader, file.getPath());
      }
      catch(Exception e) {
        print(e);
      }
    }
  }

  private void print(Exception e)
  {
    if(this.config.verbose) {
      e.printStackTrace();
    }
  }

  private void print(String format, Object... args)
  {
    if(this.config.verbose) {
      System.out.println(String.format(format, args));
    }
  }

  private static Config parseConfig(String[] args)
  {
    Config config = new Config();

    List<List<String>> optionsList = new ArrayList<List<String>>();
    List<String> optionValues = null;
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals(VERBOSE)) {
        config.verbose = true;
        continue;
      }
      if(args[i].equals(SOURCEPATH)) {
        config.sourcepath = args[++i];
        continue;
      }
      if(args[i].equals(EXCLUDES)) {
        while(i < args.length && args[++i].charAt(0) != '-') {
          config.excludes.add(args[i]);
        }
        --i;
        continue;
      }
      if(args[i].charAt(0) == '-') {
        optionValues = new ArrayList<String>();
        optionsList.add(optionValues);
      }
      optionValues.add(args[i]);
    }

    config.options = new String[optionsList.size()][];
    int optionIndex = 0;
    for(List<String> option : optionsList) {
      config.options[optionIndex++] = option.toArray(new String[option.size()]);
    }

    return config;
  }
}
