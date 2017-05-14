import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Knapsack {
    private HashMap<Integer, ArrayList<Item>> items;
    private float score;
    private float weight;
    private float cost;
    private HashSet<String> nameMemory;

    public Knapsack() {
        this.items = new HashMap<>();
        this.score = 0;
        this.weight = 0;
        this.cost = 0;
        this.nameMemory = new HashSet<>();
    }

    public Knapsack(HashMap<Integer, ArrayList<Item>> items, float score, float weight, float cost) {
        this();
        this.items = items;
        this.score = score;
        this.weight = weight;
        this.cost = cost;
    }

    public Knapsack copyKnap() {
        HashMap<Integer, ArrayList<Item>> clonedItems = new HashMap<>();
        for (int cls : this.items.keySet()) {
            clonedItems.put(cls, (ArrayList<Item>) this.items.get(cls).clone());
        }
        Knapsack copiedKnap = new Knapsack(clonedItems,
                this.score,
                this.weight,
                this.cost);
        return copiedKnap;
    }

    public void addItem(Item item) {
        if (this.nameMemory.contains(item.getName())) {
            return;
        } else {
            this.nameMemory.add(item.getName());
        }
        int cls = item.getCls();
        if (this.items.containsKey(cls)) {
            this.items.get(cls).add(item);
        } else {
            ArrayList<Item> tempList = new ArrayList<>();
            tempList.add(item);
            this.items.put(cls, tempList);
        }
        this.score += item.getScore();
        this.weight += item.getWeight();
        this.cost += item.getCost();
    }

    public HashSet<String> getNames() {
        return this.nameMemory;
    }

    public HashMap<Integer, ArrayList<Item>> getItems() {
        return items;
    }

    public float getScore() {
        return score;
    }

    public float getWeight() {
        return weight;
    }

    public float getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Knapsack{" +
                "score=" + score +
                ", weight=" + weight +
                ", cost=" + cost +
                '}';
    }
}
