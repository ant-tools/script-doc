package js.tools.script.doc.doclet;

import com.sun.javadoc.MemberDoc;

abstract class JsDocMember extends JsDocProgramElement implements MemberDoc
{
  @Override
  public boolean isSynthetic()
  {
    return false;
  }

  @Override
  public String qualifiedName()
  {
    return this.containingClass.qualifiedName() + '.' + name();
  }
}
