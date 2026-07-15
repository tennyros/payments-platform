package com.payments.processor.api

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentStatus
import com.payments.processor.repository.PaymentRepository
import com.payments.processor.service.PaymentEventPublisher
import com.payments.processor.service.PaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.UUID

class PaymentControllerTest {
    @Test
    fun `createPayment returns created payment`() {
        val repository: PaymentRepository = mock()
        val paymentEventPublisher: PaymentEventPublisher = mock()
        val paymentService = PaymentService(repository, paymentEventPublisher)
        val controller = PaymentController(paymentService)
        val request =
            CreatePaymentRequest(
                accountId = UUID.fromString("55555555-5555-5555-5555-555555555555"),
                amount = BigDecimal("99.95"),
                currency = "eur",
            )

        whenever(repository.save(any())).thenAnswer { invocation ->
            Mono.just(invocation.getArgument(0))
        }
        whenever(paymentEventPublisher.publishPaymentCreated(any())).thenReturn(Mono.empty())

        val response = controller.createPayment(request).block()!!

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(request.accountId, response.body!!.accountId)
        assertEquals(request.amount, response.body!!.amount)
        assertEquals("EUR", response.body!!.currency)
        assertEquals(PaymentStatus.PENDING, response.body!!.status)
        assertEquals(7, response.body!!.id.version())
        assertTrue(
            response.body!!
                .id
                .toString()
                .isNotBlank(),
        )
    }

    @Test
    fun `listPayments returns empty flux by default`() {
        val repository: PaymentRepository = mock()
        val paymentEventPublisher: PaymentEventPublisher = mock()
        val paymentService = PaymentService(repository, paymentEventPublisher)
        val controller = PaymentController(paymentService)

        whenever(repository.findAll()).thenReturn(Flux.empty())

        val payments = controller.listPayments().collectList().block()!!

        assertEquals(0, payments.size)
    }
}
