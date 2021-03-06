package com.proxy.config.v3_proxyfactory;

import com.proxy.app.v1.OrderControllerV1;
import com.proxy.app.v2.OrderControllerV2;
import com.proxy.app.v2.OrderRepositoryV2;
import com.proxy.app.v2.OrderServiceV2;
import com.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import com.proxy.trace.logtrace.LogTrace;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 *@title : ProxyFactoryConfigV2
 *@author : wikyubok 
 *@date : "2021-11-08 16:15:58"
 *@description : proxyfactory config V2 
*/


@Slf4j
@Configuration
public class ProxyFactoryConfigV2 {
    
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {

        OrderControllerV2 orderController = new OrderControllerV2(orderServiceV2(logTrace));

        ProxyFactory factory = new ProxyFactory(orderController);

        factory.addAdvisor(getAdvisor(logTrace));

        OrderControllerV2 proxy = (OrderControllerV2) factory.getProxy();
        log.info("ProxyFactory proxy = {} , target = {}", proxy.getClass(), orderController.getClass());

        return proxy;
    }

    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 orderService = new OrderServiceV2(orderRepositoryV2(logTrace));

        ProxyFactory factory = new ProxyFactory(orderService);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderServiceV2 proxy = (OrderServiceV2) factory.getProxy();
        log.info("ProxyFactory proxy = {} , target = {}", proxy.getClass(), orderService.getClass());
        return proxy;

    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 orderRepository = new OrderRepositoryV2();

        ProxyFactory factory = new ProxyFactory(orderRepository);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderRepositoryV2 proxy = (OrderRepositoryV2) factory.getProxy();
        log.info("ProxyFactory proxy = {} , target = {}", proxy.getClass(), orderRepository.getClass());

        return proxy;

    }

    private Advisor getAdvisor(LogTrace logTrace) {

        // pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        // advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);

        return new DefaultPointcutAdvisor(pointcut, advice);
    }

}
