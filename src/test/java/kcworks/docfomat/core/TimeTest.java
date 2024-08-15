package kcworks.docfomat.core;

import kcworks.util.LocalDateTimes;
import org.junit.jupiter.api.*;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TimeTest {
	private static final PrintStream out = System.out;

	@Test
	public void test(){
		LocalDateTime now = LocalDateTime.now();
		out.println(String.format("%d: %s", now.toEpochSecond(ZoneOffset.UTC), now.format(LocalDateTimes.FORMATTER_DATETIME_SECONDS)));

		LocalDateTime before = now.minusYears(1);
		out.println(String.format("%d: %s", before.toEpochSecond(ZoneOffset.UTC), before.format(LocalDateTimes.FORMATTER_DATETIME_SECONDS)));
		assertThat(before, lessThan(now));

		LocalDateTime after = now.plusMinutes(1);
		out.println(String.format("%d: %s", after.toEpochSecond(ZoneOffset.UTC), after.format(LocalDateTimes.FORMATTER_DATETIME_SECONDS)));
		assertThat(after, greaterThan(now));
	}
}
