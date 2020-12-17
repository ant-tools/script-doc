package js.tools.script.doc.doclet;

import java.util.List;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

public class JsDocTag implements Tag
{
  private JsDocComment parentComment;
  private String name;
  protected String text;
  private JsDocTaggedText taggedText;

  public JsDocTag(JsDocComment parentComment, String name, String text)
  {
    this.parentComment = parentComment;
    this.name = name;
    this.text = text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  @Override
  public Doc holder()
  {
    return this.parentComment.getCommentOwner();
  }

  @Override
  public Tag[] firstSentenceTags()
  {
    if(this.taggedText == null) this.taggedText = new JsDocTaggedText(this.parentComment, this.text);
    return this.taggedText.firstSentence();
  }

  @Override
  public Tag[] inlineTags()
  {
    if(this.taggedText == null) this.taggedText = new JsDocTaggedText(this.parentComment, this.text);
    return this.taggedText.tags();
  }

  @Override
  public String kind()
  {
    return this.name;
  }

  @Override
  public String name()
  {
    return this.name;
  }

  @Override
  public SourcePosition position()
  {
    return null;
  }

  @Override
  public String text()
  {
    return this.text;
  }

  protected String comment()
  {
    return this.text;
  }

  static JsDocTag[] toArray(List<JsDocTag> tags)
  {
    return tags.toArray(new JsDocTag[tags.size()]);
  }
}
