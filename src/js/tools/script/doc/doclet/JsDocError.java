package js.tools.script.doc.doclet;

/**
 * Error class. Error class is a standard j(s) doclet class but returns true on {@link #isError()} and false to
 * {@link #isOrdinaryClass()} predicates.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public final class JsDocError extends JsDocClass
{
  /**
   * Construct error class instance.
   * 
   * @param className qualified class name,
   * @param modifiers class modifiers.
   */
  JsDocError(String className, int modifiers)
  {
    super(className, modifiers);
  }

  /**
   * Returns true to indicate this instance is an error.
   * 
   * @return always true.
   */
  @Override
  public boolean isError()
  {
    return true;
  }

  /**
   * Returns false to indicate this instance is not an ordinary class.
   * 
   * @return always return false.
   */
  @Override
  public boolean isOrdinaryClass()
  {
    return false;
  }
}
