package br.com.alura.ecommerce;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderService extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                try {

                    //we are not caring about any security measures, we are only
                    //showing how to use http as a starting point
                    var email = req.getParameter("email");
                    var amount = new BigDecimal(req.getParameter("amount"));

                    var orderId = UUID.randomUUID().toString();

                    var order = new Order(orderId, amount, email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailCode = "Thank you for your order! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

                    System.out.println("New order sent successfully.");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("New order sent.");
                } catch (ExecutionException | InterruptedException e) {
                    throw new ServletException(e);
                }
            }
        }
    }
}
