package ru.hh.http.emulator.client.entity;

public enum RestrictionType {

	EQ(1),
	
	OR(101),
	
	AND(102);
	
	private final int value;

	private RestrictionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isAgregate(){
		return value > 100;
	}
}
