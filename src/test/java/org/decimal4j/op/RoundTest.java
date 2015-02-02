package org.decimal4j.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.Decimal;
import org.decimal4j.arithmetic.DecimalArithmetics;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#pow(int)}
 */
@RunWith(Parameterized.class)
public class RoundTest extends Abstract1DecimalArgToDecimalResultTest {
	
	private final int precision;
	
	public RoundTest(ScaleMetrics scaleMetrics, int precision, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.precision = precision;
	}

	@Parameters(name = "{index}: {0}, precision={1}, {2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			final int scale = s.getScale();
			for (int precision = (scale - 18) - 1; precision <= scale + 1; precision++) {
				for (final TruncationPolicy tp : TestSettings.POLICIES) {
					final DecimalArithmetics arith = s.getArithmetics(tp);
					data.add(new Object[] {s, precision, tp, arith});
				}
			}
		}
		return data;
	}
	
	@Override
	protected int getRandomTestCount() {
		return 1000;
	}
	
	@Override
	protected String operation() {
		return "round";
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		if (getScale() - precision <= 18) {
			return operand.setScale(precision, getRoundingMode()).setScale(getScale(), getRoundingMode());
		}
		throw new IllegalArgumentException("scale - precision must be <= 18 but was " + (getScale() - precision) + " for scale=" + getScale() + " and precision=" + precision);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return operand.round(precision);
		} else {
			if (isUnchecked() && RND.nextBoolean()) {
				return operand.round(precision, getRoundingMode());
			} else {
				return operand.round(precision, getTruncationPolicy());
			}
		}
	}
	
}