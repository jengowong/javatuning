package javatuning.ch2.proxy.dynamic;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import javatuning.ch2.proxy.IDBQuery;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

public class FunctionMain {

    public static void main(String[] args) throws Exception {
        IDBQuery d = null;
        d = createJdkProxy();
        System.out.println(d.request());
        System.out.println("---------");
        d = createCglibProxy();
        System.out.println(d.request());
        System.out.println("---------");
        d = createJavassistDynProxy();
        System.out.println(d.request());
        System.out.println("---------");
        d = createJavassistBytecodeDynamicProxy();
        System.out.println(d.request());
    }

    public static IDBQuery createJdkProxy() {
        return (IDBQuery) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{IDBQuery.class},
                new JdkDbQueryHandler()
        );
    }

    public static IDBQuery createCglibProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CglibDbQueryInterceptor());
        enhancer.setInterfaces(new Class[]{IDBQuery.class});
        return (IDBQuery) enhancer.create();
    }

    public static IDBQuery createJavassistDynProxy() throws Exception {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(new Class[]{IDBQuery.class});
        Class proxyClass = proxyFactory.createClass();
        IDBQuery javassistProxy = (IDBQuery) proxyClass.newInstance();
        ((ProxyObject) javassistProxy).setHandler(new JavassistDynDbQueryHandler());
        return javassistProxy;
    }

    public static IDBQuery createJavassistBytecodeDynamicProxy() throws Exception {
        ClassPool mPool = new ClassPool(true);
        CtClass mCtc = mPool.makeClass(IDBQuery.class.getName() + "JavaassistBytecodeProxy");
        mCtc.addInterface(mPool.get(IDBQuery.class.getName()));
        mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
        mCtc.addField(CtField.make("public " + IDBQuery.class.getName() + " real;", mCtc));
        String dbqueryname = DBQuery.class.getName();
        mCtc.addMethod(CtNewMethod.make(
                        "public String request() { if(real==null)real=new "
                                + dbqueryname
                                + "();return real.request(); }",
                        mCtc)
        );
        Class pc = mCtc.toClass();
        return (IDBQuery) pc.newInstance();
    }

}
