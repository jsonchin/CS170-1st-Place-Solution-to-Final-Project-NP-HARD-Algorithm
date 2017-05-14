import java.util.*;

public class Shotgun extends Solve {
    public Shotgun(float MAX_WEIGHT, float MAX_COST, ArrayList<Item> items, ArrayList<ArrayList<Integer>> constraints, ArrayList<String> chosenItemNames) {
        super(MAX_WEIGHT, MAX_COST, items, constraints, chosenItemNames);
    }

    public void shotgun(boolean startFromBest) {
        Knapsack currKnap = new Knapsack();
        if (startFromBest) {
            currKnap = this.bestKnap;
        }
        while (true) {
            System.out.println("Shotgun Try: " + shotgunTries);
            this.memoKnapApprox = new HashSet<>();
            while (true) {
                if (this.memoKnapApprox.size() > 10000) {
                    this.memoKnapApprox = new HashSet<>();
                }
                Knapsack resultingKnap = this.swapOutClassesShotgun(currKnap);
                if (resultingKnap == null) {
                    if (currKnap.getScore() > this.bestKnap.getScore()) {
                        this.bestKnap = currKnap;
                        System.out.println(this.bestKnap);
                    }
                    break;
                }

                System.out.println("Improved: " + currKnap.getScore() + " -> " + resultingKnap.getScore());

                currKnap = resultingKnap;
            }
            this.shotgunTries += 1;
            currKnap = new Knapsack();
        }
    }

    private Knapsack swapOutClassesShotgun(Knapsack knap) {
        Set<Integer> usedClasses = new HashSet<>(knap.getItems().keySet());
        Set<Integer> unusedClasses = new HashSet<>(this.itemsByClass.keySet());
        unusedClasses.removeAll(usedClasses);

        List<Integer> unusuedClassesList = new ArrayList<Integer>(unusedClasses);
        Collections.shuffle(unusuedClassesList);

        int i = 1;
        for (int cls : unusuedClassesList) {
            if (i == 2500) {
                break;
            }
            if (i % 100 == 0) {
                System.out.println(i);
            }
            i+=1;
            HashSet<Integer> toTestClasses = new HashSet<>(usedClasses);
            if (this.incompatibilities.containsKey(cls)) {
                for (int incompatibleCls : this.incompatibilities.get(cls)) {
                    if (toTestClasses.contains(incompatibleCls)) {
                        toTestClasses.remove(incompatibleCls);
                    }
                }
            }
            toTestClasses.add(cls);

            List<Integer> otherClassesList = new ArrayList<Integer>(unusedClasses);
            Collections.shuffle(otherClassesList);

            for (int otherCls : otherClassesList) {
                if (isCompletelyCompatible(otherCls, toTestClasses)) {
                    toTestClasses.add(otherCls);
                }
            }

            Knapsack resultKnap = knapsackApprox(new ArrayList<Integer>(toTestClasses));

            if (resultKnap.getScore() > knap.getScore()) {
                return resultKnap;
            }

        }

        return null;
    }
}
