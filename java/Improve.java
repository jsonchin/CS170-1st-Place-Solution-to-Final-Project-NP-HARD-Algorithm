import java.util.*;

public class Improve extends Solve {

    public Improve(float MAX_WEIGHT, float MAX_COST, ArrayList<Item> items, ArrayList<ArrayList<Integer>> constraints, ArrayList<String> chosenItemNames) {
        super(MAX_WEIGHT, MAX_COST, items, constraints, chosenItemNames);
    }

    public void improve() {
        int numToSwapIn = 1;
        while (true) {
            if (this.memoKnapApprox.size() > 10000) {
                this.memoKnapApprox = new HashSet<>();
            }

            System.out.println("Swapping in " + numToSwapIn + " items");
            Knapsack resultingKnap = this.swapOutClassesImprove(this.bestKnap, numToSwapIn);
            if (resultingKnap == null) {
                numToSwapIn += 1;
            } else {
                System.out.println("Improved: " + this.bestKnap.getScore() + " -> " + resultingKnap.getScore());
                this.bestKnap = resultingKnap;
                numToSwapIn = 1;
            }

            this.shotgunTries += 1;
        }
    }

    private Knapsack swapOutClassesImprove(Knapsack knap, int numToSwapIn) {
        Set<Integer> usedClasses = new HashSet<>(knap.getItems().keySet());
        Set<Integer> unusedClasses = new HashSet<>(this.itemsByClass.keySet());
        unusedClasses.removeAll(usedClasses);

        /**
         * Shuffle the unused classes
         */
        List<Integer> unusuedClassesList = new ArrayList<Integer>(unusedClasses);
        Collections.shuffle(unusuedClassesList);

        int i = 1;

        int n = unusuedClassesList.size();

        /**
         * For each unused class, try to swap it in
         */
        for (int cls : unusuedClassesList) {
            if (i % 100 == 0) {
                System.out.println(i);
            }
            if (this.memoKnapApprox.size() > 10000) {
                this.memoKnapApprox = new HashSet<>();
            }
            i+=1;

            /**
             * For each class you're trying to swap in, remove it's incompatibilities, then add it in
             */
            HashSet<Integer> toTestClasses = new HashSet<>(usedClasses);
            for (int j = 0; j < numToSwapIn - 1; j++) {
                int randClass = unusuedClassesList.get((int) (Math.random() * n));
                if (this.incompatibilities.containsKey(randClass)) {
                    for (int incompatibleCls : this.incompatibilities.get(randClass)) {
                        if (toTestClasses.contains(incompatibleCls)) {
                            toTestClasses.remove(incompatibleCls);
                        }
                    }
                }
                toTestClasses.add(randClass);
            }
            if (this.incompatibilities.containsKey(cls)) {
                for (int incompatibleCls : this.incompatibilities.get(cls)) {
                    if (toTestClasses.contains(incompatibleCls)) {
                        toTestClasses.remove(incompatibleCls);
                    }
                }
            }
            toTestClasses.add(cls);

            /**
             * Try to fill in the knapsack with as many classes as possible
             */
            List<Integer> otherClassesList = new ArrayList<Integer>(unusedClasses);
            Collections.shuffle(otherClassesList);

            for (int otherCls : otherClassesList) {
                if (isCompletelyCompatible(otherCls, toTestClasses)) {
                    toTestClasses.add(otherCls);
                }
            }

            /**
             * Once a set of compatible classes have been found, approximate the best knapsack
             * given this set of classes
             */

            Knapsack resultKnap = knapsackApprox(new ArrayList<Integer>(toTestClasses));

//                System.out.println(resultKnap);
            if (resultKnap.getScore() > knap.getScore()) {
                return resultKnap;
            }
        }


        return null;

    }



    protected Knapsack knapsackApprox(ArrayList<Integer> classes) {
        /**
         * ASSUMPTION: classes is a list of compatible classes
         *              I can choose any subset of items without worrying about class constraints
         */


        /**
         * Check if this set of classes has already been approximated before
         */
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

        /**
         * Choose some heuristic to greedily fill the knapsack with
         */
//        ArrayList<Item> itemsByCostWeight = currItems;
//        ArrayList<Item> itemsByCost = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByResaleCostWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByResaleWeight = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByCostPlusWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureCost = (ArrayList<Item>) currItems.clone();
//        ArrayList<Item> itemsByPureWeight = (ArrayList<Item>) currItems.clone();

//        Collections.sort(itemsByCostWeight, new WeightCostValueComparator());
//        Collections.sort(itemsByCost, new CostValueComparator());
//        Collections.sort(itemsByWeight, new WeightValueComparator());
//        Collections.sort(itemsByResaleCostWeight, new ResaleWeightValueComparator());
//        Collections.sort(itemsByResaleWeight, new ResaleWeightCostValueComparator());
//        Collections.sort(itemsByCostPlusWeight, new CostPlusWeightComparator());
        Collections.sort(itemsByPureCost, new CostComparator());
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
//                        itemsByCostWeight.get(i)
//                        itemsByCost.get(i)
//                        itemsByWeight.get(i)
                    itemsByPureCost.get(i)
//                    itemsByPureWeight.get(i)
//                    itemsByCostPlusWeight.get(i)


            ));

            /**
             * Add the item if it doesn't break any cost or weight constraints
             * No class compatibility check is needed because it is assumed that the classes given
             * are compatible
             */
            for (Item item : toAddItems) {
                if ((item.getCost() + knap.getCost() <= this.MAX_COST) && (item.getWeight() + knap.getWeight() <= this.MAX_WEIGHT)) {
                    knap.addItem(item);
                }
            }
        }

        return knap;
    }


    public void DPApprox() {
        ArrayList<Item> items = new ArrayList<>();
        for (int cls : this.bestKnap.getItems().keySet()) {
            for (Item item : this.bestKnap.getItems().get(cls)) {
                items.add(item);
            }
        }
        int maxDPCost = 5943547 + 1;
        Knapsack[] DP = new Knapsack[maxDPCost];

        DP[0] = new Knapsack();

        for (int i = 0; i < items.size(); i++) {
            if (i + 1 % 100 == 0) {
                System.out.println(i + 1);
            }
            Item item = items.get(i);
            int itemCost = (int) (item.getCost() * 100);
            float itemScore = item.getScore();
            for (int cost = 0; cost < maxDPCost; cost++) {
                int newCost = cost - itemCost;
                if (newCost >= 0) {
                    if (DP[newCost] != null) {
                        if (DP[cost] != null) {
                            if (DP[cost].getScore() > DP[newCost].getScore() + itemScore) {
                                DP[cost] = DP[newCost].copyKnap();
                                DP[cost].addItem(item);
                            }
                        } else {
                            DP[cost] = DP[newCost].copyKnap();
                            DP[cost].addItem(item);
                        }

                    }
                }

            }
        }

        for (int i = maxDPCost - 1; i >= 0; i--) {
            if (DP[i] != null) {
                this.bestKnap = DP[i];
                return;
            }
        }
    }

}
