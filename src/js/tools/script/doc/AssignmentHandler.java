package js.tools.script.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import js.tools.commons.ast.Names;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;

final class AssignmentHandler extends AbstractAstHandler
{
  private JsDocFacade facade;

  public AssignmentHandler(Context context)
  {
    super(context);
    this.facade = context.facade;
  }

  @Override
  public void handle(Node node)
  {
    Assignment assignment = (Assignment)node;
    if(assignment.getParent().getType() != Token.EXPR_RESULT) {
      // this assignment is not part of a global scope assignment; do not process it here
      return;
    }

    LeftName leftName = new LeftName(assignment);
    if(!leftName.isValid()) {
      this.log.warn("Invalid left name: %s", leftName.value());
      return;
    }
    RightValue rightValue = new RightValue(assignment);
    String rawComment = assignment.getJsDoc();

    if(leftName.isPrototypeBody()) {
      if(!rightValue.isObjectNode()) {
        this.log.warn("Invalid prototype body right value. Only object literal accepted.");
        return;
      }
      addMemebersFromPrototypeBody(leftName, rightValue.asObjectLiteral());
      return;
    }

    if(leftName.isPrototypeProperty()) {
      addMemeberFromPrototypeProperty(leftName.asClass(), leftName.asMember(), rightValue, rawComment);
      return;
    }

    if(rightValue.isFunctionNode()) {
      if(isMemberName(leftName.asMember())) {
        this.facade.addStaticMethod(leftName.asClass(), leftName.asMember(), rightValue.formalParameters(), rawComment);
      }
      else {
        createClassFromFunctionDeclaration(leftName.asOuterClass(), leftName.asClass(), rightValue, rawComment);
      }
      return;
    }

    if(rightValue.isObjectNode()) {
      if(isMemberName(leftName.asMember())) {
        this.facade.addStaticField(leftName.asClass(), leftName.asMember(), rightValue.type(), rawComment);
      }
      else {
        createClassFromObjectLiteral(leftName.asOuterClass(), leftName.asClass(), rightValue.asObjectLiteral(), rawComment);
      }
      return;
    }

    if(rightValue.isValueNode()) {
      if(isConstantName(leftName.asMember())) {
        this.facade.addStaticConstant(leftName.asClass(), leftName.asMember(), rightValue.type(), rightValue.value(), rawComment);
      }
      else {
        this.facade.addStaticField(leftName.asClass(), leftName.asMember(), rightValue.type(), rawComment);
      }
      return;
    }

    throw new IllegalStateException();
  }

  /**
   * Create class from function declaration. This helper method declares a new class and creates it constructor. If
   * given raw comment has @constructor annotation split it into class and constructor raw comments, otherwise uses at
   * it is only for class. Created class is an ordinary class, that is, not interface, enumeration or utility class.
   * Anyway, if class name obey naming convention for error or exception such type is created instead.
   * 
   * @param outerClassName outer class name,
   * @param className qualified class name,
   * @param rightValue right value,
   * @param rawComment raw comment.
   */
  private void createClassFromFunctionDeclaration(String outerClassName, String className, RightValue rightValue, String rawComment)
  {
    String[] rawComments = splitComment(rawComment);
    if(isErrorClass(className)) {
      this.facade.declareError(outerClassName, className, rawComments[0]);
    }
    else {
      this.facade.declareClass(outerClassName, className, rawComments[0]);
    }
    if(outerClassName != null) {
      className = outerClassName + '.' + className;
    }
    this.facade.addConstructor(className, rightValue.formalParameters(), rawComments[1]);
    this.context.scopes.add(new Scope(Scope.Type.FUNCTION, rightValue.asFunctionNode(), className));
  }

  /**
   * Add class member from function prototype. This helper method scans prototype body, which is an object literal and
   * applies {@link #addMemebersFromPrototypeBody(LeftName, ObjectLiteral)} for every property.
   * 
   * @param leftName left name,
   * @param prototypeBody prototype block.
   */
  private void addMemebersFromPrototypeBody(LeftName leftName, ObjectLiteral prototypeBody)
  {
    String className = leftName.asClass();
    if(leftName.asOuterClass() != null) {
      className = leftName.asOuterClass() + '.' + className;
    }
    this.context.scopes.add(new Scope(Scope.Type.PROTOTYPE, prototypeBody, className));
    for(ObjectProperty property : prototypeBody.getElements()) {
      String memberName = Names.getName(property);
      RightValue rightValue = new RightValue(property);
      String rawComment = property.getLeft().getJsDoc();
      addMemeberFromPrototypeProperty(className, memberName, rightValue, rawComment);
    }
  }

  /**
   * Add class member from prototype property. This helper ads a member to given class. If member assigned value is a
   * function add it as a method, otherwise as field. If member name obeys constant naming convention add the field as a
   * constant field. Because member attributes are extracted from a function prototype property method is added as
   * instance method whereas fields as class, i.e. static, fields.
   * 
   * @param className name of the parent class
   * @param memberName name of the member to be added
   * @param rightValue value assigned to given member
   * @param rawComment member raw comment, possible null
   */
  private void addMemeberFromPrototypeProperty(String className, String memberName, RightValue rightValue, String rawComment)
  {
    if(rightValue.isFunctionNode()) {
      if(rightValue.isAbstractMethod()) {
        this.facade.addAbstractMethod(className, memberName, rightValue.formalParameters(), rawComment);
      }
      else {
        this.facade.addMethod(className, memberName, rightValue.formalParameters(), rawComment);
      }
      return;
    }
    if(isConstantName(memberName)) {
      this.facade.addStaticConstant(className, memberName, rightValue.type(), rightValue.value(), rawComment);
    }
    else {
      this.facade.addStaticField(className, memberName, rightValue.type(), rawComment);
    }
  }

  /**
   * Create a class from object literal. Global assignment of an object literal to a qualified class name. There are
   * three types we can declare with such language construction differentiated by object structure:
   * <ol>
   * <li>interface - has at least one method, all methods are abstract and all fields are constants, if any</li>
   * <li>enumeration - has no methods, at least one field and all fields are constants</li>
   * <li>utility class - has at least one method and all methods are not abstract</li>
   * </ol>
   * 
   * @param outerClassName outer class name,
   * @param className the name of the class to be created
   * @param objectLiteral object literal containing class members
   * @param classRawComment class raw comment, possible null
   */
  private void createClassFromObjectLiteral(String outerClassName, String className, ObjectLiteral objectLiteral, String classRawComment)
  {
    List<InnerClass> innerClasses = new ArrayList<InnerClass>();
    List<Method> methods = new ArrayList<Method>();
    List<Field> fields = new ArrayList<Field>();
    int abstractMethodsCount = 0;
    boolean hasInnerClass = false;

    for(ObjectProperty objectProperty : objectLiteral.getElements()) {
      String leftName = Names.getName(objectProperty);
      RightValue rightValue = new RightValue(objectProperty);
      String rawComment = objectProperty.getLeft().getJsDoc();

      if(rightValue.isFunctionNode()) {
        if(leftName.equals("toString") && !rightValue.isAbstractMethod()) {
          // allow script interfaces declarations to implement toString for debugging purposes
          continue;
        }
        if(isClassName(leftName)) {
          hasInnerClass = true;
          createClassFromFunctionDeclaration(className, leftName, rightValue, rawComment);
          continue;
        }
        if(rightValue.isAbstractMethod()) {
          ++abstractMethodsCount;
        }
        Method method = new Method();
        method.name = leftName;
        method.formalParameters = rightValue.formalParameters();
        method.rawComment = rawComment;
        methods.add(method);
      }
      else {
        if(isClassName(leftName) && rightValue.isObjectNode()) {
          hasInnerClass = true;
          InnerClass innerClass = new InnerClass();
          innerClass.outerClassName = className;
          innerClass.className = leftName;
          innerClass.objectLiteral = rightValue.asObjectLiteral();
          innerClass.rawComment = rawComment;
          innerClasses.add(innerClass);
          // for this inner class className is outer class name whereas leftName is inner class name
          // createClassFromObjectLiteral(className, leftName, rightValue.asObjectLiteral(), rawComment);
          continue;
        }
        Field field = new Field();
        field.name = leftName;
        field.type = rightValue.type();
        field.value = rightValue.value();
        field.rawComment = rawComment;
        fields.add(field);
      }
    }

    String qualifiedClassName = outerClassName == null ? className : outerClassName + '.' + className;
    this.context.scopes.add(new Scope(Scope.Type.OBJECT, objectLiteral, qualifiedClassName));

    // declare interface and add abstract methods and static constants
    // an interface has at least one method, all methods are abstract and no fields at all
    if(!methods.isEmpty() && methods.size() == abstractMethodsCount) {
      this.facade.declareInterface(outerClassName, className, classRawComment);
      for(Field f : fields) {
        this.facade.addStaticConstant(qualifiedClassName, f.name, f.type, f.value, f.rawComment);
      }
      for(Method m : methods) {
        this.facade.addAbstractMethod(qualifiedClassName, m.name, m.formalParameters, m.rawComment);
      }
      for(InnerClass innerClass : innerClasses) {
        createClassFromObjectLiteral(innerClass.outerClassName, innerClass.className, innerClass.objectLiteral, innerClass.rawComment);
      }
      return;
    }

    // declare enumeration and add constant fields
    // an enumeration has no methods and at least one field
    if(methods.isEmpty() && !fields.isEmpty()) {
      this.facade.declareEnum(outerClassName, className, classRawComment);
      for(Field f : fields) {
        this.facade.addEnumConstant(qualifiedClassName, f.name, f.type, f.value, f.rawComment);
      }
      return;
    }

    // declare utility class and add static members
    // an utility class has at least one method and at least a method not abstract
    if(!methods.isEmpty() && abstractMethodsCount < methods.size()) {
      this.facade.declareUtility(outerClassName, className, classRawComment);
      for(Field f : fields) {
        if(isConstantName(f.name)) {
          this.facade.addStaticConstant(qualifiedClassName, f.name, f.type, f.value, f.rawComment);
        }
        else {
          this.facade.addStaticField(qualifiedClassName, f.name, f.type, f.rawComment);
        }
      }
      for(Method m : methods) {
        this.facade.addStaticMethod(qualifiedClassName, m.name, m.formalParameters, m.rawComment);
      }
      for(InnerClass innerClass : innerClasses) {
        createClassFromObjectLiteral(innerClass.outerClassName, innerClass.className, innerClass.objectLiteral, innerClass.rawComment);
      }
      return;
    }

    // declare mark interface, that is, no methods and no fields
    if(methods.isEmpty() && fields.isEmpty()) {
      if(hasInnerClass) {
        this.facade.declareUtility(outerClassName, className, classRawComment);
        for(InnerClass innerClass : innerClasses) {
          createClassFromObjectLiteral(innerClass.outerClassName, innerClass.className, innerClass.objectLiteral, innerClass.rawComment);
        }
      }
      else {
        this.facade.declareInterface(outerClassName, className, classRawComment);
      }
      return;
    }

    throw new IllegalStateException();
  }

  private static class InnerClass
  {
    String outerClassName;
    String className;
    ObjectLiteral objectLiteral;
    String rawComment;
  }

  private static class Method
  {
    String name;
    List<String> formalParameters;
    String rawComment;
  }

  private static class Field
  {
    String name;
    String type;
    Object value;
    String rawComment;
  }

  private static final Pattern CONSTRUCTOR_MARK = Pattern.compile("[ \\t]*@constructor[ \\t]*", Pattern.MULTILINE | Pattern.DOTALL);

  private static String[] splitComment(String rawComment)
  {
    String[] comments = new String[2];
    if(rawComment == null) return comments;

    Matcher m = CONSTRUCTOR_MARK.matcher(rawComment);
    if(m.find()) {
      int constructorMarkStartIndex = m.start();
      int constructorMarkEndIndex = m.end();
      comments[0] = rawComment.substring(0, constructorMarkStartIndex) + '/';
      comments[1] = "/**" + rawComment.substring(constructorMarkEndIndex);
    }
    else {
      comments[0] = rawComment;
    }
    return comments;
  }
}
