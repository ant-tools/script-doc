package js.tools.script.doc.test;

import junit.framework.Assert;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * <p>
 * An error reporter for testing that verifies that messages reported to the reporter are expected.
 * </p>
 * 
 * <p>
 * Sample use
 * </p>
 * 
 * <pre>
 * TestErrorReporter e =
 *   new TestErrorReporter(null, new String[] { "first warning" });
 * ...
 * assertTrue(e.hasEncounteredAllWarnings());
 * </pre>
 * 
 */
@SuppressWarnings("deprecation")
public class RhinoErrorReporter extends Assert implements ErrorReporter
{
  private final String[] errors;
  private final String[] warnings;
  private int errorsIndex = 0;
  private int warningsIndex = 0;

  public RhinoErrorReporter(String[] errors, String[] warnings)
  {
    this.errors = errors;
    this.warnings = warnings;
  }

  public void error(String message, String sourceName, int line, String lineSource, int lineOffset)
  {
    if(this.errors != null && this.errorsIndex < this.errors.length) {
      assertEquals(this.errors[this.errorsIndex++], message);
    }
    else {
      fail("extra error: " + message);
    }
  }

  public void warning(String message, String sourceName, int line, String lineSource, int lineOffset)
  {
    if(this.warnings != null && this.warningsIndex < this.warnings.length) {
      assertEquals(this.warnings[this.warningsIndex++], message);
    }
    else {
      fail("extra warning: " + message);
    }
  }

  public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns whether all warnings were reported to this reporter.
   */
  public boolean hasEncounteredAllWarnings()
  {
    return (this.warnings == null) ? this.warningsIndex == 0 : this.warnings.length == this.warningsIndex;
  }

  /**
   * Returns whether all errors were reported to this reporter.
   */
  public boolean hasEncounteredAllErrors()
  {
    return (this.errors == null) ? this.errorsIndex == 0 : this.errors.length == this.errorsIndex;
  }
}