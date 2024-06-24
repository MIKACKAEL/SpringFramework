package mg.prom16;

import java.lang.reflect.Method;

public class Mapping {
    private String className;
    private Method method;

    public Mapping(String className, Method method) {
        this.className = className;
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "className='" + className + '\'' +
                ", methodName='" + method + '\'' +
                '}';
    }
}