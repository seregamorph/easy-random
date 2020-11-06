/*
 * The MIT License
 *
 *   Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.jeasy.random.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * Copied from https://github.com/spring-projects/spring-framework (spring-core)
 */
final class SerializableTypeWrapper {

    private static final Class<?>[] SUPPORTED_SERIALIZABLE_TYPES = {
            GenericArrayType.class, ParameterizedType.class, TypeVariable.class, WildcardType.class};

    private SerializableTypeWrapper() {
    }

    @SuppressWarnings("unchecked")
    static <T extends Type> T unwrap(T type) {
        Type unwrapped = null;
        if (type instanceof SerializableTypeWrapper.SerializableTypeProxy) {
            unwrapped = ((SerializableTypeWrapper.SerializableTypeProxy) type).getTypeProvider().getType();
        }
        return (unwrapped != null ? (T) unwrapped : type);
    }

    static Type forTypeProvider(TypeProvider provider) {
        Type providedType = provider.getType();
        if (providedType == null || providedType instanceof Serializable) {
            // No serializable type wrapping necessary (e.g. for java.lang.Class)
            return providedType;
        }

        // Obtain a serializable type proxy for the given provider...
        Type cached = null; //cache.get(providedType);
        if (cached != null) {
            return cached;
        }
        for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
            if (type.isInstance(providedType)) {
                ClassLoader classLoader = provider.getClass().getClassLoader();
                Class<?>[] interfaces = new Class<?>[] {type, SerializableTypeWrapper.SerializableTypeProxy.class, Serializable.class};
                InvocationHandler handler = new SerializableTypeWrapper.TypeProxyInvocationHandler(provider);
                cached = (Type) Proxy.newProxyInstance(classLoader, interfaces, handler);
                //cache.put(providedType, cached);
                return cached;
            }
        }
        throw new IllegalArgumentException("Unsupported Type class: " + providedType.getClass().getName());
    }

    private interface SerializableTypeProxy {

        TypeProvider getTypeProvider();
    }

    interface TypeProvider {

        Type getType();
    }

    private static class TypeProxyInvocationHandler implements InvocationHandler {

        private final TypeProvider provider;

        private TypeProxyInvocationHandler(TypeProvider provider) {
            this.provider = provider;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("equals") && args != null) {
                Object other = args[0];
                // Unwrap proxies for speed
                if (other instanceof Type) {
                    other = unwrap((Type) other);
                }
                return ObjectUtils.nullSafeEquals(this.provider.getType(), other);
            }
            else if (method.getName().equals("hashCode")) {
                return ObjectUtils.nullSafeHashCode(this.provider.getType());
            }
            else if (method.getName().equals("getTypeProvider")) {
                return this.provider;
            }

            if (Type.class == method.getReturnType() && args == null) {
                return forTypeProvider(new SerializableTypeWrapper.MethodInvokeTypeProvider(this.provider, method, -1));
            }
            else if (Type[].class == method.getReturnType() && args == null) {
                Type[] result = new Type[((Type[]) method.invoke(this.provider.getType())).length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = forTypeProvider(new SerializableTypeWrapper.MethodInvokeTypeProvider(this.provider, method, i));
                }
                return result;
            }

            try {
                return method.invoke(this.provider.getType(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    static class FieldTypeProvider implements TypeProvider {

        private final Field field;

        FieldTypeProvider(Field field) {
            this.field = field;
        }

        @Override
        public Type getType() {
            return this.field.getGenericType();
        }
    }

    static class MethodInvokeTypeProvider implements TypeProvider {

        private final TypeProvider provider;
        private final int index;
        private final Method method;

        private transient volatile Object result;

        private MethodInvokeTypeProvider(TypeProvider provider, Method method, int index) {
            this.provider = provider;
            this.index = index;
            this.method = method;
        }

        @Override
        public Type getType() {
            Object result = this.result;
            if (result == null) {
                // Lazy invocation of the target method on the provided type
                result = SpringReflectionUtils.invokeMethod(this.method, this.provider.getType());
                // Cache the result for further calls to getType()
                this.result = result;
            }
            return (result instanceof Type[] ? ((Type[]) result)[this.index] : (Type) result);
        }
    }

}
