package js.tools.script.doc.doclet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class JsDocTypeVariable extends JsDocType implements TypeVariable
{
  public JsDocTypeVariable(String qualifiedTypeName)
  {
    super(qualifiedTypeName);
  }

  @Override
  public Type[] bounds()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProgramElementDoc owner()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public AnnotationDesc[] annotations()
  {
    return null;
  }
}
