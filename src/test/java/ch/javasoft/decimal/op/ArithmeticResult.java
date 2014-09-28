package ch.javasoft.decimal.op;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Result of an arithmetic operation which can also sometimes lead to an
 * {@link ArithmeticException}. The assertion is based on a comparable value
 * of type {@code <T>}.
 *
 * @param <T> the type of the compared value
 */
class ArithmeticResult<T> {
	private final String resultString;
	private final T compareValue;
	private final ArithmeticException exception;

	private ArithmeticResult(String resultString, T compareValue, ArithmeticException exception) {
		this.resultString = resultString;
		this.compareValue = compareValue;
		this.exception = exception;
	}

	public static <T> ArithmeticResult<T> forResult(String resultString, T comparableValue) {
		return new ArithmeticResult<T>(resultString, comparableValue, null);
	}
	public static ArithmeticResult<Long> forResult(DecimalArithmetics arithmetics, BigDecimal result) {
		final BigDecimal rnd = result.setScale(arithmetics.getScale(), arithmetics.getRoundingMode());
		final long resultUnscaled = arithmetics.getOverflowMode().isChecked() ? rnd.unscaledValue().longValueExact() : rnd.unscaledValue().longValue();
		return forResult(result.toPlainString(), resultUnscaled);
	}
	public static ArithmeticResult<Long> forResult(Decimal<?> result) {
		return forResult(result.toString(), result.unscaledValue());
	}
	public static <T> ArithmeticResult<T> forException(ArithmeticException e) {
		return new ArithmeticResult<T>(null, null, e);
	}

	public void assertEquivalentTo(ArithmeticResult<T> expected, String messagePrefix) {
		if ((expected.exception == null) != (exception == null)) {
			if (expected.exception != null) {
		        throw new AssertionError(messagePrefix + " was " + resultString + " but should lead to an exception: " + expected.exception, expected.exception);
			} else {
		        throw new AssertionError(messagePrefix + " = " + expected.resultString + " but lead to an exception: " + exception, exception);
			}
		} else {
			assertEquals(messagePrefix + " = " + expected.resultString, expected.compareValue, compareValue);
		}
	}
	
	@Override
	public String toString() {
		if (exception == null) {
			return getClass().getSimpleName() + "[" + resultString + ":" + compareValue + "]";
		}
		return getClass().getSimpleName() + "[" + exception + "]";
	}
}