package io.github.cottonmc.libcd.tweaker.preparse;


import io.github.cottonmc.libcd.tweaker.TweakerSyntaxException;
import javafx.util.Pair;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

public class LiteralParser {
	private static final Map<Identifier, Function<Object[], Object>> FACTORIES = new HashMap<>();

	public static void registerFactory(Identifier id, Function<Object[], Object> factory) {
		FACTORIES.put(id, factory);
	}

	public static Pair<String, Map<String, Object>> parseLiterals(String input) throws TweakerSyntaxException {
		String swapped = input;
		Map<String, Object> literals = new HashMap<>();
		Map<String, String> nameMap = new HashMap<>();

		while (input.length() > 0) {
			char c = input.charAt(0);
			input = input.substring(1);
			if (c == '<' && input.charAt(0) == '|') {
				input = input.substring(1);
				String fillin = getLiteral(input);
				input = input.substring(fillin.length() - 3);
				Pair<String, Object> parsed = parseLiteral(fillin);
				literals.put(parsed.getKey(), parsed.getValue());
				nameMap.put(fillin, parsed.getKey());
			}
		}

		for (String key : nameMap.keySet()) {
			swapped = swapped.replace(key, nameMap.get(key));
		}

		return new Pair<>(swapped, literals);
	}

	static String getLiteral(String input) throws TweakerSyntaxException {
		StringBuilder fillin = new StringBuilder("<|");
		boolean pipe = false;
		for (char ch: input.toCharArray()) {
			fillin.append(ch);
			switch(ch) {
				case '|':
					pipe = true;
					break;
				case '>':
					if (pipe) return fillin.toString();
					break;
				case '\n': case '\r':
					throw new TweakerSyntaxException("Illegal character in Tweaker literal " + input + ": no newlines allowed!");
				default:
					pipe = false;
			}
		}
		throw new TweakerSyntaxException("Tweaker literal never closed!");
	}

	static Pair<String, Object> parseLiteral(String input) throws TweakerSyntaxException {
		String orig = input;
		Identifier literalName = null;
		List<Object> arguments = new ArrayList<>();
		input = input.substring(2, input.length() - 2);
		boolean hasName = false;
		int expectedArgSize = 1;

		literal: while (input.length() > 0) {
			char c = input.charAt(0);
			input = input.substring(1);
			switch(c) {
				case ' ': break;
				case ':':
					if (hasName) throw new TweakerSyntaxException("Illegal character in tweaker literal: only one literal type allowed!");
					hasName = true;
					continue literal;
				case ',':
					expectedArgSize++;
					continue  literal;
				case '"': case '\'':
					StringBuilder builder = new StringBuilder();
					for (char ch : input.toCharArray()) {
						if (ch == c && !builder.toString().endsWith("\\")) break;
						builder.append(ch);
					}
					input = input.substring(builder.length() + 1);
					if (literalName == null) literalName = new Identifier(builder.toString());
					else arguments.add(builder.toString());
					continue literal;
				default:
					if (Character.isDigit(c)) {
						StringBuilder numBuilder = new StringBuilder(String.valueOf(c));
						for(char ch : input.toCharArray()) {
							if(ch == '.' || Character.isDigit(ch)) numBuilder.append(ch);
							else break;
						}
						int dots = StringUtils.countMatches(numBuilder, '.');
						if(dots > 1) throw new TweakerSyntaxException("Illegal character " + c + " in tweaker literal number param!");
						String number = numBuilder.toString();
						arguments.add(dots == 0 ? Integer.parseInt(number) : Float.parseFloat(number));
						input = input.substring(numBuilder.length()-1);
						continue literal;
					}
					throw new TweakerSyntaxException("Illegal character " + c + " in tweaker literal!");
			}
		}
		if (arguments.size() != expectedArgSize) throw new TweakerSyntaxException("Error in tweaker literal " + orig +
				": expected " + expectedArgSize + " argument(s) from commas, but found " + arguments.size() + " argument(s)!");
		if (literalName == null) throw new TweakerSyntaxException("Literal factory name never found in tweaker literal " + orig + "!");
		Object res = FACTORIES.get(literalName).apply(arguments.toArray());
		int hash = res.hashCode();
		String keyBuilder = literalName.getNamespace() + "_" +
				literalName.getPath() + "_" +
				Integer.toHexString(hash).substring(0, 5);
		return new Pair<>(keyBuilder, res);
	}
}
