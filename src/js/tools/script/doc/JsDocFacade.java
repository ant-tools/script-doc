package js.tools.script.doc;

import java.util.List;

import js.tools.script.doc.doclet.JsDocPackage;
import js.tools.script.doc.doclet.JsDocRoot;

public interface JsDocFacade
{
  JsDocRoot getJsDocRoot();

  /**
   * Declare a package.
   * 
   * @param packageName package name
   * @return doc package.
   */
  JsDocPackage declarePackage(String packageName);

  /**
   * Set package comment.
   * 
   * @param packageName package name,
   * @param rawComment raw comment.
   */
  void setPackageComment(String packageName, String rawComment);

  /**
   * Declare a class. If outer class is null class name should be a qualified name and class is declared in package
   * scope, with package extracted from class name. If outer class is not null, class name is not qualified and class is
   * declared as nested class to given outer.
   * 
   * @param outerClassName outer class name, possible null,
   * @param className class name,
   * @param rawComment optional raw comment.
   */
  void declareClass(String outerClassName, String className, String rawComment);

  void declareClass(String className, String rawComment);

  void declareUtility(String outerClassName, String utilityName, String rawComment);

  void declareUtility(String utilityName, String rawComment);

  void declareInterface(String outerClassName, String interfaceName, String rawComment);

  void declareInterface(String interfaceName, String rawComment);

  void declareEnum(String outerClassName, String enumName, String rawComment);

  void declareEnum(String enumName, String rawComment);

  void declareError(String outerClassName, String errorName, String rawComment);

  void declareError(String errorName, String rawComment);

  void extendsClass(String subClassName, String superClassName);

  void implementsInterface(String className, String interfaceName);

  void mixinClass(String className, String mixinName);

  void addConstructor(String className, List<String> formalParameters, String rawComment);

  /**
   * Add class field. There is a hint about field type deducted from right value type. Only JavaScript basic types are
   * recognized. Note that if comment with @param tag is present this method overrides suggested type.
   * 
   * @param className owning class qualified name
   * @param fieldName name of the field to be added
   * @param typeName suggested basic type name
   * @param rawComment optional field raw comment
   */
  void addField(String className, String fieldName, String typeName, String rawComment);

  void addMethod(String className, String methodName, List<String> formalParameters, String rawComment);

  void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment);

  void addStaticField(String className, String fieldName, String typeName, String rawComment);

  void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment);

  void addConstant(String className, String fieldName, String typeName, Object value, String rawComment);

  void addStaticConstant(String className, String fieldName, String typeName, Object value, String rawComment);

  void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment);
}
