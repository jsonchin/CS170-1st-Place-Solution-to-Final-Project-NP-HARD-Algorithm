from collections import defaultdict
import signal
from cvxpy import *

from utilities import *

@time_it
def solve(P, M, N, C, items, constraints, chosen_item_names, class_to_start_from):
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

    class Knap:
        def __init__(self):
            self.items = {}
            self.score = 0
            self.weight = 0
            self.cost = 0
            self.items_i_memory = set()

        def add_items(self, items_l, items_i):
            for i in items_i:
                if i in self.items_i_memory:
                    continue
                else:
                    self.items_i_memory.add(i)
                item = items_l[i]
                item_class = item['class']
                if item_class not in self.items:
                    self.items[item_class] = [item]
                else:
                    self.items[item_class].append(item)
                self.score += item['score']
                self.weight += item['weight']
                self.cost += item['cost']
            if self.weight > MAX_WEIGHT:
                raise ValueError
            if self.cost > MAX_COST:
                raise ValueError

        def reset_memory(self):
            self.items_i_memory = set()

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

    # random.seed(420)

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
        if d['score'] > 0 and d['cost'] <= MAX_COST and d['weight'] <= MAX_WEIGHT:
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



    def sort_by_weight_cost(i):
        item = items[i]
        if item['weight'] + item['cost'] == 0:
            return -item['score']
        return -item['score'] / (item['weight'] + item['cost'])

    def sort_by_cost(i):
        item = items[i]
        if item['cost'] == 0:
            return -item['score']
        return -item['score'] / item['cost']

    def sort_by_weight(i):
        item = items[i]
        if item['weight'] == 0:
            return -item['score']
        return -item['score'] / item['weight']

    def sort_by_score(i):
        item = items[i]
        return -item['score']

    def sort_by_pure_weight(i):
        item = items[i]
        return item['weight']

    def sort_by_pure_cost(i):
        item = items[i]
        return item['cost']

    def sort_by_resale_weight_cost(i):
        item = items[i]
        if item['weight'] + item['cost'] == 0:
            return -(item['score'] + item['cost'])
        return -item['score'] / (item['weight'] + item['cost'])

    def sort_by_resale_weight(i):
        item = items[i]
        if item['weight'] == 0:
            return -(item['score'] + item['cost'])
        return -(item['score'] + item['cost']) / item['weight']

    def sort_by_resale(i):
        item = items[i]
        return -(item['score'] + item['cost'])


    memo = set()
    def greedily_fill(cls, heuristic=set([1,2,3])):
        """
        Given a knap and items, greedily fill the knap by their score/constraint value
        :param knap:
        :param items:
        :return:
        """
        invalid_classes = set(incompatibility_d[cls])
        t = tuple(sorted(invalid_classes))
        if t in memo:
            return Knap()
        else:
            memo.add(t)

        knap = Knap()

        i = 0
        while i < len(items):
            #items sorted by just score

            # item = items[i_sorted_by_pure_cost[i]]
            # item_cls = item['class']
            # if item_cls not in invalid_classes:
            #     if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
            #         pass
            #     else:
            #         if item_cls not in knap.items:
            #             for incompatible_cls in incompatibility_d[item_cls]:
            #                 invalid_classes.add(incompatible_cls)
            #         knap.add_items(items, [i_sorted_by_pure_cost[i]])




            item = items[i_sorted_by_score[i]]
            item_cls = item['class']
            if item_cls not in invalid_classes and 0 in heuristic:
                if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
                    pass
                else:
                    if item_cls not in knap.items:
                        for incompatible_cls in incompatibility_d[item_cls]:
                            invalid_classes.add(incompatible_cls)
                    knap.add_items(items, [i_sorted_by_score[i]])


            # item = items[i_sorted_by_cost_weight[i]]
            # item_cls = item['class']
            # if item_cls not in invalid_classes and 1 in heuristic:
            #     if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
            #         pass
            #     else:
            #         if item_cls not in knap.items:
            #             for incompatible_cls in incompatibility_d[item_cls]:
            #                 invalid_classes.add(incompatible_cls)
            #         knap.add_items(items, [i_sorted_by_cost_weight[i]])
            #
            #
            # item = items[i_sorted_by_cost[i]]
            # item_cls = item['class']
            # if item_cls not in invalid_classes and 2 in heuristic:
            #     if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
            #         pass
            #     else:
            #         if item_cls not in knap.items:
            #             for incompatible_cls in incompatibility_d[item_cls]:
            #                 invalid_classes.add(incompatible_cls)
            #         knap.add_items(items, [i_sorted_by_cost[i]])
            #
            #
            # item = items[i_sorted_by_weight[i]]
            # item_cls = item['class']
            # if item_cls not in invalid_classes and 3 in heuristic:
            #     if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
            #         pass
            #     else:
            #         if item_cls not in knap.items:
            #             for incompatible_cls in incompatibility_d[item_cls]:
            #                 invalid_classes.add(incompatible_cls)
            #         knap.add_items(items, [i_sorted_by_weight[i]])
            i += 1
        return knap

    items = [item for cls in items_d_d for item in items_d_d[cls]]
    n = len(items)

    i_sorted_by_cost_weight = sorted(range(n), key=sort_by_weight_cost)
    i_sorted_by_cost = sorted(range(n), key=sort_by_cost)
    i_sorted_by_weight = sorted(range(n), key=sort_by_weight)
    i_sorted_by_score = sorted(range(n), key=sort_by_score)
    # i_sorted_by_pure_weight = sorted(range(n), key=sort_by_pure_weight)
    i_sorted_by_pure_cost = sorted(range(n), key=sort_by_pure_cost)
    i_sorted_by_resale_cost_weight = sorted(range(n), key=sort_by_resale_weight_cost)
    i_sorted_by_resale_weight = sorted(range(n), key=sort_by_resale_weight)
    i_sorted_by_resale = sorted(range(n), key=sort_by_resale)


    shotgun_tries = 1
    best_knap = LightweightKnap()
    for name in chosen_item_names:
        item = items_by_name[name]
        best_knap.add_item(item)

    class TimeoutException(Exception):
        pass

    def signal_handler(signum, frame):
        raise TimeoutException('Time is up')

    signal.signal(signal.SIGALRM, signal_handler)
    TIMEOUT_FOR_N_MINUTES = 60*24
    signal.alarm(60*TIMEOUT_FOR_N_MINUTES)

    combos = [set([1]), set([2]), set([3]), set([1, 2]), set([2, 3]), set([1, 3]), set([1, 2, 3])]

    combos = [set([0])]

    print 'n_classes: {}'.format(len(items_d_d.keys()))
    print 'n_items: {}'.format(len(items))
    for cls in sorted(items_d_d.keys()):
        if cls < class_to_start_from:
            continue
        try:
            if shotgun_tries % 50 == 0:
                print 'Shotgun attempt: {}'.format(shotgun_tries)
            for heuristic in combos:
                resulting_knap = greedily_fill(cls, heuristic=heuristic)
                if resulting_knap.score > best_knap.score:
                    print 'Improved: {} -> {}'.format(best_knap.score, resulting_knap.score)
                    best_knap = resulting_knap
                    print_knap(best_knap)
            # items_chosen = [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]
            # write_output('{}/{}.out'.format(folder_name, testcase_name), items_chosen)
            # return [knap_item['name'] for cls in knap.items for knap_item in knap.items[cls]]
        except TimeoutException as e:
            print e
            print 'Tried to shotgun {} times'.format(shotgun_tries)
            print 'Last class visited: {}'.format(cls)
            if resulting_knap.score > best_knap.score:
                best_knap = resulting_knap
            print_knap(best_knap)
            return [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]
        except KeyboardInterrupt:
            print 'Tried to shotgun {} times'.format(shotgun_tries)
            print 'Last class visited: {}'.format(cls)
            if resulting_knap.score > best_knap.score:
                best_knap = resulting_knap
            print_knap(best_knap)
            return [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]
        finally:
            signal.alarm(0)
        shotgun_tries += 1
    return [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]



if __name__ == '__main__':
    CLASS_TO_START_FROM = -100

    for i in range(1, 21 + 1):
        if i != 7:
            continue
        testcase_name = 'problem{}'.format(i)
        print testcase_name
        folder_name = 'search_merge'
        print folder_name
        P, M, N, C, items, constraints = read_input('in/{}.in'.format(testcase_name))
        previous_chosen_items = read_output('{}/{}.out'.format(folder_name, testcase_name))
        # previous_chosen_items = []
        items_chosen = solve(P, M, N, C, items, constraints, previous_chosen_items, CLASS_TO_START_FROM)
        write_output('{}/{}.out'.format(folder_name, testcase_name), items_chosen)