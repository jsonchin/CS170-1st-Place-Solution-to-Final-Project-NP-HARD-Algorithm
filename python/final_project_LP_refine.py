import random
from collections import defaultdict
from cvxpy import *

from utilities import *

def refine(P, M, N, C, items, constraints, chosen_item_names):
    """
    P: float can carry pounds
    M: float can spend dollars
    N: integer number of items to choose from
    C: integer number of constraints
    items: list of tuples of the form (item_name, class, weight, cost, resale_value)
    constraints: list of sets

    Write your amazing algorithm here.

    Return: a list of strings, corresponding to item names.
    """

    class LightweightKnap:
        def __init__(self):
            self.items = {}
            self.score = 0
            self.weight = 0
            self.cost = 0

        def add_item(self, item):
            item_class = item['class']
            if item_class not in self.items:
                self.items[item_class] = [item]
            else:
                self.items[item_class].append(item)
            self.score += item['score']
            self.weight += item['weight']
            self.cost += item['cost']

        def copy_knap(self):
            new_knap = LightweightKnap()
            new_knap.score = self.score
            new_knap.weight = self.weight
            new_knap.cost = self.cost
            new_items = {}
            for cls in self.items:
                new_items[cls] = self.items[cls][:]
            new_knap.items = new_items
            return new_knap

    random.seed(27)

    MAX_WEIGHT = P
    MAX_COST = M

    item_cols = ['name', 'class', 'weight', 'cost', 'resale_value']

    # list of all "good" items (positive profit)
    items_d_l = []
    items_d_d = {}
    items_by_name = {}
    classes = set()
    for item in items:
        d = {col:val for col, val in zip(item_cols, item)}
        d['score'] = d['resale_value'] - d['cost']
        if d['score'] >= 0 and d['cost'] <= MAX_COST and d['weight'] <= MAX_WEIGHT:
            item_class = d['class']
            items_d_l.append(d)
            items_by_name[d['name'].strip()] = d
            if item_class in items_d_d:
                items_d_d[item_class].append(d)
            else:
                items_d_d[item_class] = [d]
            classes.add(item_class)


    # dict keyed by classes, value is a set of classes that it is not compatible with
    incompatibility_d = defaultdict(set)
    for constraint in constraints:
        constraint_elements = list(constraint)
        n_elements = len(constraint_elements)
        for i in range(n_elements):
            for j in range(n_elements):
                if j != i:
                    incompatibility_d[constraint_elements[i]].add(constraint_elements[j])


    def LP_solver(classes):
        """
        Using LP

        Given a list of compatible classes, approximate the optimal solution
        :param knap:
        :param classes:
        :return: unwanted out of the classes?
        """

        items = [item for cls in classes for item in items_d_d[cls]]

        variables = [Bool() for _ in range(len(items))]
        score_variable = Variable()

        weight_constraint = sum([item['weight'] * variable for item, variable in zip(items, variables)]) <= MAX_WEIGHT
        cost_constraint = sum([item['cost'] * variable for item, variable in zip(items, variables)]) <= MAX_COST
        score_objective = sum([item['score'] * variable for item, variable in zip(items, variables)]) == score_variable
        constraints = [weight_constraint, cost_constraint, score_objective]

        objective = Maximize(score_variable)

        prob = Problem(objective, constraints)
        prob.solve()


        knap = LightweightKnap()
        for i, variable in enumerate(variables):
            if round(variable.value) == 1:
                knap.add_item(items[i])

        return knap

    knap = LightweightKnap()
    for name in chosen_item_names:
        item = items_by_name[name]
        knap.add_item(item)

    print_knap(knap)
    classes = list(knap.items.keys())
    try:
        result_knap = LP_solver(classes)
    except Exception as e:
        print e
        result_knap = knap
    if result_knap.score > knap.score:
        knap = result_knap
    print_knap(knap)
    return [knap_item['name'] for cls in knap.items for knap_item in knap.items[cls]]


if __name__ == '__main__':
    # testcase_name = 'problem3'
    # for i in [3, 19]:
    # for i in [6]:
    for i in range(1, 21 + 1):
        # if i != 8:
        #     continue
        if i == 21 or i == 8: #memory issues, 6561265.95 for 8
            continue
        testcase_name = 'problem{}'.format(i)
        print testcase_name
        P, M, N, C, items, constraints = read_input('in/{}.in'.format(testcase_name))
        previous_chosen_items = read_output('submission_final/{}.out'.format(testcase_name))
        items_chosen = refine(P, M, N, C, items, constraints, previous_chosen_items)
        # write_output('LP_refine_search/{}.out'.format(testcase_name), items_chosen)