package org.kie.efesto.common.api.utils;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class CollectionUtilsTest {
	
	private Optional<Integer> result;

	@Test
	public void findAtMostOne_emptyCollection() throws Exception {
		result = findAtMostOne(asList(), matchAll(), exceptionProvider());
		
		assertThat(result).isNotPresent();
	}

	@Test
	public void findAtMostOne_oneElementNoFilter() throws Exception {
		result = findAtMostOne(asList(1), matchAll(), exceptionProvider());
		
		assertThat(result).isPresent().hasValue(1);
	}

	@Test
	public void findAtMostOne_oneElement_filterFails() throws Exception {
		result = findAtMostOne(asList(1), match(2), exceptionProvider());
		
		assertThat(result).isNotPresent();
	}

	
	@Test
	public void findAtMostOne_oneElement_filterSuccess() throws Exception {
		result = findAtMostOne(asList(2), match(2), exceptionProvider());
		
		assertThat(result).isPresent().hasValue(2);
	}
	
	@Test
	public void findAtMostOne_twoIdenticalElements_exception() throws Exception {
		assertThatExceptionOfType(KieEfestoCommonException.class).isThrownBy(
				() -> findAtMostOne(asList(1, 1), matchAll(), exceptionProvider()));
	}
	
	private Predicate<Integer> matchAll() {
		return x -> true;
	}

	private Predicate<Integer> match(int value) {
		return x -> x.equals(value);
	}

	private BiFunction<Integer, Integer, KieEfestoCommonException> exceptionProvider() {
		return (x, t) -> new KieEfestoCommonException();
	}
	


}
