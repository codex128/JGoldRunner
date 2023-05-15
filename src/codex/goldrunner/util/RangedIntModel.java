/*
 * The MIT License
 *
 * Copyright 2023 gary.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package codex.goldrunner.util;

import codex.jmeutil.math.IDomain;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.core.VersionedReference;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 *
 * @author gary
 */
public class RangedIntModel implements RangedValueModel {
	
	private long version;
	private int value;
	private IDomain domain;
	
	public RangedIntModel() {
		this(0, 100, 0);
	}
	public RangedIntModel(int min, int max, int value) {
		domain = new IDomain(min, max);
		this.value = domain.applyConstrain(value);
	}
	
	@Override
	public void setValue(double val) {
		value = domain.applyConstrain((int)val);
		version++;
	}
	@Override
	public double getValue() {
		return value;
	}
	@Override
	public void setPercent(double val) {
		double range = domain.getRange();
        double projected = domain.getMin()+range*val;
        setValue(projected);
	}
	@Override
	public double getPercent() {
		double range = domain.getMax()-domain.getMin();
        if(range == 0) return 0;
        double part = getValue()-domain.getMin();
        return part/range;
	}
	@Override
	public void setMaximum(double max) {
		domain.setMax((int)max);
		value = domain.applyConstrain(value);
		version++;
	}
	@Override
	public double getMaximum() {
		return domain.getMax();
	}
	@Override
	public void setMinimum(double min) {
		domain.setMin((int)min);
		value = domain.applyConstrain(value);
		version++;
	}
	@Override
	public double getMinimum() {
		return domain.getMin();
	}
	@Override
	public long getVersion() {
		return version;
	}
	@Override
	public Double getObject() {
		return (double)value;
	}
	@Override
	public VersionedReference<Double> createReference() {
		return new VersionedReference<Double>(this);
	}	
	
}
