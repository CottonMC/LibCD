package io.github.cottonmc.libcd.tweaker.nbt;

import net.minecraft.nbt.*;

import java.util.Arrays;
import java.util.List;

public class WrappedListTag {
	private ListTag underlying;

	public WrappedListTag(ListTag underlying) {
		this.underlying = underlying;
	}

	public static WrappedListTag create() {
		return new WrappedListTag(new ListTag());
	}

	public static WrappedListTag create(List orig) {
		ListTag ret = new ListTag();
		for (Object o : orig) {
			ret.addTag(ret.size(), NbtUtils.getTagFor(o));
		}
		return new WrappedListTag(ret);
	}

	public static WrappedListTag create(Object[] orig) {
		ListTag ret = new ListTag();
		for (Object o : orig) {
			ret.addTag(ret.size(), NbtUtils.getTagFor(o));
		}
		return new WrappedListTag(ret);
	}

	public ListTag getUnderlying() {
		return underlying;
	}

	public String getListType() {
		return NbtUtils.getTypeName(underlying.getListType());
	}

	public int getSize() {
		return underlying.size();
	}

	public Object get(int index) {
		return NbtUtils.getObjectFor(underlying.get(index));
	}

	public boolean set(int index, Object value) {
		switch(underlying.getListType()) {
			case 0:
				return underlying.setTag(index, NbtUtils.getTagFor(value));
			case 1:
				if (value instanceof Byte) {
					return underlying.setTag(index, new ByteTag((Byte)value));
				} else return false;
			case 2:
				if (value instanceof Short) return underlying.setTag(index, new ShortTag((Short)value));
				 else return false;
			case 3:
				if (value instanceof Integer) return underlying.setTag(index, new IntTag((Integer)value));
				else return false;
			case 4:
				if (value instanceof Long) return underlying.setTag(index, new LongTag((Long)value));
				else return false;
			case 5:
				if (value instanceof Float) return underlying.setTag(index, new LongTag((Long)value));
				else return false;
			case 6:
				if (value instanceof Double) return underlying.setTag(index, new DoubleTag((Long)value));
				else return false;
			case 7:
				if (value instanceof Byte[]) return underlying.setTag(index, new ByteArrayTag(Arrays.asList((Byte[])value)));
				else return false;
			case 8:
				if (value instanceof String) return underlying.setTag(index, new StringTag((String)value));
				else return false;
			case 9:
				if (value instanceof WrappedListTag) return underlying.setTag(index, ((WrappedListTag)value).getUnderlying());
				else return false;
			case 10:
				if (value instanceof WrappedCompoundTag) return underlying.setTag(index, ((WrappedCompoundTag)value).getUnderlying());
				else return false;
			case 11:
				if (value instanceof Integer[]) return underlying.setTag(index, new IntArrayTag(Arrays.asList((Integer[])value)));
				else return false;
			case 12:
				if (value instanceof Long[]) return underlying.setTag(index, new LongArrayTag(Arrays.asList((Long[])value)));
				else return false;
		}
		return false;
	}

	public boolean add(Object value) {
		return add(getSize(), value);
	}

	public boolean add(int index, Object value) {
		switch(underlying.getListType()) {
			case 0:
				return underlying.addTag(index, NbtUtils.getTagFor(value));
			case 1:
				if (value instanceof Byte) {
					return underlying.addTag(index, new ByteTag((Byte)value));
				} else return false;
			case 2:
				if (value instanceof Short) return underlying.addTag(index, new ShortTag((Short)value));
				else return false;
			case 3:
				if (value instanceof Integer) return underlying.addTag(index, new IntTag((Integer)value));
				else return false;
			case 4:
				if (value instanceof Long) return underlying.addTag(index, new LongTag((Long)value));
				else return false;
			case 5:
				if (value instanceof Float) return underlying.addTag(index, new LongTag((Long)value));
				else return false;
			case 6:
				if (value instanceof Double) return underlying.addTag(index, new DoubleTag((Long)value));
				else return false;
			case 7:
				if (value instanceof Byte[]) return underlying.addTag(index, new ByteArrayTag(Arrays.asList((Byte[])value)));
				else return false;
			case 8:
				if (value instanceof String) return underlying.addTag(index, new StringTag((String)value));
				else return false;
			case 9:
				if (value instanceof WrappedListTag) return underlying.addTag(index, ((WrappedListTag)value).getUnderlying());
				else return false;
			case 10:
				if (value instanceof WrappedCompoundTag) return underlying.addTag(index, ((WrappedCompoundTag)value).getUnderlying());
				else return false;
			case 11:
				if (value instanceof Integer[]) return underlying.addTag(index, new IntArrayTag(Arrays.asList((Integer[])value)));
				else return false;
			case 12:
				if (value instanceof Long[]) return underlying.addTag(index, new LongArrayTag(Arrays.asList((Long[])value)));
				else return false;
		}
		return false;
	}

	public String remove(int index) {
		return underlying.method_10536(index).toString();
	}

	public boolean isEmpty() {
		return underlying.isEmpty();
	}

	public String toString() {
		return underlying.asString();
	}

	public int hashCode() {
		return underlying.hashCode();
	}
}
