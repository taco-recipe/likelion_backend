package org.example.backendproject.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.backendproject.threadlocal.TraceIdHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Aspect
public class LogAspect {

    @Pointcut("execution(* org.example.backendproject..service..*(..))")
    public void method() {}

    //PointCut
    @Around("execution(* org.example.backendproject..service..*(..)) || " +
            "execution(* org.example.backendproject.oauth2..*(..)) || " +
            "execution(* org.example.backendproject.stompwebsocket..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        try{
            log.info("[AOP_LOG][TraceId] {} {} 메서드 호출, 호출 시간 {}",TraceIdHolder.get(), methodName,start);
            Object result = joinPoint.proceed();
            return result;

        }catch (Exception e){
            log.error("[AOP_LOG][TraceId] {} {} 메서드 예외 {}",TraceIdHolder.get(),methodName,e.getMessage());
            throw e;
        }
        finally{
            long end = System.currentTimeMillis();
            log.info("[AOP_LOG][TraceId] {} {} 메서드 실행 완료 {}, 소요시간 {} ms",TraceIdHolder.get(),methodName,end,(end-start));
            TraceIdHolder.clear();
        }

    }
}
