package com.livk.caffeine.aspect;

import com.livk.caffeine.annotation.DoubleCache;
import com.livk.caffeine.handler.CacheHandlerAdapter;
import com.livk.caffeine.util.SpelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.TreeMap;

/**
 * <p>
 * CacheAspect
 * </p>
 *
 * @author livk
 * @date 2022/3/30
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheHandlerAdapter adapter;

    @Around("@annotation(doubleCache)")
    public Object around(ProceedingJoinPoint point, DoubleCache doubleCache) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (doubleCache == null) {
            doubleCache = AnnotationUtils.findAnnotation(method, DoubleCache.class);
        }
        Assert.notNull(doubleCache, "doubleCache is null");
        String[] paramNames = signature.getParameterNames();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i], point.getArgs()[i]);
        }
        String spelResult = SpelUtils.parse(doubleCache.key(), treeMap);
        String realKey = doubleCache.cacheName() + ":" + spelResult;
        switch (doubleCache.type()) {
            case FULL -> {
                return adapter.readAndPut(realKey, point, doubleCache.timeOut());
            }
            case PUT -> {
                Object proceed = point.proceed();
                adapter.put(realKey, proceed, doubleCache.timeOut());
                return proceed;
            }
            case DELETE -> {
                Object proceed = point.proceed();
                adapter.delete(realKey);
                return proceed;
            }
        }
        return point.proceed();
    }
}