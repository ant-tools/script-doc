package js.tools.script.doc;

public class JsDocException extends RuntimeException
{
  /**
   * Java serialization version.
   */
  private static final long serialVersionUID = 4163413765381913815L;

  public JsDocException(String message, Object... args)
  {
    super(String.format(message, args));
  }
}
