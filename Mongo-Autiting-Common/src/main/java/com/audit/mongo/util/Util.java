package com.audit.mongo.util;

import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

    public static <T> T cast(final Object obj, final Class<T> _class) {
        T result = null;

        if (ObjectUtils.isNotEmpty(obj)) {
            if (_class.isInstance(obj)) {
                result = _class.cast(obj);
            } else {
                throw new ClassCastException(
                    String.format("class %s cannot be cast to class %s",
                        obj.getClass().getName(),
                        _class.getName()));
            }
        }

        return result;
    }

    public String getIdentityKey(Object object) {
        return org.springframework.util.ObjectUtils.getIdentityHexString(object);
    }
}
