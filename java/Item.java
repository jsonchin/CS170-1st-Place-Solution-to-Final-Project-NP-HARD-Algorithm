public class Item {
    private final String name;
    private final int cls;
    private final float weight;
    private final float cost;
    private final float score;

    private final float weightCostValue;
    private final float costValue;
    private final float weightValue;

    private final float resaleWeightCostValue;
    private final float resaleCostValue;
    private final float resaleWeightValue;



    public Item(String name, int cls, float weight, float cost, float score) {
        this.name = name;
        this.cls = cls;
        this.weight = weight;
        this.cost = cost;
        this.score = score;

        if (this.weight + this.cost == 0) {
            this.weightCostValue = this.score;
            this.resaleWeightCostValue = this.score + this.cost;
        } else {
            this.weightCostValue =-this.score / (this.weight + this.cost);
            this.resaleWeightCostValue = (this.score + this.cost) / (this.weight + this.cost);
        }

        if (this.cost == 0) {
            this.costValue = this.score;
            this.resaleCostValue = (this.score + this.cost);
        } else {
            this.costValue = this.score / this.cost;
            this.resaleCostValue = (this.score + this.cost) / this.cost;
        }

        if (this.weight == 0) {
            this.weightValue = this.score;
            this.resaleWeightValue = (this.score + this.cost);
        } else {
            this.weightValue = this.score / this.weight;
            this.resaleWeightValue = (this.score + this.cost) / this.weight;
        }

    }


    public String getName() {
        return name;
    }

    public int getCls() {
        return cls;
    }

    public float getWeight() {
        return weight;
    }

    public float getCost() {
        return cost;
    }

    public float getScore() {
        return score;
    }

    public float getWeightCostValue() {
        return weightCostValue;
    }

    public float getCostValue() {
        return costValue;
    }

    public float getWeightValue() {
        return weightValue;
    }

    public float getResaleWeightCostValue() {
        return resaleWeightCostValue;
    }

    public float getResaleCostValue() {
        return resaleCostValue;
    }

    public float getResaleWeightValue() {
        return resaleWeightValue;
    }
}
