package js.tools.script.doc.doclet;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

/**
 * j(s)-doc tree node. j(s)-doc is a bidirectional tree with {@link JsDocRoot} as its root. Every tree node has a name and
 * comment.
 * 
 * @author Iulian Rotaru
 */
abstract class JsDocNode implements Doc
{
  protected String nodeName;
  protected JsDocComment jsDocComment = JsDocComment.empty;

  public void setComment(JsDocComment jsDocComment)
  {
    this.jsDocComment = jsDocComment;
  }

  /**
   * Set this j(s)-doc node name. Given name must be not qualified, suitable to be returned by {@link #name()}. Failing to obey
   * this constrain is considered a bug.
   * 
   * @param jsDocName this j(s)-doc node name
   * @exception IllegalArgumentException if supply with a qualified name.
   */
  void setName(String jsDocName)
  {
    if(jsDocName == null) return;

    if(jsDocName.indexOf('.') != -1) {
      throw new IllegalArgumentException("j(s)-doc node name must be not qualified: " + jsDocName);
    }
    this.nodeName = jsDocName;
  }

  /**
   * Create a j(s)-doc comment instance wrapping given raw comment string. Raw comment can be missing in which case an empty
   * j(s)-doc comment is created.
   * 
   * @param rawComment raw comment
   * @return created j(s)-doc comment instance.
   */
  public JsDocComment createComment(String rawComment)
  {
    this.jsDocComment = new JsDocComment(this, rawComment != null ? rawComment : CT.EMPTY);
    return this.jsDocComment;
  }

  public JsDocComment comment()
  {
    return this.jsDocComment;
  }

  @Override
  public String commentText()
  {
    return this.jsDocComment.commentText();
  }

  @Override
  public int compareTo(Object object)
  {
    if(object == null) {
      throw new NullPointerException("Can't compare this j(s)-doc node with null.");
    }
    if(!(object instanceof JsDocNode)) {
      throw new ClassCastException(String.format("Can't cast %s to %s.", object.getClass(), this.getClass()));
    }
    JsDocNode that = (JsDocNode)object;
    return this.nodeName.compareTo(that.nodeName);
  }

  @Override
  public Tag[] firstSentenceTags()
  {
    return this.jsDocComment.firstSentenceTags();
  }

  @Override
  public Tag[] inlineTags()
  {
    return this.jsDocComment.inlineTags();
  }

  @Override
  public String getRawCommentText()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAnnotationType()
  {
    return false;
  }

  @Override
  public boolean isAnnotationTypeElement()
  {
    return false;
  }

  @Override
  public boolean isClass()
  {
    return false;
  }

  @Override
  public boolean isConstructor()
  {
    return false;
  }

  @Override
  public boolean isEnum()
  {
    return false;
  }

  @Override
  public boolean isEnumConstant()
  {
    return false;
  }

  @Override
  public boolean isError()
  {
    return false;
  }

  @Override
  public boolean isException()
  {
    return false;
  }

  @Override
  public boolean isField()
  {
    return false;
  }

  private boolean included = true;

  public void setIncluded(boolean included)
  {
    this.included = included;
  }

  /**
   * For the purpose of j(s)-doc all classes are included.
   */
  @Override
  public boolean isIncluded()
  {
    return this.included;
  }

  @Override
  public boolean isInterface()
  {
    return false;
  }

  @Override
  public boolean isMethod()
  {
    return false;
  }

  @Override
  public boolean isOrdinaryClass()
  {
    return false;
  }

  /**
   * Returns the non-qualified name of this j(s)-doc node.
   */
  @Override
  public String name()
  {
    return this.nodeName;
  }

  /**
   * Do not use position so always returns null.
   */
  @Override
  public SourcePosition position()
  {
    return null;
  }

  public String getRawComment()
  {
    return this.jsDocComment.getRawComment();
  }

  @Override
  public void setRawCommentText(String rawCommentText)
  {
    createComment(rawCommentText);
  }

  @Override
  public SeeTag[] seeTags()
  {
    return this.jsDocComment.seeTags();
  }

  @Override
  public Tag[] tags()
  {
    return this.jsDocComment.tags();
  }

  @Override
  public Tag[] tags(String tagKind)
  {
    return this.jsDocComment.tags(tagKind);
  }

  @Override
  public String toString()
  {
    return this.nodeName;
  }

  protected String unqualify(String qualifiedName)
  {
    if(qualifiedName == null || qualifiedName.isEmpty()) {
      throw new IllegalArgumentException("Qualified name can't be null or empty.");
    }
    return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
  }
}
