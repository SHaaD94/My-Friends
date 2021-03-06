package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.*
import com.github.shaad.myfriends.util.WithLogger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FriendshipService(
    private val timeProvider: CurrentTimeProvider,
    private val eventLogService: EventLogService
) : WithLogger {
    private val peopleAdded = ConcurrentSkipListMap<Person, AtomicLong>()
    private val peopleRemoved = ConcurrentSkipListMap<Person, AtomicLong>()
    private val person2Friendships = ConcurrentHashMap<Person, MutableSet<Friendship>>()
    private val friendshipsAdded = ConcurrentHashMap<Friendship, AtomicLong>()
    private val friendshipsRemoved = ConcurrentHashMap<Friendship, AtomicLong>()

    fun addPerson(name: String): Person {
        val person = Person(name)
        val now = timeProvider.now()
        eventLogService.writeEvent(AddPersonEvent(now, name))
        updateTsIfGreater(now, peopleAdded, person)
        return person
    }

    fun doesExist(name: String): Boolean = doesExist(Person(name))

    fun getFriends(name: String): List<String> {
        val person = Person(name)
        if (!doesExist(person)) return emptyList()
        val visited = mutableSetOf<Person>()
        val queue = LinkedList<Person>()
        queue.add(person)
        while (queue.isNotEmpty()) {
            val curPerson = queue.pollFirst()
            visited.add(curPerson)
            val entry = person2Friendships[curPerson] ?: continue
            entry.filter { doesExist(it) }.forEach { (p1, p2) ->
                val next = if (p1 == curPerson) p2 else p1
                if (!visited.contains(next)) queue.add(next)
            }
        }
        return visited.asSequence().filter { it != person }.map { it.name }.toList().sorted()
    }

    fun getHandshakes(fromName: String, toName: String): List<String> {
        val from = Person(fromName)
        if (!doesExist(from)) return emptyList()
        val to = Person(toName)
        if (!doesExist(toName)) return emptyList()
        val visited = mutableSetOf<Person>()
        val queue = LinkedList<Person>()
        val previous = HashMap<Person, Person>()
        queue.add(from)

        fun buildResultPath(): List<String> {
            var currentPerson = to
            val resultPath = LinkedList<String>()
            resultPath.add(currentPerson.name)
            while (currentPerson != from) {
                currentPerson = previous[currentPerson]!!
                resultPath.addFirst(currentPerson.name)
            }
            return resultPath
        }

        while (queue.isNotEmpty()) {
            val curPerson = queue.pollFirst()
            val friendships = person2Friendships[curPerson] ?: continue
            visited.add(curPerson)
            friendships.filter { doesExist(it) }.forEach { (p1, p2) ->
                val next = if (p1 == curPerson) p2 else p1
                if (visited.contains(next)) return@forEach
                previous[next] = curPerson
                if (next == to) {
                    return buildResultPath()
                }
                queue.add(next)
            }
        }
        return emptyList()
    }

    fun removePerson(name: String) {
        val person = getPersonIfExists(name) ?: return
        val now = timeProvider.now()
        eventLogService.writeEvent(RemovePersonEvent(now, name))
        updateTsIfGreater(now, peopleRemoved, person)
        person2Friendships[person]?.forEach { fr ->
            updateTsIfGreater(now, friendshipsRemoved, fr)
        }
    }

    fun addFriendship(from: String, to: String) {
        val p1 = Person(from)
        val p2 = Person(to)
        val friendship = Friendship.instance(p1, p2)
        val now = timeProvider.now()
        eventLogService.writeEvent(AddFriendshipEvent(now, from, to))
        updateTsIfGreater(now, friendshipsAdded, friendship)
        person2Friendships.computeIfAbsent(p1) { ConcurrentHashMap.newKeySet() }.add(friendship)
        person2Friendships.computeIfAbsent(p2) { ConcurrentHashMap.newKeySet() }.add(friendship)
    }

    fun removeFriendship(fromName: String, toName: String) {
        val friendship = Friendship.instance(Person(fromName), Person(toName))
        val now = timeProvider.now()
        eventLogService.writeEvent(RemoveFriendshipEvent(now, fromName, toName))
        updateTsIfGreater(now, friendshipsRemoved, friendship)
    }

    private fun getPersonIfExists(name: String): Person? {
        val person = Person(name)
        if (!doesExist(person)) return null
        return person
    }

    private fun doesExist(person: Person): Boolean =
        (peopleRemoved[person]?.get() ?: Long.MIN_VALUE) < (peopleAdded[person]?.get() ?: Long.MIN_VALUE)

    private fun doesExist(friendship: Friendship): Boolean {
        if (!doesExist(friendship.p1) || !doesExist(friendship.p2)) return false
        return (friendshipsRemoved[friendship]?.get() ?: Long.MIN_VALUE) < (friendshipsAdded[friendship]?.get()
            ?: Long.MIN_VALUE)
    }

    private fun <K> updateTsIfGreater(now: Long, map: ConcurrentMap<K, AtomicLong>, key: K) {
        val lastAdded = map.computeIfAbsent(key) { AtomicLong(Long.MIN_VALUE) }
        var prevValue = lastAdded.get()

        while (now > prevValue && !lastAdded.compareAndSet(prevValue, now)) {
            prevValue = lastAdded.get()
        }
    }

    fun feedEvent(event: Event) {
        when (event) {
            is AddPersonEvent -> updateTsIfGreater(event.ts, peopleAdded, Person(event.name))
            is RemovePersonEvent -> updateTsIfGreater(event.ts, peopleRemoved, Person(event.name))
            is AddFriendshipEvent -> {
                val fr = Friendship.instance(Person(event.from), Person(event.to))
                updateTsIfGreater(event.ts, friendshipsAdded, fr)
                person2Friendships.computeIfAbsent(fr.p1) { ConcurrentHashMap.newKeySet() }.add(fr)
                person2Friendships.computeIfAbsent(fr.p2) { ConcurrentHashMap.newKeySet() }.add(fr)
            }
            is RemoveFriendshipEvent -> updateTsIfGreater(
                event.ts,
                friendshipsRemoved,
                Friendship.instance(Person(event.from), Person(event.to))
            )
        }
    }
}

