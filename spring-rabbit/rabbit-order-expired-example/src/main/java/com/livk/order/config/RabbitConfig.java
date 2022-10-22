package com.livk.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * RabbitConfig
 * </p>
 *
 * @author livk
 * @date 2022/10/13
 */
@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    public final static String orderQueue = "order_queue";

    //交换机名称
    public final static String orderExchange = "order_exchange";

    // routingKey
    public final static String routingKeyOrder = "routing_key_order";

    //死信消息队列名称
    public final static String dealQueueOrder = "deal_queue_order";

    //死信交换机名称
    public final static String dealExchangeOrder = "deal_exchange_order";

    //死信 routingKey
    public final static String deadRoutingKeyOrder = "dead_routing_key_order";

    public final static String MESSAGE_TTL = "x-message-ttl";

    //死信队列 交换机标识符
    public static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";

    //死信队列交换机绑定键标识符
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Bean
    public Queue orderQueue() {
        Map<String, Object> args = new HashMap<>(2);
        args.put(MESSAGE_TTL, 30 * 1000);
        args.put(DEAD_LETTER_QUEUE_KEY, dealExchangeOrder);
        args.put(DEAD_LETTER_ROUTING_KEY, deadRoutingKeyOrder);
        return new Queue(orderQueue, true, false, false, args);
    }

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(orderExchange);
    }

    @Bean
    Binding bindingDirectExchangeDemo5() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(routingKeyOrder);
    }

    @Bean
    public Queue deadQueueOrder() {
        return new Queue(dealQueueOrder, true);
    }

    @Bean
    public DirectExchange deadExchangeOrder() {
        return new DirectExchange(dealExchangeOrder);
    }

    @Bean
    public Binding bindingDeadExchange() {
        return BindingBuilder.bind(deadQueueOrder()).to(deadExchangeOrder()).with(deadRoutingKeyOrder);
    }
}