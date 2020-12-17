package js.tools.script.doc.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import js.tools.commons.util.Classes;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class TestCaseEx extends TestCase
{
  protected static void assertInvokeTrue(Object object, String methodName, Object... arguments)
  {
    try {
      assertTrue((Boolean)Classes.invoke(object, methodName, arguments));
    }
    catch(AssertionFailedError e) {
      throw e;
    }
    catch(Throwable t) {
      fail(String.format("Invocation of %s on %s throws %s", methodName, object.getClass(), t.getClass()));
    }
  }

  protected static void assertInvokeFalse(Object object, String methodName, Object... arguments)
  {
    try {
      assertFalse((Boolean)Classes.invoke(object, methodName, arguments));
    }
    catch(AssertionFailedError e) {
      throw e;
    }
    catch(Throwable t) {
      fail(String.format("Invocation of %s on %s throws %s", methodName, object.getClass(), t.getClass()));
    }
  }

  protected static void assertInvokeEquals(Object expected, Object object, String methodName, Object... arguments)
  {
    try {
      assertEquals(expected, Classes.invoke(object, methodName, arguments));
    }
    catch(AssertionFailedError e) {
      throw e;
    }
    catch(Throwable t) {
      fail(String.format("Invocation of %s on %s throws %s", methodName, object.getClass(), t.getClass()));
    }
  }

  protected static void assertInvokeThrows(Class<? extends Throwable> expected, Object object, String methodName, Object... arguments)
  {
    String s = String.format("Invocation of %s on %s should throw %s", methodName, object.getClass(), expected);
    try {
      invoke(object, methodName, arguments);
      fail(s);
    }
    catch(Throwable t) {
      if(t instanceof InvocationTargetException) {
        t = ((InvocationTargetException)t).getTargetException();
      }
      if(!t.getClass().equals(expected)) fail(s);
    }
  }

  protected static void assertInvokeThrows(String expectedMessage, Object object, String methodName, Object... arguments)
  {
    String s = String.format("Invocation of %s on %s should throw \"%s\"", methodName, object.getClass(), expectedMessage);
    try {
      invoke(object, methodName, arguments);
      fail(s);
    }
    catch(Throwable t) {
      if(t instanceof InvocationTargetException) {
        t = ((InvocationTargetException)t).getTargetException();
      }
      if(t.getMessage() == null || !t.getMessage().equals(expectedMessage)) fail(s);
    }
  }

  protected static void assertEmpty(Map<?, ?> map)
  {
    assertTrue(map.isEmpty());
  }

  protected static void assertLength(int length, Map<?, ?> map)
  {
    assertEquals(length, map.size());
  }

  protected static void assertLength(int length, Collection<?> collection)
  {
    assertEquals(length, collection.size());
  }

  private static void invoke(Object object, String methodName, Object... arguments) throws Throwable
  {
    Class<?>[] parameterTypes = new Class<?>[arguments.length];
    for(int i = 0; i < arguments.length; i++) {
      Object argument = arguments[i];
      parameterTypes[i] = argument == null ? Object.class : argument.getClass();
    }

    Class<?> clazz = (Class<?>)(object instanceof Class ? object : object.getClass());
    try {
      Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
      invoke(object, method, arguments);
    }
    catch(NoSuchMethodException e) {
      methodsLoop: for(Method method : clazz.getDeclaredMethods()) {
        Class<?>[] methodParameters = method.getParameterTypes();
        if(!method.getName().equals(methodName)) continue;
        if(methodParameters.length != arguments.length) continue;
        for(int i = 0; i < arguments.length; i++) {
          if(!isInstanceOf(arguments[i], methodParameters[i])) continue methodsLoop;
        }
        invoke(object, method, arguments);
      }
    }
  }

  private static Object invoke(Object object, Method method, Object... arguments) throws Throwable
  {
    try {
      method.setAccessible(true);
      return method.invoke(object instanceof Class<?> ? null : object, arguments);
    }
    catch(InvocationTargetException e) {
      throw e.getCause();
    }
  }

  private static boolean isInstanceOf(Object o, Type t)
  {
    if(o == null) return true;
    if(t instanceof Class) {
      Class<?> clazz = (Class<?>)t;
      return clazz.isInstance(o);
    }
    return false;
  }
}
