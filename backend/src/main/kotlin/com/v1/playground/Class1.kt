package com.v1.playground

import kotlin.times

/*
* val/var
* functions
* null safety
* when
* collections and lambdas
* data class
* java decompile
* */

fun main() {
    varVal()
    functions() // calling a function
    nullSafety()
    whenExp()
    collections()
    firstDataClass()
}

fun varVal() {
    val town = "Kragujevac" // val means immutable (just like final or readonly)
    var spotsNumber = 18    // mutable, type is inferred
    spotsNumber += 2
    val pricePerHour: Double = 120.0

    println("Parking in town $town has $spotsNumber spots, and two hours costs ${pricePerHour * 2}.")
}

// Functions: expression body, default and named arguments
fun functions() {
    println(price(3))
    println(price(3, discount = 0.1))
    println(price(hours = 2, pricePerHour = 150.0))
}

fun price(hours: Int, pricePerHour: Double = 120.0, discount: Double = 0.0) = hours * pricePerHour * (1 - discount)

// Null safety show of
fun nullSafety(){
    var vozac: String? = null                       // ? = can be null
    println(vozac?.length)                          // safe call -> null
    println(vozac?.uppercase() ?: "Unknown driver") // Elvis operator -> default valueDo

    vozac = "Ana"
    println(vozac.length)                           // smart cast: compiler knows it is not null
}

// if/when expressions show of
fun occupancyDescription(procent: Int): String = when {
    procent < 30 -> "Empty"
    procent in 30..70 -> "Normal"
    procent > 70 -> "Almost full"
    else -> "?"
}

fun whenExp() {
    val procent = 45
    val message = if (procent > 50) "Crowded" else "There are spots available"
    println("$message / ${occupancyDescription(procent)}")

    for (i in 1..3) print("A$i ")
    for (i in 6 downTo 1 step 2) print("B$i ")
    println()
}

// Collections and lambdas (instead of Stream API/LINQ)
fun collections() {
    val labels = listOf("A1", "A2", "B1", "B2", "B3", "C1")   // immutable list
    val taken = mutableSetOf("A2", "B3")                    // mutable set

    val free = labels.filter { it !in taken }
    println("Free spots: $free")

    val perZones = labels.groupBy { it.first() }              // Map<Char, List<String>>
    println(perZones)
    println(perZones.mapValues { (_, mesta) -> mesta.size })

    println(labels.joinToString(", ") { it.lowercase() })
    println(labels.count { it.startsWith("B") })
    println(labels.firstOrNull { it.startsWith("Z") } ?: "no zone Z")
}

/* 1. Mini Task for students
    Using labels, print only spots from B zone in caps lock, separated by comma.

    Build solution step by step.
    labele.filter { it.startsWith("B") }.map { it.uppercase() }.joinToString(", ")
* */

// data class show of with decompile to Java
data class Spot(val sign: String, val zone: String, val taken: Boolean = false)

fun firstDataClass(){
    val a1 = Spot("A1", "A1", true)
    val a1Taken = a1.copy(taken = true)                     // immutable objects + copy()
    println(a1)                                             // prewritten toString() for us
    println( a1 == Spot("A1","A"))             // prewritten equals() for us
    println(a1Taken)
    val (sign, zone) = a1                                   // decoupling
}