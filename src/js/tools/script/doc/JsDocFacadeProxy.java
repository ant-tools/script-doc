package js.tools.script.doc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import js.tools.commons.ast.Log;

/**
 * Proxy all doclet facade calls in order to catch and log generated exceptions.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public class JsDocFacadeProxy implements InvocationHandler
{
  public static JsDocFacade getInstance(Config config, Log log, JsDocFacade instance)
  {
    Class<? extends JsDocFacade> clazz = instance.getClass();
    JsDocFacadeProxy handler = new JsDocFacadeProxy(config, log, instance);
    return (JsDocFacade)Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), handler);
  }

  private Config config;
  private Log log;
  private JsDocFacade instance;

  private JsDocFacadeProxy(Config config, Log log, JsDocFacade instance)
  {
    this.config = config;
    this.log = log;
    this.instance = instance;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
  {
    try {
      return method.invoke(this.instance, args);
    }
    catch(InvocationTargetException e) {
      this.log.warn(e.getTargetException().getMessage());
      if(this.config.verbose) {
        e.printStackTrace();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
