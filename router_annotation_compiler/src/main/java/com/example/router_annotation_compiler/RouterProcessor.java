package com.example.router_annotation_compiler;

import com.example.router_annotation.Route;
import com.example.router_annotation.model.RouteBean;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

//注册注解处理器
@AutoService(Processor.class)
//注册注解Route
@SupportedAnnotationTypes(Constants.ANN_TYPE_ROUTE)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RouterProcessor extends AbstractProcessor {
    /**
     * 文件生成器
     */
    private Filer filer;
    private Log log;
    private Types typeUtils;
    private Elements elementUtils;
    /**
     * Route Map
     * key: group name
     * value: Grouped RouteBean List
     * <p>
     * RouteBean{
     * path
     * group
     * annotationElement
     * RouteBean.type
     * destination[class]
     * }
     */
    private Map<String, List<RouteBean>> groupedMap = new HashMap<>();

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
            //got all specific annotation set routes
            Set<? extends Element> routes = roundEnvironment.getElementsAnnotatedWith(Route.class);
            //traversal all annotation , parse and add
            for (Element e : routes) {
                parseElementAndAddMap(e);

            }

            TypeElement typeElementIRouteGroup = elementUtils.getTypeElement(Constants.IROUTE_GROUP);
            generateIRouteClass(typeElementIRouteGroup);

            return true;
        }

        return false;
    }

    /**
     * 生成IRoute类文件
     *
     * @param typeElementIRouteGroup
     */
    private void generateIRouteClass(TypeElement typeElementIRouteGroup) {
        //创建方法参数 Map<String,RouteBean>
        ParameterizedTypeName param = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteBean.class));

        //创建参数名 atlas
        ParameterSpec spec = ParameterSpec.builder(param, "map").build();

        //According group to create java files
        //key  groupName , value grouped path List
        //traversal groupedMap
        for (Map.Entry<String, List<RouteBean>> entry : groupedMap.entrySet()) {
            //@Override
            //public void loadInto(Map<String,RouteBean> map)
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInto")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(ParameterizedTypeName.get(Map.class, String.class, RouteBean.class), "map");


            //value
            List<RouteBean> routeBeans = entry.getValue();
            //add statement!!!!!
            for (RouteBean routeBean : routeBeans) {
                methodBuilder.addStatement("map.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeBean.getPath(),
                        RouteBean.class,
                        RouteBean.Type.class,
                        routeBean.getType(),
                        ClassName.get((TypeElement) routeBean.getElement()),
                        routeBean.getPath().toLowerCase(),
                        routeBean.getGroup().toLowerCase());
            }

            //生成分组路径map类
            String groupName = entry.getKey();
            String clazzName = "Route$Group$" + groupName;
            try {
                JavaFile.builder("com.route", TypeSpec.classBuilder(clazzName)
                        .addSuperinterface(ClassName.get(typeElementIRouteGroup))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodBuilder.build()).build())
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * 转换注解元素并添加入MAP
     *
     * @param e
     */
    private void parseElementAndAddMap(Element e) {
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
        if (typeUtils.isSubtype(typeMirrorRoute, typeMirrorActivity)) {
            //TODO:此处无法传入注解修饰的类【destination】 XXXActivity.class，是因为没有依赖对应的module,所以需要
            routeBean = new RouteBean(RouteBean.Type.ACTIVITY, route, e);
            addRouteMap(routeBean);

            log.i("houge");
        } else {
            throw new RuntimeException("**Just Support Activity/IService Route**---" + e);
        }
    }

    /**
     * 依据分组添加到分组表
     *
     * @param routeBean
     */
    private void addRouteMap(RouteBean routeBean) {
        if (routeVerify(routeBean)) {
            List<RouteBean> routeBeanList = groupedMap.get(routeBean.getGroup());
            if (routeBeanList != null) {
                //grouped list already
                routeBeanList.add(routeBean);
            } else {
                //group not exist
                routeBeanList = new ArrayList<>();
                routeBeanList.add(routeBean);
                groupedMap.put(routeBean.getGroup(), routeBeanList);
            }
        } else {
            log.i("**Group info Error**: " + routeBean.getPath());
        }
    }

    private boolean routeVerify(RouteBean routeBean) {
        String path = routeBean.getPath();
        String group = routeBean.getGroup();
        if (path.equals("") || !path.startsWith("/")) {
            return false;
        }
        //如果注解没有设置Group参数 那么就从path中取得 e.g. path=/[group]/[path]
        if (group.equals("")) {
            String subStrGroup = path.substring(1, path.indexOf("/", 1));
            if (subStrGroup.equals("")) {
                return false;
            }

            routeBean.setGroup(subStrGroup);
            return true;
        }

        return true;
    }




}
