import java.math.BigInteger

class Ipv4Range(
	cidr: String
) {
	private val low: BigInteger
	private val high: BigInteger

	private companion object {
		private const val octet = "(0|[1-2]\\d{0,2})"
		private val ipv4Rgx = """^[1-2]\d{0,2}\.$octet\.$octet\.$octet$""".toRegex()
	}

	init {
		val (host, prefixLength) = cidr.split("/")
		require(ipv4Rgx.matches(host)) { "Invalid host $host" }

		val maskLength = prefixLength.toInt()
		require(maskLength in 0..32) { "Invalid network mask $prefixLength" }

		// 1. convert the host to byte array.
		// there is an Inet4Address class which I did not know if it is allowed.
		val baseAddr = host.toOctetBytes()

		// 2. initialize the mask buffer with 1's (noop mask)
		val maskBuffer = ByteArray(Int.SIZE_BYTES) { 0xff.toByte() }

		// 3. create the mask
		val maskValue = BigInteger(1, maskBuffer)
			// flip all magnitude bits, leave the sign bit
			.not()
			// shift right to create the mask (copy the sign bit maskLength times)
			.shr(maskLength)

		// 4. wrap the bytes of the base address so we can perform logical operations on it
		val hostValue = BigInteger(1, baseAddr)

		// 5. the low address can be found using a logical AND operation with the mask.
		low = hostValue.and(maskValue)

		// 6. the high address is the sum of the low address and the mask complement (logical OR).
		high = low.add(maskValue.not())
	}

	operator fun contains(address: String): Boolean {
		val addressNum = BigInteger(1, address.toOctetBytes())

		return addressNum in low..high
	}

	private fun String.toOctetBytes() = split(".").map { it.toInt().toByte() }.toByteArray()
}
