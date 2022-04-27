package com.example.rezny_plan;


class Item implements Comparable<Item> {

	final int length;
	int quantity;
	int limit;
	int used;
	int temp;

	Item(int length, int quantity) {
		this.length = length;
		this.quantity = quantity;
	}

	@Override
	public int compareTo(Item other) {
		return length - other.length;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return length == ((Item) o).length;
	}

	@Override
	public int hashCode() {
		return length;
	}
}
