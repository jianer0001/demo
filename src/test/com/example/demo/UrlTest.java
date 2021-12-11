package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月10日 22:14:00
 */
@SpringBootTest(classes = DemoApplication.class)
public class UrlTest {

    @Autowired
    RequestMappingHandlerMapping handlerMapping;

    @Autowired

    WebApplicationContext applicationContext;

    @Test
    public void test() {
        System.out.println(getAllUrl());
    }

    public List<Map<String, Object>> getAllUrl() {

        List<Map<String, Object>> urls = new ArrayList<>();

        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();


            // 这里可获取请求方式 Get,Post等等
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            String method = "";
            String methodType = "form";
            for (RequestMethod requestMethod : methods) {
                method = requestMethod.name();
            }

            List<Object> param = new ArrayList<>();
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            for (MethodParameter methodParameter : methodParameters) {
                // 参数
                RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
                if (requestBody != null) {
                    methodType = "json";
                }
                List<Map<String,Object>> mapList = new ArrayList<>();
                Class<?> parameterType = methodParameter.getParameterType();
                String parameterName = methodParameter.getParameterName();
                processParam(parameterType,parameterName,mapList);
                param.add(mapList);
            }

            Set<String> patternValues = info.getPatternValues();
            for (String url : patternValues) {
                if ("/error".equals(url)) {
                    continue;
                }
                Map<String, Object> map1 = new HashMap<>();
                map1.put("url", url);
                map1.put("method", method);
                map1.put("methodType", methodType);
                map1.put("param", param);
                urls.add(map1);
            }
        }
        return urls;
    }

    private void processParam(Class<?> param, String paramName, List<Map<String,Object>> list) {
        if (param.isPrimitive() ||
                param.isAssignableFrom(String.class)||
                param.isAssignableFrom(Long.class)||
                param.isAssignableFrom(Boolean.class)||
                param.isAssignableFrom(Double.class)||
                param.isAssignableFrom(Float.class)||
                param.isAssignableFrom(Integer.class)) {
            Map<String,Object> map = new HashMap<>();
            // 基本数据类型或者String类型
            map.put("paramType",param.getTypeName());
            map.put("paramName", paramName);
            list.add(map);
            // 参数名称为 paramName 类型为param
        } else {
            // 获取Class的所有字段
            List<Field> allFields = getAllFields(param);
            for (Field allField : allFields) {
                Class<?> type = allField.getType();
                String name = allField.getName();
                String paramN = paramName + "." + name;
                processParam(type, paramN,list);
            }
        }
    }

    public List<Field> getAllFields(Class<?> c) {
        List<Field> fieldList = new ArrayList<>();
        while (c != null) {
            fieldList.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        return fieldList;
    }
}