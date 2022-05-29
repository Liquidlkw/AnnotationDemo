package com.example.router_annotation_compiler;

import com.example.router_annotation.Route;
import com.example.router_annotation.model.RouteBean;
import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

//注册注解
@AutoService(Processor.class)
@SupportedAnnotationTypes(Constants.ANN_TYPE_ROUTE)

public class RouterProcessor extends AbstractProcessor {
    /**
     * 文件生成器
     */
    private Filer filer;
    private Log log;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = Log.getInstance(processingEnv.getMessager());
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //all annotation set
        for (TypeElement s : set) {
            //specific annotation set routes
            Set<? extends Element> routes = roundEnvironment.getElementsAnnotatedWith(Route.class);
            //traversal parse annotation
            for (Element e : routes) {
                log.i("Route===" + routes.toString());
                parseElement(e);
            }
            return true;
        }

        return false;
    }

    private void parseElement(Element e) {
        //annotation class info eg: Activity
        //got Activity element node
        TypeElement typeElement = elementUtils.getTypeElement(Constants.CLZ_ACTIVITY);
        //got element node info
        final TypeMirror typeMirrorActivity = typeElement.asType();

        //got route element node info
        TypeMirror typeMirrorRoute = e.asType();
        //got route annotation
        Route route = e.getAnnotation(Route.class);

        RouteBean routeBean;
        //typeMirrorRoute is subType for typeMirrorActivity?
        if (typeUtils.isSubtype(typeMirrorRoute, typeMirrorActivity)){
            routeBean = new RouteBean(RouteBean.Type.ACTIVITY, route, e);
            createRouteMap(routeBean);
        }else {
            throw new RuntimeException("**Just Support Activity/IService Route**---"+e);
        }



    }

    /**
     * 创建分组表
     * @param routeBean
     */
    private void createRouteMap(RouteBean routeBean) {
        if (routeVerify(routeBean)){

        }
    }

    private boolean routeVerify(RouteBean routeBean) {
       String path = routeBean.getPath();
       String group = routeBean.getGroup();
       if (path.equals("")||!path.startsWith("/")){
           return false;
       }
       if (group.equals("")){
           String subStrPath = path.substring(1, path.indexOf("/", 1));

       }


       return true;
    }


}
