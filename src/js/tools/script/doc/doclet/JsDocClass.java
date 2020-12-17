package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import js.tools.commons.util.Classes;
import js.tools.script.doc.JsDocException;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/**
 * Represents a java class or interface and provides access to information about the class, the class's comment and
 * tags, and the members of the class. A JsDocClass only exists if it was processed in this run of javadoc. References
 * to classes which may or may not have been processed in this run are referred to using Type (which can be converted to
 * JsDocClass, if possible).
 * 
 * @author Iulian Rotaru since 1.0
 */
public class JsDocClass extends JsDocProgramElement implements ClassDoc
{
  private JsDocType type;
  private List<FieldDoc> fields = new ArrayList<FieldDoc>();
  private List<ConstructorDoc> constructors = new ArrayList<ConstructorDoc>();
  private List<MethodDoc> methods = new ArrayList<MethodDoc>();
  private String superClassName;
  private ClassDoc superClass;
  private Type superClassType;
  private List<String> interfaceNames = new ArrayList<String>();
  private ClassDoc[] interfaces;
  private List<ClassDoc> innerClasses = new ArrayList<ClassDoc>();
  private List<TypeVariable> typeParameters = new ArrayList<TypeVariable>();

  /**
   * Create doclet class with specified modifiers.
   * 
   * @param className qualified class name.
   * @param modifiers requested modifiers.
   */
  JsDocClass(String className, int modifiers)
  {
    this.type = new JsDocType(className);
    setName(unqualify(className));
    setModifiers(modifiers);
  }

  public void setSuperClassName(String superClassName)
  {
    this.superClassName = superClassName;
  }

  public JsDocClass createInnerClass(String className, int modifiers)
  {
    return createInner(JsDocClass.class, className, modifiers);
  }

  public JsDocInterface createInnerInterface(String className, int modifiers)
  {
    return createInner(JsDocInterface.class, className, modifiers);
  }

  public JsDocEnum createInnerEnum(String className, int modifiers)
  {
    return createInner(JsDocEnum.class, className, modifiers);
  }

  public JsDocError createInnerError(String className, int modifiers)
  {
    return createInner(JsDocError.class, className, modifiers);
  }

  public JsDocUtility createInnerUtilityClass(String className, int modifiers)
  {
    return createInner(JsDocUtility.class, className, modifiers);
  }

  private <T extends JsDocClass> T createInner(Class<T> clazz, String className, int modifiers)
  {
    T innerClass = null;
    try {
      innerClass = Classes.newInstance(clazz, this.containingPackage.name() + '.' + className, modifiers);
    }
    catch(Exception e) {
      throw new JsDocException("Fail to create |%s| instance: %s", clazz.getCanonicalName(), e);
    }
    innerClass.setContainingClass(this);
    this.innerClasses.add(innerClass);
    this.containingPackage.addClass(innerClass);
    return innerClass;
  }

  /**
   * Create a new constructor with given modifiers, for this js-doc class.
   * 
   * @param modifiers constructor modifiers
   * @return newly created constructor
   */
  public JsDocConstructor createConstructor(int modifiers)
  {
    JsDocConstructor c = new JsDocConstructor(name(), modifiers);
    c.setContainingClass(this);
    this.constructors.add(c);
    return c;
  }

  public JsDocMethod createMethod(String name, int modifiers)
  {
    JsDocMethod m = new JsDocMethod(name, modifiers);
    m.setContainingClass(this);
    this.methods.add(m);
    return m;
  }

  public JsDocField createField(String name, int modifiers)
  {
    JsDocField f = new JsDocField(name, modifiers);
    f.setContainingClass(this);
    this.fields.add(f);
    return f;
  }

  public JsDocConstant createConstant(String name, int modifiers)
  {
    JsDocConstant c = new JsDocConstant(name, modifiers);
    c.setContainingClass(this);
    this.fields.add(c);
    return c;
  }

  public void addInterface(String interfaceName)
  {
    this.interfaceNames.add(interfaceName);
  }

  @Override
  public ConstructorDoc[] constructors()
  {
    return this.constructors.toArray(new ConstructorDoc[this.constructors.size()]);
  }

  @Override
  public ConstructorDoc[] constructors(boolean filter)
  {
    return constructors();
  }

  /**
   * In script all fields are serializable so special class member serialPersistentFields is not used.
   */
  @Override
  public boolean definesSerializableFields()
  {
    return false;
  }

  @Override
  public FieldDoc[] enumConstants()
  {
    return new FieldDoc[0];
  }

  @Override
  public FieldDoc[] fields()
  {
    return this.fields.toArray(new FieldDoc[this.fields.size()]);
  }

  @Override
  public FieldDoc[] fields(boolean filter)
  {
    return fields();
  }

  @Override
  public ClassDoc findClass(String qualifiedClassName)
  {
    return JsDocRoot.getInstance().classNamed(qualifiedClassName);
  }

  private static final String[] EMPTY_PARAMETERS = new String[0];

  public MethodDoc findMethod(String methodName)
  {
    return searchMethod(methodName, EMPTY_PARAMETERS, new HashSet<JsDocClass>());
  }

  public MethodDoc findMethod(String methodName, String[] parameterTypes)
  {
    return searchMethod(methodName, parameterTypes, new HashSet<JsDocClass>());
  }

  private JsDocMethod searchMethod(String methodName, String[] parameterTypes, Set<JsDocClass> searched)
  {
    // circular dependencies protection: returns null if this class was already searched
    if(searched.contains(this)) return null;
    searched.add(this);

    for(MethodDoc m : this.methods) {
      JsDocMethod jsDocMethod = (JsDocMethod)m;
      if(jsDocMethod.name().equals(methodName) && jsDocMethod.hasParameterTypes(parameterTypes)) return jsDocMethod;
    }

    JsDocClass jsDocClass = (JsDocClass)containingClass();
    if(jsDocClass != null) {
      JsDocMethod jsDocMethod = jsDocClass.searchMethod(methodName, parameterTypes, searched);
      if(jsDocMethod != null) return jsDocMethod;
    }

    ClassDoc interfaces[] = interfaces();
    for(int i = 0; i < interfaces.length; i++) {
      jsDocClass = (JsDocClass)interfaces[i];
      JsDocMethod jsDocMethod = jsDocClass.searchMethod(methodName, parameterTypes, searched);
      if(jsDocMethod != null) return jsDocMethod;
    }

    jsDocClass = (JsDocClass)superclass();
    if(jsDocClass != null) {
      JsDocMethod jsDocMethod = jsDocClass.searchMethod(methodName, parameterTypes, searched);
      if(jsDocMethod != null) return jsDocMethod;
    }

    return null;
  }

  /**
   * Find constructor in this class.
   * 
   * @param parameterTypes the array of String for constructor parameters types.
   * @return the first ConstructorDoc which matches, null if not found.
   */
  public ConstructorDoc findConstructor(String[] parameterTypes)
  {
    for(ConstructorDoc c : this.constructors) {
      JsDocConstructor jsDocConstructor = (JsDocConstructor)c;
      if(jsDocConstructor.hasParameterTypes(parameterTypes)) return jsDocConstructor;
    }
    return null;
  }

  /**
   * Find a field in this class scope. Search order: this class, outer classes, interfaces, super classes. IMP: If see
   * tag is defined in an inner class, which extends a super class and if outerclass and the super class have a visible
   * field in common then Java compiler cribs about the ambiguity, but the following code will search in the above given
   * search order.
   * 
   * @param fieldName the unqualified name to search for.
   * @return the first FieldDocImpl which matches, null if not found.
   */
  public FieldDoc findField(String fieldName)
  {
    return searchField(fieldName, new HashSet<JsDocClass>());
  }

  private JsDocField searchField(String fieldName, Set<JsDocClass> searched)
  {
    // circular dependencies protection: if this class was already reached return null
    if(searched.contains(this)) return null;
    searched.add(this);

    for(FieldDoc f : this.fields) {
      if(f.name().equals(fieldName)) return (JsDocField)f;
    }

    JsDocClass jsDocClass = (JsDocClass)containingClass();
    if(jsDocClass != null) {
      JsDocField jsDocField = jsDocClass.searchField(fieldName, searched);
      if(jsDocField != null) return jsDocField;
    }

    ClassDoc interfaces[] = interfaces();
    for(int i = 0; i < interfaces.length; i++) {
      jsDocClass = (JsDocClass)interfaces[i];
      JsDocField jsDocField = jsDocClass.searchField(fieldName, searched);
      if(jsDocField != null) return jsDocField;
    }

    jsDocClass = (JsDocClass)superclass();
    if(jsDocClass != null) {
      JsDocField jsDocField = jsDocClass.searchField(fieldName, searched);
      if(jsDocField != null) return jsDocField;
    }

    return null;
  }

  private static final ClassDoc[] EMPTY_IMPORTED_CLASSES = new ClassDoc[0];
  private static final PackageDoc[] EMPTY_IMPORTED_PACKAGES = new PackageDoc[0];

  @Override
  public ClassDoc[] importedClasses()
  {
    return (ClassDoc[])EMPTY_IMPORTED_CLASSES;
  }

  @Override
  public PackageDoc[] importedPackages()
  {
    return (PackageDoc[])EMPTY_IMPORTED_PACKAGES;
  }

  @Override
  public ClassDoc[] innerClasses()
  {
    return this.innerClasses.toArray(new ClassDoc[this.innerClasses.size()]);
  }

  @Override
  public ClassDoc[] innerClasses(boolean filter)
  {
    return innerClasses();
  }

  @Override
  public Type[] interfaceTypes()
  {
    return interfaces();
  }

  @Override
  public ClassDoc[] interfaces()
  {
    if(this.interfaces == null) {
      this.interfaces = new ClassDoc[this.interfaceNames.size()];
      for(int i = 0; i < this.interfaceNames.size(); ++i) {
        ClassDoc intf = findClass(this.interfaceNames.get(i));
        if(intf == null) throw new IllegalStateException("Interface not found: " + this.interfaceNames.get(i));
        this.interfaces[i] = findClass(this.interfaceNames.get(i));
      }
    }
    return this.interfaces;
  }

  @Override
  public boolean isAbstract()
  {
    return Modifier.isAbstract(this.modifiers);
  }

  /**
   * Return true if this class implements or interface extends java.io.Externalizable. Note that because script classes
   * are considered not externalizable for the purpose of this script doclet, this method always returns false.
   * 
   * @return always return false.
   */
  @Override
  public boolean isExternalizable()
  {
    return false;
  }

  /**
   * Return true if this class implements or interface extends java.io.Serializable. Since java.io.Externalizable
   * extends java.io.Serializable, Externalizable objects are also Serializable. Note that because script classes are
   * considered not serializable for the purpose of this script doclet, this method always returns false.
   * 
   * @return always return false.
   */
  @Override
  public boolean isSerializable()
  {
    return false;
  }

  @Override
  public MethodDoc[] methods()
  {
    return this.methods.toArray(new MethodDoc[this.methods.size()]);
  }

  @Override
  public MethodDoc[] methods(boolean filter)
  {
    return methods();
  }

  /**
   * In script all fields are serializable.
   */
  @Override
  public FieldDoc[] serializableFields()
  {
    return fields();
  }

  /**
   * In script all methods are serializable.
   */
  @Override
  public MethodDoc[] serializationMethods()
  {
    return methods();
  }

  @Override
  public boolean subclassOf(ClassDoc candidateSuperclass)
  {
    return false;
  }

  /**
   * Return the superclass of this class. Under normal use case this getter is called by doclet processor, that is, the
   * standard Java apidoc generator, AFTER all source files were scanned. This is important because super class instance
   * is lazy initialized at first call.
   * 
   * @return this doc class superclass.
   * @throws IllegalStateException if this class is ordinary class or error and superclass name is null.
   */
  @Override
  public ClassDoc superclass()
  {
    if(this.superClass == null) {
      if(this.superClassName == null) {
        if((this.getClass() == JsDocClass.class || this.getClass() == JsDocError.class) && this.isIncluded() && !CT.OBJECT.equals(name())) {
          throw new IllegalStateException(String.format("Class |%s| has no superclass declared.", qualifiedName()));
        }
        return null;
      }
      this.superClass = JsDocRoot.getInstance().getLazyClass(JsDocClass.class, this.superClassName);
      this.superClassType = this.superClass;
    }
    return (ClassDoc)this.superClass;
  }

  @Override
  public Type superclassType()
  {
    return this.superClassType;
  }

  @Override
  public ParamTag[] typeParamTags()
  {
    return new ParamTag[0];
  }

  @Override
  public TypeVariable[] typeParameters()
  {
    return this.typeParameters.toArray(new TypeVariable[this.typeParameters.size()]);
  }

  @Override
  public boolean isClass()
  {
    return true;
  }

  @Override
  public boolean isEnum()
  {
    return false;
  }

  @Override
  public boolean isEnumConstant()
  {
    return false;
  }

  @Override
  public boolean isError()
  {
    return false;
  }

  @Override
  public boolean isException()
  {
    return false;
  }

  @Override
  public boolean isField()
  {
    return false;
  }

  @Override
  public boolean isInterface()
  {
    return false;
  }

  @Override
  public boolean isMethod()
  {
    return false;
  }

  @Override
  public boolean isOrdinaryClass()
  {
    return true;
  }

  @Override
  public AnnotationTypeDoc asAnnotationTypeDoc()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClassDoc asClassDoc()
  {
    return this;
  }

  @Override
  public ParameterizedType asParameterizedType()
  {
    return null;
  }

  @Override
  public TypeVariable asTypeVariable()
  {
    return null;
  }

  @Override
  public WildcardType asWildcardType()
  {
    return null;
  }

  @Override
  public String dimension()
  {
    return this.type.dimension();
  }

  @Override
  public boolean isPrimitive()
  {
    return false;
  }

  @Override
  public String typeName()
  {
    return name();
  }

  @Override
  public String simpleTypeName()
  {
    return this.type.simpleTypeName();
  }

  @Override
  public String qualifiedTypeName()
  {
    return qualifiedName();
  }

  @Override
  public String name()
  {
    return this.containingClass != null ? (this.containingClass.name() + '.' + super.name()) : super.name();
  }

  @Override
  public String qualifiedName()
  {
    return this.containingClass != null ? (this.containingClass.qualifiedName() + '.' + super.name()) : this.type.qualifiedTypeName();
  }

  @Override
  public String toString()
  {
    return qualifiedName();
  }

  @Override
  public AnnotatedType asAnnotatedType()
  {
    return null;
  }

  @Override
  public Type getElementType()
  {
    return null;
  }
}
