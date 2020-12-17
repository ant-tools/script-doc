package js.tools.script.doc.test;

import java.util.List;

import js.tools.script.doc.JsDocFacade;
import js.tools.script.doc.doclet.JsDocPackage;
import js.tools.script.doc.doclet.JsDocRoot;

abstract class JsDocFacadeStub implements JsDocFacade
{
  int flag;

  @Override
  public void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addConstant(String className, String fieldName, String typeName, Object value, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addConstructor(String className, List<String> formalParameters, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addField(String className, String fieldName, String typeName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addStaticField(String className, String fieldName, String typeName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareClass(String outerClassName, String className, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareEnum(String outerClassName, String enumName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareError(String outerClassName, String errorName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareInterface(String outerClassName, String interfaceName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareClass(String className, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareEnum(String enumName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareError(String errorName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareInterface(String interfaceName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareUtility(String utilityName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsDocPackage declarePackage(String packageName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setPackageComment(String packageName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareUtility(String outerClassName, String utilityName, String rawComment)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void extendsClass(String subClassName, String superClassName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsDocRoot getJsDocRoot()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void mixinClass(String className, String mixinName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void implementsInterface(String className, String interfaceName)
  {
    throw new UnsupportedOperationException();
  }
}
