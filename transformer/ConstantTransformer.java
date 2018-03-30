package com.eng.cber.na.transformer;

import org.apache.commons.collections15.Transformer;

/**
 * Transformer that returns the same value every time.
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version
 * of the Commons Collections class ConstantTransformer.
 * 
 */
public class ConstantTransformer<I,O> implements Transformer<I, O> {

	private O o;
	
	public ConstantTransformer(O o) {
		this.o = o;
	}
	
	public static <I,O> ConstantTransformer<I,O> getTransformer(O o) {
		return new ConstantTransformer<I,O>(o);
	}
	
	@Override
	public O transform(I i) {
		return o;
	}

}
