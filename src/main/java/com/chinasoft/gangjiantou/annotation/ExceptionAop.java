package com.chinasoft.gangjiantou.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 异常切面。记录异常上下文（打印发生异常时所在的方法和参数）
 */
@Aspect
@Component
@Slf4j
public class ExceptionAop {

    @Around(value = "execution(* com.chinasoft.gangjiantou.controller..*.*(..))")
    public Object invoke(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            Object[] args = joinPoint.getArgs();
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            String[] parameterNames = methodSignature.getParameterNames();
            Method method = methodSignature.getMethod();

            log.error("exception!method full name:{}", getClassAndMethodName(method));
            for (int i = 0; i < parameterNames.length; i++)
                log.error("exception!parameterName:{} = {}", parameterNames[i], args[i] == null ? "null" : args[i].toString());
            throw (RuntimeException) throwable;
        }
    }

    //获取方法类全名+方法名
    private String getClassAndMethodName(Method method) {
        //获取类全名
        String className = method.getDeclaringClass().getName();
        //获取方法名
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        StringBuffer stringBuffer = new StringBuffer(className).append(".")
                .append(methodName).append("(");
        Arrays.stream(parameters).forEach(e -> stringBuffer.append(e).append(", "));
        return stringBuffer.append(")").toString();
    }
}
