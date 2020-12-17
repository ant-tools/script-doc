package js.tools.script.doc.test;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collections;

import js.tools.commons.util.Classes;
import js.tools.script.doc.JsDocFacade;
import js.tools.script.doc.doclet.JsDocClass;
import js.tools.script.doc.doclet.JsDocInterface;
import js.tools.script.doc.doclet.JsDocMethod;
import js.tools.script.doc.doclet.JsDocPackage;
import js.tools.script.doc.doclet.JsDocRoot;
import js.tools.script.doc.doclet.RootDocDumper;
import junit.framework.TestCase;

import com.sun.javadoc.RootDoc;

public class JavaStandardGeneratorIntegrationTests extends TestCase
{
  /**
   * Create a doclet tree for a single class and generate HTML documentation with external references. Uses standard
   * Java generator, configured with -link option to search for JRE classes on external URL.
   * <p>
   * It seems, at least in this simple test case, that in order to generates external links for a doc class there are
   * two conditions:
   * <ol>
   * <li>doc class containing package is part of <em>package-list</em> from external URL,
   * <li>doc class isIncluded MUST return false.
   * </ol>
   * While first is obviously reasonable the second is not and is not documented, but this seems to be accordingly
   * sources I found. Maybe is place to mention that the key of this external linkage logic is a file residing on URL
   * root path, named <em>package-list</em> and containing all documented packages accessible from specified URL.
   * <p>
   * On class <code>com.sun.tools.doclets.internal.toolkit.util.Extern#isExternal(ProgramElementDoc)</code> there is
   * next line of code:
   * 
   * <pre>
   * return packageToItemMap.get(programElementDoc.containingPackage().name()) != null;
   * </pre>
   * 
   * where packageToItemMap is a map of package items initialized from <em>package-list</em>, clearly justifying first
   * stated condition.
   * <p>
   * On class <code>com.sun.tools.doclets.formats.html.LinkFactoryImpl#getClassLink(LinkInfo)</code> there is next
   * pseudo-code:
   * 
   * <pre>
   * if(classDoc.isIncluded()) {
   *    String filename = pathString(classLinkInfo);
   *    // some logic to create hyperlink string based on filename and write to HTML
   * }
   * else {
   *   String crossLink = m_writer.getCrossClassLink(...);
   *   // logic to write external link to HTML; please note that getCrossClassLink 'return a class cross link to external class documentation', excerpt from documentation.  
   * }
   * </pre>
   * 
   * To conclude on above snippet, if doc class isIncluded returns true a relative link is created into filesystem
   * otherwise the link points to external URL. Finally, please note that many -link options are allowed and therefore
   * many external URLs.
   * <p>
   * Thanks to <a href="http://grepcode.com/">http://grepcode.com/</a>, the only place I was able to find doclet
   * sources.
   * 
   * @throws Throwable for any kind of problems developer may want to dela with.
   */
  public void testExternalReference() throws Throwable
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("js.demo");
    JsDocClass jsDocClass = jsDocPackage.createClass("ExternalDemo", Modifier.PUBLIC);

    Object facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    Classes.invoke(facade, "extendsClass", "js.demo.ExternalDemo", "java.lang.Object");

    JsDocMethod jsDocMethod = jsDocClass.createMethod("toString", Modifier.PUBLIC);
    jsDocMethod.addParameter("java.util.Date", "date");
    jsDocMethod.setReturnType("java.lang.String");

    String[][] options = new String[2][2];
    options[0][0] = "-d";
    options[0][1] = "c:\\temp\\jsdoc";
    options[1][0] = "-link";
    options[1][1] = "http://docs.oracle.com/javase/6/docs/api/";

    jsDocRoot.setOptions(options);
    com.sun.tools.doclets.standard.Standard.start(jsDocRoot);
  }

  public void testGlobalSpaceClass() throws Throwable
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("");
    JsDocClass jsDocClass = jsDocPackage.createClass("XMLHttpRequest", Modifier.PUBLIC);
    jsDocClass.createMethod("send", Modifier.PUBLIC);

    String[][] options = new String[1][2];
    options[0][0] = "-d";
    options[0][1] = "c:\\temp\\jsdoc";

    jsDocRoot.setOptions(options);
    jsDocRoot.seal();
    Object facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    Classes.invoke(facade, "extendsClass", "XMLHttpRequest", "Object");

    com.sun.tools.doclets.standard.Standard.start(jsDocRoot);
  }

  public void testRootDocDump() throws Exception
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("");
    JsDocClass jsDocClass = jsDocPackage.createClass("X", Modifier.PUBLIC);
    jsDocClass.setSuperClassName("java.lang.Object");

    JsDocMethod jsDocMethod = jsDocClass.createMethod("toString", Modifier.PUBLIC);
    jsDocMethod.setReturnType("java.lang.String");

    // RootDocDumper dumper = new RootDocDumper(new FileWriter("c:/temp/jsclass"));
    Classes.setFieldValue(jsDocRoot, "sealed", false);
    // dumper.dump(jsDocRoot);
  }

  public void _testCustomDoclet()
  {
    // "-d", "c:/temp/jsdoc",
    com.sun.tools.javadoc.Main.main(new String[]
    {
        "c:/temp/java/X.java", "-doclet", "JavaStandardGeneratorIntegrationTests$Doclet"
    });
  }

  public void _testJsDocInterface()
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("js.net");
    JsDocInterface jsDocInterface = jsDocPackage.createInterface("ServiceChannel", Modifier.PUBLIC);

    JsDocMethod jsDocMethod = jsDocInterface.createMethod("register", Modifier.PUBLIC);
    jsDocMethod.addParameter("java.lang.Object...", "listeners");
    jsDocMethod.setReturnType("java.lang.String[]");

    com.sun.tools.doclets.standard.Standard.start(jsDocRoot);
  }

  public void _testJsDocClass()
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("js.net");

    JsDocClass jsDocClass = jsDocPackage.createClass("ServiceChannelImpl", Modifier.PUBLIC);
    jsDocClass.createConstructor(Modifier.PUBLIC);

    JsDocMethod jsDocMethod = jsDocClass.createMethod("register", Modifier.PUBLIC);
    jsDocMethod.addParameter("java.lang.Object...", "listeners");
    jsDocMethod.setReturnType("java.lang.String[]");

    com.sun.tools.doclets.standard.Standard.start(jsDocRoot);
  }

  public void _testJsDocInnerClass()
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("js.net");

    JsDocClass service = jsDocPackage.createClass("Service", Modifier.PUBLIC);
    service.createConstructor(Modifier.PUBLIC);

    JsDocClass connection = service.createInnerClass("Connection", Modifier.PUBLIC);
    connection.createConstructor(Modifier.PUBLIC);

    JsDocClass trasnport = connection.createInnerClass("Transport", Modifier.PUBLIC);
    trasnport.createConstructor(Modifier.PUBLIC);

    JsDocMethod jsDocMethod = trasnport.createMethod("send", Modifier.PUBLIC);
    jsDocMethod.addParameter("java.lang.Object...", "listeners");
    jsDocMethod.setReturnType("java.lang.String[]");

    com.sun.tools.doclets.standard.Standard.start(jsDocRoot);
  }

  public void _testLinkTag()
  {
    JsDocRoot jsDocRoot = getJsDocRoot();
    JsDocPackage jsDocPackage = jsDocRoot.createPackage("js.net");

    JsDocClass service = jsDocPackage.createClass("Service", Modifier.PUBLIC);
    service.createConstructor(Modifier.PUBLIC);
    service.createMethod("send", Modifier.PUBLIC);
    JsDocMethod method = service.createMethod("send", Modifier.PUBLIC);
    method.addParameter("String", "text");

    String rawComment = "Connection. See {@link js.net.Service#send(String)}. See also {@link js.net.Service#send()}.\r\n" + //
        "@see j(s)-lib online manual";

    JsDocClass connection = jsDocPackage.createClass("Connection", Modifier.PUBLIC);
    connection.createConstructor(Modifier.PUBLIC);
    connection.setRawCommentText(rawComment);

    com.sun.tools.doclets.standard.Standard.start(jsDocRoot);
  }

  @SuppressWarnings("unchecked")
  public void _testLinkTagFacade() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    facade.declarePackage("js.net");

    String connectionName = "js.net.Connection";
    String connectionRawComment = "Connection class.\r\n";
    facade.declareClass(null, connectionName, connectionRawComment);
    facade.addMethod(connectionName, "send", Collections.EMPTY_LIST, null);

    String transportName = "js.net.Transport";
    String transportRawComment = "/**\r\n * Transport. See {@link js.net.Connection#send()}.\r\n */";
    facade.declareClass(null, transportName, transportRawComment);

    String[][] options = new String[1][2];
    options[0][0] = "-d";
    options[0][1] = "c:\\temp\\jsdoc";

    JsDocRoot root = facade.getJsDocRoot();
    root.setOptions(options);
    com.sun.tools.doclets.standard.Standard.start(root);
  }

  private JsDocRoot getJsDocRoot()
  {
    String[][] options = new String[1][2];
    options[0][0] = "-d";
    options[0][1] = "c:\\temp\\jsdoc";

    JsDocRoot jsDocRoot = JsDocRoot.newInstance();
    jsDocRoot.setOptions(options);
    return jsDocRoot;
  }

  public static class Doclet
  {
    public static boolean start(RootDoc rootDoc) throws IOException
    {
      RootDocDumper dumper = new RootDocDumper(new FileWriter("c:/temp/javaclass"));
      dumper.dump(rootDoc);
      return true;
    }
  }
}
