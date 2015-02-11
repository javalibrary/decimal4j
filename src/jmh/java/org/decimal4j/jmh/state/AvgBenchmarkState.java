package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class AvgBenchmarkState extends AbstractValueBenchmarkState {
	@Param({"DOWN", "HALF_UP"})
	public RoundingMode roundingMode;
	@Setup
	public void init() {
		initForBinaryOp(BenchmarkType.Avg, roundingMode, ValueType.Long, ValueType.Long);
	}
}