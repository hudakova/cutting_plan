package com.example.rezny_plan;


import java.util.Map;

class Cutting {

	final Map<Integer, Integer> countByLength;
	final int waste;

	public Cutting(Map<Integer, Integer> countByLength, int waste) {
		this.countByLength = countByLength;
		this.waste = waste;
	}
}
