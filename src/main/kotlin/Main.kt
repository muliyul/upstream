fun main(args: Array<String>) {
	println(isAllowed("192.170.1.1"))
	println(isAllowed("192.170.5.5"))
}

const val BLACKLIST_FILE = "suspicious_ips.txt"

fun isAllowed(address: String): Boolean {
	return readBlackList(BLACKLIST_FILE).none { range ->
		address in range
	}
}

fun readBlackList(blacklistPath: String): List<Ipv4Range> {
	// if the blacklist is static and shipped with production code this is ok, otherwise it's probably
	// better to retrieve this list from a trusted source.
	val inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(blacklistPath)
		?: error("Could not find blacklist file in $blacklistPath.")

	return inputStream.bufferedReader()
		.readLines()
		.map { Ipv4Range(it) }
}

