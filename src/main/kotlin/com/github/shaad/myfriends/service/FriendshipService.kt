package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Friendship
import com.github.shaad.myfriends.domain.Person
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FriendshipService(private val timeProvider: CurrentTimeProvider) {
    private val peopleAdded = ConcurrentHashMap<Person, Long>()
    private val peopleRemoved = ConcurrentHashMap<Person, Long>()
    private val person2Friendships = ConcurrentHashMap<Person, MutableSet<Friendship>>()
    private val friendshipsAdded = ConcurrentHashMap<Friendship, Long>()
    private val friendshipsRemoved = ConcurrentHashMap<Friendship, Long>()

    fun addPerson(name: String): Person {
        val person = Person(name)
        peopleAdded[person] = timeProvider.getNowNanos()
        // `now` supposed to be always greater than any data already existed
        peopleRemoved.remove(person)

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
        val now = timeProvider.getNowNanos()
        peopleRemoved[person] = now
        person2Friendships[person]?.filter { doesExist(it) }?.forEach { fr ->
            friendshipsRemoved[fr] = now
        }
    }

    fun addFriendship(from: String, to: String) {
        val p1 = getOrCreatePerson(from)
        val p2 = getOrCreatePerson(to)
        val friendship = Friendship.instance(p1, p2)
        friendshipsAdded[friendship] = timeProvider.getNowNanos()
        friendshipsRemoved.remove(friendship)
        person2Friendships.computeIfAbsent(p1) { ConcurrentHashMap.newKeySet() }.add(friendship)
        person2Friendships.computeIfAbsent(p2) { ConcurrentHashMap.newKeySet() }.add(friendship)
    }

    fun removeFriendship(fromName: String, toName: String) {
        val friendship = Friendship.instance(Person(fromName), Person(toName))
        friendshipsRemoved[friendship] = timeProvider.getNowNanos()
    }

    private fun getPersonIfExists(name: String): Person? {
        val person = Person(name)
        if (!doesExist(person)) return null
        return person
    }

    private fun getOrCreatePerson(name: String): Person {
        return getPersonIfExists(name) ?: addPerson(name)
    }

    private fun doesExist(person: Person): Boolean =
        peopleRemoved.getOrDefault(person, Long.MIN_VALUE) < peopleAdded.getOrDefault(person, Long.MIN_VALUE)

    private fun doesExist(friendship: Friendship): Boolean {
        if (!doesExist(friendship.p1) || !doesExist(friendship.p2)) return false
        return friendshipsRemoved.getOrDefault(friendship, Long.MIN_VALUE) < friendshipsAdded.getOrDefault(
            friendship, Long.MIN_VALUE
        )
    }
}

