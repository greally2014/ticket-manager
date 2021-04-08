package com.greally2014.ticketmanager.aspect;

import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Pointcut("execution(* com.greally2014.ticketmanager.controller.*.*(..))")
    private void forControllerPackage() {};

    @Pointcut("execution(* com.greally2014.ticketmanager.service.*.*(..))")
    private void forServicePackage() {};

    @Pointcut("execution(* com.greally2014.ticketmanager.dao.*.*(..))")
    private void forDaoPackage() {};

    @Pointcut("forControllerPackage() || forServicePackage() || forDaoPackage()")
    private void forApplication() {};

    @Pointcut("execution(public String com.greally2014.ticketmanager.controller.*.*(.., BindingResult))")
    private void forBindingResult() {};

    @Before(value = "forApplication()")
    public void beforeCallingArguments(JoinPoint joinpoint) {
        String method = joinpoint.getSignature().toShortString();

        logger.info("=====> @Before: " + method);
        Object[] args = joinpoint.getArgs();
        Arrays.stream(args).forEach((arg) -> logger.info("=====> Argument: " + arg));

        System.out.println("\n");
    }

    @AfterReturning(pointcut = "forApplication()", returning = "result")
    public void afterReturningValue(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("=====> @AfterReturning: " + method);
        logger.info("=====> Result: " + result);

        System.out.println("\n");
    }

    @Before(value = "forBindingResult()")
    public void afterReturningBindingError(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BindingResult bindingResult = (BindingResult) args[args.length - 1];
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            logger.info("=====> Form error(s): " + errors);
        }
    }

}
