package com.example.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@SpringBootApplication
@RestController
@RequestMapping("/payment")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @PostMapping("/process")
    public String processPayment(@RequestBody PaymentRequest request) {
        // Simulación de la lógica de pago
        if (isValid(request)) {
            return "Pago procesado exitosamente por el monto de: " + request.getAmount();
        } else {
            return "Falló la validación del pago";
        }
    }

    // Cambiamos la visibilidad de private a protected o public
    protected boolean isValid(PaymentRequest request) {
        // Simulación de la validación de los detalles de pago
        return request.getCardNumber().length() == 16 &&
                request.getCvv().length() == 3 &&
                request.getExpirationDate().isAfter(LocalDate.now()) &&
                request.getAmount() > 0;
    }
}
