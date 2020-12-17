package js.tools.script.doc;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.tools.commons.ast.SemanticException;
import js.tools.commons.util.Strings;
import js.tools.script.doc.doclet.JsDocClass;
import js.tools.script.doc.doclet.JsDocComment;
import js.tools.script.doc.doclet.JsDocConstant;
import js.tools.script.doc.doclet.JsDocConstructor;
import js.tools.script.doc.doclet.JsDocEnum;
import js.tools.script.doc.doclet.JsDocEnumConstant;
import js.tools.script.doc.doclet.JsDocError;
import js.tools.script.doc.doclet.JsDocExecutableMember;
import js.tools.script.doc.doclet.JsDocField;
import js.tools.script.doc.doclet.JsDocInterface;
import js.tools.script.doc.doclet.JsDocMethod;
import js.tools.script.doc.doclet.JsDocPackage;
import js.tools.script.doc.doclet.JsDocParamTag;
import js.tools.script.doc.doclet.JsDocRoot;
import js.tools.script.doc.doclet.JsDocTag;
import js.tools.script.doc.doclet.JsDocThrowsTag;
import js.tools.script.doc.doclet.JsDocUtility;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

final class JsDocFacadeImpl implements JsDocFacade
{
  private JsDocRoot jsDocRoot = JsDocRoot.getInstance();

  @Override
  public JsDocRoot getJsDocRoot()
  {
    return this.jsDocRoot;
  }

  @Override
  public JsDocPackage declarePackage(String packageName)
  {
    if(packageName == null) throw new SemanticException("Null class name.");
    return this.jsDocRoot.createPackage(packageName);
  }

  @Override
  public void setPackageComment(String packageName, String rawComment)
  {
    if(packageName == null) throw new SemanticException("Null package name.");
    if(rawComment == null) throw new SemanticException("Null raw comment.");
    JsDocPackage jsDocPackage = (JsDocPackage)this.jsDocRoot.packageNamed(packageName);
    if(jsDocPackage == null) jsDocPackage = this.jsDocRoot.createPackage(packageName);
    jsDocPackage.createComment(rawComment);
  }

  @Override
  public void declareClass(String outerClassName, String className, String rawComment)
  {
    if(outerClassName != null) {
      declareInnerClass(JsDocClass.class, outerClassName, className, rawComment);
    }
    else {
      declareClass(JsDocClass.class, className, rawComment);
    }
  }

  @Override
  public void declareUtility(String outerClassName, String utilityName, String rawComment)
  {
    JsDocUtility jsDocUtility = null;
    if(outerClassName != null) {
      jsDocUtility = declareInnerClass(JsDocUtility.class, outerClassName, utilityName, rawComment);
    }
    else {
      jsDocUtility = declareClass(JsDocUtility.class, utilityName, rawComment);
    }
    JsDocConstructor jsDocConstructor = jsDocUtility.createConstructor(Modifier.PRIVATE);
    jsDocConstructor.setRawCommentText("Private constructor. Prevent default constructor synthesis for this utility class.");
  }

  @Override
  public void declareInterface(String outerClassName, String interfaceName, String rawComment)
  {
    if(outerClassName != null) {
      declareInnerClass(JsDocInterface.class, outerClassName, interfaceName, rawComment);
    }
    else {
      declareClass(JsDocInterface.class, interfaceName, rawComment);
    }
  }

  @Override
  public void declareEnum(String outerClassName, String enumName, String rawComment)
  {
    if(outerClassName != null) {
      declareInnerClass(JsDocEnum.class, outerClassName, enumName, rawComment);
    }
    else {
      declareClass(JsDocEnum.class, enumName, rawComment);
    }
  }

  @Override
  public void declareError(String outerClassName, String errorName, String rawComment)
  {
    if(outerClassName != null) {
      declareInnerClass(JsDocError.class, outerClassName, errorName, rawComment);
    }
    else {
      declareClass(JsDocError.class, errorName, rawComment);
    }
  }

  @Override
  public void declareClass(String className, String rawComment)
  {
    declareClass(JsDocClass.class, className, rawComment);
  }

  @Override
  public void declareEnum(String enumName, String rawComment)
  {
    declareClass(JsDocEnum.class, enumName, rawComment);
  }

  @Override
  public void declareError(String errorName, String rawComment)
  {
    declareClass(JsDocError.class, errorName, rawComment);
  }

  @Override
  public void declareInterface(String interfaceName, String rawComment)
  {
    declareClass(JsDocInterface.class, interfaceName, rawComment);
  }

  @Override
  public void declareUtility(String utilityName, String rawComment)
  {
    declareClass(JsDocUtility.class, utilityName, rawComment);
  }

  private <T extends JsDocClass> T declareClass(Class<T> clazz, String className, String rawComment)
  {
    String packageName = getPackageName(className);
    JsDocPackage jsDocPackage = (JsDocPackage)this.jsDocRoot.packageNamed(packageName);
    if(jsDocPackage == null) {
      // if package is not yet declared is because class is into global space and compilation unit has no $package
      // pseudo-operator; create default package instead
      jsDocPackage = this.jsDocRoot.createPackage(JsDocPackage.DEFAUL_PACKAGE_NAME);
    }

    String unqualifiedName = getClassName(className);
    int modifiers = modifiers(unqualifiedName);
    T t = createClass(jsDocPackage, clazz, unqualifiedName, modifiers);
    t.createComment(rawComment);
    return t;
  }

  @SuppressWarnings("unchecked")
  private <T extends JsDocClass> T createClass(JsDocPackage jsDocPackage, Class<T> clazz, String name, int modifiers)
  {
    if(jsDocPackage == null) {
      throw new IllegalArgumentException();
    }
    if(clazz.equals(JsDocInterface.class)) return (T)jsDocPackage.createInterface(name, modifiers);
    if(clazz.equals(JsDocEnum.class)) return (T)jsDocPackage.createEnum(name, modifiers);
    if(clazz.equals(JsDocError.class)) return (T)jsDocPackage.createError(name, modifiers);
    if(clazz.equals(JsDocUtility.class)) return (T)jsDocPackage.createUtility(name, modifiers);
    return (T)jsDocPackage.createClass(name, modifiers);
  }

  private <T extends JsDocClass> T declareInnerClass(Class<T> clazz, String outerClassName, String innerClassName, String rawComment)
  {
    if(outerClassName == null) throw new SemanticException("Null outer class name.");
    if(innerClassName == null) throw new SemanticException("Null inner class name.");
    if(!isQualified(outerClassName)) throw new SemanticException("Outer class name must be qualified.");
    if(isQualified(innerClassName)) throw new SemanticException("Inner class name must not be qualified.");

    JsDocClass outerClass = this.jsDocRoot.findClass(outerClassName);
    if(outerClass == null) throw new SemanticException("outer class not found: %s", outerClassName);
    T innerClass = createInnerClass(outerClass, clazz, innerClassName, modifiers(innerClassName));
    innerClass.createComment(rawComment);
    return innerClass;
  }

  @SuppressWarnings("unchecked")
  private <T extends JsDocClass> T createInnerClass(JsDocClass outerClass, Class<T> clazz, String name, int modifiers)
  {
    if(clazz.equals(JsDocInterface.class)) return (T)outerClass.createInnerInterface(name, modifiers);
    if(clazz.equals(JsDocEnum.class)) return (T)outerClass.createInnerEnum(name, modifiers);
    if(clazz.equals(JsDocError.class)) return (T)outerClass.createInnerError(name, modifiers);
    if(clazz.equals(JsDocUtility.class)) return (T)outerClass.createInnerUtilityClass(name, modifiers);
    return (T)outerClass.createInnerClass(name, modifiers);
  }

  @Override
  public void extendsClass(String subClassName, String superClassName)
  {
    if(subClassName == null) throw new SemanticException("Null subclass name.");
    if(superClassName == null) throw new SemanticException("Null super class name.");

    JsDocClass subClass = this.jsDocRoot.findClass(subClassName);
    if(subClass == null) throw new SemanticException("Class not found: %s", subClassName);
    subClass.setSuperClassName(superClassName);
  }

  @Override
  public void mixinClass(String className, String mixinName)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(mixinName == null) throw new SemanticException("Null mixin name.");

    JsDocClass clazz = this.jsDocRoot.findClass(className);
    if(clazz == null) throw new SemanticException("Class not found: %s", className);
    JsDocClass mixin = this.jsDocRoot.findClass(mixinName);
    if(mixin == null) throw new SemanticException("Class not found: %s", mixinName);

    for(FieldDoc field : mixin.fields()) {
      if(!classMemberExists(className, field.name())) {
        addField(className, field.name(), field.type().typeName(), 0, ((JsDocField)field).getRawComment());
      }
    }
    for(MethodDoc method : mixin.methods()) {
      if(!classMemberExists(className, method.name())) {
        List<String> formalParameters = new ArrayList<String>();
        for(Parameter parameter : method.parameters()) {
          formalParameters.add(parameter.name());
        }
        addMethod(className, method.name(), formalParameters, 0, ((JsDocMethod)method).getRawComment());
      }
    }
  }

  @Override
  public void implementsInterface(String className, String interfaceName)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(interfaceName == null) throw new SemanticException("Null interface name.");

    JsDocClass jsDocClass = this.jsDocRoot.findClass(className);
    if(jsDocClass == null) throw new SemanticException("class not found: %s", className);
    jsDocClass.addInterface(interfaceName);
  }

  @Override
  public void addConstructor(String className, List<String> formalParameters, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    JsDocClass jsDocClass = this.jsDocRoot.findClass(className);
    if(jsDocClass == null) throw new SemanticException("Class not found: %s", className);
    if(jsDocClass instanceof JsDocInterface) throw new SemanticException("Can't add constructor to interface %s.", className);
    if(jsDocClass instanceof JsDocUtility) throw new SemanticException("Can't add constructor to utility class %s.", className);

    JsDocConstructor jsDocConstructor = jsDocClass.createConstructor(modifiers(className));
    setSignature(jsDocConstructor, formalParameters, rawComment);
  }

  @Override
  public void addField(String className, String fieldName, String typeName, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(fieldName == null) throw new SemanticException("Null field name.");
    if(!classMemberExists(className, fieldName)) {
      addField(className, fieldName, typeName, 0, rawComment);
    }
  }

  @Override
  public void addStaticField(String className, String fieldName, String typeName, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(fieldName == null) throw new SemanticException("Null field name.");
    if(!classMemberExists(className, fieldName)) {
      addField(className, fieldName, typeName, Modifier.STATIC, rawComment);
    }
  }

  @Override
  public void addMethod(String className, String methodName, List<String> formalParameters, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(methodName == null) throw new SemanticException("Null method name.");
    if(!classMemberExists(className, methodName)) {
      addMethod(className, methodName, formalParameters, 0, rawComment);
    }
  }

  @Override
  public void addAbstractMethod(String className, String methodName, List<String> formalParameters, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(methodName == null) throw new SemanticException("Null method name.");
    if(!classMemberExists(className, methodName)) {
      addMethod(className, methodName, formalParameters, Modifier.ABSTRACT, rawComment);
    }
  }

  @Override
  public void addStaticMethod(String className, String methodName, List<String> formalParameters, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(methodName == null) throw new SemanticException("Null method name.");
    if(!classMemberExists(className, methodName)) {
      addMethod(className, methodName, formalParameters, Modifier.STATIC, rawComment);
    }
  }

  @Override
  public void addConstant(String className, String constantName, String typeName, Object value, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(constantName == null) throw new SemanticException("Null constant name.");
    JsDocClass jsDocClass = this.jsDocRoot.findClass(className);
    if(jsDocClass == null) throw new SemanticException("Class not found: %s", className);

    if(!classMemberExists(className, constantName)) {
      JsDocConstant jsDocConstant = jsDocClass.createConstant(constantName, modifiers(constantName));
      jsDocConstant.createComment(rawComment);
      setFieldType(jsDocConstant, typeName);
      jsDocConstant.setValue(value);
    }
  }

  @Override
  public void addStaticConstant(String className, String constantName, String typeName, Object value, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(constantName == null) throw new SemanticException("Null constant name.");
    JsDocClass jsDocClass = this.jsDocRoot.findClass(className);
    if(jsDocClass == null) throw new SemanticException("Class not found: %s", className);

    if(!classMemberExists(className, constantName)) {
      JsDocConstant jsDocConstant = jsDocClass.createConstant(constantName, modifiers(constantName) | Modifier.STATIC);
      jsDocConstant.createComment(rawComment);
      setFieldType(jsDocConstant, typeName);
      jsDocConstant.setValue(value);
    }
  }

  @Override
  public void addEnumConstant(String enumName, String constantName, String typeName, Object value, String rawComment)
  {
    if(enumName == null) throw new SemanticException("Null enumeration name.");
    if(constantName == null) throw new SemanticException("Null constant name.");
    JsDocEnum jsDocEnum = this.jsDocRoot.findClass(enumName);
    if(jsDocEnum == null) throw new SemanticException("Enumeration not found: %s", enumName);

    if(!classMemberExists(enumName, constantName)) {
      JsDocEnumConstant jsDocEnumConstant = jsDocEnum.createEnumConstant(constantName);
      jsDocEnumConstant.createComment(rawComment);
      setFieldType(jsDocEnumConstant, typeName);
      jsDocEnumConstant.setValue(value);
    }
  }

  private void addField(String className, String fieldName, String typeName, int modifiers, String rawComment)
  {
    if(className == null) throw new SemanticException("Null class name.");
    if(fieldName == null) throw new SemanticException("Null field name.");
    JsDocClass jsDocClass = this.jsDocRoot.findClass(className);
    if(jsDocClass == null) throw new SemanticException("Class not found: %s", className);

    JsDocField jsDocField = jsDocClass.createField(fieldName, modifiers | modifiers(fieldName));
    jsDocField.createComment(rawComment);
    setFieldType(jsDocField, typeName);
  }

  private void setFieldType(JsDocField jsDocField, String typeName)
  {
    JsDocComment jsDocComment = jsDocField.comment();
    JsDocTag typeTag = jsDocComment.typeTag();
    if(typeTag != null) {
      typeName = typeTag.text();
      jsDocComment.removeTag("@type");
    }
    jsDocField.setType(typeName);
  }

  private void addMethod(String className, String methodName, List<String> formalParameters, int modifiers, String rawComment)
  {
    JsDocClass jsDocClass = this.jsDocRoot.findClass(className);
    if(jsDocClass == null) throw new SemanticException("Class not found: %s", className);
    if(Modifier.isAbstract(modifiers) && jsDocClass.getClass() == JsDocClass.class) {
      jsDocClass.addModifiers(Modifier.ABSTRACT);
    }

    JsDocMethod jsDocMethod = jsDocClass.createMethod(methodName, modifiers | modifiers(methodName));
    if(jsDocMethod == null) throw new SemanticException("Method not found: %s#%s", className, methodName);

    setSignature(jsDocMethod, formalParameters, rawComment);
    setMethodReturnType(jsDocMethod);
  }

  private void setSignature(JsDocExecutableMember jsDocExecutable, List<String> formalParameters, String rawComment)
  {
    JsDocComment jsDocComment = jsDocExecutable.createComment(rawComment);

    for(int i = 0; i < jsDocComment.paramTags().length; i++) {
      JsDocParamTag p = jsDocComment.paramTags()[i];
      String parameterType = p.parameterName();
      String parameterName = Strings.firstWord(p.parameterComment());
      String parameterComment = Strings.removeFirstWord(p.parameterComment());

      if(i < formalParameters.size() && !parameterName.equals(formalParameters.get(i))) {
        throw new SemanticException("Formal parameter for %s does not match tag parameter counterpart: %s != %s", jsDocExecutable.qualifiedName(),
            formalParameters.get(i), parameterName);
      }
      p.setParameterName(parameterName);
      p.setParameterComment(parameterComment);

      jsDocExecutable.addParameter(parameterType, parameterName);
    }
    if(jsDocExecutable.parameters().length == 0) {
      // as a last resort uses formal parameters to initialize method parameters list, of undefined types
      for(String formalParameter : formalParameters) {
        jsDocExecutable.addParameter(formalParameter);
      }
    }
    else {
      if(jsDocExecutable.parameters().length < formalParameters.size()) {
        throw new SemanticException("More formal than tag parameters for %s", jsDocExecutable.qualifiedName());
      }
    }

    for(JsDocThrowsTag t : jsDocComment.throwsTags()) {
      String className = Strings.firstWord(t.text());
      String comment = Strings.removeFirstWord(t.text());
      t.setClassName(className);
      t.setComment(comment);
      jsDocExecutable.addThrownClassName(className);
    }
  }

  private void setMethodReturnType(JsDocMethod jsDocMethod)
  {
    JsDocComment jsDocComment = jsDocMethod.comment();
    JsDocTag returnTag = (JsDocTag)jsDocComment.returnTag();
    String returnType = null;
    if(returnTag != null) {
      String returnText = returnTag.text();
      String qualifiedTypeName = Strings.firstWord(returnText);
      returnType = qualifiedTypeName;
      returnTag.setText(Strings.removeFirstWord(returnText));
    }
    jsDocMethod.setReturnType(returnType);
  }

  // ------------------------------------------------------
  // private helpers

  private static String getPackageName(String qualifiedClassName)
  {
    if(qualifiedClassName == null) throw new SemanticException("Null class name.");
    if(qualifiedClassName.isEmpty()) throw new SemanticException("Empty class name.");
    int i = qualifiedClassName.lastIndexOf('.');
    if(i == -1) return "";// throw new SemanticException("Unqualified class name.");
    return qualifiedClassName.substring(0, i);
  }

  private static String getClassName(String qualifiedClassName)
  {
    if(qualifiedClassName == null) throw new SemanticException("Null class name.");
    if(qualifiedClassName.isEmpty()) throw new SemanticException("Empty class name.");
    return qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);
  }

  private static int modifiers(String name)
  {
    if(name.isEmpty()) throw new SemanticException("Empty name.");
    return name.charAt(0) == '_' ? Modifier.PRIVATE : Modifier.PUBLIC;
  }

  private static boolean isQualified(String name)
  {
    if(name.isEmpty()) throw new SemanticException("Empty name.");
    return name.indexOf('.') != -1;
  }

  private Map<String, Set<String>> classesMembers = new HashMap<String, Set<String>>();

  private boolean classMemberExists(String className, String memberName)
  {
    Set<String> members = this.classesMembers.get(className);
    if(members == null) {
      members = new HashSet<String>();
      members.add(memberName);
      this.classesMembers.put(className, members);
      return false;
    }
    return !members.add(memberName);
  }
}
