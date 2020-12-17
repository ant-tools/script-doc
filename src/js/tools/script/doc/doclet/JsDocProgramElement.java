package js.tools.script.doc.doclet;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import js.tools.commons.util.Strings;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;

abstract class JsDocProgramElement extends JsDocNode implements ProgramElementDoc
{
  protected JsDocPackage containingPackage;
  protected JsDocClass containingClass;
  protected int modifiers;

  void setContainingPackage(JsDocPackage containingPackage)
  {
    this.containingPackage = containingPackage;
  }

  void setContainingClass(JsDocClass containingClass)
  {
    this.containingClass = containingClass;
  }

  void setModifiers(int modifiers)
  {
    this.modifiers = modifiers;
  }

  public void addModifiers(int modifiers)
  {
    this.modifiers |= modifiers;
  }

  /**
   * Return this program element annotations.
   * 
   * @return always empty array as j(s)-script does not support annotations.
   */
  @Override
  public AnnotationDesc[] annotations()
  {
    return new AnnotationDesc[0];
  }

  @Override
  public ClassDoc containingClass()
  {
    return this.containingClass;
  }

  @Override
  public PackageDoc containingPackage()
  {
    return this.containingPackage;
  }

  @Override
  public boolean isFinal()
  {
    return Modifier.isFinal(this.modifiers);
  }

  @Override
  public boolean isPackagePrivate()
  {
    return !(Modifier.isPrivate(this.modifiers) || Modifier.isProtected(this.modifiers) || Modifier.isPublic(this.modifiers));
  }

  @Override
  public boolean isPrivate()
  {
    return Modifier.isPrivate(this.modifiers);
  }

  @Override
  public boolean isProtected()
  {
    return Modifier.isProtected(this.modifiers);
  }

  @Override
  public boolean isPublic()
  {
    return Modifier.isPublic(this.modifiers);
  }

  @Override
  public boolean isStatic()
  {
    return Modifier.isStatic(this.modifiers);
  }

  @Override
  public int modifierSpecifier()
  {
    return this.modifiers;
  }

  @Override
  public String modifiers()
  {
    List<String> modifiersList = new ArrayList<String>();
    if(Modifier.isSynchronized(this.modifiers)) modifiersList.add(CT.SYNCHRONIZED);
    if(Modifier.isNative(this.modifiers)) modifiersList.add(CT.NATIVE);
    if(Modifier.isStrict(this.modifiers)) modifiersList.add(CT.STRICT);
    if(Modifier.isPrivate(this.modifiers)) modifiersList.add(CT.PRIVATE);
    if(Modifier.isProtected(this.modifiers)) modifiersList.add(CT.PROTECTED);
    if(Modifier.isPublic(this.modifiers)) modifiersList.add(CT.PUBLIC);
    if(Modifier.isFinal(this.modifiers)) modifiersList.add(CT.FINAL);
    if(Modifier.isStatic(this.modifiers)) modifiersList.add(CT.STATIC);
    if(Modifier.isTransient(this.modifiers)) modifiersList.add(CT.TRANSIENT);
    if(Modifier.isVolatile(this.modifiers)) modifiersList.add(CT.VOLATILE);
    if(Modifier.isAbstract(this.modifiers)) modifiersList.add(CT.ABSTRACT);
    if(Modifier.isInterface(this.modifiers)) modifiersList.add(CT.INTERFACE);
    return Strings.join(modifiersList, ' ');
  }

  @Override
  public String qualifiedName()
  {
    // throw new UnsupportedOperationException();
    return name();
  }
}
