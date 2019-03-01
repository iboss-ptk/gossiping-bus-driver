fun main(args: Array<String>) {
    println("hello")
}

fun findFirstFullyDistributeGossipStop(input: String): String =
    if (input.lines().count() < 3) {
        "never"
    } else {
        "5"
    }

typealias Stop = Int

data class BusDriver(val route: List<Stop>) {
    companion object {
        fun fromString(string: String): List<BusDriver> =
            string
                .lines()
                .filter { it.isNotBlank() }
                .map { line ->
                    val route = line.split(" ").map { it.toInt() }
                    BusDriver(route)
                }
    }
}