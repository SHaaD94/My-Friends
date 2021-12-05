package com.github.shaad.myfriends.service

import java.time.Clock
import javax.enterprise.context.ApplicationScoped

interface CurrentTimeProvider {
    fun now(): Long
}

/**
 * For sake of simplicity milliseconds are used
 * Obviously, it would be pretty weak idea to use only this timestamp for conflict resolution in prod
 */
@ApplicationScoped
class SimpleCurrentTimeProvider(private val clock: Clock = Clock.systemUTC()) : CurrentTimeProvider {
    override fun now(): Long = clock.millis()
}