import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class GreedySearch extends Solve {

    public GreedySearch(float MAX_WEIGHT, float MAX_COST, ArrayList<Item> items, ArrayList<ArrayList<Integer>> constraints, ArrayList<String> chosenItemNames) {
        super(MAX_WEIGHT, MAX_COST, items, constraints, chosenItemNames);
    }

    public void greedySearch(ArrayList<Integer> heuristics, float probTakeClass) {
        /**
         * Greedily fills the knapsack using the heuristics specified by heuristics
         * probTakeClass is the probability it takes a new class
         * that's not already in the knapsack
         */
        ArrayList<Item> currItems = new ArrayList<>();

        for (int cls : this.itemsByClass.keySet()) {
            for (Item item : this.itemsByClass.get(cls)) {
                currItems.add(item);
            }
        }
        ArrayList<Item> itemsByCostWeight = currItems;
        ArrayList<Item> itemsByCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleCostWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByCostPlusWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureScore = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureCostTimesWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByValueCostTimesWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleCostTimesWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleMinusWeight = (ArrayList<Item>) currItems.clone();

        Collections.sort(itemsByCostWeight, new WeightCostValueComparator());
        Collections.sort(itemsByCost, new CostValueComparator());
        Collections.sort(itemsByWeight, new WeightValueComparator());
        Collections.sort(itemsByResaleCostWeight, new ResaleWeightValueComparator());
        Collections.sort(itemsByResaleWeight, new ResaleWeightCostValueComparator());
        Collections.sort(itemsByCostPlusWeight, new CostPlusWeightComparator());
        Collections.sort(itemsByPureScore, new ScoreComparator());
        Collections.sort(itemsByPureCost, new CostComparator());
        Collections.sort(itemsByPureWeight, new WeightComparator());
        Collections.sort(itemsByPureCostTimesWeight, new WeightTimesCostComparator());
        Collections.sort(itemsByValueCostTimesWeight, new WeightTimesCostValueComparator());
        Collections.sort(itemsByResaleCostTimesWeight, new WeightTimesCostResaleComparator());
        Collections.sort(itemsByResaleMinusWeight, new ResaleMinusWeightComparator());


        Knapsack bestKnapSoFar = new Knapsack();
        for (int cls : this.itemsByClass.keySet()) {
            HashSet<Integer> invalidClasses;
            if (this.incompatibilities.containsKey(cls)) {
                invalidClasses = new HashSet<>(this.incompatibilities.get(cls));
            } else {
                invalidClasses = new HashSet<>();
            }
            Knapsack knap = new Knapsack();
            int n = currItems.size();
            for (int i = n - 1; i >= 0; i--) {
                for (int heuristic : heuristics) {
                    Item item;
                    switch (heuristic) {
                        case (0):
                            item = itemsByCostWeight.get(i);
                            break;
                        case (1):
                            item = itemsByCost.get(i);
                            break;
                        case (2):
                            item = itemsByWeight.get(i);
                            break;
                        case (3):
                            item = itemsByResaleCostWeight.get(i);
                            break;
                        case (4):
                            item = itemsByResaleWeight.get(i);
                            break;
                        case (5):
                            item = itemsByCostPlusWeight.get(i);
                            break;
                        case (6):
                            item = itemsByPureScore.get(i);
                            break;
                        case (7):
                            item = itemsByPureCost.get(i);
                            break;
                        case (8):
                            item = itemsByPureWeight.get(i);
                            break;
                        case (9):
                            item = itemsByPureCostTimesWeight.get(i);
                            break;
                        case (10):
                            item = itemsByValueCostTimesWeight.get(i);
                            break;
                        case (11):
                            item = itemsByResaleMinusWeight.get(i);
                            break;
                        default:
                            item = itemsByCostWeight.get(i);
                            break;
                    }

                    int itemCls = item.getCls();
                    if (!invalidClasses.contains(itemCls)) {
                        if (!knap.getItems().containsKey(itemCls)) {
                            if (Math.random() > probTakeClass) {
                                continue;
                            }

                            if (this.incompatibilities.containsKey(itemCls)) {
                                for (int invalidCls : this.incompatibilities.get(itemCls)) {
                                    invalidClasses.add(invalidCls);
                                }
                            }
                        }

                        if (((item.getCost() + knap.getCost()) <= this.MAX_COST) && ((item.getWeight() + knap.getWeight()) <= this.MAX_WEIGHT)) {
                            knap.addItem(item);
                        }
                    }
                }
            }
            if (knap.getScore() > this.bestKnap.getScore()) {
                System.out.println("Improved: " + this.bestKnap.getScore() + " -> " + knap.getScore());
                this.bestKnap = knap;
            }
            if (knap.getScore() > bestKnapSoFar.getScore()) {
                System.out.println("Improved: " + bestKnapSoFar.getScore() + " -> " + knap.getScore());
                bestKnapSoFar = knap;
            }
        }

    }


    public void bestHeuristic() {
        /**
         * Prints the score for each heuristic
         */
        ArrayList<Item> currItems = new ArrayList<>();

        for (int cls : this.itemsByClass.keySet()) {
            for (Item item : this.itemsByClass.get(cls)) {
                currItems.add(item);
            }
        }
        ArrayList<Item> itemsByCostWeight = currItems;
        ArrayList<Item> itemsByCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleCostWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByCostPlusWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureWeight = (ArrayList<Item>) currItems.clone();

        Collections.sort(itemsByCostWeight, new WeightCostValueComparator());
        Collections.sort(itemsByCost, new CostValueComparator());
        Collections.sort(itemsByWeight, new WeightValueComparator());
        Collections.sort(itemsByResaleCostWeight, new ResaleWeightValueComparator());
        Collections.sort(itemsByResaleWeight, new ResaleWeightCostValueComparator());
        Collections.sort(itemsByCostPlusWeight, new CostPlusWeightComparator());
        Collections.sort(itemsByPureCost, new CostComparator());
        Collections.sort(itemsByPureWeight, new WeightComparator());


        String[] heuristicNames = new String[]{
                "CostWeightValue",
                "CostValue",
                "WeightValue",
                "ResaleCostWeightValue",
                "ResaleWeightValue",
                "PureCostWeight",
                "PureCost",
                "PureWeight"
        };

        ArrayList<Knapsack> knaps = new ArrayList<>();

        for (int heuristic = 0; heuristic < 8; heuristic++) {
            Knapsack bestKnapSoFar = new Knapsack();
            for (int cls : this.itemsByClass.keySet()) {
                HashSet<Integer> invalidClasses;
                if (this.incompatibilities.containsKey(cls)) {
                    invalidClasses = new HashSet<>(this.incompatibilities.get(cls));
                } else {
                    invalidClasses = new HashSet<>();
                }
                Knapsack knap = new Knapsack();
                int n = currItems.size();
                for (int i = n - 1; i >= 0; i--) {
                    Item item;
                    switch (heuristic) {
                        case (0):
                            item = itemsByCostWeight.get(i);
                            break;
                        case (1):
                            item = itemsByCost.get(i);
                            break;
                        case (2):
                            item = itemsByWeight.get(i);
                            break;
                        case (3):
                            item = itemsByResaleCostWeight.get(i);
                            break;
                        case (4):
                            item = itemsByResaleWeight.get(i);
                            break;
                        case (5):
                            item = itemsByCostPlusWeight.get(i);
                            break;
                        case (6):
                            item = itemsByPureCost.get(i);
                            break;
                        case (7):
                            item = itemsByPureWeight.get(i);
                            break;
                        default:
                            item = itemsByCostWeight.get(i);
                            break;
                    }

                    int itemCls = item.getCls();
                    if (!invalidClasses.contains(itemCls)) {
                        if (!knap.getItems().containsKey(itemCls)) {
                            if (this.incompatibilities.containsKey(itemCls)) {
                                for (int invalidCls : this.incompatibilities.get(itemCls)) {
                                    invalidClasses.add(invalidCls);
                                }
                            }
                        }

                        if ((item.getCost() + knap.getCost() <= this.MAX_COST) && (item.getWeight() + knap.getWeight() <= this.MAX_WEIGHT)) {
                            knap.addItem(item);
                        }
                    }
                }
                if (knap.getScore() >= this.bestKnap.getScore()) {
                    this.bestKnap = knap;
                }
                if (knap.getScore() >= bestKnapSoFar.getScore()) {
                    bestKnapSoFar = knap;
                }
            }
            knaps.add(bestKnapSoFar);
            System.out.println(heuristicNames[heuristic] + ": " + knaps.get(heuristic).getScore());
        }


//        for (int i = 0; i < knaps.size(); i++) {
//            System.out.println(heuristicNames[i] + ": " + knaps.get(i).getScore());
//        }
    }



    public void bestHeuristicSpread() {
        /**
         * Fills a knapsack using greedy heuristics
         * Tries each heuristic for each class before moving onto the next class
         */
        ArrayList<Item> currItems = new ArrayList<>();

        for (int cls : this.itemsByClass.keySet()) {
            for (Item item : this.itemsByClass.get(cls)) {
                currItems.add(item);
            }
        }
        ArrayList<Item> itemsByCostWeight = currItems;
        ArrayList<Item> itemsByCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleCostWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByResaleWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByCostPlusWeight = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureCost = (ArrayList<Item>) currItems.clone();
        ArrayList<Item> itemsByPureWeight = (ArrayList<Item>) currItems.clone();

        Collections.sort(itemsByCostWeight, new WeightCostValueComparator());
        Collections.sort(itemsByCost, new CostValueComparator());
        Collections.sort(itemsByWeight, new WeightValueComparator());
        Collections.sort(itemsByResaleCostWeight, new ResaleWeightValueComparator());
        Collections.sort(itemsByResaleWeight, new ResaleWeightCostValueComparator());
        Collections.sort(itemsByCostPlusWeight, new CostPlusWeightComparator());
        Collections.sort(itemsByPureCost, new CostComparator());
        Collections.sort(itemsByPureWeight, new WeightComparator());


        String[] heuristicNames = new String[]{
                "CostWeightValue",
                "CostValue",
                "WeightValue",
                "ResaleCostWeightValue",
                "ResaleWeightValue",
                "PureCostWeight",
                "PureCost",
                "PureWeight"
        };

        ArrayList<Knapsack> knaps = new ArrayList<>();
        for (int heuristic = 0; heuristic < 8; heuristic++) {
            knaps.add(new Knapsack());
        }


        for (int cls : this.itemsByClass.keySet()) {
            for (int heuristic = 0; heuristic < 8; heuristic++) {
                HashSet<Integer> invalidClasses;
                if (this.incompatibilities.containsKey(cls)) {
                    invalidClasses = new HashSet<>(this.incompatibilities.get(cls));
                } else {
                    invalidClasses = new HashSet<>();
                }
                Knapsack knap = new Knapsack();
                int n = currItems.size();
                for (int i = n - 1; i >= 0; i--) {
                    Item item;
                    switch (heuristic) {
                        case (0):
                            item = itemsByCostWeight.get(i);
                            break;
                        case (1):
                            item = itemsByCost.get(i);
                            break;
                        case (2):
                            item = itemsByWeight.get(i);
                            break;
                        case (3):
                            item = itemsByResaleCostWeight.get(i);
                            break;
                        case (4):
                            item = itemsByResaleWeight.get(i);
                            break;
                        case (5):
                            item = itemsByCostPlusWeight.get(i);
                            break;
                        case (6):
                            item = itemsByPureCost.get(i);
                            break;
                        case (7):
                            item = itemsByPureWeight.get(i);
                            break;
                        default:
                            item = itemsByCostWeight.get(i);
                            break;
                    }

                    int itemCls = item.getCls();
                    if (!invalidClasses.contains(itemCls)) {
                        if (!knap.getItems().containsKey(itemCls)) {
                            if (this.incompatibilities.containsKey(itemCls)) {
                                for (int invalidCls : this.incompatibilities.get(itemCls)) {
                                    invalidClasses.add(invalidCls);
                                }
                            }
                        }

                        if ((item.getCost() + knap.getCost() <= this.MAX_COST) && (item.getWeight() + knap.getWeight() <= this.MAX_WEIGHT)) {
                            knap.addItem(item);
                        }
                    }
                }
                if (knap.getScore() >= this.bestKnap.getScore()) {
                    this.bestKnap = knap;
                }
                if (knap.getScore() >= knaps.get(heuristic).getScore()) {
                    knaps.set(heuristic, knap);
                }
                System.out.println(heuristicNames[heuristic] + ": " + knaps.get(heuristic).getScore());

            }
        }


        for (int i = 0; i < knaps.size(); i++) {
            System.out.println(heuristicNames[i] + ": " + knaps.get(i).getScore());
        }
    }
}
