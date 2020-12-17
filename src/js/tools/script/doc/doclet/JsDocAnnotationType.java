package js.tools.script.doc.doclet;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;

public class JsDocAnnotationType extends JsDocClass implements AnnotationTypeDoc
{
  public JsDocAnnotationType(String name, int modifiers)
  {
    super(name, modifiers);
  }

  @Override
  public AnnotationTypeElementDoc[] elements()
  {
    throw new UnsupportedOperationException();
  }
}
