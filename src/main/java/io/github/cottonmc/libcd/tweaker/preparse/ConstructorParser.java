package io.github.cottonmc.libcd.tweaker.preparse;


import io.github.cottonmc.libcd.tweaker.TweakerSyntaxException;
import javafx.util.Pair;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

public class ConstructorParser {
	static final Map<Identifier, Function<Object[], Object>> CONSTRUCTORS = new HashMap<>();
	public static Pair<String, Map<String, Object>> parseConstructors(String input) throws TweakerSyntaxException {
		String swapped = input;
		Map<String, Object> fillins = new HashMap<>();
		Map<String, String> nameMap = new HashMap<>();

		constructor: while (input.length() > 0) {
			char c = input.charAt(0);
			input = input.substring(1);
			if (c == '<' && input.charAt(0) == '|') {
				input = input.substring(1);
				String fillin = getFillin(input);
				input = input.substring(fillin.length() - 3);
				Pair<String, Object> parsed = parseFillin(fillin);
				fillins.put(parsed.getKey(), parsed.getValue());
				nameMap.put(fillin, parsed.getKey());
			}
		}

		for (String key : nameMap.keySet()) {
			swapped = swapped.replace(key, nameMap.get(key));
		}

		return new Pair<>(swapped, fillins);
	}

	static String getFillin(String input) throws TweakerSyntaxException {
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
				case '\n': case '\r':
					throw new TweakerSyntaxException("Illegal character in Tweaker constructor " + input + ": " + ch);

			}
		}
		throw new TweakerSyntaxException("Tweaker constructor never closed!");
	}

	static Pair<String, Object> parseFillin(String input) throws TweakerSyntaxException {
		String orig = input;
		Identifier fillinName = null;
		List<Object> arguments = new ArrayList<>();

		fillin: while (input.length() > 0) {
			char c = input.charAt(0);
			input = input.substring(1);
			switch(c) {
				//TODO: make this actually properly parsed instead of this bad ugly hack
				case ' ': case ':': case ',': break;
				case '"': case '\'':
					StringBuilder builder = new StringBuilder();
					for (char ch : input.toCharArray()) {
						if (ch == c && !builder.toString().endsWith("\\")) break;
						builder.append(ch);
					}
					input = input.substring(builder.length() + 2);
					if (fillinName == null) fillinName = new Identifier(builder.toString());
					else arguments.add(builder.toString());
					continue fillin;
				default:
					if (Character.isDigit(c)) {
						StringBuilder numBuilder = new StringBuilder(String.valueOf(c));
						for(char ch : input.toCharArray()) {
							if(ch == '.' || Character.isDigit(ch)) numBuilder.append(ch);
							else break;
						}
						int dots = StringUtils.countMatches(numBuilder, '.');
						if(dots > 1) throw new TweakerSyntaxException("Illegal character " + c + " in tweaker constructor number!");
						String number = numBuilder.toString();
						arguments.add(dots == 0 ? Integer.parseInt(number) : Float.parseFloat(number));
						input = input.substring(numBuilder.length()-1);
						continue fillin;
					}
					throw new TweakerSyntaxException("Illegal character " + c + " in tweaker contructor!");
			}
		}
		if (fillinName == null) throw new TweakerSyntaxException("Fillin name never found in tweaker constructor " + orig + "!");
		Object res = CONSTRUCTORS.get(fillinName).apply(arguments.toArray());
		int hash = res.hashCode();
		String keyBuilder = fillinName.getNamespace() + "_" +
				fillinName.getPath() + "_" +
				Integer.toHexString(hash).substring(0, 5);
		return new Pair<>(keyBuilder, res);
	}
}
