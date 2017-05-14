import java.util.*;

public abstract class Solve {

    protected final float MAX_WEIGHT;
    protected final float MAX_COST;
    protected HashMap<Integer, ArrayList<Item>> itemsByClass;
    protected HashMap<String, Item> itemsByName;
    protected HashMap<Integer, HashSet<Integer>> incompatibilities;

    protected HashSet<ArrayList<Integer>> memoKnapApprox;
    protected final Knapsack NULL_KNAP = new Knapsack();

    protected Knapsack bestKnap = new Knapsack();
    protected int shotgunTries = 0;

    public Solve() {
        MAX_WEIGHT = 0;
        MAX_COST = 0;
    }



    public Solve(float MAX_WEIGHT, float MAX_COST, ArrayList<Item> items, ArrayList<ArrayList<Integer>> constraints, ArrayList<String> chosenItemNames) {
        this.MAX_WEIGHT = MAX_WEIGHT;
        this.MAX_COST = MAX_COST;

        this.memoKnapApprox = new HashSet<>();
        initializeItemsByClass(items);
        initializeItemsByName(items);
        initializeIncompatibilities(constraints);
        initializePreviousKnap(chosenItemNames);
    }

    private void initializePreviousKnap(ArrayList<String> chosenItemNames) {
        Knapsack knap = new Knapsack();
        for (String name : chosenItemNames) {
            knap.addItem(itemsByName.get(name));
        }
        this.bestKnap = knap;
        System.out.println(this.bestKnap);
    }

    private void initializeItemsByName(ArrayList<Item> items) {
        this.itemsByName = new HashMap<>();
        for (Item item : items) {
            this.itemsByName.put(item.getName(), item);
        }
    }

    private void initializeItemsByClass(ArrayList<Item> items) {
        this.itemsByClass = new HashMap<>();

        for (Item item : items) {
            int cls = item.getCls();

            if (itemsByClass.containsKey(cls)) {
                itemsByClass.get(cls).add(item);
            } else {
                ArrayList<Item> tempList = new ArrayList<>();
                tempList.add(item);
                itemsByClass.put(cls, tempList);
            }
        }
    }

    private void initializeIncompatibilities(ArrayList<ArrayList<Integer>> constraints) {
        this.incompatibilities = new HashMap<>();

        for (ArrayList<Integer> constraint : constraints) {
            for (int cls1 : constraint) {
                for (int cls2 : constraint) {
                    if (cls1 != cls2) {
                        if (this.incompatibilities.containsKey(cls1)) {
                            this.incompatibilities.get(cls1).add(cls2);
                        } else {
                            HashSet<Integer> tempSet = new HashSet<>();
                            tempSet.add(cls2);
                            this.incompatibilities.put(cls1, tempSet);
                        }
                    }

                }
            }
        }
    }

    public Knapsack getBestKnap() {
        return this.bestKnap;
    }

    public int getShotgunTries() {
        return shotgunTries;
    }

    protected class WeightCostValueComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getWeightCostValue();
            float v2 = i2.getWeightCostValue();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class CostValueComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getCostValue();
            float v2 = i2.getCostValue();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class WeightValueComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getWeightValue();
            float v2 = i2.getWeightValue();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class ResaleWeightCostValueComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getResaleWeightCostValue();
            float v2 = i2.getResaleWeightCostValue();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class ResaleWeightValueComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getResaleWeightValue();
            float v2 = i2.getResaleWeightValue();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class CostPlusWeightComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getCost() + i1.getWeight();
            float v2 = i2.getCost() + i2.getWeight();
            if (v1 == v2) {
                return 0;
            } else if (v1 < v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class CostComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getCost();
            float v2 = i2.getCost();
            if (v1 == v2) {
                return 0;
            } else if (v1 < v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class WeightComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getWeight();
            float v2 = i2.getWeight();
            if (v1 == v2) {
                return 0;
            } else if (v1 < v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class ScoreComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getScore();
            float v2 = i2.getScore();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class ResaleComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getCost() + i1.getScore();
            float v2 = i2.getCost() + i2.getScore();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class ResaleMinusWeightComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getCost() + i1.getScore() - i1.getWeight();
            float v2 = i2.getCost() + i2.getScore() - i2.getWeight();
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class WeightTimesCostComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getWeight() * i1.getCost();
            float v2 = i2.getWeight() * i2.getCost();
            if (v1 == v2) {
                return 0;
            } else if (v1 < v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class WeightTimesCostValueComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = i1.getScore() / (i1.getWeight() * i1.getCost());
            float v2 = i2.getScore() / (i2.getWeight() * i2.getCost());
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    protected class WeightTimesCostResaleComparator implements Comparator<Item> {

        @Override
        public int compare(Item i1, Item i2) {
            float v1 = (i1.getScore() + i1.getCost()) / (i1.getWeight() * i1.getCost());
            float v2 = (i2.getScore() + i2.getCost()) / (i2.getWeight() * i2.getCost());
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }



    protected Knapsack knapsackApprox(ArrayList<Integer> classes) {
        Collections.sort(classes);
        if (this.memoKnapApprox.contains(classes)) {
            return this.NULL_KNAP;
        } else {
            this.memoKnapApprox.add(classes);
        }

        ArrayList<Item> currItems = new ArrayList<>();

        for (int cls : classes) {
            for (Item item : this.itemsByClass.get(cls)) {
                currItems.add(item);
            }
        }
        ArrayList<Item> itemsByCostWeight = currItems;
        ArrayList<Item> itemsByCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByResaleCostWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByResaleWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByCostPlusWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByPureCost = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByPureWeight = (ArrayList<Item>) currItems.clone();

        Collections.sort(itemsByCostWeight, new WeightCostValueComparator());
        Collections.sort(itemsByCost, new CostValueComparator());
        Collections.sort(itemsByWeight, new WeightValueComparator());
//        Collections.sort(itemsByResaleCostWeight, new ResaleWeightValueComparator());
//        Collections.sort(itemsByResaleWeight, new ResaleWeightCostValueComparator());
//        Collections.sort(itemsByCostPlusWeight, new CostPlusWeightComparator());
//        Collections.sort(itemsByPureCost, new CostComparator());
//        Collections.sort(itemsByPureWeight, new WeightComparator());

        Knapsack knap = new Knapsack();
        int n = currItems.size();
        for (int i = n - 1; i >= 0; i--) {
//                Item item;
//                    item = itemsByCostWeight.get(i);
//                    item = itemsByCost.get(i);
//                    item = itemsByWeight.get(i);
//                    item = itemsByResaleCostWeight.get(i);
//                    item = itemsByResaleWeight.get(i);
//                    item = itemsByCostPlusWeight.get(i);
//                    item = itemsByPureCost.get(i);
//                    item = itemsByPureWeight.get(i);
            ArrayList<Item> toAddItems = new ArrayList<>(Arrays.asList(
                        itemsByCostWeight.get(i),
                        itemsByCost.get(i),
                        itemsByWeight.get(i)
//                    itemsByPureWeight.get(i)
//                    itemsByPureCost.get(i)
            ));

            for (Item item : toAddItems) {
                if ((item.getCost() + knap.getCost() <= this.MAX_COST) && (item.getWeight() + knap.getWeight() <= this.MAX_WEIGHT)) {
                    knap.addItem(item);
                }
            }
        }

        return knap;
    }

    protected boolean isCompletelyCompatible(int toTestCls, Set<Integer> classes) {
        if (!this.incompatibilities.containsKey(toTestCls)) {
            return true;
        }

        for (int cls : this.incompatibilities.get(toTestCls)) {
            if (classes.contains(cls)) {
                return false;
            }
        }
        return true;
    }
}
