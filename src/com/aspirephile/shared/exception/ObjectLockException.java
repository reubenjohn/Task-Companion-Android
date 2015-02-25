package com.aspirephile.shared.exception;

import com.aspirephile.shared.debug.NullPointerAsserter;

public class ObjectLockException extends Exception {
	private static class defaults {

		public static final String argumentName = "<Unknown>";
		public static final String objectName = "<Unknown>";

	}

	NullPointerAsserter asserter = new NullPointerAsserter(
			ObjectLockException.class);

	/**
	 * Exception thrown when an attempt is made to set/modify an argument of a
	 * locked object
	 */
	private static final long serialVersionUID = 6859641333683205071L;

	private String argumentName;
	private Class<?> argumentClass;
	private String objectName;
	private Class<?> objectClass;

	public ObjectLockException() {
		// TODO Use setters for the ObjectLockException constructor here
		argumentName = ObjectLockException.defaults.argumentName;
		argumentClass = Object.class;
		objectName = ObjectLockException.defaults.objectName;
		objectClass = Object.class;

		StackTraceElement[] stackTrace = getStackTrace();
		int line = stackTrace.length;
		StackTraceElement mSTE = new StackTraceElement("cls", "method", "file",
				line + 1);
		StackTraceElement[] mSTEA = new StackTraceElement[line + 1];
		for (int i = 0; i < line; i++)
			mSTEA[i] = stackTrace[i];
		mSTEA[line] = mSTE;
		super.setStackTrace(mSTEA);

	}

	public void setArgumentName(String name) throws NullPointerException {
		if (asserter.assertPointer(name))
			this.argumentName = name;
		else
			throw new NullPointerException();
	}

	public void setArgumentClass(Class<?> argumentType)
			throws NullPointerException {
		if (asserter.assertPointer(argumentType))
			this.argumentClass = argumentType;
		else
			throw new NullPointerException();
	}

	public void setObjectName(String name) throws NullPointerException {
		if (asserter.assertPointer(name))
			this.argumentName = name;
		else
			throw new NullPointerException();
	}

	public void setObjectClass(Class<?> objectType) throws NullPointerException {
		if (asserter.assertPointer(objectType))
			this.objectClass = objectType;
		else
			throw new NullPointerException();
	}

	public String getArgumentName() {
		return argumentName;
	}

	public Class<?> getArgumentType() {
		return argumentClass;
	}

	public String getObjectName() {
		return objectName;
	}

	public Class<?> getObjectType() {
		return objectClass;
	}

}
