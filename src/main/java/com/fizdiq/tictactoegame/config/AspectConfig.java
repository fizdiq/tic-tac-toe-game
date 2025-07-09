package com.fizdiq.tictactoegame.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class AspectConfig {

    static final String IN = " in ";

    @Around("execution(* com..services..*(..)) || execution(* com..service..*(..))")
    public Object interceptorForServices(ProceedingJoinPoint joinPoint) throws Throwable {
        return getLogInterceptor(joinPoint, "Start method ", "Method ");
    }

    @Around("execution(* com..controllers..*(..)) || execution(* com..controller..*(..))")
    public Object interceptorForControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        return getLogInterceptor(joinPoint, "START Request ", "END Request ");
    }

    @Around("execution(* com..schedulers..*(..)) || execution(* com..scheduler..*(..))")
    public Object interceptorForScheduler(ProceedingJoinPoint joinPoint) throws Throwable {
        return getLogInterceptor(joinPoint, "Start Scheduler ", "Scheduler ");
    }

    private Object getLogInterceptor(ProceedingJoinPoint joinPoint, String startString, String endString)
            throws Throwable {
        StopWatch stopWatch = new StopWatch();
        String className = getMethodSignature(joinPoint).getDeclaringType().getSimpleName();
        String methodName = getMethodSignature(joinPoint).getName();
        try {
            stopWatch.start();
            log.info("{}{}" + IN + "{}", startString, methodName, className);
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            String elapsedTime = Common.getElapsedTime(stopWatch.getTotalTimeMillis());
            log.info("{}{}" + IN + "{} done in {}",
                    endString,
                    methodName,
                    className,
                    elapsedTime);
        }
    }

    private MethodSignature getMethodSignature(ProceedingJoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }
}
