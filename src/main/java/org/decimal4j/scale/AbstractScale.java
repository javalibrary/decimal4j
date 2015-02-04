package org.decimal4j.scale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.EnumMap;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Abstract base class for {@link ScaleMetrics} implementations.
 */
abstract public class AbstractScale implements ScaleMetrics {

	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	final static long LONG_MASK = 0xffffffffL;

	private final long maxIntegerValue = divideByScaleFactor(Long.MAX_VALUE);
	private final long minIntegerValue = divideByScaleFactor(Long.MIN_VALUE);
	private final BigInteger biScaleFactor = BigInteger.valueOf(getScaleFactor());
	private final BigDecimal bdScaleFactor = new BigDecimal(biScaleFactor);

	private final EnumMap<RoundingMode, DecimalArithmetic> roundingModeToArithmetic = initArithmetic();
	private final EnumMap<RoundingMode, DecimalArithmetic> roundingModeToCheckedArithmetic = initCheckedArithmetic();

	/**
	 * Initializes the arithmetic map. {@link Scale0f} overrides this method
	 * hence protected.
	 * 
	 * @return the mapping from rounding mode to (unchecked) arithmetic
	 */
	protected EnumMap<RoundingMode, DecimalArithmetic> initArithmetic() {
		final EnumMap<RoundingMode, DecimalArithmetic> map = new EnumMap<RoundingMode, DecimalArithmetic>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new UncheckedScaleNfTruncatingArithmetic(this));
			} else {
				map.put(roundingMode, new UncheckedScaleNfRoundingArithmetic(this, dr));
			}
		}
		return map;
	}

	/**
	 * Initializes the checked arithmetic map. {@link Scale0f} overrides this
	 * method hence protected.
	 * 
	 * @return the mapping from rounding mode to checked arithmetic
	 * @see OverflowMode#CHECKED
	 */
	protected EnumMap<RoundingMode, DecimalArithmetic> initCheckedArithmetic() {
		final EnumMap<RoundingMode, DecimalArithmetic> map = new EnumMap<RoundingMode, DecimalArithmetic>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new CheckedScaleNfTruncatingArithmetic(this));
			} else {
				map.put(roundingMode, new CheckedScaleNfRoundingArithmetic(this, dr));
			}
		}
		return map;
	}

	@Override
	public final BigInteger getScaleFactorAsBigInteger() {
		return biScaleFactor;
	}

	@Override
	public final BigDecimal getScaleFactorAsBigDecimal() {
		return bdScaleFactor;
	}

	@Override
	public final long getMaxIntegerValue() {
		return maxIntegerValue;
	}

	@Override
	public final long getMinIntegerValue() {
		return minIntegerValue;
	}

	@Override
	public final DecimalArithmetic getDefaultArithmetic() {
		return getArithmetic(TruncationPolicy.DEFAULT);
	}

	@Override
	public final DecimalArithmetic getTruncatingArithmetic(OverflowMode overflowMode) {
		return overflowMode == OverflowMode.UNCHECKED ? roundingModeToArithmetic.get(RoundingMode.DOWN) : roundingModeToCheckedArithmetic.get(RoundingMode.DOWN);
	}

	@Override
	public final DecimalArithmetic getArithmetic(RoundingMode roundingMode) {
		return roundingModeToArithmetic.get(roundingMode);
	}

	@Override
	public final DecimalArithmetic getArithmetic(TruncationPolicy truncationPolicy) {
		final OverflowMode overflow = truncationPolicy.getOverflowMode();
		final RoundingMode rounding = truncationPolicy.getRoundingMode();
		return overflow == OverflowMode.UNCHECKED ? roundingModeToArithmetic.get(rounding) : roundingModeToCheckedArithmetic.get(rounding);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
