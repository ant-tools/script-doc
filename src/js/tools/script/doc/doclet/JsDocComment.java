package js.tools.script.doc.doclet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Comment helper. This class gets a raw comment, most probably extracted from source code and separate comment text
 * from embedded tags. Note that given raw comment may or may not contain <b>/*</b> comment marks. Supplies methods to
 * access comment text and tags by name or kind but not for raw comment.
 * <p>
 * There are two types of tags: in-line and block. In-line tags have the form {name text} whereas block tags have no
 * curly braces and start from new line beginning.
 * 
 * @author Iulian Rotaru
 */
public final class JsDocComment
{
  static final JsDocComment empty = new JsDocComment(null, null);

  private JsDocNode commentOwner;
  private String rawComment;
  private String commentText;
  private JsDocTaggedText description;
  private final List<JsDocTag> blockTags = new ArrayList<JsDocTag>();

  public JsDocComment(JsDocNode commentOwner, String rawComment)
  {
    this.commentOwner = commentOwner;
    if(rawComment == null) {
      this.rawComment = this.commentText = CT.EMPTY;
      this.description = new JsDocTaggedText(this, CT.EMPTY);
    }
    else {
      this.rawComment = normalize(rawComment);
      if(this.rawComment == null) this.rawComment = CT.EMPTY;
      new Parser().parse();
      this.description = new JsDocTaggedText(this, this.commentText);
    }
  }

  /**
   * Normalize API comment text before parsing. This method removes comment marks and white spaces from line start,
   * comment begin and end.
   * 
   * @param comment source raw comment text.
   * @return normalized comment text.
   */
  private static String normalize(String comment)
  {
    // TODO this is a hack; remove it and enhance the parser
    if(comment == null) {
      return null;
    }
    if(comment.startsWith("/**")) {
      comment = comment.substring(3);
      comment = comment.replaceAll("\\r\\n[ \\t\\x0B\\f]*[*][ ]?", "\r\n");
      comment = comment.replaceAll("\\n[ \\t\\x0B\\f]*[*][ ]?", "\r\n");
      if(comment.startsWith("\r\n")) {
        comment = comment.substring(2);
      }
      if(comment.startsWith("\n")) {
        comment = comment.substring(1);
      }
      if(comment.endsWith("\r\n/")) {
        comment = comment.substring(0, comment.length() - 3);
      }
      if(comment.endsWith("\n/")) {
        comment = comment.substring(0, comment.length() - 2);
      }
    }
    return comment;
  }

  public boolean isEmpty()
  {
    return this.rawComment.isEmpty();
  }

  public JsDocNode getCommentOwner()
  {
    return this.commentOwner;
  }

  public String getRawComment()
  {
    return rawComment;
  }

  /**
   * Get comment text with tags removed.
   * 
   * @return comment text
   */
  public String commentText()
  {
    return this.commentText;
  }

  public JsDocTag[] firstSentenceTags()
  {
    return this.description.firstSentence();
  }

  public JsDocTag[] inlineTags()
  {
    return this.description.tags();
  }

  public JsDocTag[] tags()
  {
    return JsDocTag.toArray(this.blockTags);
  }

  public JsDocTag[] tags(String tagKind)
  {
    return findTagsByKind(JsDocTag.class, tagKind);
  }

  public JsDocParamTag[] paramTags()
  {
    return findTagsByKind(JsDocParamTag.class, CT.PARAM_TAG);
  }

  public JsDocParamTag[] typeParamTags()
  {
    return new JsDocParamTag[0];
  }

  public JsDocTag returnTag()
  {
    return getTagByKind(JsDocTag.class, CT.RETURN_TAG);
  }

  public JsDocTag typeTag()
  {
    return getTagByKind(JsDocTag.class, CT.TYPE_TAG);
  }

  public JsDocThrowsTag[] throwsTags()
  {
    return findTagsByKind(JsDocThrowsTag.class, CT.THROWS_TAG);
  }

  public JsDocReferenceTag[] seeTags()
  {
    return findTagsByKind(JsDocReferenceTag.class, CT.SEE_TAG);
  }

  public void removeTag(JsDocTag tag)
  {
    removeTag(tag.name());
  }

  public boolean hasTag(String tagName)
  {
    tagName = normalizeTag(tagName);
    Iterator<JsDocTag> it = this.blockTags.iterator();
    while(it.hasNext()) {
      if(it.next().name().equals(tagName)) return true;
    }
    return false;
  }

  public void removeTag(String tagName)
  {
    tagName = normalizeTag(tagName);
    Iterator<JsDocTag> it = this.blockTags.iterator();
    while(it.hasNext()) {
      if(it.next().name().equals(tagName)) it.remove();
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends JsDocTag> T getTagByKind(Class<T> type, String tagKind)
  {
    tagKind = normalizeTag(tagKind);
    for(JsDocTag tag : this.blockTags) {
      if(tag.kind().equals(tagKind)) return (T)tag;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T extends JsDocTag> T[] findTagsByKind(Class<T> type, String tagKind)
  {
    List<T> found = new ArrayList<T>();
    tagKind = normalizeTag(tagKind);
    for(JsDocTag tag : this.blockTags) {
      if(tag.kind().equals(tagKind)) found.add((T)tag);
    }
    return found.toArray((T[])Array.newInstance(type, found.size()));
  }

  private enum State
  {
    NONE, IN_TEXT, TAG_GAP, TAG_NAME
  }

  private class Parser
  {
    void parse()
    {
      State state = State.TAG_GAP;
      boolean newLine = true;
      String tagName = null;
      int tagStart = 0;
      int textStart = 0;
      int lastNonWhite = -1;
      int len = JsDocComment.this.rawComment.length();

      for(int inx = 0; inx < len; ++inx) {
        char ch = JsDocComment.this.rawComment.charAt(inx);
        boolean isWhite = Character.isWhitespace(ch);
        switch(state) {
        case TAG_NAME:
          if(isWhite) {
            tagName = JsDocComment.this.rawComment.substring(tagStart, inx);
            state = State.TAG_GAP;
          }
          break;

        case TAG_GAP:
          if(isWhite) {
            break;
          }
          textStart = inx;
          state = State.IN_TEXT;

        case IN_TEXT:
          if(newLine && ch == '@') {
            parseBlockTag(tagName, textStart, lastNonWhite + 1);
            tagStart = inx;
            state = State.TAG_NAME;
          }
          break;

        default:
          break;
        }

        if(ch == '\n') {
          newLine = true;
        }
        else if(!isWhite) {
          lastNonWhite = inx;
          newLine = false;
        }
      }

      switch(state) {
      case TAG_NAME:
        tagName = JsDocComment.this.rawComment.substring(tagStart, len);

      case TAG_GAP:
        textStart = len;

      case IN_TEXT:
        parseBlockTag(tagName, textStart, lastNonWhite + 1);
        break;

      default:
        break;
      }
    }

    void parseBlockTag(String tagName, int from, int upto)
    {
      String tagText = upto <= from ? CT.EMPTY : JsDocComment.this.rawComment.substring(from, upto);
      if(tagName == null) {
        JsDocComment.this.commentText = tagText;
      }
      else if(tagName.equals(CT.EXCEPTION_TAG) || tagName.equals(CT.THROWS_TAG)) {
        JsDocComment.this.blockTags.add(new JsDocThrowsTag(JsDocComment.this, tagName, tagText));
      }
      else if(tagName.equals(CT.PARAM_TAG)) {
        JsDocComment.this.blockTags.add(new JsDocParamTag(JsDocComment.this, tagName, tagText));
      }
      else if(tagName.equals(CT.SEE_TAG)) {
        JsDocComment.this.blockTags.add(new JsDocReferenceTag(JsDocComment.this, tagName, tagText));
      }
      else {
        JsDocComment.this.blockTags.add(new JsDocTag(JsDocComment.this, tagName, tagText));
      }
    }
  }

  /**
   * Normalize tag name or kind.
   * 
   * @param tag tag name or kind
   * @return normalized tag name.
   */
  private static String normalizeTag(String tag)
  {
    return tag.charAt(0) == '@' ? tag : '@' + tag;
  }
}
