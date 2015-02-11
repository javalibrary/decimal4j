package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SquareBenchmarkState extends AbstractValueBenchmarkState {
	@Param({"Int", "Long"})
	public ValueType valueType;
	@Param({"DOWN", "HALF_UP"})
	public RoundingMode roundingMode;
	@Setup
	public void init() {
		initForUnaryOp(BenchmarkType.Square, roundingMode, valueType);
	}
}