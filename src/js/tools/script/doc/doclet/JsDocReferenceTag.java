package js.tools.script.doc.doclet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SeeTag;
import com.sun.tools.javac.util.ListBuffer;

public class JsDocReferenceTag extends JsDocTag implements SeeTag
{
  private String scopeName; // package, package.Class, package.Class.NestedClass, Class, Class.NestedClass
  private String memberName;
  private PackageDoc referencedPackage;
  private ClassDoc referencedClass;
  private MemberDoc referencedMember;
  private String label;
  private JsDocClass containingClass;
  private boolean referencesResolved = false;

  /**
   * Construct a new <em>reference</em> tag. A reference can be created using in-line or block tags. This class accept "see",
   * "link" and "linkplain" as in-lines and "see" for block tags.
   * 
   * Like all tags this one has a name and some attached text. Text comes in three variants:
   * <ol>
   * <li>plain text - this is a double quoted text like "The j(s)-lib User Guide"</li>
   * <li>external link - external references in form of HTML anchor, e.g. <a href="http://ug.js-lib.com">User Guide</a></li>
   * <li>reference - documentation reference in the form package.class#member label</li>
   * </ol>
   * Plain texts and external links are passed as they are to {linkplain Doclet#start()}. For documentation references this
   * constructor tries to discover referenced package, class and|or method so that Doclet to be able to create proper links.
   * 
   * @param parentComment comment embedding this tag
   * @param name tag name all three variants: "see", "link" and "linkplain"
   * @param text tag text see above details
   */
  public JsDocReferenceTag(JsDocComment parentComment, String name, String text)
  {
    super(parentComment, name, text);
    // nothing to do if text is empty, this reference is plain text or external link
    if(text.isEmpty() || text.charAt(0) == '"' || text.charAt(0) == '<') return;

    if(parseTagText(text)) {
      JsDocNode commentOwner = parentComment.getCommentOwner();
      if(commentOwner instanceof MemberDoc) {
        this.containingClass = (JsDocClass)((JsDocProgramElement)commentOwner).containingClass();
      }
      else if(commentOwner instanceof ClassDoc) {
        this.containingClass = (JsDocClass)commentOwner;
      }
    }
  }

  @Override
  public String kind()
  {
    return CT.SEE_TAG;
  }

  @Override
  public String label()
  {
    // although API doc says "...Return null if no label is present" doing so will will determine standard doclet to throw null
    // pointer exception
    return this.label == null ? CT.EMPTY : this.label;
  }

  @Override
  public ClassDoc referencedClass()
  {
    if(!this.referencesResolved) findReferenced();
    return this.referencedClass;
  }

  @Override
  public String referencedClassName()
  {
    return this.referencedClass == null ? null : referencedClass().qualifiedName();
  }

  @Override
  public MemberDoc referencedMember()
  {
    if(!this.referencesResolved) findReferenced();
    return this.referencedMember;
  }

  @Override
  public String referencedMemberName()
  {
    return this.referencedMember == null ? null : referencedMember().name();
  }

  @Override
  public PackageDoc referencedPackage()
  {
    if(!this.referencesResolved) findReferenced();
    return this.referencedPackage;
  }

  // ------------------------------------------------------
  // internal helpers

  /**
   * Parse tag text trying to fill scope and member names and label.
   * 
   * @param text tag text to be parsed
   * @return true if text is well formatted, but not necessarily all information present.
   */
  private boolean parseTagText(String text)
  {
    int length = text.length();
    int parenthesisCount = 0;
    int labelIndex = -1;

    // (in)sanity check and label index discovery, if any
    textloop: for(int i = 0, cp; i < length; i += Character.charCount(cp)) {
      cp = text.codePointAt(i);

      switch(cp) {
      case '(':
        parenthesisCount++;
        break;
      case ')':
        parenthesisCount--;
        break;

      case '[':
      case ']':
      case '.':
      case '#':
        break;

      case ',':
        if(parenthesisCount <= 0) {
          // TODO add bad reference format warning
          return false;
        }
        break;

      case ' ':
      case '\t':
      case '\n':
        if(parenthesisCount == 0) {
          labelIndex = i;
          break textloop;
        }
      }
    }
    if(parenthesisCount != 0) {
      // TODO add bad reference format warning
      return false;
    }

    // reference := referenceText space labelText
    // referenceText := package.Class.NestedClass#member

    String referenceText = null;
    if(labelIndex != -1) {
      referenceText = text.substring(0, labelIndex);
      String labelText = text.substring(labelIndex + 1);

      // skip over spaces and assign label value
      for(int i = 0; i < labelText.length(); i++) {
        char c = labelText.charAt(i);
        if(!(c == ' ' || c == '\t' || c == '\n')) {
          this.label = labelText.substring(i);
          break;
        }
      }
    }
    else {
      referenceText = text;
    }

    int sharpIndex = referenceText.indexOf('#');
    if(sharpIndex >= 0) {
      this.scopeName = referenceText.substring(0, sharpIndex);
      this.memberName = referenceText.substring(sharpIndex + 1);
    }
    else {
      this.scopeName = referenceText;
      this.memberName = null;
    }
    return true;
  }

  /**
   * Find referenced entities. If possible, sets {@link #referencedPackage}, {@link #referencedClass} and|or
   * {@link #referencedMember}.
   */
  private void findReferenced()
  {
    this.referencesResolved = true;
    if(this.scopeName.length() > 0) {
      if(this.containingClass != null) {
        if(this.scopeName.equals(this.containingClass.name())) {
          this.referencedClass = this.containingClass;
        }
        else if(this.scopeName.equals(this.containingClass.qualifiedName())) {
          this.referencedClass = this.containingClass;
        }
        else {
          this.referencedClass = this.containingClass.findClass(this.scopeName);
          if(this.referencedClass == null) {
            this.referencedClass = JsDocRoot.getInstance().getLazyClass(JsDocClass.class, this.scopeName);
          }
        }
      }
      if(this.referencedClass == null) {
        return;
      }
    }
    else {
      if(this.containingClass == null) return;
      this.referencedClass = this.containingClass;
    }

    if(this.memberName == null) return;
    // at this point member name may contains formal parameters, i.e. method signature
    int parenthesisIndex = this.memberName.indexOf('(');
    String nameSubstring = (parenthesisIndex >= 0 ? this.memberName.substring(0, parenthesisIndex) : this.memberName);
    String[] arguments = null;

    if(parenthesisIndex > 0) {
      String argumentsSubstring = this.memberName.substring(parenthesisIndex, this.memberName.length());
      arguments = new ParameterParser(argumentsSubstring).parse();
      if(arguments != null) {
        // if arguments are not well formatted parameters parser returns null
        this.referencedMember = findExecutableMember(nameSubstring, arguments, this.referencedClass);
      }
    }
    else {
      // if no arguments declared into reference text try first executable member - first found with the name, then field
      this.referencedMember = findExecutableMember(nameSubstring, null, this.referencedClass);
      if(this.referencedMember == null) {
        this.referencedMember = ((JsDocClass)this.referencedClass).findField(nameSubstring);
      }
    }
  }

  private MemberDoc findExecutableMember(String name, String[] arguments, ClassDoc classDoc)
  {
    JsDocClass c = (JsDocClass)classDoc;
    return name.equals(c.name()) ? c.findConstructor(arguments) : c.findMethod(name, arguments);
  }

  private enum State
  {
    NONE, START, TYPE, NAME, TYPE_NAME_SPACE, ARRAY_DECORATION

  }

  private class ParameterParser
  {
    private String parameters;
    private StringBuffer typeId;
    private ListBuffer<String> paramList;

    ParameterParser(String parameters)
    {
      this.parameters = parameters;
      this.paramList = new ListBuffer<String>();
      this.typeId = new StringBuffer();
    }

    String[] parse()
    {
      if(this.parameters.equals("()")) return new String[0];

      State state = State.START;
      State prevstate = State.START;
      this.parameters = this.parameters.substring(1, this.parameters.length() - 1);

      for(int index = 0, cp; index < this.parameters.length(); index += Character.charCount(cp)) {
        cp = this.parameters.codePointAt(index);
        switch(state) {
        case START:
          if(Character.isJavaIdentifierStart(cp)) {
            this.typeId.append(Character.toChars(cp));
            state = State.TYPE;
          }
          prevstate = State.START;
          break;

        case TYPE:
          if(Character.isJavaIdentifierPart(cp) || cp == '.') {
            this.typeId.append(Character.toChars(cp));
          }
          else if(cp == '[') {
            this.typeId.append('[');
            state = State.ARRAY_DECORATION;
          }
          else if(Character.isWhitespace(cp)) {
            state = State.TYPE_NAME_SPACE;
          }
          else if(cp == ',') {
            addTypeToParamList();
            state = State.START;
          }
          prevstate = State.TYPE;
          break;

        case TYPE_NAME_SPACE:
          if(Character.isJavaIdentifierStart(cp)) {
            if(prevstate == State.ARRAY_DECORATION) return (String[])null;
            addTypeToParamList();
            state = State.NAME;
          }
          else if(cp == '[') {
            this.typeId.append('[');
            state = State.ARRAY_DECORATION;
          }
          else if(cp == ',') {
            addTypeToParamList();
            state = State.START;
          }
          prevstate = State.TYPE_NAME_SPACE;
          break;

        case ARRAY_DECORATION:
          if(cp == ']') {
            this.typeId.append(']');
            state = State.TYPE_NAME_SPACE;
          }
          else if(!Character.isWhitespace(cp)) {
            return (String[])null;
          }
          prevstate = State.ARRAY_DECORATION;
          break;

        case NAME:
          if(cp == ',') {
            state = State.START;
          }
          prevstate = State.NAME;
          break;

        default:
          break;
        }
      }
      if(this.typeId.length() > 0) this.paramList.append(this.typeId.toString());
      return (String[])this.paramList.toArray(new String[this.paramList.length()]);
    }

    void addTypeToParamList()
    {
      if(this.typeId.length() > 0) {
        this.paramList.append(this.typeId.toString());
        this.typeId.setLength(0);
      }
    }
  }
}
