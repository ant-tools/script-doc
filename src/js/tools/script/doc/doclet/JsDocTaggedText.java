package js.tools.script.doc.doclet;

import java.util.ArrayList;
import java.util.List;

import js.tools.commons.util.Strings;

final class JsDocTaggedText
{
  private static final String SEE = "see";
  private static final String LINK = "link";
  private static final String LINK_PLAIN = "linkplain";

  private JsDocComment parentComment;
  private String text;
  private List<JsDocTag> tags = new ArrayList<JsDocTag>();
  private int firstSentenceTagsCount;

  JsDocTaggedText(JsDocComment parentComment, String text)
  {
    this.parentComment = parentComment;
    this.text = text;
    parse(text);
    if(this.firstSentenceTagsCount == 0) this.firstSentenceTagsCount = this.tags.size();
  }

  public JsDocTag[] tags()
  {
    return this.tags.toArray(new JsDocTag[this.tags.size()]);
  }

  public JsDocTag[] firstSentence()
  {
    // return tags.subList(0, firstSentenceTagsCount).toArray(new Tag[firstSentenceTagsCount]);

    // TODO horrible hack
    JsDocTaggedText taggedText = new JsDocTaggedText(this.parentComment, Strings.firstSentence(this.text));
    return taggedText.tags();
  }

  private void parse(String text)
  {
    int delimEnd = 0, textStart = 0, length = text.length();
    if(length == 0) return;

    while(true) {
      int linkStart;
      if((linkStart = indexOfInlineTag(text, textStart)) == -1) {
        addText(text.substring(textStart));
        break;
      }

      int seeTextStart = linkStart;
      for(int i = linkStart; i < text.length(); i++) {
        char c = text.charAt(i);
        if(Character.isWhitespace(c) || c == '}') {
          seeTextStart = i;
          break;
        }
      }

      String linkName = text.substring(linkStart + 2, seeTextStart);

      // Move past the white space after the inline tag name.
      while(Character.isWhitespace(text.charAt(seeTextStart))) {
        if(text.length() <= seeTextStart) {
          addText(text.substring(textStart, seeTextStart));
        }
        seeTextStart++;
      }
      addText(text.substring(textStart, linkStart));

      textStart = seeTextStart; // this text is actually seetag
      if((delimEnd = findInlineTagDelim(text, textStart)) == -1) {
        // Missing closing '}' character.
        // store the text as it is with the {@link.
        addText(text.substring(textStart));
      }

      // Found closing '}' character.
      if(SEE.equals(linkName) || LINK.equals(linkName) || LINK_PLAIN.equals(linkName)) {
        addReference(linkName, text.substring(textStart, delimEnd));
      }
      else {
        addTag(linkName, text.substring(textStart, delimEnd));
      }

      textStart = delimEnd + 1;
      if(textStart == text.length()) break;
    }
  }

  private int indexOfInlineTag(String text, int offset)
  {
    if(offset == text.length()) return -1;
    int tagIndex = text.indexOf("{@", offset);
    if(tagIndex == -1) return -1;
    if(text.indexOf('}', tagIndex) == -1) return -1;
    return tagIndex;
  }

  private int findInlineTagDelim(String text, int searchStart)
  {
    int delimEnd, nestedOpenBrace;
    if((delimEnd = text.indexOf('}', searchStart)) == -1) return -1;
    if(((nestedOpenBrace = text.indexOf('{', searchStart)) != -1) && nestedOpenBrace < delimEnd) {
      int nestedCloseBrace = findInlineTagDelim(text, nestedOpenBrace + 1);
      return (nestedCloseBrace != -1) ? findInlineTagDelim(text, nestedCloseBrace + 1) : -1;
    }
    return delimEnd;
  }

  private void addText(String text)
  {
    this.tags.add(new JsDocTextTag(this.parentComment, "Text", text));
  }

  private void addTag(String tagName, String text)
  {
    this.tags.add(new JsDocTag(this.parentComment, '@' + tagName, text));
  }

  private void addReference(String tagName, String text)
  {
    this.tags.add(new JsDocReferenceTag(this.parentComment, '@' + tagName, text));
  }
}
