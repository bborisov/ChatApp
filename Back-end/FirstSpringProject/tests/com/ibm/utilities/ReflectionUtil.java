package com.ibm.utilities;

import java.lang.reflect.Field;

public class ReflectionUtil {
	
	public static void setField(Object fromClass, String fieldToGet, Object fieldToSet) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = fromClass.getClass().getDeclaredField(fieldToGet);
		field.setAccessible(true);
		field.set(fromClass, fieldToSet);
	}

}
