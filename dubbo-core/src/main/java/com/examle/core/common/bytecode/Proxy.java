package com.examle.core.common.bytecode;

import com.examle.core.common.utils.ClassHelper;
import com.examle.core.common.utils.ReflectUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Proxy {

    private static final Map<ClassLoader, Map<String, Object>> ProxyCacheMap = new WeakHashMap<ClassLoader, Map<String, Object>>();
    private static final Object PendingGenerationMarker = new Object();

    private static final AtomicLong PROXY_CLASS_COUNTER = new AtomicLong(0);

    private static final String PACKAGE_NAME = Proxy.class.getPackage().getName();

    public static Proxy getProxy(Class<?>... ics) {
        return getProxy(ClassHelper.getClassLoader(Proxy.class), ics);
    }

    public static Proxy getProxy(ClassLoader cl, Class<?>... ics) {
        if (ics.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ics.length; i++) {
            String itf = ics[i].getName();
            if (!ics[i].isInterface()) {
                throw new RuntimeException(itf + " is not a interface.");
            }
            Class<?> tmp = null;
            try {
                tmp = Class.forName(itf, false, cl);
            } catch (ClassNotFoundException e) {
            }

            if (tmp != ics[i]) {
                throw new IllegalArgumentException(ics[i] + " is not visible from class loader");
            }

            sb.append(itf).append(';');
        }

        //使用接口类名列表作为键
        String key = sb.toString();

        //通过类加载器获取缓存
        Map<String, Object> cache;
        synchronized (ProxyCacheMap) {
            cache = ProxyCacheMap.get(cl);
            if (cache == null) {
                cache = new HashMap<String, Object>();
                ProxyCacheMap.put(cl, cache);
            }
        }

        Proxy proxy = null;
        synchronized (cache) {
            do {
                cache.put(key, PendingGenerationMarker);
                break;
            }while (true);
        }

        long id = PROXY_CLASS_COUNTER.getAndIncrement();
        String pkg = null;
        ClassGenerator ccp = null, ccm = null;
        try {
            ccp = ClassGenerator.newInstance(cl);

            Set<String> worked = new HashSet<String>();
            List<Method> methods = new ArrayList<Method>();

            for (int i = 0; i < ics.length; i++) {
                if (!Modifier.isPublic(ics[i].getModifiers())) {
                    String npkg = ics[i].getPackage().getName();
                    if (pkg == null) {
                        pkg = npkg;
                    } else {
                        if (!pkg.equals(npkg)) {
                            throw new IllegalArgumentException("non-public interfaces from different packages");
                        }
                    }
                }
                ccp.addInterface(ics[i]);

                for (Method method : ics[i].getMethods()) {
                    String desc = ReflectUtils.getDesc(method);
                    if (worked.contains(desc)) {
                        continue;
                    }
                    worked.add(desc);

                    int ix = methods.size();
                    Class<?> rt = method.getReturnType();
                    Class<?>[] pts = method.getParameterTypes();

                    /**
                     * code的值：
                     * Object[] args = new Object[1];
                     * args[0] = ($w)$1;
                     * Object ret = handler.invoke(this, methods[0], args);
                     * return (com.example.model.Result)ret;
                     */
                    StringBuilder code = new StringBuilder("Object[] args = new Object[").append(pts.length).append("];");
                    for (int j = 0; j < pts.length; j++) {
                        code.append(" args[").append(j).append("] = ($w)$").append(j + 1).append(";");
                    }
                    code.append(" Object ret = handler.invoke(this, methods[" + ix + "], args);");
                    if (!Void.TYPE.equals(rt)) {
                        code.append(" return ").append(asArgument(rt, "ret")).append(";");
                    }

                    methods.add(method);
                    ccp.addMethod(method.getName(), method.getModifiers(), rt, pts, method.getExceptionTypes(), code.toString());
                }
            }

                if (pkg == null) {
                    pkg = PACKAGE_NAME;
                }

                // create ProxyInstance class.
                String pcn = pkg + ".proxy" + id;
                ccp.setClassName(pcn);
                ccp.addField("public static java.lang.reflect.Method[] methods;");
                ccp.addField("private " + InvocationHandler.class.getName() + " handler;");
                ccp.addConstructor(Modifier.PUBLIC, new Class<?>[]{InvocationHandler.class}, new Class<?>[0], "handler=$1;");
                ccp.addDefaultConstructor();
                Class<?> clazz = ccp.toClass();
                clazz.getField("methods").set(null, methods.toArray(new Method[0]));

                // create Proxy class.
                String fcn = Proxy.class.getName() + id;
                ccm = ClassGenerator.newInstance(cl);
                ccm.setClassName(fcn);
                ccm.addDefaultConstructor();
                ccm.setSuperClass(Proxy.class);
                ccm.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " h){ return new " + pcn + "($1); }");
                Class<?> pc = ccm.toClass();
                proxy = (Proxy) pc.newInstance();

        }catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            // release ClassGenerator
            if (ccp != null) {
                ccp.release();
            }
            if (ccm != null) {
                ccm.release();
            }
            synchronized (cache) {
                if (proxy == null) {
                    cache.remove(key);
                } else {
                    cache.put(key, new WeakReference<Proxy>(proxy));
                }
                cache.notifyAll();
            }
        }

        return proxy;
    }

    private static String asArgument(Class<?> cl, String name) {
        if (cl.isPrimitive()) {
            if (Boolean.TYPE == cl) {
                return name + "==null?false:((Boolean)" + name + ").booleanValue()";
            }
            if (Byte.TYPE == cl) {
                return name + "==null?(byte)0:((Byte)" + name + ").byteValue()";
            }
            if (Character.TYPE == cl) {
                return name + "==null?(char)0:((Character)" + name + ").charValue()";
            }
            if (Double.TYPE == cl) {
                return name + "==null?(double)0:((Double)" + name + ").doubleValue()";
            }
            if (Float.TYPE == cl) {
                return name + "==null?(float)0:((Float)" + name + ").floatValue()";
            }
            if (Integer.TYPE == cl) {
                return name + "==null?(int)0:((Integer)" + name + ").intValue()";
            }
            if (Long.TYPE == cl) {
                return name + "==null?(long)0:((Long)" + name + ").longValue()";
            }
            if (Short.TYPE == cl) {
                return name + "==null?(short)0:((Short)" + name + ").shortValue()";
            }
            throw new RuntimeException(name + " is unknown primitive type.");
        }
        return "(" + ReflectUtils.getName(cl) + ")" + name;
    }


    abstract public Object newInstance(InvocationHandler handler);
}
