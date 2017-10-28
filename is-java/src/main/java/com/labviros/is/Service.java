package com.labviros.is;

import java.lang.reflect.Method;

/**
 * Created by picoreti on 09/04/17.
 */
public class Service {

    final Method impl;
    final Class RequestType;
    final Class ResponseType;

    public Method getImpl() {
        return impl;
    }

    public Class getRequestType() {
        return RequestType;
    }

    public Service(Method impl, Class requestType, Class responseType) {
        this.impl = impl;
        RequestType = requestType;
        ResponseType = responseType;
    }

}
