import io.kotlintest.properties.Gen

fun Gen.Companion.gossips() = Gen.set(Gen.int().map(::Gossip))
fun Gen.Companion.route() = Gen.list(Gen.string().map(::Stop))

fun Gen.Companion.busDriver() =
    Gen.bind(Gen.int(), Gen.route(), Gen.gossips(), ::BusDriver)

fun Gen.Companion.busDrivers() =
    Gen.busDriver().let { Gen.set(it) }

