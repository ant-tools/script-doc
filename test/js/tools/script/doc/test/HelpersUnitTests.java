package js.tools.script.doc.test;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import js.tools.commons.util.Classes;
import js.tools.script.doc.JsDocFacade;
import js.tools.script.doc.Main;

public class HelpersUnitTests extends TestCaseEx
{
  public void testAbstractAstHandler_isClassName() throws Throwable
  {
    Class<?> clazz = Class.forName("js.tools.script.doc.AbstractAstHandler");
    assertInvokeTrue(clazz, "isClassName", "ClassName");
    assertInvokeFalse(clazz, "isClassName", "className");
    assertInvokeThrows(StringIndexOutOfBoundsException.class, clazz, "isClassName", "");
    assertInvokeThrows(NullPointerException.class, clazz, "isClassName", (String)null);
  }

  public void testAbstractAstHandler_isMemberName() throws ClassNotFoundException
  {
    Class<?> clazz = Class.forName("js.tools.script.doc.AbstractAstHandler");
    assertInvokeTrue(clazz, "isMemberName", "className");
    assertInvokeTrue(clazz, "isMemberName", "_className");
    assertInvokeFalse(clazz, "isMemberName", "ClassName");
    assertInvokeThrows(StringIndexOutOfBoundsException.class, clazz, "isMemberName", "");
    assertInvokeThrows(NullPointerException.class, clazz, "isMemberName", (String)null);
  }

  public void testAbstractAstHandler_isConstantName() throws ClassNotFoundException
  {
    Class<?> clazz = Class.forName("js.tools.script.doc.AbstractAstHandler");
    assertInvokeTrue(clazz, "isConstantName", "CLASSNAME");
    assertInvokeTrue(clazz, "isConstantName", "CLASS_NAME");
    assertInvokeTrue(clazz, "isConstantName", "LOG2E");
    assertInvokeTrue(clazz, "isConstantName", "SQRT1_2");
    assertInvokeFalse(clazz, "isConstantName", "classname");
    assertInvokeFalse(clazz, "isConstantName", "className");
    assertInvokeFalse(clazz, "isConstantName", "ClassName");
    assertInvokeFalse(clazz, "isConstantName", "");
    assertInvokeThrows(NullPointerException.class, clazz, "isConstantName", (String)null);
  }

  public void testAbstractAstHandler_isErrorClass() throws ClassNotFoundException
  {
    Class<?> clazz = Class.forName("js.tools.script.doc.AbstractAstHandler");
    assertInvokeTrue(clazz, "isErrorClass", "Error");
    assertInvokeTrue(clazz, "isErrorClass", "ClassError");
    assertInvokeTrue(clazz, "isErrorClass", "CLASSError");
    assertInvokeFalse(clazz, "isErrorClass", "ERROR");
    assertInvokeFalse(clazz, "isErrorClass", "ClassERROR");
    assertInvokeFalse(clazz, "isErrorClass", "");
    assertInvokeThrows(NullPointerException.class, clazz, "isErrorClass", (String)null);
  }

  public void testAssignmentHandler_splitComment() throws Throwable
  {
    Class<?> clazz = Class.forName("js.tools.script.doc.AssignmentHandler");

    String rawComment = "/**\r\n * class comment\r\n * @constructor \r\n * constructor comment\r\n */";
    String[] comments = (String[])Classes.invoke(clazz, "splitComment", rawComment);
    assertEquals("/**\r\n * class comment\r\n */", comments[0]);
    assertEquals("/**\r\n * constructor comment\r\n */", comments[1]);

    rawComment = "/**\r\n * class comment\r\n * constructor comment\r\n */";
    comments = (String[])Classes.invoke(clazz, "splitComment", rawComment);
    assertEquals(rawComment, comments[0]);
    assertNull(comments[1]);
  }

  public void testJsDocFacadeImpl_getPackageName() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    assertInvokeEquals("js.tools.script.doc", facade, "getPackageName", "js.tools.script.doc.JsDocFacadeImpl");
    // assertInvokeThrows("Unqualified class name.", facade, "getPackageName", "JsDocFacadeImpl");
    assertInvokeThrows("Empty class name.", facade, "getPackageName", "");
    assertInvokeThrows("Null class name.", facade, "getPackageName", (String)null);
  }

  public void testJsDocFacadeImpl_getClassName() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    assertInvokeEquals("JsDocFacadeImpl", facade, "getClassName", "js.tools.script.doc.JsDocFacadeImpl");
    assertInvokeEquals("JsDocFacadeImpl", facade, "getClassName", "JsDocFacadeImpl");
    assertInvokeThrows("Empty class name.", facade, "getClassName", "");
    assertInvokeThrows("Null class name.", facade, "getClassName", (String)null);
  }

  public void testJsDocFacadeImpl_modifiers() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    assertInvokeEquals(Modifier.PUBLIC, facade, "modifiers", "JsDocFacadeImpl");
    assertInvokeEquals(Modifier.PRIVATE, facade, "modifiers", "_JsDocFacadeImpl");
    assertInvokeEquals(Modifier.PUBLIC, facade, "modifiers", "read");
    assertInvokeEquals(Modifier.PRIVATE, facade, "modifiers", "_read");
    assertInvokeThrows(NullPointerException.class, facade, "modifiers", (String)null);
    assertInvokeThrows("Empty name.", facade, "modifiers", "");
  }

  public void testJsDocFacadeImpl_isQualified() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    assertInvokeTrue(facade, "isQualified", "js.tools.script.doc.JsDocFacadeImpl");
    assertInvokeFalse(facade, "isQualified", "JsDocFacadeImpl");
    assertInvokeThrows(NullPointerException.class, facade, "isQualified", (String)null);
    assertInvokeThrows("Empty name.", facade, "isQualified", "");
  }

  public void testJsDocFaqcadeImpl_classMemeberExists() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    Map<String, Set<String>> classesMembers = Classes.getFieldValue(facade, "classesMembers");
    assertEmpty(classesMembers);

    assertInvokeFalse(facade, "classMemberExists", "js.net.Connection", "port");
    assertLength(classesMembers, "js.net.Connection", 1);

    assertInvokeTrue(facade, "classMemberExists", "js.net.Connection", "port");
    assertLength(classesMembers, "js.net.Connection", 1);

    assertInvokeFalse(facade, "classMemberExists", "js.net.Connection", "protocol");
    assertLength(classesMembers, "js.net.Connection", 2);

    assertInvokeTrue(facade, "classMemberExists", "js.net.Connection", "protocol");
    assertLength(classesMembers, "js.net.Connection", 2);

    assertInvokeFalse(facade, "classMemberExists", "js.net.Connection", "send");
    assertLength(classesMembers, "js.net.Connection", 3);

    assertInvokeTrue(facade, "classMemberExists", "js.net.Connection", "send");
    assertLength(classesMembers, "js.net.Connection", 3);

    assertInvokeFalse(facade, "classMemberExists", "js.net.Connection", "receive");
    assertLength(classesMembers, "js.net.Connection", 4);

    assertInvokeTrue(facade, "classMemberExists", "js.net.Connection", "receive");
    assertLength(classesMembers, "js.net.Connection", 4);
  }

  private static void assertLength(Map<String, Set<String>> classesMemebers, String className, int membersCount)
  {
    assertLength(1, classesMemebers);
    assertLength(membersCount, classesMemebers.get("js.net.Connection"));
  }

  public void testMain_parseConfig() throws Throwable
  {
    String commandLineArgs = "-verbose -private -author -version -sourcepath src -excludes bootstrap legacy -d apidoc -stylesheetfile apidoc/stylesheet.css";
    String args[] = commandLineArgs.split(" ");
    Object config = Classes.invoke(Main.class, "parseConfig", (Object)args);

    assertEquals("src", Classes.getFieldValue(config, "sourcepath"));

    String[][] options = Classes.getFieldValue(config, "options");
    assertEquals(5, options.length);

    assertEquals(1, options[0].length);
    assertEquals("-private", options[0][0]);

    assertEquals(1, options[1].length);
    assertEquals("-author", options[1][0]);

    assertEquals(1, options[2].length);
    assertEquals("-version", options[2][0]);

    assertEquals(2, options[3].length);
    assertEquals("-d", options[3][0]);
    assertEquals("apidoc", options[3][1]);

    assertEquals(2, options[4].length);
    assertEquals("-stylesheetfile", options[4][0]);
    assertEquals("apidoc/stylesheet.css", options[4][1]);
  }
}
