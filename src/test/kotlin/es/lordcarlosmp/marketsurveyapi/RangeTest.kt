package es.lordcarlosmp.marketsurveyapi

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RangeTest {
	
	@Test
	fun contains() {
		assertTrue(Range(1, 2) in Range(1, 2))
		assertTrue(Range(1, 1) in Range(1, 2))
		assertTrue(Range(2, 2) in Range(1, 2))
		assertFalse(Range(1, 3) in Range(1, 2))
		assertFalse(Range(0, 1) in Range(1, 2))
		assertFalse(Range(-1, 2) in Range(1, 2))
	}
}