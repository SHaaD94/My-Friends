package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Friendship
import com.github.shaad.myfriends.domain.Person
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FriendshipService(private val timeProvider: CurrentTimeProvider) {
    private val name2Person = ConcurrentHashMap<String, Person>()
    private val peopleAdded = ConcurrentHashMap<Person, Long>()
    private val peopleRemoved = ConcurrentHashMap<Person, Long>()
    private val person2Friendships = ConcurrentHashMap<Person, MutableSet<Friendship>>()
    private val friendshipsAdded = ConcurrentHashMap<Friendship, Long>()
    private val friendshipsRemoved = ConcurrentHashMap<Friendship, Long>()

    fun addPerson(name: String) {
        val person = name2Person.computeIfAbsent(name) { Person(it) }
        require(!peopleRemoved.contains(person)) { "Can not add removed person" }
        peopleAdded[person] = timeProvider.getNowNanos()
    }

    fun doesExist(name: String): Boolean = name2Person[name]?.let { doesExist(it) } ?: false

    fun getFriends(name: String): List<String> {
        val person = name2Person[name] ?: return emptyList()
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
        val from = name2Person[fromName] ?: return emptyList()
        val to = name2Person[toName] ?: return emptyList()
        val visited = mutableSetOf<Person>()
        val queue = LinkedList<LinkedList<Person>>()
        val startList = LinkedList<Person>()
        startList.add(from)
        queue.add(startList)
        while (queue.isNotEmpty()) {
            val curPath = queue.pollFirst()
            val curPerson = curPath.last
            visited.add(curPerson)
            val friendships = person2Friendships[curPerson] ?: continue
            friendships.filter { doesExist(it) }.forEach { (p1, p2) ->
                val next = if (p1 == curPerson) p2 else p1
                if (visited.contains(next)) return@forEach
                val nextPath = LinkedList(curPath)
                nextPath.add(next)
                if (next == to) return nextPath.map { it.name }
                queue.add(nextPath)
            }
        }
        return emptyList()
    }

    fun removePerson(name: String) {
        val person = name2Person[name] ?: return
        peopleRemoved[person] = timeProvider.getNowNanos()
    }

    fun addFriendship(from: String, to: String) {
        val p1 = name2Person[from] ?: return
        val p2 = name2Person[to] ?: return
        val friendship = Friendship.instance(p1, p2)
        synchronized(friendship.p1) {
            synchronized(friendship.p2) {
                person2Friendships.computeIfAbsent(p1) { ConcurrentHashMap.newKeySet() }.add(friendship)
                person2Friendships.computeIfAbsent(p2) { ConcurrentHashMap.newKeySet() }.add(friendship)
                friendshipsAdded[friendship] = timeProvider.getNowNanos()
            }
        }
    }

    fun removeFriendship(fromName: String, toName: String) {
        val p1 = name2Person[fromName] ?: return
        val p2 = name2Person[toName] ?: return
        friendshipsRemoved[Friendship.instance(p1, p2)] = timeProvider.getNowNanos()
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

