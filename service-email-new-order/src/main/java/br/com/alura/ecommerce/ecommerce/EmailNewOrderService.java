package br.com.alura.ecommerce.ecommerce;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.Message;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        var message = record.value();
        var order = message.getPayload();

        CorrelationId id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        var emailCode = "Thank you for your order! We are processing your order!";

        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
