from collections import defaultdict

from utilities import *


def calc_score(P, M, N, C, items, constraints, chosen_item_names):
    """
    P: float can carry pounds
    M: float can spend dollars
    N: integer number of items to choose from
    C: integer number of constraints
    items: list of tuples of the form (item_name, class, weight, cost, resale_value)
    constraints: list of sets
    chosen_item_names: list of str (names)

    return score
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

    MAX_WEIGHT = P
    MAX_COST = M

    item_cols = ['name', 'class', 'weight', 'cost', 'resale_value']

    # list of all "good" items (positive profit)
    items_d_d = {}
    items_by_name = {}
    classes = set()
    for item in items:
        d = {col:val for col, val in zip(item_cols, item)}
        d['score'] = (d['resale_value'] - d['cost'])
        item_class = d['class']
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

    knap = LightweightKnap()
    for name in chosen_item_names:
        item = items_by_name[name]
        knap.add_item(item)
    if knap.cost > MAX_COST or knap.weight >  MAX_WEIGHT:
        print 'cost or weight overflow'
        return 0

    for cls in knap.items:
        for constraint in incompatibility_d[cls]:
            if constraint in knap.items:
                print 'constraint'
                return 0
    return knap.score




if __name__ == '__main__':
    for i in range(1, 21 + 1):
        # if i not in [7]:
        #     continue
        testcase_name = 'problem{}'.format(i)
        P, M, N, C, items, constraints = read_input('in/{}.in'.format(testcase_name))
        chosen_item_names = read_output('submission_final/{}.out'.format(testcase_name))
        score = calc_score(P, M, N, C, items, constraints, chosen_item_names)

        print testcase_name
        print score

