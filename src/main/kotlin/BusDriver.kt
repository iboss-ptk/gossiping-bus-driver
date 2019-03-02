data class BusDriver(val id: Int, val route: List<Stop>, val gossips: Set<Gossip> = setOf()) {
    fun dailyRoute(): Sequence<Stop> = generateSequence { route }
        .flatten()
        .take(DAILY_STOPS)

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