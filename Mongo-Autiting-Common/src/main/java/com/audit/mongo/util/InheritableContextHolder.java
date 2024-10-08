package com.audit.mongo.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class InheritableContextHolder {

    private  final InheritableThreadLocal<Map<String, Object>> THREAD_LOCAL = new InheritableThreadLocal<>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    public void setObject(final String key, final Object value) {
        if (StringUtils.isEmpty(key) || ObjectUtils.isEmpty(value)) {
            log.debug("Key {} or value {} is empty cannot put into inheritable thread local", key, value);
            return;
        }
        THREAD_LOCAL.get().put(key, value);
    }
    public <T> T getObject(final String key, final Class<T> _class) {
        return Util.cast(getObject(key), _class);
    }

    public Object getObject(final String key) {
        return THREAD_LOCAL.get().get(key);
    }

    public Object remove(final String key) {
        return THREAD_LOCAL.get().remove(key);
    }


}
