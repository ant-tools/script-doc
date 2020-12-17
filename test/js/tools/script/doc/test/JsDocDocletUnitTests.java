package js.tools.script.doc.test;

import js.tools.commons.util.Classes;
import js.tools.script.doc.doclet.JsDocClass;
import js.tools.script.doc.doclet.JsDocComment;
import js.tools.script.doc.doclet.JsDocPackage;
import js.tools.script.doc.doclet.JsDocRoot;
import junit.framework.TestCase;

public class JsDocDocletUnitTests extends TestCase
{
  public void _testJsDocRoot_classNamed()
  {
    JsDocRoot jsDocRoot = JsDocRoot.newInstance();

    JsDocPackage ecmaLang = (JsDocPackage)jsDocRoot.packageNamed("ecma.lang");
    JsDocPackage w3cDom = (JsDocPackage)jsDocRoot.packageNamed("w3c.dom");
    JsDocPackage jsNet = jsDocRoot.createPackage("js.net");

    ecmaLang.createClass("String", 0);
    w3cDom.createClass("Document", 0);
    JsDocClass jsDocClass = jsNet.createClass("Connection", 0);
    jsDocClass.createInnerClass("Transport", 0);

    assertFindClass(jsDocRoot, "ecma.lang.String", "ecma.lang.String");
    assertFindClass(jsDocRoot, "String", "ecma.lang.String");
    assertFindClass(jsDocRoot, "w3c.dom.Document", "w3c.dom.Document");
    assertFindClass(jsDocRoot, "Document", "w3c.dom.Document");
    assertFindClass(jsDocRoot, "js.net.Connection", "js.net.Connection");
    assertFindClass(jsDocRoot, "js.net.Connection.Transport", "js.net.Connection.Transport");
  }

  private static void assertFindClass(JsDocRoot root, String classSearchedFor, String expectedClass)
  {
    JsDocClass jsDocClass = (JsDocClass)root.classNamed(classSearchedFor);
    assertNotNull(jsDocClass);
    assertEquals(expectedClass, jsDocClass.qualifiedName());
  }

  public void testJsDocClass_findConstructor()
  {
  }

  public void testJsDocClass_findField()
  {
  }

  public void testJsDocClass_findMethod()
  {
  }

  public void testJsDocComment_normalize() throws Throwable
  {
    String rawComment = "/**\r\n" + //
        " * Inner class.\r\n" + //
        " * <pre>\r\n" + //
        " *      // forward iteration\r\n" + //
        " *      while(it.hasNext()) {\r\n" + //
        " *          var item = it.next();\r\n" + //
        " *          . . .\r\n" + //
        " *      };\r\n" + //
        " * </pre>\r\n" + //
        " *\r\n" + //
        " * @param URL url, resource URL,\r\n" + //
        " * @param Object... data, data to sent.\r\n" + //
        " * @see j(s)-lib online manual\r\n" + //
        " */";

    String normalComment = "" + //
        "Inner class.\r\n" + //
        "<pre>\r\n" + //
        "     // forward iteration\r\n" + //
        "     while(it.hasNext()) {\r\n" + //
        "         var item = it.next();\r\n" + //
        "         . . .\r\n" + //
        "     };\r\n" + //
        "</pre>\r\n" + //
        "\r\n" + //
        "@param URL url, resource URL,\r\n" + //
        "@param Object... data, data to sent.\r\n" + //
        "@see j(s)-lib online manual";

    assertEquals(normalComment, Classes.invoke(JsDocComment.class, "normalize", rawComment));
  }
};
