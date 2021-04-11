package com.greally2014.ticketmanager.aspect;

import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Pointcut("execution(* com.greally2014.ticketmanager.controller.*.*(..))")
    private void forControllerPackage() {};

    @Pointcut("execution(* com.greally2014.ticketmanager.service.*.*(..))")
    private void forServicePackage() {};

    @Pointcut("execution(* com.greally2014.ticketmanager.dao.*.*(..))")
    private void forDaoPackage() {};

    @Pointcut("forControllerPackage() || forServicePackage() || forDaoPackage()")
    private void forApplication() {};

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

    @Before(value = "forControllerPackage()")
    public void afterReturningBindingError(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Optional<BindingResult> optionalBindingResult = Arrays.stream(args)
                .filter(o -> o instanceof BindingResult)
                .findAny()
                .map(o -> (BindingResult) o);
        if (optionalBindingResult.isPresent()) {
            BindingResult bindingResult = optionalBindingResult.get();
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            logger.info("=====> Form error(s): " + errors);
            System.out.println();
        }
    }
}
