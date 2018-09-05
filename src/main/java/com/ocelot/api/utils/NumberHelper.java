package com.ocelot.api.utils;

import java.util.Map;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.google.common.collect.Maps;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Contains methods to help with numbers.
 * 
 * @author Ocelot5836
 */
public class NumberHelper {

	private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

	/**
	 * Parses an equation from a string.
	 * 
	 * @param equation
	 *            The equation to parse
	 * @param variables
	 *            All the variables used
	 * @return The number parsed
	 */
	@Nullable
	public static double parseEquation(String equation, Variable... variables) {
		try {
			Map<String, Object> vars = Maps.newHashMap();
			for (Variable var : variables) {
				vars.put(var.variable, var.value);
			}
			Object value = engine.eval(equation, new SimpleBindings(vars));
			return value == null ? 0 : parseDouble(String.valueOf(value));
		} catch (ScriptException e) {
			return 0;
		}
	}

	/**
	 * Parses a byte from a string and catches all exceptions.
	 * 
	 * @param value
	 *            The value to parse
	 * @return The number parsed
	 */
	public static byte parseByte(String value) {
		try {
			return Byte.parseByte(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Parses a short from a string and catches all exceptions.
	 * 
	 * @param value
	 *            The value to parse
	 * @return The number parsed
	 */
	public static short parseShort(String value) {
		try {
			return Short.parseShort(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Parses an integer from a string and catches all exceptions.
	 * 
	 * @param value
	 *            The value to parse
	 * @return The number parsed
	 */
	public static int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Parses a float from a string and catches all exceptions.
	 * 
	 * @param value
	 *            The value to parse
	 * @return The number parsed
	 */
	public static float parseFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Parses a double from a string and catches all exceptions.
	 * 
	 * @param value
	 *            The value to parse
	 * @return The number parsed
	 */
	public static double parseDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Parses a boolean from a string and catches all exceptions.
	 * 
	 * @param value
	 *            The value to parse
	 * @return The boolean parsed
	 */
	public static boolean parseBoolean(String value) {
		try {
			try {
				return Integer.parseInt(value) == 1;
			} catch (NumberFormatException e) {
			}
			return Boolean.parseBoolean(value);
		} catch (Exception e1) {
			return false;
		}
	}

	public static class Variable {

		private String variable;
		private Object value;

		private Variable(String variable, Object value) {
			this.variable = variable;
			this.value = value;
		}
	}
}