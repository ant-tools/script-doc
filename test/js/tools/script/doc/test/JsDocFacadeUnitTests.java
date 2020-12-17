package js.tools.script.doc.test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import js.tools.commons.util.Classes;
import js.tools.script.doc.JsDocFacade;
import js.tools.script.doc.doclet.JsDocClass;
import js.tools.script.doc.doclet.JsDocEnum;
import js.tools.script.doc.doclet.JsDocReferenceTag;
import js.tools.script.doc.doclet.JsDocRoot;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;

@SuppressWarnings("unchecked")
public class JsDocFacadeUnitTests extends TestCaseEx
{
  @SuppressWarnings("rawtypes")
  public void testDeclarePackage() throws Exception
  {
    String packageName = "js.net";
    String rawComment = "Package comment.\r\n" + //
        "@see j(s)-lib online manual";

    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage(packageName);
    facade.setPackageComment(packageName, rawComment);
    JsDocRoot root = facade.getJsDocRoot();

    assertEquals(0, root.classes().length);
    assertEquals(0, root.specifiedClasses().length);
    assertEquals(1, root.specifiedPackages().length);
    assertEquals(1, ((Map)Classes.getFieldValue(root, "packages")).size());
    assertNull(root.packageNamed("js.nets"));

    PackageDoc p = root.packageNamed(packageName);
    assertEquals(packageName, p.name());
    assertTrue(p.isIncluded());
    assertNull(p.position());
    assertNull(p.findClass("FakeClass"));

    assertEquals(0, p.allClasses().length);
    assertEquals(0, p.annotations().length);
    assertEquals(0, p.enums().length);
    assertEquals(0, p.errors().length);
    assertEquals(0, p.exceptions().length);
    assertEquals(0, p.interfaces().length);
    assertEquals(0, p.ordinaryClasses().length);

    assertTypes(p);
    assertComment(p, "Package comment.");
  }

  public void _testDeclareGlobalClass() throws Exception
  {
    String className = "String";
    String rawComment = "String class.\r\n" + //
        "@see j(s)-lib online manual";

    JsDocFacade facade = getJsDocFacade();
    facade.declareClass(className, rawComment);
    JsDocRoot root = facade.getJsDocRoot();

    ClassDoc c = root.classNamed(className);
    assertNotNull(c);
    assertEquals("String", c.name());
    assertEquals("window.String", c.qualifiedName());
  }

  public void testDeclareClass() throws Exception
  {
    String className = "js.net.Connection";
    String rawComment = "Class comment.\r\n" + //
        "@see j(s)-lib online manual";

    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage("js.net");
    facade.declareClass(className, rawComment);
    JsDocRoot root = facade.getJsDocRoot();

    assertEquals(1, root.classes().length);
    assertEquals(1, root.specifiedClasses().length);
    assertEquals(1, root.specifiedPackages().length);

    ClassDoc c = root.classNamed(className);
    assertNotNull(c);
    assertEquals(root.packageNamed("js.net"), c.containingPackage());
    assertEquals("Connection", c.name());
    assertEquals("js.net.Connection", c.qualifiedName());
    assertEquals("Connection", c.simpleTypeName());
    assertEquals("Connection", c.typeName());
    assertEquals("js.net.Connection", c.qualifiedTypeName());
    assertTrue(c.isIncluded());
    assertNull(c.position());
    assertNull(c.containingClass());
    assertEquals(Modifier.PUBLIC, c.modifierSpecifier());
    assertEquals("public", c.modifiers());
    assertEquals(c, c.asClassDoc());
    assertTrue(c.dimension().isEmpty());
    assertFalse(c.isPrimitive());

    assertEquals(0, c.annotations().length);
    assertEquals(0, c.constructors().length);
    assertEquals(0, c.fields().length);
    assertEquals(0, c.innerClasses().length);
    assertEquals(0, c.interfaces().length);
    assertEquals(0, c.interfaceTypes().length);
    assertEquals(0, c.methods().length);
    assertEquals(0, c.typeParameters().length);
    assertEquals(0, c.typeParamTags().length);

    assertTypes(c, "isClass", "isOrdinaryClass");
    assertModifiers(c, "isPublic");
    assertComment(c, "Class comment.");
  }

  public void testDeclareInnerClass() throws Exception
  {
    String outerClassName = "js.net.Connection";
    String outerRawComment = "Outer class.\r\n" + //
        "@see j(s)-lib online manual";

    String innerClassName = "Transport";
    String innerRawComment = "Inner class.\r\n" + //
        "@see j(s)-lib online manual";

    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage("js.net");
    facade.declareClass(outerClassName, outerRawComment);
    facade.declareClass(outerClassName, innerClassName, innerRawComment);
    JsDocRoot root = facade.getJsDocRoot();

    assertEquals(2, root.classes().length);
    assertEquals(2, root.specifiedClasses().length);
    assertEquals(1, root.specifiedPackages().length);

    ClassDoc c = root.classNamed("js.net.Connection.Transport");
    assertNotNull(c);
    assertEquals(root.packageNamed("js.net"), c.containingPackage());
    assertEquals(root.classNamed("js.net.Connection"), c.containingClass());
    assertEquals("Connection.Transport", c.name());
    assertEquals("js.net.Connection.Transport", c.qualifiedName());
    assertEquals("Transport", c.simpleTypeName());
    assertEquals("Connection.Transport", c.typeName());
    assertEquals("js.net.Connection.Transport", c.qualifiedTypeName());
    assertTrue(c.isIncluded());
    assertNull(c.position());
    assertEquals(Modifier.PUBLIC, c.modifierSpecifier());
    assertEquals("public", c.modifiers());
    assertEquals(c, c.asClassDoc());
    assertTrue(c.dimension().isEmpty());
    assertFalse(c.isPrimitive());

    assertEquals(0, c.annotations().length);
    assertEquals(0, c.constructors().length);
    assertEquals(0, c.fields().length);
    assertEquals(0, c.innerClasses().length);
    assertEquals(0, c.interfaces().length);
    assertEquals(0, c.interfaceTypes().length);
    assertEquals(0, c.methods().length);
    assertEquals(0, c.typeParameters().length);
    assertEquals(0, c.typeParamTags().length);

    assertTypes(c, "isClass", "isOrdinaryClass");
    assertModifiers(c, "isPublic");
    assertComment(c, "Inner class.");
  }

  public void testLinkTag() throws Exception
  {
    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage("js.net");

    String connectionName = "js.net.Connection";
    String connectionRawComment = "Connection class.\r\n";
    facade.declareClass(connectionName, connectionRawComment);
    facade.addMethod(connectionName, "send", Collections.EMPTY_LIST, null);

    String transportName = "js.net.Transport";
    String transportRawComment = "/**\r\n * Transport. See {@link js.net.Connection#send}.\r\n */";
    facade.declareClass(transportName, transportRawComment);

    JsDocRoot root = facade.getJsDocRoot();
    assertEquals(2, root.classes().length);
    assertEquals(2, root.specifiedClasses().length);
    assertEquals(1, root.specifiedPackages().length);

    JsDocClass transport = (JsDocClass)root.classNamed("js.net.Transport");
    JsDocReferenceTag reference = (JsDocReferenceTag)transport.inlineTags()[1];

    JsDocClass connectionClass = (JsDocClass)root.classNamed(connectionName);
    assertNotNull(connectionClass);
    assertEquals(connectionClass, reference.referencedClass());
    assertEquals("js.net.Connection", reference.referencedClassName());

    MemberDoc connectionMethod = connectionClass.findMethod("send");
    assertNotNull(connectionMethod);
    assertEquals(connectionMethod, reference.referencedMember());
    assertEquals("send", reference.referencedMemberName());
  }

  public void testVariableArguments() throws Exception
  {
    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage("js.net");
    facade.declareClass("js.net.Connection", null);

    String rawComment = "Inner class.\r\n" + //
        "@param URL url, resource URL,\r\n" + //
        "@param Object... data, data to sent.\r\n" + //
        "@see j(s)-lib online manual";
    facade.addMethod("js.net.Connection", "send", Arrays.asList("url"), rawComment);

    // TODO assertions
  }

  public void testMultipleFieldsDeclaration() throws Exception
  {
    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage("js.net");

    facade.declareClass("js.net.Connection", null);
    JsDocClass jsDocClass = facade.getJsDocRoot().findClass("js.net.Connection");
    List<FieldDoc> fields = Classes.getFieldValue(jsDocClass, "fields");
    final int LOOPS = 10;

    for(int i = 0; i < LOOPS; ++i) {
      facade.addField("js.net.Connection", "protocol", "String", null);
      assertLength(1, fields);
    }

    for(int i = 0; i < LOOPS; ++i) {
      facade.addStaticField("js.net.Connection", "HEADER", "String", null);
      assertLength(2, fields);
    }

    for(int i = 0; i < LOOPS; ++i) {
      facade.addConstant("js.net.Connection", "TIMEOUT", "Number", 1000, null);
      assertLength(3, fields);
    }

    for(int i = 0; i < LOOPS; ++i) {
      facade.addStaticConstant("js.net.Connection", "OFFSET", "Number", 1000, null);
      assertLength(4, fields);
    }

    facade.declareEnum("js.net.State", null);
    JsDocEnum jsDocEnum = facade.getJsDocRoot().findClass("js.net.State");
    fields = Classes.getFieldValue(jsDocEnum, "enumConstants");

    for(int i = 0; i < LOOPS; ++i) {
      facade.addEnumConstant("js.net.State", "DELAY", "Number", 1000, null);
      assertLength(1, fields);
    }
  }

  public void testMultipleMethodsDeclaration() throws Exception
  {
    JsDocFacade facade = getJsDocFacade();
    facade.declarePackage("js.net");

    facade.declareClass("js.net.Connection", null);
    JsDocClass jsDocClass = facade.getJsDocRoot().findClass("js.net.Connection");
    List<MethodDoc> methods = Classes.getFieldValue(jsDocClass, "methods");
    final int LOOPS = 10;

    for(int i = 0; i < LOOPS; ++i) {
      facade.addMethod("js.net.Connection", "send", Collections.EMPTY_LIST, null);
      assertLength(1, methods);
    }

    for(int i = 0; i < LOOPS; ++i) {
      facade.addAbstractMethod("js.net.Connection", "receive", Collections.EMPTY_LIST, null);
      assertLength(2, methods);
    }

    for(int i = 0; i < LOOPS; ++i) {
      facade.addStaticMethod("js.net.Connection", "loopback", Collections.EMPTY_LIST, null);
      assertLength(3, methods);
    }
  }

  public void testDefaultPackages()
  {

  }

  // ------------------------------------------------------
  // internal helpers

  private static void assertComment(Doc doc, String commentText)
  {
    try {
      doc.getRawCommentText();
      fail("Raw comment getter is used for 'operations like internalization' and is not supported by j(s)-doc.");
    }
    catch(UnsupportedOperationException e) {}

    assertEquals(commentText, doc.commentText());
    assertEquals(1, doc.inlineTags().length);
    assertEquals(1, doc.tags().length);
    assertEquals("j(s)-lib online manual", doc.tags("@see")[0].text());
    assertEquals(doc, doc.tags("@see")[0].holder());
    assertEquals("j(s)-lib online manual", doc.seeTags()[0].text());
    assertEquals(doc, doc.seeTags()[0].holder());
    assertEquals(0, doc.tags("@author").length);
  }

  private static final String[] MODIFIERS = new String[]
  {
      "isFinal", "isPackagePrivate", "isPrivate", "isProtected", "isPublic", "isStatic"
  };

  private static void assertModifiers(ProgramElementDoc object, String... modifiersToMatch)
  {
    for(String modifierPredicate : MODIFIERS) {
      boolean predicateResult = false;
      try {
        predicateResult = (Boolean)Classes.invoke(object, ProgramElementDoc.class, modifierPredicate);
      }
      catch(Throwable t) {
        throw new RuntimeException(t);
      }
      boolean predicateToMatchFound = false;
      for(String predicateToMatch : modifiersToMatch) {
        if(modifierPredicate.equals(predicateToMatch)) {
          predicateToMatchFound = true;
          break;
        }
      }
      if(predicateResult != predicateToMatchFound) fail("Modifier " + modifierPredicate + " should be true.");
    }
  }

  private static final String[] TYPES = new String[]
  {
      "isAnnotationType", "isAnnotationTypeElement", "isClass", "isConstructor", "isEnum", "isEnumConstant", "isError", "isException", "isField", "isInterface", "isMethod", "isOrdinaryClass"
  };

  /**
   * All Doc subclasses inherits a dozen of predicates like "isClass" or "isEnum". For a particular instance only couple
   * should be true. This assert check specifically for those true, as given in argument truePredicates. All others
   * should be false. This method <em>fail()</em> at first predicate found to be incorrectly.
   * 
   * @param object a Doc instance
   * @param typesToMatch list of - possible empty true predicates
   */
  private static void assertTypes(Doc object, String... typesToMatch)
  {
    for(String typePredicate : TYPES) {
      boolean predicateResult;
      try {
        predicateResult = (Boolean)Classes.invoke(object, Doc.class, typePredicate);
      }
      catch(Throwable t) {
        throw new RuntimeException(t);
      }
      boolean predicateToMatchFound = false;
      for(String predicateToMatch : typesToMatch) {
        if(typePredicate.equals(predicateToMatch)) {
          predicateToMatchFound = true;
          break;
        }
      }
      if(predicateResult != predicateToMatchFound) fail("Predicate " + typePredicate + " should be true.");
    }
  }

  private static JsDocFacade getJsDocFacade() throws Exception
  {
    JsDocFacade facade = Classes.newInstance("js.tools.script.doc.JsDocFacadeImpl");
    Classes.setFieldValue(facade, "jsDocRoot", JsDocRoot.newInstance());
    return facade;
  }
}
