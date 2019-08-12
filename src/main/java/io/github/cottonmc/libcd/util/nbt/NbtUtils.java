package io.github.cottonmc.libcd.util.nbt;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.nbt.*;

import java.util.Arrays;

public class NbtUtils {
	public static String getTypeName(int type) {
		switch (type) {
			case 0:
				return "end";
			case 1:
				return "byte";
			case 2:
				return "short";
			case 3:
				return "int";
			case 4:
				return "long";
			case 5:
				return "float";
			case 6:
				return "double";
			case 7:
				return "byte array";
			case 8:
				return "string";
			case 9:
				return "list";
			case 10:
				return "compound";
			case 11:
				return "int array";
			case 12:
				return "long array";
			case 99:
				return "any number";
			default:
				return "unknown";
		}
	}

	public static int getTypeValue(String type) {
		String lower = type.toLowerCase();
		switch(lower) {
			case "end":
				return 0;
			case "byte":
				return 1;
			case "short":
				return 2;
			case "int":
				return 3;
			case "long":
				return 4;
			case "float":
				return 5;
			case "double":
				return 6;
			case "byte array":
				return 7;
			case "string":
				return 8;
			case "list":
				return 9;
			case "compound":
				return 10;
			case "int array":
				return 11;
			case "long array":
				return 12;
			case "any number":
				return 99;
			default:
				return -1;
		}
	}

	public static Tag getTagFor(Object value) {
		if (value instanceof Byte) {
			return new ByteTag((byte)value);
		} else if (value instanceof Boolean) {
			return new ByteTag((byte)((boolean)value ? 1 : 0));
		} else if (value instanceof Short) {
			return new ShortTag((short)value);
		} else if (value instanceof Integer) {
			return new IntTag((int)value);
		} else if (value instanceof Long) {
			return new LongTag((long)value);
		} else if (value instanceof Float) {
			return new FloatTag((float)value);
		} else if (value instanceof Double) {
			return new DoubleTag((double) value);
		} else if (value instanceof Byte[]) {
			return new ByteArrayTag(Arrays.asList((Byte[])value));
		} else if (value instanceof String) {
			return new StringTag((String)value);
		} else if (value instanceof WrappedListTag) {
			return ((WrappedListTag)value).getUnderlying();
		} else if (value instanceof WrappedCompoundTag) {
			return ((WrappedCompoundTag) value).getUnderlying();
		} else if (value instanceof Integer[]) {
			return new IntArrayTag(Arrays.asList((Integer[])value));
		} else if (value instanceof Long[]) {
			return new LongArrayTag(Arrays.asList((Long[])value));
		} else return new StringTag(value.toString());
	}

	public static Object getObjectFor(Tag tag) {
		if (tag instanceof ByteTag) {
			return ((ByteTag)tag).getByte();
		} else if (tag instanceof ShortTag) {
			return ((ShortTag)tag).getShort();
		} else if (tag instanceof IntTag) {
			return ((IntTag)tag).getInt();
		} else if (tag instanceof LongTag) {
			return ((LongTag)tag).getLong();
		} else if (tag instanceof FloatTag) {
			return ((FloatTag)tag).getFloat();
		} else if (tag instanceof DoubleTag) {
			return ((DoubleTag)tag).getDouble();
		} else if (tag instanceof ByteArrayTag) {
			return ((ByteArrayTag)tag).getByteArray();
		} else if (tag instanceof StringTag) {
			return tag.asString();
		} else if (tag instanceof ListTag) {
			return new WrappedListTag((ListTag)tag);
		} else if (tag instanceof CompoundTag) {
			return new WrappedCompoundTag((CompoundTag)tag);
		} else if (tag instanceof IntArrayTag) {
			IntTag[] tags = ((IntArrayTag)tag).toArray(new IntTag[0]);
			IntList ret = new IntArrayList();
			for (IntTag intTag : tags) {
				ret.add(intTag.getInt());
			}
			return ret.toArray(new int[0]);
		} else if (tag instanceof LongArrayTag) {
			LongTag[] tags = ((LongArrayTag)tag).toArray(new LongTag[0]);
			LongList ret = new LongArrayList();
			for (LongTag longTag : tags) {
				ret.add(longTag.getLong());
			}
			return ret.toArray(new long[0]);
		} else return tag.asString();
	}
}