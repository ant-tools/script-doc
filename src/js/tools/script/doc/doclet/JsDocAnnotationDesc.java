package js.tools.script.doc.doclet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;

public class JsDocAnnotationDesc implements AnnotationDesc
{
  private AnnotationTypeDoc annotationType;
  private ElementValuePair[] elementValues;

  @Override
  public AnnotationTypeDoc annotationType()
  {
    return this.annotationType;
  }

  @Override
  public ElementValuePair[] elementValues()
  {
    return this.elementValues;
  }

  @Override
  public boolean isSynthesized()
  {
    return false;
  }
}
