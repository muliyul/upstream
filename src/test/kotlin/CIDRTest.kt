import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

// Can be read from file but too much work already into this task
class CIDRTest {
	@ParameterizedTest(name = "CIDR: {0}, Parseable: {1}")
	@CsvSource("192.170.1.0/28,true", "192.170.1.0/-1,false", "192.170.1.0a/28,false", "300.170.1.0/28,false")
	fun parsing(input: String, parseable: Boolean) {
		if (parseable) {
			assertDoesNotThrow { Ipv4Range(input) }
		} else {
			assertThrows { Ipv4Range(input) }
		}
	}

	@ParameterizedTest(name = "CIDR: {0}, IP: {1}, In range: {2}")
	@CsvSource("192.170.1.0/28,192.170.1.1,true", "192.170.1.0/28,192.170.5.5,false")
	fun range(cidr: String, ip: String, expected: Boolean) {
		assertEquals(Ipv4Range(cidr).contains(ip), expected)
	}
}
