package js.tools.script.doc.doclet;

import java.util.ArrayList;
import java.util.List;

import js.tools.commons.util.Classes;
import js.tools.script.doc.JsDocException;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

public class JsDocPackage extends JsDocProgramElement implements PackageDoc
{
  /**
   * Default package name used when package declaration is missing.
   */
  public static final String DEFAUL_PACKAGE_NAME = "window";

  private JsDocRoot jsDocRoot;
  private List<ClassDoc> classes = new ArrayList<ClassDoc>();
  private List<AnnotationTypeDoc> annotationTypes = new ArrayList<AnnotationTypeDoc>();

  /**
   * Construct js-doc package with given name.
   *
   * @param jsDocRoot document root.
   * @param packageName package name
   */
  JsDocPackage(JsDocRoot jsDocRoot, String packageName)
  {
    this.jsDocRoot = jsDocRoot;
    this.nodeName = packageName;
  }

  public void addClass(JsDocClass jsDocClass)
  {
    jsDocClass.setContainingPackage(this);
    this.classes.add(jsDocClass);
    this.jsDocRoot.addClass(jsDocClass);
  }

  /**
   * Create a new ordinary class into this package.
   * 
   * @param className class full name, i.e. qualified name plus dimension
   * @param modifiers class modifiers
   * @return newly created class
   */
  public JsDocClass createClass(String className, int modifiers)
  {
    return createClass(JsDocClass.class, className, modifiers);
  }

  public JsDocInterface createInterface(String className, int modifiers)
  {
    return createClass(JsDocInterface.class, className, modifiers);
  }

  public JsDocEnum createEnum(String className, int modifiers)
  {
    return createClass(JsDocEnum.class, className, modifiers);
  }

  public JsDocError createError(String className, int modifiers)
  {
    return createClass(JsDocError.class, className, modifiers);
  }

  public JsDocUtility createUtility(String className, int modifiers)
  {
    return createClass(JsDocUtility.class, className, modifiers);
  }

  <T extends JsDocClass> T createClass(Class<T> clazz, String className, int modifiers)
  {
    if(className == null) {
      throw new IllegalArgumentException();
    }
    if(className.indexOf('.') != -1) {
      throw new IllegalArgumentException("Invalid name for class creation: " + className);
    }

    String qualifiedClassName = this.nodeName.isEmpty() ? className : (this.nodeName + '.' + className);
    T t = null;
    try {
      t = Classes.newInstance(clazz, qualifiedClassName, modifiers);
    }
    catch(Exception e) {
      throw new JsDocException("Fail to create |%s| instance: %s", clazz.getCanonicalName(), e);
    }

    t.setContainingPackage(this);
    this.classes.add(t);
    this.jsDocRoot.addClass(t);
    return t;
  }

  public void add(AnnotationTypeDoc annotationTypeDoc)
  {
    this.annotationTypes.add(annotationTypeDoc);
  }

  @Override
  public ClassDoc[] allClasses()
  {
    return this.classes.toArray(new ClassDoc[this.classes.size()]);
  }

  @Override
  public ClassDoc[] allClasses(boolean filter)
  {
    return allClasses();
  }

  @Override
  public AnnotationTypeDoc[] annotationTypes()
  {
    return this.annotationTypes.toArray(new AnnotationTypeDoc[this.annotationTypes.size()]);
  }

  @Override
  public ClassDoc[] enums()
  {
    List<ClassDoc> enums = new ArrayList<ClassDoc>();
    for(ClassDoc classDoc : this.classes) {
      if(classDoc.isEnum()) enums.add(classDoc);
    }
    return enums.toArray(new ClassDoc[enums.size()]);
  }

  @Override
  public ClassDoc[] errors()
  {
    List<ClassDoc> errors = new ArrayList<ClassDoc>();
    for(ClassDoc classDoc : this.classes) {
      if(classDoc.isError()) errors.add(classDoc);
    }
    return errors.toArray(new ClassDoc[errors.size()]);
  }

  @Override
  public ClassDoc[] exceptions()
  {
    List<ClassDoc> exceptions = new ArrayList<ClassDoc>();
    for(ClassDoc classDoc : this.classes) {
      if(classDoc.isException()) exceptions.add(classDoc);
    }
    return exceptions.toArray(new ClassDoc[exceptions.size()]);
  }

  @Override
  public ClassDoc findClass(String className)
  {
    for(ClassDoc classDoc : this.classes) {
      if(classDoc.name().equals(className)) return classDoc;
    }
    return null;
  }

  @Override
  public ClassDoc[] interfaces()
  {
    List<ClassDoc> interfaces = new ArrayList<ClassDoc>();
    for(ClassDoc classDoc : this.classes) {
      if(classDoc.isInterface()) interfaces.add(classDoc);
    }
    return interfaces.toArray(new ClassDoc[interfaces.size()]);
  }

  @Override
  public ClassDoc[] ordinaryClasses()
  {
    List<ClassDoc> ordinaryClasses = new ArrayList<ClassDoc>();
    for(ClassDoc classDoc : this.classes) {
      if(classDoc.isOrdinaryClass()) ordinaryClasses.add(classDoc);
    }
    return ordinaryClasses.toArray(new ClassDoc[ordinaryClasses.size()]);
  }
}
