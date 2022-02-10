/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.logging;

import org.slf4j.Logger;

public class RuleLogger {

	public static Logger logger;

	public static void log(String format, Object... args) {

		if(logger.isDebugEnabled()) {
			  debug(format, args);
		}
		else if(logger.isInfoEnabled()) {
			  info(format, args);
		}
		else if(logger.isWarnEnabled()) {
			  warn(format, args);
		} 
		else if(logger.isErrorEnabled()) {
			  error(format, args);
		} 
		else if(logger.isTraceEnabled()) {
			  trace(format, args);
		} 
		else {
			System.out.println("No logger enabled");
		}
		
	}
	
	public static void debug(String format, Object... args) {
		if(logger == null) {
			System.out.println("No Logger injected.");
			return;
		}
		logger.debug(format, args);
	}

	public static void info(String format, Object... args) {
		if(logger == null) {
			System.out.println("No Logger injected.");
			return;
		}
		logger.info(format, args);
	}

	public static void warn(String format, Object... args) {
		if(logger == null) {
			System.out.println("No Logger injected.");
			return;
		}
		logger.warn(format, args);
	}

	public static void error(String format, Object... args) {
		if(logger == null) {
			System.out.println("No Logger injected.");
			return;
		}
		logger.error(format, args);
	}

	public static void trace(String format, Object... args) {
		if(logger == null) {
			System.out.println("No Logger injected.");
			return;
		}
		logger.trace(format, args);
	}

}
