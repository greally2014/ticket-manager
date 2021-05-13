package com.greally2014.ticketmanager.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = Logger.getLogger(getClass().getName());

    // all controller methods
    @Pointcut("execution(* com.greally2014.ticketmanager.controller.*.*(..))")
    private void forControllerPackage() {};

    // all service methods
    @Pointcut("execution(* com.greally2014.ticketmanager.service.*.*(..))")
    private void forServicePackage() {};

    // all repository methods
    @Pointcut("execution(* com.greally2014.ticketmanager.dao.*.*(..))")
    private void forDaoPackage() {};

    // all controller, service and repository methods
    @Pointcut("forControllerPackage() || forServicePackage() || forDaoPackage()")
    private void forApplication() {};

    /**
     * Logs all method arguments before the method is called
     * @param joinpoint method call
     */
    @Before(value = "forApplication()")
    public void beforeCallingMethods(JoinPoint joinpoint) {
        String method = joinpoint.getSignature().toShortString(); // method name
        logger.info("=====> @Before: " + method);

        Object[] args = joinpoint.getArgs();
        Arrays.stream(args).forEach((arg) -> logger.info("=====> Argument: " + arg)); // logs each method argument

        System.out.println("\n");
    }

    /**
     * Logs method return value if the method returns a value
     * @param joinPoint method call
     * @param result method return value
     */
    @AfterReturning(pointcut = "forApplication()", returning = "result")
    public void afterReturningValue(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString(); // method name

        logger.info("=====> @AfterReturning: " + method);
        logger.info("=====> Result: " + result); // logs method return value

        System.out.println("\n");
    }
}
