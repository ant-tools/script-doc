package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.tools.commons.ast.SemanticException;
import js.tools.script.doc.JsDocException;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;

/**
 * j(s)-doclet tree root for packages, classes and options.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public final class JsDocRoot extends JsDocNode implements RootDoc
{
  // ------------------------------------------------------
  // Doclet API RootDoc interface implementation

  /**
   * All doclet instance included classes. Included classes are those declared by user on command line options.
   */
  private ClassDoc[] includedClasses;

  /**
   * Return the included classes and interfaces in all packages. Included classes are those defined in command line as
   * package or class names considering access control options like <em>-private</em>. Please note that external linked
   * classes are not included.
   * 
   * @return included classes and interfaces in all packages.
   */
  @Override
  public ClassDoc[] classes()
  {
    if(this.includedClasses != null) {
      return this.includedClasses;
    }

    List<ClassDoc> includedClasses = new ArrayList<ClassDoc>();
    for(ClassDoc c : this.classes.values()) {
      if(c.isIncluded()) {
        includedClasses.add(c);
      }
    }
    ClassDoc[] includedClassesArray = includedClasses.toArray(new ClassDoc[includedClasses.size()]);
    if(!this.sealed) {
      return includedClassesArray;
    }
    this.includedClasses = includedClassesArray;
    return this.includedClasses;
  }

  /**
   * Lookup for j(s)-doc class. This method tries to locate the j(s)-doc class related to given class name. Note that
   * this method accepts full names but discards dimension. Also, if class name is not qualified, uses global scope.
   * 
   * @param className name of the class to search for.
   * @return class doc instance or null if not defined.
   */
  @Override
  public ClassDoc classNamed(String className)
  {
    if(className == null) return null;
    if(className.endsWith(CT.ELLIPSIS)) {
      return this.classes.get(className.substring(0, className.length() - 3));
    }
    int i = className.indexOf('[');
    if(i != -1) {
      return this.classes.get(className.substring(0, i));
    }
    return this.classes.get(className);
  }

  /**
   * Command line options.
   * 
   * @return an array of arrays of String.
   */
  @Override
  public String[][] options()
  {
    return this.options;
  }

  /**
   * Implements RootDoc#packageNamed interface. This implementation depart a little from interface specification in that
   * it returns default package instead of null, if searched package does not exist.
   * 
   * @param packageName the name of package to look for
   * @return searched PackageDoc or default package is searched package does not exist.
   * @see com.sun.javadoc.RootDoc#packageNamed(String)
   */
  @Override
  public PackageDoc packageNamed(String packageName)
  {
    return this.packages.get(packageName);
  }

  /**
   * Return the classes and interfaces specified as source file names on the command line. Current implementation does
   * not consider access control options so all specified classes are also included; for this reason this method just
   * delegates {@link #classes()}.
   * 
   * @return classes and interfaces specified on the command line.
   */
  @Override
  public ClassDoc[] specifiedClasses()
  {
    return classes();
  }

  /**
   * All this doclet instance included packages. Included packages are those declared by command line options.
   */
  private PackageDoc[] includedPackages;

  /**
   * Return the packages specified on the command line. Current implementation does not consider access control options
   * like <code>-private</code> or package inclusion control <code>-subpackages</code> and <code>-exclude</code>; for
   * this reason it simply returns all packages from command line. Please note that external linked packages are not
   * included.
   * 
   * @return packages specified on the command line.
   */
  @Override
  public PackageDoc[] specifiedPackages()
  {
    if(this.includedPackages != null) {
      return this.includedPackages;
    }

    List<PackageDoc> includedPackages = new ArrayList<PackageDoc>();
    for(PackageDoc p : this.packages.values()) {
      if(p.isIncluded()) {
        includedPackages.add(p);
      }
    }
    PackageDoc[] includedPackagesArray = includedPackages.toArray(new PackageDoc[includedPackages.size()]);
    if(!this.sealed) {
      return includedPackagesArray;
    }
    this.includedPackages = includedPackagesArray;
    return this.includedPackages;
  }

  // ------------------------------------------------------
  // This class specific implementation

  private String[][] options = new String[0][0];
  private Map<String, PackageDoc> packages = new HashMap<String, PackageDoc>();
  private Map<String, ClassDoc> classes = new HashMap<String, ClassDoc>();
  private boolean sealed;

  public void setOptions(String[][] options)
  {
    if(this.sealed) throw new JsDocException("Attempt to change sealed doclet.");
    this.options = options;
  }

  /**
   * Create a new package to this j(s)-doc root.
   * 
   * @param packageName package name
   * @return newly created package
   */
  public JsDocPackage createPackage(String packageName)
  {
    if(this.sealed) throw new JsDocException("Attempt to change sealed doclet.");
    JsDocPackage p = (JsDocPackage)this.packages.get(packageName);
    if(p == null) {
      p = new JsDocPackage(this, packageName);
      this.packages.put(packageName, p);
    }
    return p;
  }

  /**
   * Get class identified by qualified name. Get doclet class not discovered at scanning phase, hopefully defined into
   * external apidoc. Extract package name from class name; if class name is not qualified uses
   * {@link JsDocPackage#DEFAUL_PACKAGE_NAME}. Tries to get package from {@link #packages this packages list} and if not
   * found create and store it. Then uses package to find the class and if not found create it. Because this method is
   * specifically designed to be used after doclet sealed both package and class are created with <em>included</em> set
   * to false.
   * 
   * @param klass document class,
   * @param className qualified class name.
   * @param <T> auto cast.
   * @return requested doclet class.
   * @throws JsDocException if attempt to create package or class before doclet sealed.
   */
  @SuppressWarnings("unchecked")
  <T> T getLazyClass(Class<? extends JsDocClass> klass, String className)
  {
    JsDocClass jsDocClass = (JsDocClass)this.classes.get(className);
    if(jsDocClass != null) {
      return (T)jsDocClass;
    }

    String packageName = Utils.getPackageName(className);
    if(packageName == null) {
      packageName = JsDocPackage.DEFAUL_PACKAGE_NAME;
    }
    JsDocPackage jsDocPackage = (JsDocPackage)this.packages.get(packageName);
    if(jsDocPackage == null) {
      if(!this.sealed) throw new JsDocException("Lazy doclet packages can be created only after doclet root was sealed.");
      jsDocPackage = new JsDocPackage(this, packageName);
      this.packages.put(packageName, jsDocPackage);
      jsDocPackage.setIncluded(false);
    }

    String simpleClassName = Utils.getClassName(className);
    jsDocClass = (JsDocClass)jsDocPackage.findClass(simpleClassName);
    if(jsDocClass == null) {
      if(!this.sealed) throw new JsDocException("Lazy doclet class can be created only after doclet root was sealed.");
      // class MUST be public, otherwise sun standard apidoc generator will ugly broke with null pointer
      jsDocClass = jsDocPackage.createClass(klass, simpleClassName, Modifier.PUBLIC);
      jsDocClass.setIncluded(false);
    }
    return (T)jsDocClass;
  }

  public void addClass(JsDocClass jsDocClass)
  {
    if(this.classes.put(jsDocClass.qualifiedName(), jsDocClass) != null) {
      throw new SemanticException("Class already registered: " + jsDocClass.qualifiedName());
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T findClass(String className)
  {
    return (T)classNamed(className);
  }

  /**
   * Seal this doclet. Attempting to include package and/or classes after sealing will rise logic flaw exception.
   */
  public void seal()
  {
    this.sealed = true;
  }

  // ------------------------------------------------------
  // Doclet API DocErrorReporter implementation

  /**
   * Print error message and increment error count.
   * 
   * @param msg message to print.
   */
  @Override
  public void printError(String msg)
  {
    System.out.println(msg);
  }

  /**
   * Print an error message and increment error count.
   * 
   * @param pos the position item where the error occurs,
   * @param msg message to print.
   */
  @Override
  public void printError(SourcePosition pos, String msg)
  {
    System.out.println(msg);
  }

  /**
   * Print a message.
   * 
   * @param msg message to print.
   */
  @Override
  public void printNotice(String msg)
  {
    System.out.println(msg);
  }

  /**
   * Print a message.
   * 
   * @param pos the position item where the error occurs,
   * @param msg message to print.
   */
  @Override
  public void printNotice(SourcePosition pos, String msg)
  {
    System.out.println(msg);
  }

  /**
   * Print warning message and increment warning count.
   * 
   * @param msg message to print.
   */
  @Override
  public void printWarning(String msg)
  {
    System.out.println(msg);
  }

  /**
   * Print warning message and increment warning count.
   * 
   * @param pos the position item where the error occurs,
   * @param msg message to print.
   */
  @Override
  public void printWarning(SourcePosition pos, String msg)
  {
    System.out.println(msg);
  }

  // ------------------------------------------------------
  // singleton logic

  /**
   * Singleton instance.
   */
  private static JsDocRoot jsDocRoot = null;

  /**
   * Create a new instance of JsDocRoot singleton, for testing purposes only.
   * 
   * @return JsDocRoot instance.
   */
  public static JsDocRoot newInstance()
  {
    jsDocRoot = new JsDocRoot();
    return jsDocRoot;
  }

  /**
   * Get doc root singleton creating on the fly if not already created.
   * 
   * @return doc root instance.
   */
  public static JsDocRoot getInstance()
  {
    if(jsDocRoot == null) {
      jsDocRoot = new JsDocRoot();
    }
    return jsDocRoot;
  }

  /**
   * Destroy doc root singleton. This is necessary only if generate many apidoc trees in a single virtual machine
   * instance.
   */
  public static void destroy()
  {
    jsDocRoot = null;
  }

  /**
   * Forbid default constructor.
   */
  private JsDocRoot()
  {
  }
}
