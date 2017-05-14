# CS170 1st Place Solution to Final Project NP-HARD Algorithm

This is my 1st place solution to the CS170 Final Project (Spring 2017) for an NP-HARD heursitic algorithm to solve the given problem.

## Problem Overview

The problem *PICKITEMS* is an NP-HARD problem that mixes the NP-HARD problem [KNAPSACK](https://en.wikipedia.org/wiki/Knapsack_problem) and the NP-HARD problem [INDEPENDENT SET](https://en.wikipedia.org/wiki/Independent_set_(graph_theory)).

*PICKITEMS* is defined as follows:

We send our professor Prasad to the supermarket with to fill a GargSack (Garg was our other professor) of items that we will buy so that we can resell the items for a profit. Each item has a weight, a cost, a resale value, and a class. Furthermore Prasad is sent with *M* dollars and can only fit *P* pounds worth of items into the GargSack. Addtitionally there are constraints on items based on their classes. For example, if there we buy a desk with class "Wood" and buy a bag of termites with class "Wood Destroyer" then the GargSack will explode and we will get 0 dollars when trying to buy the items. (It's more formally defined as classes are integers and we are given many lists of integers such that buying two different classes of items from the same list will make the GargSack explode.)

KNAPSACK is shown here in trying to fill a knapsack with standard constraints such as weight and cost and INDEPENDENT SET is shown here with the class constraints.

## Initial Thoughts on the Problem:

The hard part didn't seem to be finding the optimal set of items given a set of classes but rather finding the optimal set of classes across all 2^M subsets of classes for a few reasons. The first is that KNAPSACK is a weak NP-HARD problem meaning that there is a psuedo-polynomial time algorithm to find the optimal solution by using Dynamic Programming whereas INDEPENDENT SET is a NP-HARD problem that does not even have an constant approximation algorithm for it which makes it extremely hard. The second reason is that the hard testcases seemed to have a lot of class constraints which made the problem more weighted towards INDEPENDENT SET. 

Initially there were a few things that came to mind that I wanted to try:
- Randomized (or add some sort of randomness)
- Greedy
- Some sort of improvement

There was no way to be able to go through all combinations of classes due to the enormous size of the inputs so randomized and or greedy seemed like good starting points to finding a good subset of classes. Then the idea was to improve on a given subset of classes for a more effective way to go through the search space.

## Overview of Algorithms

For each algorithm, I'll give a brief overview, thoughts, and some psuedocode.

The first algorithm I'll go over is **Improve** which seeks to improve a given set of classes by trying swap in all classes one at a time and see if there's any gain. To prevent a set of classes from becoming invalid, take out of the class's incompatibilities and then add the class in. The hope here was that there is some class that I can swap in and some classes that I can take out to get a better set of classes. In addition to swapping in that one class, I tried to fill the knapsack wtih more classes by going through the remaining unused classes randomly (don't force a swap in, if a class is compatible with the current set of classes, let it in).

Thinking about this visually, imagine a hill and you are at some point on the hill. By swapping classes in and only changing your knapsack on beneficial swaps, you can imagine yourself climbing up the hill gradually. And if the score space was convex, then **Improve** would eventually reach the optimal point or set of classes.

```python
import random
all_classes = set() #initialized with all classes
def improve(knapsack):
  classes = knapsack.classes
  unused_classes = all_classes - classes
  random.shuffle(unused_classes) #add some randomness
  for to_swap_in_class in unused_classes:
    new_classes = remove_incompatibilities(classes, to_swap_in_classes) #remove any conflicting classes
    add_additional_classes(new_classes, unusued_classes) #try to fill as much as possible

    resulting_knap = fill_knap_with_items_from_classes(new_classes) #assume we fill it optimally LP/DP or approx
    if resulting_knap.score > knapsack.score: #there was a gain
      return resulting_knap
```

#### But a question that arises from this is where to start and what to do since the score space is almost definitely not convex?

My first approach which I call **Shotgun** after reading a Wikipedia article on [Hill climbing](https://en.wikipedia.org/wiki/Hill_climbing) (it stands for Shotgun Hill Climbing). It starts by choosing a random subset of classes. Then improves upon it using **Improve**. Now why randomly? Going back to thinking visually, imagine lots of hills with one hill a lot higher than the others (the optimal hill). If we **Shotgun** multiple times, we'll visit lots of hills and climb them and hopefully, we'll visit the optimal hill.

```python
all_classes = set() #initialized with all classes
def shotgun():
  best_knap_so_far = Knapsack()
  while not timed_out:
    initial_classes = generate_class_set() #gets a random subset of classes that are all compatible
    knapsack = fill_knap_with_items_from_classes(initial_classes)
    while True:
      resulting_knap = improve(knapsack)
      if resulting_knap.score > knapsack.score:
        knapsack = resulting_knap
      if knapsack.score > best_knap_so_far.score:
        best_knap_so_far = knapsack
  return best_knap_so_far
```

The **Shotgun** approach worked well for a while but didn't seem to be making a lot of gain for the time spent on each problem so I turned to greedy algorithms. I called these greedy algorithms **Search** because they do a little more than just greedy. For each class, we add its incompatibilities to an invalid class set and then try to fill a knapsack ignoring items that have a class contianed in the invalid class set. The idea here was to remedy the problem of greedy algorithms. Greedy is great as a heuristic but falls short due to its shortsighted nature. For example, it gets caught up in trying to choose the "best" choice at a current time which might not be in the optimal solution. By iterating through each class and invalidating its incompatible classes, we effectively allow items of that class to try to be taken up the greedy algorithm without fear of a class incompatible item greedily being taken up by the algorithm first (which would prevent these items from being picked). There are two ways of greedily filling a knapsack once we have invalidated some classes.

- by items
- by classes

The first, *by items*, is we look at all items, sort them by some heuristic (a long list of heuristics I tried is mentioned below), then iteratively add items to the knapsack if they don't conflict with an item that is already in there.

The second, *by classes*, is we look at classes. We can compute statistics (total cost, total weight, total score, total resale value) about each class by summing over all items of a given class. Then perform the same procedure but instead of adding items to the knapsack, add classes to a set and later, fill a knapsack with items from the chosen classes. The hope here was that classes as a whole better represent the possible end result.

```python
def search(invalid_classes):
  knapsack = Knapsack()
  for item in sorted(items, key=heuristic)[::-1]:
    if item.class not in invalid_classes:
      knapsack.add_item(item) # only adds it if cost and weight limits are not exceeded and no conflicting items
  return knapsack
  
def search(invalid_classes):
  to_use_classes = set()
  for cls in sorted(classes, key=heuristic)[::-1]:
    if cls not in invalid_classes and incompatible_classes(cls) not in to_use_classes:
      to_use_classes.add(cls) # only adds it if no concflicting classes
  knapsack = fill_knap_with_items_from_classes(to_use_classes)
  return knapsack
```

Here are a list of heuristics that I tried (I define score as resale_value - cost):
- score / (cost + weight)
- score / cost
- score / weight
- pure cost
- pure weight
- pure score
- cost * weight
- pure resale_value
- resale_value / weight
- resale_value / (cost + weight)

There was no best heuristic for all testcases. Some worked betters for some than they did for others.


One last algorithm I used to touch up my knapsacks was **LP Refine**. It takes in a knapsack, gets its classes, gets all items from those classes, then runs Integer Linear Programming on them. The important thing to note is that all items are compatible with each other so there's only two constraints (weight and cost) so this actually runs fairly quick (30 seconds or less on most testcases)

```python
from cvxpy import *
def LP_solver(knapsack):
    classes = knapsack.classes
    items = get_items_with_class(classes)

    variables = [Bool() for _ in range(len(items))]
    score_variable = Variable()

    weight_constraint = sum([item['weight'] * variable for item, variable in zip(items, variables)]) <= MAX_WEIGHT
    cost_constraint = sum([item['cost'] * variable for item, variable in zip(items, variables)]) <= MAX_COST
    score_objective = sum([item['score'] * variable for item, variable in zip(items, variables)]) == score_variable
    constraints = [weight_constraint, cost_constraint, score_objective]

    objective = Maximize(score_variable)

    prob = Problem(objective, constraints)
    prob.solve()

    knap = Knapsack()
    for i, variable in enumerate(variables):
        if round(variable.value) == 1:
            knap.add_item(items[i])

    return knap
```


## Thoughts and Commentary

One thing that I've been ignoring in the psuedocode and algorithms above is how to fill a knapsack given a set of classes. I tried using LP and greedy and I found that greedy overall was better due to its speed and its good approximation of the optimal solution (in most cases it seemed that greedy was optimal). I didn't think it was necessary to get the optimal knapsack at every single iteration of **Improve**, rather it was more important to try a lot of subsets of classes in that timespan and improve a lot more.

Also in **Improve**, once I see a gain I immediately take it rather than searching for some more and choosing the best option from a given point. Visually, choosing the best option is being on the hill, looking all 360 degrees, then choosing a direction which wastes some time although you'd get a direct path to the top. By taking the first improvement, you could imagine it as going in zigzags indirectly up the hill (because you're taking the first improvement) and eventually reaching the top but in a much quicker fashion despite not having a direct path. Also the number of classes to try to swap in was enormous for many of the testcases.

I rewrote most of my code in Java a week before submission to speed up **Improve** and **Shotgun** to get about a x8 speedup. There was a minor issue with precision. One testcase had a solution that I found in Python where the cost was exactly the MAX_COST but the solution I found in Java would overstep the MAX_COST due to precision errors (it was adding in an item of additional cost 6).


## tldr

Wrote an improvement algorithm. Started from random knapsacks, improved on them with the improvement algorithm. Tried lots of greedy heuristics, improved on the knapsacks they produced with the improvement algorithm.

## Ending

I really enjoyed working on this project (thinking of different approaches although a lot didn't work, seeing improvements in your score, etc). Thanks to the staff for making this project and the course great!
