package com.hrbb.test.xstreamTest;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("body")
public class Body {
	
	private Record record;

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}
	
	
}
