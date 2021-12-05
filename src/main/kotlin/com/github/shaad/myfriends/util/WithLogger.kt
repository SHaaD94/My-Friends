package com.github.shaad.myfriends.util

import org.jboss.logging.Logger

interface WithLogger {
    fun log(): Logger = Logger.getLogger(this::class.java)
}