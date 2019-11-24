package com.alibaba.nacos.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/web_client")
public class WebClientTestController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * 在定义WebClient.Builder的时候，也增加了@LoadBalanced注解，其原理与之前的RestTemplate时一样的
     * @return
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder(){
        return WebClient.builder();
    }

    /**
     * 很奇怪，一直报错java.lang.IllegalStateException: No suitable default ClientHttpConnector found
     * @return
     */
    @GetMapping("/test")
    public Mono<String> test() {
        Mono<String> result = webClientBuilder.build()
                .get()
                .uri("http://service-provider/echo/didi")
                .retrieve()
                .bodyToMono(String.class);
        return result;
    }
}
