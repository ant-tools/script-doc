package js.tools.script.doc.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import js.tools.commons.ast.AstHandler;
import js.tools.commons.ast.Scanner;
import js.tools.commons.util.Classes;
import js.tools.script.doc.Context;
import js.tools.script.doc.JsDocFacade;
import js.tools.script.doc.doclet.JsDocRoot;
import junit.framework.TestCase;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

public class JsDocIntegrationTests extends TestCase
{
  private static final File SRC_DIR = new File("../client/src/");
  private static final File SCRITPS_DIR = new File("res/");

  public void _test() throws Exception
  {
    exercise(SRC_DIR, "js/dom/template/Template.js");
  }

  public void testEmptyClass() throws Exception
  {
    exercise(SCRITPS_DIR, "empty-class.js");
  }

  public void testAssignToMethod() throws Exception
  {
    exercise(SCRITPS_DIR, "assign-to-method.js");
  }

  public void testThrowsExternalError() throws Exception
  {
    exercise(SCRITPS_DIR, "throws-external-error.js");
  }

  public void testInnerStaticClass() throws Exception
  {
    exercise(SCRITPS_DIR, "in-class-utility.js");
  }

  private static void exercise(File codeBase, String sourceName) throws Exception
  {
    FileReader sourceReader = reader(codeBase, sourceName);
    Scanner scanner = new Scanner();
    Context context = new Context(scanner.getLog(), (JsDocFacade)Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl"));

    scanner.bind(AstRoot.class, (AstHandler)Classes.newInstance("js.tools.script.doc.RootHandler", context));
    scanner.bind(Assignment.class, (AstHandler)Classes.newInstance("js.tools.script.doc.AssignmentHandler", context));
    scanner.bind(FunctionCall.class, (AstHandler)Classes.newInstance("js.tools.script.doc.FunctionCallHandler", context));
    scanner.bind(PropertyGet.class, (AstHandler)Classes.newInstance("js.tools.script.doc.PropertyGetHandler", context));

    scanner.parse(sourceReader, sourceName);

    String[][] options = new String[5][2];
    options[0][0] = "-d";
    options[0][1] = "c://temp//jsdoc";
    options[1][0] = "-tag";
    options[1][1] = "assert:cm:Assert:";
    options[2][0] = "-tag";
    options[2][1] = "note:cfm:Note:";
    options[3][0] = "-link";
    options[3][1] = "http://api.bbnet.ro/window/";
    options[4][0] = "-noqualifier";
    options[4][1] = "window";

    JsDocRoot root = context.getJsDocRoot();
    root.setOptions(options);
    root.seal();
    System.out.println("=======================================");
    com.sun.tools.doclets.standard.Standard.start(root);

    // destroy doc root static since all tests are using the same virtual machine
    JsDocRoot.destroy();
  }

  private static FileReader reader(File dir, String scriptFile)
  {
    try {
      return new FileReader(new File(dir, scriptFile));
    }
    catch(FileNotFoundException e) {
      TestCase.fail("Script file not found: " + scriptFile);
      return null;
    }
  }
}
