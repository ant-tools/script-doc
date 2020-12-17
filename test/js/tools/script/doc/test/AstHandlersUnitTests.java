package js.tools.script.doc.test;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import js.tools.commons.ast.AstHandler;
import js.tools.commons.ast.Scanner;
import js.tools.commons.util.Classes;
import js.tools.script.doc.Context;
import js.tools.script.doc.doclet.JsDocPackage;
import js.tools.script.doc.doclet.JsDocRoot;
import junit.framework.TestCase;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

// TODO add test cases for inner exception, error and utility
public class AstHandlersUnitTests extends TestCase
{
  public void testPseudoOperators()
  {
    int flag = exercise("language-extension.js", new JsDocFacadeStub()
    {
      public JsDocPackage declarePackage(String packageName)
      {
        this.flag |= 1;
        assertEquals("js.net", packageName);
        return null;
      }

      public void implementsInterface(String className, String interfaceName)
      {
        this.flag |= 2;
        assertEquals("js.net.URL", className);
        assertEquals("js.net.Address", interfaceName);
      }

      public void extendsClass(String subClassName, String superClassName)
      {
        this.flag |= 4;
        assertEquals("js.net.RPC", subClassName);
        assertEquals("js.net.XHR", superClassName);
      }
    });
    // flag should be 7 if every method was called at least once
    assertEquals(7, flag);
  }

  public void testGlobalClass()
  {
    int flag = exercise("global-class.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("Connection", className);
        assertEquals("/**\r\n * Global class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("Connection", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Global class constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("Connection", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n     * Global class field.\r\n     */", rawComment);
      }

      public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("Connection", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n     * Global class constant.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("Connection", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Global class method.\r\n     */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testGlobalInterface()
  {
    int flag = exercise("global-interface.js", new JsDocFacadeStub()
    {
      public void declareInterface(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("Service", className);
        assertEquals("/**\r\n * Global interface.\r\n */", rawComment);
      }

      public void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("Service", className);
        assertEquals("register", methodName);
        assertEquals(3, formalParameters.size());
        assertEquals("event", formalParameters.get(0));
        assertEquals("listener", formalParameters.get(1));
        assertEquals("scope", formalParameters.get(2));
        assertEquals("/**\r\n     * Global interface method.\r\n     */", rawComment);
      }
    });
    // flag should be 3 if every method was called at least once
    assertEquals(3, flag);
  }

  public void testGlobalEnum()
  {
    int flag = exercise("global-enum.js", new JsDocFacadeStub()
    {
      public void declareEnum(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("State", className);
        assertEquals("/**\r\n * Global enum.\r\n */", rawComment);
      }

      public void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment)
      {
        this.flag |= 2;
        assertEquals("State", enumName);
        assertEquals("OPENED", constantName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Global enum constant.\r\n     */", rawComment);
      }
    });
    // flag should be 3 if all methods was called at least once
    assertEquals(3, flag);
  }

  public void testGlobalUtility()
  {
    int flag = exercise("global-utility.js", new JsDocFacadeStub()
    {
      public void declareUtility(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("Utils", className);
        assertEquals("/**\r\n * Global utility.\r\n */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 2;
        assertEquals("Utils", className);
        assertEquals("DEFAULT_SEPARATOR", fieldName);
        assertEquals("String", typeName);
        assertEquals("'.'", value);
        assertEquals("/**\r\n     * Global utility constant.\r\n     */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("Utils", className);
        assertEquals("separator", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n     * Global utility field.\r\n     */", rawComment);
      }

      public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("Utils", className);
        assertEquals("split", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("string", formalParameters.get(0));
        assertEquals("separator", formalParameters.get(1));
        assertEquals("/**\r\n     * Global utility method.\r\n     */", rawComment);
      }
    });
    // flag should be 15 if every method was called at least once
    assertEquals(15, flag);
  }

  public void testGlobalError()
  {
    int flag = exercise("global-error.js", new JsDocFacadeStub()
    {
      public void declareError(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("ConnectionError", className);
        assertEquals("/**\r\n * Global error.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("ConnectionError", className);
        assertEquals(0, formalParameters.size());
        assertEquals("/**\r\n * Global error constructor.\r\n */", rawComment);
      }
    });
    // flag should be 3 if all methods was called at least once
    assertEquals(3, flag);
  }

  public void testPackageClass()
  {
    int flag = exercise("package-class.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Package class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Package class constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n     * Package class field.\r\n     */", rawComment);
      }

      public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n     * Package class constant.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Package class method.\r\n     */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testPackageInterface()
  {
    int flag = exercise("package-interface.js", new JsDocFacadeStub()
    {
      public void declareInterface(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Service", className);
        assertEquals("/**\r\n * Package interface.\r\n */", rawComment);
      }

      public void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Service", className);
        assertEquals("register", methodName);
        assertEquals(3, formalParameters.size());
        assertEquals("event", formalParameters.get(0));
        assertEquals("listener", formalParameters.get(1));
        assertEquals("scope", formalParameters.get(2));
        assertEquals("/**\r\n     * Package interface method.\r\n     */", rawComment);
      }
    });
    // flag should be 3 if every method was called at least once
    assertEquals(3, flag);
  }

  public void testPackageEnum()
  {
    int flag = exercise("package-enum.js", new JsDocFacadeStub()
    {
      public void declareEnum(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.State", className);
        assertEquals("/**\r\n * Package enum.\r\n */", rawComment);
      }

      public void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.State", enumName);
        assertEquals("OPENED", constantName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Package enum constant.\r\n     */", rawComment);
      }
    });
    // flag should be 3 if all methods was called at least once
    assertEquals(3, flag);
  }

  public void testPackageUtility()
  {
    int flag = exercise("package-utility.js", new JsDocFacadeStub()
    {
      public void declareUtility(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Utils", className);
        assertEquals("/**\r\n * Package utility.\r\n */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Utils", className);
        assertEquals("DEFAULT_SEPARATOR", fieldName);
        assertEquals("String", typeName);
        assertEquals("'.'", value);
        assertEquals("/**\r\n     * Package utility constant.\r\n     */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Utils", className);
        assertEquals("separator", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n     * Package utility field.\r\n     */", rawComment);
      }

      public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Utils", className);
        assertEquals("split", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("string", formalParameters.get(0));
        assertEquals("separator", formalParameters.get(1));
        assertEquals("/**\r\n     * Package utility method.\r\n     */", rawComment);
      }
    });
    // flag should be 15 if every method was called at least once
    assertEquals(15, flag);
  }

  public void testPackageError()
  {
    int flag = exercise("package-error.js", new JsDocFacadeStub()
    {
      public void declareError(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.ConnectionError", className);
        assertEquals("/**\r\n * Package error.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.ConnectionError", className);
        assertEquals(0, formalParameters.size());
        assertEquals("/**\r\n * Package error constructor.\r\n */", rawComment);
      }
    });
    // flag should be 3 if all methods was called at least once
    assertEquals(3, flag);
  }

  public void _testInClassClass()
  {
    int flag = exercise("in-class-class.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        if(outerClassName == null) return;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Transport", className);
        assertEquals("/**\r\n     * Nested class.\r\n     */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n     * Nested class constructor.\r\n     */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n         * Nested class field.\r\n         */", rawComment);
      }

      public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n         * Nested class constant.\r\n         */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n         * Nested class method.\r\n         */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void _testInClassInterface()
  {
    int flag = exercise("in-class-interface.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Outer class.\r\n */", rawComment);
      }

      public void declareInterface(String outerClassName, String interfaceName, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Service", interfaceName);
        assertEquals("/**\r\n     * Inner interface.\r\n     */", rawComment);
      }

      public void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Service", className);
        assertEquals("register", methodName);
        assertEquals(3, formalParameters.size());
        assertEquals("event", formalParameters.get(0));
        assertEquals("listener", formalParameters.get(1));
        assertEquals("scope", formalParameters.get(2));
        assertEquals("/**\r\n         * Inner interface method.\r\n         */", rawComment);
      }
    });
    // flag should be 7 if every method was called at least once
    assertEquals(7, flag);
  }

  public void _testInClassEnum()
  {
    int flag = exercise("in-class-enum.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Outer class.\r\n */", rawComment);
      }

      public void declareEnum(String outerClassName, String className, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("State", className);
        assertEquals("/**\r\n     * Inner enum.\r\n     */", rawComment);
      }

      public void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.State", enumName);
        assertEquals("OPENED", constantName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n         * Inner enum constant.\r\n         */", rawComment);
      }
    });
    // flag should be 7 if all methods was called at least once
    assertEquals(7, flag);
  }

  public void _testInClassUtility()
  {
    int flag = exercise("in-class-utility.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Outer class.\r\n */", rawComment);
      }

      public void declareUtility(String outerClassName, String className, String rawComment)
      {
        this.flag |= 2;
        if(outerClassName == null) return;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Utils", className);
        assertEquals("/**\r\n     * Inner utility.\r\n     */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Utils", className);
        assertEquals("DEFAULT_SEPARATOR", fieldName);
        assertEquals("String", typeName);
        assertEquals("'.'", value);
        assertEquals("/**\r\n         * Inner utility constant.\r\n         */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection.Utils", className);
        assertEquals("separator", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n         * Inner utility field.\r\n         */", rawComment);
      }

      public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection.Utils", className);
        assertEquals("split", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("string", formalParameters.get(0));
        assertEquals("separator", formalParameters.get(1));
        assertEquals("/**\r\n         * Inner utility method.\r\n         */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void _testInClassError()
  {
    int flag = exercise("in-class-error.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Outer class.\r\n */", rawComment);
      }

      public void declareError(String outerClassName, String className, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Error", className);
        assertEquals("/**\r\n     * Inner error.\r\n     */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Error", className);
        assertEquals(0, formalParameters.size());
        assertEquals("/**\r\n     * Inner error constructor.\r\n     */", rawComment);
      }
    });
    // flag should be 7 if all methods was called at least once
    assertEquals(7, flag);
  }

  public void testOutClassClass()
  {
    int flag = exercise("out-class-class.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Transport", className);
        assertEquals("/**\r\n * Inner class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Inner class constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n     * Inner class field.\r\n     */", rawComment);
      }

      public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n     * Inner class constant.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection.Transport", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Inner class method.\r\n     */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testOutClassInterface()
  {
    int flag = exercise("out-class-interface.js", new JsDocFacadeStub()
    {
      public void declareInterface(String outerClassName, String innerClassName, String rawComment)
      {
        this.flag |= 1;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Service", innerClassName);
        assertEquals("/**\r\n * Inner interface.\r\n */", rawComment);
      }

      public void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection.Service", className);
        assertEquals("register", methodName);
        assertEquals(3, formalParameters.size());
        assertEquals("event", formalParameters.get(0));
        assertEquals("listener", formalParameters.get(1));
        assertEquals("scope", formalParameters.get(2));
        assertEquals("/**\r\n     * Inner interface method.\r\n     */", rawComment);
      }
    });
    // flag should be 3 if every method was called at least once
    assertEquals(3, flag);
  }

  public void testOutClassEnum()
  {
    int flag = exercise("out-class-enum.js", new JsDocFacadeStub()
    {
      public void declareEnum(String outerClassName, String innerClassName, String rawComment)
      {
        this.flag |= 1;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("State", innerClassName);
        assertEquals("/**\r\n * Inner enum.\r\n */", rawComment);
      }

      public void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection.State", enumName);
        assertEquals("OPENED", constantName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Inner enum constant.\r\n     */", rawComment);
      }
    });
    // flag should be 3 if all methods was called at least once
    assertEquals(3, flag);
  }

  public void testOutClassUtility()
  {
    int flag = exercise("out-class-utility.js", new JsDocFacadeStub()
    {
      public void declareUtility(String outerClassName, String innerClassName, String rawComment)
      {
        this.flag |= 1;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Utils", innerClassName);
        assertEquals("/**\r\n * Inner utility.\r\n */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection.Utils", className);
        assertEquals("DEFAULT_SEPARATOR", fieldName);
        assertEquals("String", typeName);
        assertEquals("'.'", value);
        assertEquals("/**\r\n     * Inner utility constant.\r\n     */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Utils", className);
        assertEquals("separator", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n     * Inner utility field.\r\n     */", rawComment);
      }

      public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection.Utils", className);
        assertEquals("split", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("string", formalParameters.get(0));
        assertEquals("separator", formalParameters.get(1));
        assertEquals("/**\r\n     * Inner utility method.\r\n     */", rawComment);
      }
    });
    // flag should be 15 if every method was called at least once
    assertEquals(15, flag);
  }

  public void testOutClassError()
  {
    int flag = exercise("out-class-error.js", new JsDocFacadeStub()
    {
      public void declareError(String outerClassName, String innerClassName, String rawComment)
      {
        this.flag |= 1;
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Error", innerClassName);
        assertEquals("/**\r\n * Inner error.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection.Error", className);
        assertEquals(0, formalParameters.size());
        assertEquals("/**\r\n * Inner error constructor.\r\n */", rawComment);
      }
    });
    // flag should be 3 if all methods was called at least once
    assertEquals(3, flag);
  }

  public void testMembersInsideFunctionFromConstructor()
  {
    int flag = exercise("members-inside-function-from-constructor.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n         * Object field.\r\n         */", rawComment);
      }

      public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n         * Object constant.\r\n         */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Object method.\r\n     */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testMembersInsideFunctionFromObject()
  {
    int flag = exercise("members-inside-function-from-object.js", new JsDocFacadeStub()
    {
      public void declareUtility(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n         * Static field.\r\n         */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n         * Static constant.\r\n         */", rawComment);
      }

      public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Static method.\r\n     */", rawComment);
      }
    });
    // flag should be 15 if every method was called at least once
    assertEquals(15, flag);
  }

  public void testMembersInsideFunctionFromPrototype()
  {
    int flag = exercise("members-inside-function-from-prototype.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals(0, formalParameters.size());
        assertEquals("/**\r\n * Constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n         * Object field.\r\n         */", rawComment);
      }

      public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection", className);
        assertEquals("_DEFAULT_ROUTE", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n         * Object constant.\r\n         */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Object method.\r\n     */", rawComment);
      }
    });
    // flag should be 15 if every method was called at least once
    assertEquals(15, flag);
  }

  public void testMembersFromPrototype()
  {
    int flag = exercise("members-from-prototype.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Constructor.\r\n */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection", className);
        assertEquals("progress", fieldName);
        assertEquals("Boolean", typeName);
        assertEquals("/**\r\n     * Static field.\r\n     */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection", className);
        assertEquals("DELAY", fieldName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Static constant.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection", className);
        assertEquals("send", methodName);
        assertEquals(2, formalParameters.size());
        assertEquals("url", formalParameters.get(0));
        assertEquals("data", formalParameters.get(1));
        assertEquals("/**\r\n     * Object method.\r\n     */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testMulitlevelInnerClass()
  {
    int flag = exercise("multilevel-inner-class.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String innerClassName, String rawComment)
      {
        this.flag |= 1;
        if("js.net.Connection.Transport".equals(outerClassName)) {
          return;
        }
        assertEquals("js.net.Connection", outerClassName);
        assertEquals("Transport", innerClassName);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        if("js.net.Connection".equals(className)) return; // ignore outer class constructor
        assertEquals("js.net.Connection.Transport.Channel", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Inner constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection.Transport.Channel", className);
        assertEquals("_url", fieldName);
        assertEquals("Unknown", typeName);
        assertEquals("/**\r\n     * Inner object field.\r\n     */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection.Transport.Channel", className);
        assertEquals("DELAY", fieldName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Inner static constant.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection.Transport.Channel", className);
        assertEquals("send", methodName);
        assertEquals(1, formalParameters.size());
        assertEquals("data", formalParameters.get(0));
        assertEquals("/**\r\n     * Inner object method.\r\n     */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testStaticMembers()
  {
    int flag = exercise("static-members.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("js.net.Connection", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("js.net.Connection", className);
        assertEquals("url", formalParameters.get(0));
        assertEquals("/**\r\n * Constructor.\r\n */", rawComment);
      }

      public void addStaticField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("js.net.Connection", className);
        assertEquals("state", fieldName);
        assertEquals("String", typeName);
        assertEquals("/**\r\n * Static field.\r\n */", rawComment);
      }

      public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
      {
        this.flag |= 8;
        assertEquals("js.net.Connection", className);
        assertEquals("TIMEOUT", fieldName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n * Static constant.\r\n */", rawComment);
      }

      public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 16;
        assertEquals("js.net.Connection", className);
        assertEquals("config", methodName);
        assertEquals(1, formalParameters.size());
        assertEquals("cfg", formalParameters.get(0));
        assertEquals("/**\r\n * Static method.\r\n */", rawComment);
      }
    });
    // flag should be 31 if every method was called at least once
    assertEquals(31, flag);
  }

  public void testGlobalClassWithPrototype()
  {
    int flag = exercise("global-class-with-prototype.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("String", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("String", className);
        assertEquals("value", formalParameters.get(0));
        assertEquals("/**\r\n * Constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("String", className);
        assertEquals("length", fieldName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Object field.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("String", className);
        assertEquals("charAt", methodName);
        assertEquals(1, formalParameters.size());
        assertEquals("index", formalParameters.get(0));
        assertEquals("/**\r\n     * Object method.\r\n     */", rawComment);
      }
    });
    // flag should be 15 if all methods was called at least once
    assertEquals(15, flag);
  }

  public void testUnqualifiedMemberFromPrototypeProperty()
  {
    int flag = exercise("unqualified-member-from-prototype-property.js", new JsDocFacadeStub()
    {
      public void declareClass(String outerClassName, String className, String rawComment)
      {
        this.flag |= 1;
        assertNull(outerClassName);
        assertEquals("String", className);
        assertEquals("/**\r\n * Class.\r\n */", rawComment);
      }

      public void addConstructor(String className, List<String> formalParameters, String rawComment)
      {
        this.flag |= 2;
        assertEquals("String", className);
        assertEquals("value", formalParameters.get(0));
        assertEquals("/**\r\n * Constructor.\r\n */", rawComment);
      }

      public void addField(String className, String fieldName, String typeName, String rawComment)
      {
        this.flag |= 4;
        assertEquals("String", className);
        assertEquals("length", fieldName);
        assertEquals("Number", typeName);
        assertEquals("/**\r\n     * Object field.\r\n     */", rawComment);
      }

      public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
      {
        this.flag |= 8;
        assertEquals("String", className);
        assertEquals("charAt", methodName);
        assertEquals(1, formalParameters.size());
        assertEquals("index", formalParameters.get(0));
        assertEquals("/**\r\n * Object method.\r\n */", rawComment);
      }
    });
    // flag should be 15 if all methods was called at least once
    assertEquals(15, flag);
  }

  public void testPackageInfo() throws Exception
  {
    JsDocRoot.newInstance();
    Scanner scanner = new Scanner();
    Context context = new Context(scanner.getLog(), new JsDocFacadeStub()
    {
      public void setPackageComment(String packageName, String rawComment)
      {
        assertEquals("js.xml", packageName);
        assertNotNull(rawComment);
        assertEquals("/**\r\n * Package comment.\r\n */", rawComment);
      }
    });
    scanner.bind(AstRoot.class, (AstHandler)Classes.newInstance("js.tools.script.doc.RootHandler", context));
    scanner.parse(new FileReader(getFile("package-info.js")), "package-info.js");
  }

  private static int exercise(String sourceScript, JsDocFacadeStub facade)
  {
    try {
      JsDocRoot.newInstance();
      Scanner scanner = new Scanner();
      Context context = new Context(scanner.getLog(), facade);

      scanner.bind(Assignment.class, (AstHandler)Classes.newInstance("js.tools.script.doc.AssignmentHandler", context));
      scanner.bind(FunctionCall.class, (AstHandler)Classes.newInstance("js.tools.script.doc.FunctionCallHandler", context));
      scanner.bind(PropertyGet.class, (AstHandler)Classes.newInstance("js.tools.script.doc.PropertyGetHandler", context));

      File script = getFile(sourceScript);
      scanner.parse(new FileReader(script), sourceScript);
    }
    catch(Exception e) {
      e.printStackTrace();
      fail();
    }
    return facade.flag;
  }

  private static File getFile(String fileName)
  {
    String projectPath = System.getProperty("project.path");
    return new File(new File(new File(projectPath == null ? "." : projectPath), "res"), fileName);
  }
}
