/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * This class is meant to be replaced by a logger that implements slf4j.  This logger, or its replacement, is injected at runtime.
 */
public class MinimalLogger implements Logger {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void debug(String msg) {
		System.out.println(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void debug(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void info(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void info(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void warn(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void warn(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void error(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void error(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

}
