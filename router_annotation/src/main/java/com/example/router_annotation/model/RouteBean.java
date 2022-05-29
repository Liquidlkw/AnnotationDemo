package com.example.router_annotation.model;

import com.example.router_annotation.Route;

import javax.lang.model.element.Element;

public class RouteBean {
    public RouteBean(Type type, Route route, Element e) {
        this(type, e, null, route.path(), route.Group());
    }

    public enum Type {
        ACTIVITY, ISERVICE
    }

    private Type type;
    private Element element;
    private Class<?> destination;
    private String path;
    private String group;


    public RouteBean(Type type, Element element, Class<?> destination, String path, String group) {
        this.type = type;
        this.element = element;
        this.destination = destination;
        this.path = path;
        this.group = group;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
