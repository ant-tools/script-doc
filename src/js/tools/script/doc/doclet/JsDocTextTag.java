package js.tools.script.doc.doclet;

import js.tools.commons.util.Strings;

import com.sun.javadoc.Tag;

public class JsDocTextTag extends JsDocTag
{
  private Tag[] firstSentenceTags = new Tag[1];
  private Tag[] inlineTags = new Tag[1];

  public JsDocTextTag(JsDocComment parentComment, String name, String text)
  {
    super(parentComment, name, text);
    this.firstSentenceTags[0] = new JsDocTag(parentComment, name, Strings.firstSentence(text));
    this.inlineTags[0] = new JsDocTag(parentComment, name, text);
  }

  @Override
  public Tag[] firstSentenceTags()
  {
    return this.firstSentenceTags;
  }

  @Override
  public Tag[] inlineTags()
  {
    return this.inlineTags;
  }
}
