// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HookEvent.java
package com.drdanick.McRKit;

import com.drdanick.McRKit.module.Module;

public class HookEvent {

    public enum Type {

        STDIN,
        STDOUT,
        STDERR,
        COMMAND;
    }

    public HookEvent(Type type1, Object obj) {
        data = obj;
        type = type1;
        module = null;
    }

    public HookEvent(Type type1, Object obj, Module module1) {
        data = obj;
        type = type1;
        module = module1;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public Module getModule() {
        return module;
    }
    private Object data;
    private Type type;
    private Module module;
}
