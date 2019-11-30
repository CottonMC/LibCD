package io.github.cottonmc.libcd.tweaker;

import io.github.cottonmc.libcd.api.CDSyntaxError;

@Deprecated
/**
 * Deprecated, use {@link CDSyntaxError} instead.
 */
public class TweakerSyntaxException extends CDSyntaxError {
	public TweakerSyntaxException(String message) {
		super(message);
	}
}
