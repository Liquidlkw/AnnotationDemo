package com.example.router_annotation_compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Log {
    private Messager messager;
    private Log(Messager messager){
        this.messager = messager;
    }

    public static Log getInstance(Messager messager){
        return new Log(messager);
    }

    public void i(String msg){
        messager.printMessage(Diagnostic.Kind.NOTE,msg+"   LKW" );
    }
}
