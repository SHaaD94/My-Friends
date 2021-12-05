package com.github.shaad.myfriends.service

import java.time.Clock
import javax.enterprise.context.ApplicationScoped

interface CurrentTimeProvider {
    fun now(): Long
}

/**
 * For sake of simplicity milliseconds are used
 * Obviously, it would be pretty poor choice for prod
 */
@ApplicationScoped
class SimpleCurrentTimeProvider(private val clock: Clock = Clock.systemUTC()) : CurrentTimeProvider {
    override fun now(): Long = clock.millis()
}