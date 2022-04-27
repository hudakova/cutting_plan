package com.example.rezny_plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CuttingStock {

    private final int max;
    private final List<Item> items;
    private int minLength;

    List<Cutting> cuttingPlan;

    CuttingStock(int max, List<Item> items) {
        for (Item item : items) {
            if (item.length > max)
                throw new IllegalArgumentException("Item " + item.length + " longer than max " + max);
        }
        this.max = max;
        this.items = items;
        Collections.sort(items);
        //Collections.reverse(items);
        minLength = items.get(0).length;
        cuttingPlan = new ArrayList<>();
    }

    public void calculatePlan() {
        int[] lenghts = new int[items.size()];
        for (int i = 0; i < items.size(); i++)
            lenghts[i] = items.get(i).length;

        for (int combinations = 1; combinations <= max / minLength; combinations++) {
            int inputItemIndices[] = new int[combinations];
            combinationRepetitionUtil(inputItemIndices, lenghts, 0, combinations, 0, lenghts.length);
        }
    }

    void combinationRepetitionUtil(int[] inputItemIndices, int inputSetItems[],
                                   int outputIndex, int outputSize, int start, int end) {
        if (outputIndex == outputSize) {
            int[] output = new int[outputSize];
            for (int i = 0; i < outputSize; i++) {
                output[i] = inputSetItems[inputItemIndices[i]];
            }

            int sum = 0;
            for (int n : output)
                sum += n;

            if (sum <= max && max - sum < minLength) {
                Map<Integer, Integer> counts = new HashMap<>();
                for (int n : output) {
                    Integer c = counts.get(n);
                    if (c == null) c = 0;
                    counts.put(n, c + 1);
                }
                cuttingPlan.add(new Cutting(counts, max - sum));
            }
            return;
        }

        for (int i = start; i < end; i++) {
            inputItemIndices[outputIndex] = i;
            combinationRepetitionUtil(inputItemIndices, inputSetItems, outputIndex + 1, outputSize, i, end);
        }
    }

    private void calculate() {
        int div;
        for (Item item : items) {
            div = max / item.length;
            if (item.quantity > div)
                item.limit = div;
            else
                item.limit = item.quantity;
        }

        boolean start = true, more = true;
        int best = 0, sum = 0;

        while (start) {
            for (int i = items.size() - 1; ; ) {
                Item item = items.get(i);
                if (item.used != item.limit) {
                    item.used++;
                    break;
                } else {
                    if (i == 0 && item.used != item.limit)
                        i = items.size() - 1;
                    else {
                        item.used = 0;
                        i--;
                    }
                }
            }

            sum = 0;
            for (Item item : items) {
                sum += item.length * item.used;
                if (sum > max) {
                    sum = 0;
                    break;
                }
            }

            if (sum > 0) {
                if (sum == max) {
                    combination(0);
                    updateLimitResetComb();
                    best = 0;
                    sum = 0;
                } else if (sum > best) {
                    best = sum;
                    for (Item item : items)
                        item.temp = item.used;
                    sum = 0;
                }
            }
            for (Item item : items) {
                if (item.used != item.limit) {
                    more = true;
                    break;
                }
                more = false;
            }
            if (!more) {
                combination(best);
                updateLimitResetComb();
                best = 0;
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(0).quantity == 0 && i != items.size() - 1)
                    continue;
                else if (i == items.size() - 1 && items.get(0).quantity == 0)
                    start = false;
                break;
            }
        }
    }

    private void combination(int used) {
        boolean flag = false;
        if (used == 0) {
            Map<Integer, Integer> tempMap = new HashMap<>();
            for (Item item : items) {
                if (item.used != 0) {
                    tempMap.put(item.length, item.used);
                    item.quantity = item.quantity - item.used;
                    if ((item.quantity - item.used) < 0)
                        flag = true;
                }
            }

            if (flag) {
                cuttingPlan.add(new Cutting(tempMap, 0));
                return;
            }
            combination(0);

        } else {
            Map<Integer, Integer> tempMap = new HashMap<>();
            for (Item item : items) {
                if (item.temp != 0)
                    tempMap.put(item.length, item.temp);
            }
            cuttingPlan.add(new Cutting(tempMap, max - used));
            for (Item item : items)
                item.quantity = item.quantity - item.temp;

            for (Item item : items) {
                if ((item.quantity - item.used) < 0)
                    return;
            }
            combination(used);
        }
    }

    private void updateLimitResetComb() {
        for (Item item : items) {
            if (item.quantity < item.limit)
                item.limit = item.quantity;
            item.used = 0;
        }
    }
}
