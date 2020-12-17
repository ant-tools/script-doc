package js.tools.script.doc.doclet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.ParamTag;

public class JsDocParamTag extends JsDocTag implements ParamTag
{
  private static Pattern typeParamPattern = Pattern.compile("<([^<>]+)>");
  private String parameterName;
  private String parameterComment;
  private final boolean isTypeParameter;

  public JsDocParamTag(JsDocComment parentComment, String name, String text)
  {
    super(parentComment, name, text);
    String[] a = Utils.divideAtWhitespace(text);
    Matcher m = typeParamPattern.matcher(a[0]);
    this.isTypeParameter = m.matches();
    this.parameterName = this.isTypeParameter ? m.group(1) : a[0];
    this.parameterComment = a[1];
  }

  public void setParameterName(String parameterName)
  {
    this.parameterName = parameterName;
  }

  public void setParameterComment(String parameterComment)
  {
    this.parameterComment = parameterComment;
    this.text = parameterComment;
  }

  @Override
  public boolean isTypeParameter()
  {
    return this.isTypeParameter;
  }

  @Override
  public String parameterComment()
  {
    return this.parameterComment;
  }

  @Override
  public String parameterName()
  {
    return this.parameterName;
  }

  @Override
  public String comment()
  {
    return this.parameterComment;
  }
}
