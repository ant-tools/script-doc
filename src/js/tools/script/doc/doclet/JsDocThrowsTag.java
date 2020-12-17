package js.tools.script.doc.doclet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

public class JsDocThrowsTag extends JsDocTag implements ThrowsTag
{
  private JsDocError jsDocError;
  private String className;
  private String comment;

  public JsDocThrowsTag(JsDocComment parentComment, String name, String text)
  {
    super(parentComment, name, text);
    String[] a = Utils.divideAtWhitespace(text);
    this.className = a[0];
    this.comment = a[1];
  }

  @Override
  public String kind()
  {
    return CT.THROWS_TAG;
  }

  @Override
  public ClassDoc exception()
  {
    if(this.jsDocError == null) {
      this.jsDocError = (JsDocError)JsDocRoot.getInstance().getLazyClass(JsDocError.class, this.className);
    }
    return this.jsDocError;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public void setComment(String comment)
  {
    this.comment = comment;
    this.text = comment;
  }

  @Override
  public String exceptionComment()
  {
    return this.comment;
  }

  @Override
  public String exceptionName()
  {
    return this.className;
  }

  @Override
  public Type exceptionType()
  {
    return exception();
  }

  public String comment()
  {
    return this.comment;
  }
}
