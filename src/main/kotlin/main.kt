fun main(args: Array<String>) {
    println("hello")
}

fun entryPoint(input: String): String {
    val busDrivers= BusDriver.fromString(input).toSet()
    val result = Gossip.numStopsToCompletelySpread(busDrivers)?.toString()

    return  result ?: "never"
}
