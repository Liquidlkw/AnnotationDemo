package com.example.route_core;

import com.example.router_annotation.model.RouteBean;

import java.util.Map;

public interface IRouteGroup {
    void loadInto(Map<String, RouteBean> map);
}
