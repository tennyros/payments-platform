package com.payments.common.id

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UuidV7GeneratorTest {
    @Test
    fun `generate returns uuid version 7`() {
        val uuid = UuidV7Generator.generate()

        assertEquals(7, uuid.version())
        assertEquals(2, uuid.variant())
        assertTrue(uuid.toString().isNotBlank())
    }

    @Test
    fun `generate returns unique values`() {
        val first = UuidV7Generator.generate()
        val second = UuidV7Generator.generate()

        assertTrue(first != second)
    }
}
