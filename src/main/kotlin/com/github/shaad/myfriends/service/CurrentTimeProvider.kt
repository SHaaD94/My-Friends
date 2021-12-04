package com.github.shaad.myfriends.service

import javax.enterprise.context.ApplicationScoped

interface CurrentTimeProvider {
    fun getNowNanos(): Long
}

@ApplicationScoped
class SimpleCurrentTimeProvider : CurrentTimeProvider {
    override fun getNowNanos(): Long = System.nanoTime()
}