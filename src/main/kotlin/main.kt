fun main(args: Array<String>) {
    println("hello")
}

fun entryPoint(input: String): String {
    val busDrivers= BusDriver.fromString(input)
    val timeToCompletelySpreadGossip = BusDriver.timeToCompletelySpreadGossip(busDrivers)
    val numStopsToCompletelySpreadGossip = timeToCompletelySpreadGossip?.plus(1)?.toString()

    return  numStopsToCompletelySpreadGossip ?: "never"
}
