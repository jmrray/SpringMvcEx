package org.stringtree.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.beanutils.MethodUtils;

public class JSONWriter {

    private StringBuffer buf = new StringBuffer();
    private Stack<Object> calls = new Stack<Object>();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    boolean emitClassName = true;
    boolean emitObject = false;
    boolean translateNull = false;
    
    public JSONWriter(boolean emitClassName) {
        this.emitClassName = emitClassName;
    }
    
    public JSONWriter(boolean emitClassName, boolean emitObject) {
        this.emitClassName = emitClassName;
        this.emitObject = emitObject;
    }
    
    public JSONWriter setTranslateNull(boolean translateNull) {
    	this.translateNull = translateNull;
    	return this;
    }
    
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public JSONWriter() {
        this(true);
    }

    public String write(Object object) {
        buf.setLength(0);
        value(object);
        return buf.toString();
    }

    public String write(long n) {
        return String.valueOf(n);
    }

    public String write(double d) {
        return String.valueOf(d);
    }

    public String write(char c) {
        return "\"" + c + "\"";
    }
    
    public String write(boolean b) {
        return String.valueOf(b);
    }
    
    private void value(Object object){
    	value(object, false);
    }

    @SuppressWarnings("unchecked")
	private void value(Object object, boolean emitObj) {
        if (object == null || cyclic(object)) {
        	if(translateNull)
        		add("\"\"");
        	else
        		add("null");
        } else {
            calls.push(object);
            try {
				if (object instanceof Class) string(object);
				else if (object instanceof Boolean) bool(((Boolean) object).booleanValue());
				else if (object instanceof Number) add(object);
				else if (object instanceof String) string(object);
				else if (object instanceof Date) date(object);
				else if (object instanceof Character) string(object);
				else if (object instanceof Map) map((Map)object);
				else if (object.getClass().isArray()) array(object);
				else if (object instanceof Iterator) array((Iterator)object);
				else if (object instanceof Collection) array(((Collection)object).iterator());
				else  {
				    if (emitObj) {
				        beanWitIdValue(object);
				    } else {
				        bean(object);
				    }
				}
			} catch (Exception ignore) { //org.hibernate.LazyInitializationException
                ignore.printStackTrace();
				add("null");
			}
            calls.pop();
        }
    }

    private boolean cyclic(Object object) {
        Iterator<Object> it = calls.iterator();
        while (it.hasNext()) {
            Object called = it.next();
            if (object == called) return true;
        }
        return false;
    }
    
    private void beanWitIdValue(Object object) {
        BeanInfo info;
        boolean addedSomething = false;
        try {
            try {
                Object obj = MethodUtils.invokeMethod(object, "getId", null);
                value(obj, false);
                return;
            } catch (NoSuchMethodException ex) {
            }
            add("{");
            info = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (int i = 0; i < props.length; ++i) {
                PropertyDescriptor prop = props[i];
                String fieldName = prop.getName();
                try {
                    Field field = object.getClass().getField(fieldName);
                    if (field != null && Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                } catch (NoSuchFieldException e) {
                }
                String name = prop.getName();
                Method accessor = prop.getReadMethod();
                if ((emitClassName==true || !"class".equals(name)) && accessor != null) {
                    if (prop.getName().equals("id")) {
                        if (!accessor.isAccessible()) accessor.setAccessible(true);
                        Object value = accessor.invoke(object, (Object[])null);
                        if (addedSomething) add(',');
                        add(name, value);
                        addedSomething = true;
                    }
                }
            }
            Field[] ff = object.getClass().getFields();
            for (int i = 0; i < ff.length; ++i) {
                Field field = ff[i];
                if (field.getName().equals("id")) {
                    if (addedSomething) add(',');
                    add(field.getName(), field.get(object));
                    addedSomething = true;
                }
            }
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.getCause().printStackTrace();
            ite.printStackTrace();
        } catch (IntrospectionException ie) {
            ie.printStackTrace();
        }
        add("}");
    }
    
    private void bean(Object object) {
        add("{");
        BeanInfo info;
        boolean addedSomething = false;
        try {
            info = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (int i = 0; i < props.length; ++i) {
                PropertyDescriptor prop = props[i];
                String name = prop.getName();
                Method accessor = prop.getReadMethod();
                if ((emitClassName==true || !"class".equals(name)) && accessor != null) {
                    if (!accessor.isAccessible()) accessor.setAccessible(true);
                    Object value = null;
                    JSONSkipAnno skipAnno = accessor.getAnnotation(JSONSkipAnno.class);
                    if (skipAnno != null) {
                        if ("true".equalsIgnoreCase(skipAnno.value())) {
                            continue;
                        }
                    }
                    JSONTitleAnno titleAnno = accessor.getAnnotation(JSONTitleAnno.class);
                    if (titleAnno != null) {
                        name = skipAnno.value();
                    }
                    
                    try {
                        value = accessor.invoke(object, (Object[])null);
                    } catch (Exception e) {
                        value = e.getMessage();
                    }
                    if (addedSomething) add(',');
                    add(name, value);
                    addedSomething = true;
                }
            }
            Field[] ff = object.getClass().getFields();
            for (int i = 0; i < ff.length; ++i) {
                Field field = ff[i];
                if (addedSomething) add(',');
                add(field.getName(), field.get(object));
                addedSomething = true;
            }
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (IntrospectionException ie) {
            ie.printStackTrace();
        } 
        add("}");
    }

    private void add(String name, Object value) {
        add('"');
        add(name);
        add("\":");
        value(value, emitObject);
    }

    private void map(Map<String,Object> map) {
        add("{");
        Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?,?> e = (Map.Entry<?,?>) it.next();
            value(e.getKey(), emitObject);
            add(":");
            value(e.getValue(), emitObject);
            if (it.hasNext()) add(',');
        }
        add("}");
    }
    
    private void array(Iterator<?> it) {
        add("[");
        while (it.hasNext()) {
            value(it.next(), emitObject);
            if (it.hasNext()) add(",");
        }
        add("]");
    }

    private void array(Object object) {
        add("[");
        int length = Array.getLength(object);
        for (int i = 0; i < length; ++i) {
            value(Array.get(object, i), emitObject);
            if (i < length - 1) add(',');
        }
        add("]");
    }

    private void bool(boolean b) {
        add(b ? "true" : "false");
    }

    private void date(Object obj) {
        string(dateFormat.format((Date)obj));
    }

    private void string(Object obj) {
        add('"');
        CharacterIterator it = new StringCharacterIterator(obj.toString());
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (c == '"') add("\\\"");
            else if (c == '\\') add("\\\\");
            else if (c == '/') add("\\/");
            else if (c == '\b') add("\\b");
            else if (c == '\f') add("\\f");
            else if (c == '\n') add("\\n");
            else if (c == '\r') add("\\r");
            else if (c == '\t') add("\\t");
            else if (Character.isISOControl(c)) {
                unicode(c);
            } else {
                add(c);
            }
        }
        add('"');
    }

    private void add(Object obj) {
        buf.append(obj);
    }

    private void add(char c) {
        buf.append(c);
    }

    static char[] hex = "0123456789ABCDEF".toCharArray();

    private void unicode(char c) {
        add("\\u");
        int n = c;
        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xf000) >> 12;
            add(hex[digit]);
            n <<= 4;
        }
    }
}
