data class Stop(val id: String)

fun stopsOf(vararg ids: String): List<Stop> =
    ids.toList().map(::Stop)

fun stopsOf(ids: List<String>): List<Stop> =
    ids.toList().map(::Stop)
