import arrow.syntax.collections.tail

data class BusDriver(
    val id: Int,
    val route: List<Stop>,
    val gossips: Set<Gossip> = setOf(Gossip(id)),
    val numStop: Int = 1
) {
    val dailyRoute: Sequence<Stop> = generateSequence { route }
        .flatten()
        .take(DAILY_STOPS)

    val currentStop = route.first()

    fun move(): BusDriver? = if (numStop < DAILY_STOPS) {
        copy(
            route = route.tail() + route.first(),
            numStop = numStop + 1
        )
    } else {
        null
    }


    fun listen(newGossip: Set<Gossip>): BusDriver =
        this.copy(gossips = gossips.union(newGossip))

    companion object {
        private const val DAILY_DRIVE_TIME = 480
        const val DAILY_STOPS = DAILY_DRIVE_TIME + 1

        fun fromString(string: String): List<BusDriver> =
            string
                .lines()
                .filter { it.isNotBlank() }
                .mapIndexed { id, line -> BusDriver(id, stopsOf(line.split(" "))) }
    }
}