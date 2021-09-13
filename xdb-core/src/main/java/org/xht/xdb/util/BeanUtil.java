package org.xht.xdb.util;

import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Slf4j
public class BeanUtil {

    public static Map<String, Object> toMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beaninfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                try {
                    String key = property.getName();//得到属性的name
                    Method get = property.getReadMethod();
                    Object value = get.invoke(bean);//执行get方法得到属性的值
                    map.put(key, value);
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            log.error("",e);
        }
        return map;
    }

    public static <T> T toBean(Map<String, Object> map, Class<T> beanClass) {
        T bean = null;//创建对象
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
            BeanInfo beaninfo = Introspector.getBeanInfo(beanClass, Object.class);
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                try {
                    String key = property.getName();
                    Object value = map.get(key);//得到属性name在map中对应的value。
                    Method set = property.getWriteMethod();//得到属性的set方法
                    set.invoke(bean, value);//执行set方法
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            log.error("",e);
        }
        return bean;
    }
}
