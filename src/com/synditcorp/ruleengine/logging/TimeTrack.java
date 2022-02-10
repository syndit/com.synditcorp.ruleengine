/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.logging;

public class TimeTrack {

	private Long start = null;

	public TimeTrack() {
		start = System.currentTimeMillis();
	}
	
	private Long getStart() {
		return start;
	}

	public static Long getStartTime(TimeTrack start) {
		return start.getStart();
	}
	
	public static Long getElapsedTime(TimeTrack start, TimeTrack end) throws Exception {
		if(start == null || start.getStart() == null) throw new Exception("Start time not set.");
		if(end == null || end.getStart() == null) throw new Exception("End time not set.");
		return elapsed(start.getStart(), end.getStart());
	}

	public static Long getElapsedTime(TimeTrack start) throws Exception {
		if(start == null || start.getStart() == null) throw new Exception("Start time not set.");
		Long end = System.currentTimeMillis();
		return elapsed(start.getStart(), end);
	}

	private static Long elapsed(Long start, Long end) throws Exception {
		return (end - start);
	}

	
}
