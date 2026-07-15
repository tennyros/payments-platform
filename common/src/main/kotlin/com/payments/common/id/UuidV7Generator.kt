package com.payments.common.id

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

object UuidV7Generator {
    fun generate(): UUID {
        val unixTimeMillis = System.currentTimeMillis() and 0xFFFFFFFFFFFFL
        val random = ThreadLocalRandom.current()

        val mostSignificantBits =
            (unixTimeMillis shl 16) or
                (0x7L shl 12) or
                random.nextLong(0x1000)

        val leastSignificantBits =
            random.nextLong() and 0x3FFF_FFFF_FFFF_FFFFL or Long.MIN_VALUE

        return UUID(mostSignificantBits, leastSignificantBits)
    }
}
