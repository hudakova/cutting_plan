package com.example.rezny_plan;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class MainClass {

	public static void main(String[] args) {
		List<Item> items = new ArrayList<>();
		int blocks[] = {5,4,3};//{700, 500, 250, 380};
		int quantities[] = {8,14,20};//{4, 3, 6, 5};
		for (int i = 0; i < blocks.length; i++)
			items.add(new Item(blocks[i], quantities[i]));

		CuttingStock cuttingStock = new CuttingStock(2000, items);
		int i = 0;
		for (Cutting c : cuttingStock.cuttingPlan) {
			System.out.println("Stock " + (++i) + ". Waste " + c.waste);
			for (Entry<Integer, Integer> e : c.countByLength.entrySet())
				System.out.println("" + e.getKey() + " * " + e.getValue());
			System.out.println();
		}
	}
}

