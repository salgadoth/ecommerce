package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(var orderDispatcher = new KafkaDispatcher<Order>()){
            try(var emailDispatcher = new KafkaDispatcher<Email>()) {
                for (var i = 0; i < 5; i++) {
                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 + 1;

                    var order = new Order(userId, orderId, new BigDecimal(amount));
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

                    var body = "Thanks for your order! We are currently processing your order.";
                    var email = new Email(userId, body);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
                }
            }
        };
    }
}
