package com.example.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PaymentServiceApplication paymentServiceApplication;

	@Test
	void testValidPayment() {
		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("1234567812345678");
		request.setCvv("123");
		request.setExpirationDate(LocalDate.of(2025, 12, 31));
		request.setAmount(100.0);

		assertTrue(paymentServiceApplication.isValid(request), "El pago debería ser válido");
	}

	@Test
	void testInvalidPayment_CardNumber() {
		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("1234");
		request.setCvv("123");
		request.setExpirationDate(LocalDate.of(2025, 12, 31));
		request.setAmount(100.0);

		assertFalse(paymentServiceApplication.isValid(request), "El número de la tarjeta no es válido");
	}

	@Test
	void testInvalidPayment_Cvv() {
		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("1234567812345678");
		request.setCvv("12");
		request.setExpirationDate(LocalDate.of(2025, 12, 31));
		request.setAmount(100.0);

		assertFalse(paymentServiceApplication.isValid(request), "El CVV no es válido");
	}

	@Test
	void testInvalidPayment_ExpirationDate() {
		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("1234567812345678");
		request.setCvv("123");
		request.setExpirationDate(LocalDate.of(2020, 12, 31)); // Fecha expirada
		request.setAmount(100.0);

		assertFalse(paymentServiceApplication.isValid(request), "La fecha de expiración no es válida");
	}

	@Test
	void testInvalidPayment_Amount() {
		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("1234567812345678");
		request.setCvv("123");
		request.setExpirationDate(LocalDate.of(2025, 12, 31));
		request.setAmount(-100.0); // Monto negativo

		assertFalse(paymentServiceApplication.isValid(request), "El monto no debería ser negativo");
	}

	// Prueba de integración para el endpoint /process
	@Test
	void testProcessPaymentEndpoint_ValidPayment() {
		String url = "/payment/process";

		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("1234567812345678");
		request.setCvv("123");
		request.setExpirationDate(LocalDate.of(2025, 12, 31));
		request.setAmount(100.0);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		assertEquals("Pago procesado exitosamente por el monto de: 100.0", response.getBody());
	}

	@Test
	void testProcessPaymentEndpoint_InvalidPayment() {
		String url = "/payment/process";

		PaymentRequest request = new PaymentRequest();
		request.setCardNumber("123456781234");
		request.setCvv("12");
		request.setExpirationDate(LocalDate.of(2025, 12, 31));
		request.setAmount(100.0);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		assertEquals("Falló la validación del pago", response.getBody());
	}
}
