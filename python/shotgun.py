import random
from collections import defaultdict
import signal
from cvxpy import *

from utilities import *


@time_it
def solve(P, M, N, C, items, constraints, chosen_item_names, use_lp, start_from_given):
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

    def LP_solver(classes):
        """
        Using LP

        Given a list of compatible classes, approximate the optimal solution
        :param knap:
        :param classes:
        :return: unwanted out of the classes?
        """
        t = tuple(sorted(classes))
        if t in memo_knap_approx:
            return NULL_KNAP
        else:
            memo_knap_approx.add(t)

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
            if variable.value is not None and round(variable.value) == 1:
                knap.add_item(items[i])

        return knap

    NULL_KNAP = LightweightKnap()

    memo_knap_approx = set()
    def knap_approx(classes):
        """
        http://math.mit.edu/~goemans/18434S06/knapsack-katherine.pdf

        Given a list of compatible classes, approximate the optimal solution
        :param knap:
        :param classes:
        :return: unwanted out of the classes?
        """
        t = tuple(sorted(classes))
        if t in memo_knap_approx:
            return NULL_KNAP
        else:
            memo_knap_approx.add(t)

        def sort_by_weight_cost(i):
            item = items[i]
            if item['weight'] + item['cost'] == 0:
                return -item['score']
            return -item['score']/(item['weight'] + item['cost'])

        def sort_by_cost(i):
            item = items[i]
            if item['cost'] == 0:
                return -item['score']
            return -item['score']/item['cost']

        def sort_by_weight(i):
            item = items[i]
            if item['weight'] == 0:
                return -item['score']
            return -item['score']/item['weight']


        def greedily_fill(knap, items, i_sorted_by_cost_weight, i_sorted_by_cost, i_sorted_by_weight):
            """
            Given a knap and items, greedily fill the knap by their score/constraint value
            :param knap:
            :param items:
            :return:
            """
            i = 0
            while i < len(items):
                item = items[i_sorted_by_cost_weight[i]]
                if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
                    pass
                else:
                    knap.add_items(items, [i_sorted_by_cost_weight[i]])

                item = items[i_sorted_by_cost[i]]
                if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
                    pass
                else:
                    knap.add_items(items, [i_sorted_by_cost[i]])

                item = items[i_sorted_by_weight[i]]
                if (item['cost'] + knap.cost) > MAX_COST or (item['weight'] + knap.weight) > MAX_WEIGHT:
                    pass
                else:
                    knap.add_items(items, [i_sorted_by_weight[i]])
                i += 1

        items = [item for cls in classes for item in items_d_d[cls]]
        n = len(items)




        i_sorted_by_cost_weight = sorted(range(n), key=sort_by_weight_cost)
        i_sorted_by_cost = sorted(range(n), key=sort_by_cost)
        i_sorted_by_weight = sorted(range(n), key=sort_by_weight)

        # l_knaps = [[random.randint(0, len(items) - 1)], []]
        l_knaps = [[]]

        i = 1
        # greedily fill knaps

        best_knap = LightweightKnap()
        for knap_i in l_knaps:
            knap = Knap()
            knap.add_items(items, knap_i)
            greedily_fill(knap, items, i_sorted_by_cost_weight, i_sorted_by_cost, i_sorted_by_weight)
            if knap.score > best_knap.score:
                best_knap = knap
            i+=1
        return best_knap


    # swap out classes for each unused class
    # fill in with as many classes as possible
    # perform greedy knap approx
    # if better, keep, restart swapping out classes
    # if no improvement over 3 tries, end

    def swap_out_classes(knap):
        used_classes = set(knap.items.keys())
        unused_classes = classes - used_classes

        def completely_compatible(to_test_cls, classes):
            """
            if to_test_cls is compatible with classes, return True, else False
            :param to_test_cls:
            :param classes:
            :return:
            """
            for cls in incompatibility_d[to_test_cls]:
                if cls in classes:
                    return False
            return True

        shuffled_unused_classes = list(unused_classes)
        random.shuffle(shuffled_unused_classes)


        for i, cls in enumerate(shuffled_unused_classes):
            if len(memo_knap_approx) > 10000:
                memo_knap_approx.clear()
            to_test_classes = set(knap.items.keys())
            for incompatible_cls in incompatibility_d[cls]:
                if incompatible_cls in to_test_classes:
                    to_test_classes.remove(incompatible_cls)
            to_test_classes.add(cls)

            other_classes = list(unused_classes - set([cls]))
            random.shuffle(other_classes)

            for other_cls in other_classes:
                if completely_compatible(other_cls, to_test_classes):
                    to_test_classes.add(other_cls)

            if not use_lp:
                # FOR FAST AND GREEDY
                result_knap = knap_approx(to_test_classes)
            else:
                # FOR LP SOLVER
                try:
                    result_knap = LP_solver(to_test_classes)
                except ValueError as e:
                    result_knap = LightweightKnap()

            print result_knap.score

            if result_knap.score > knap.score:
                print i
                return result_knap

        return knap

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

    while True:
        try:
            print 'Shotgun attempt: {}'.format(shotgun_tries)
            if start_from_given:
                knap = best_knap
                print_knap(knap)
                start_from_given = False
            else:
                knap = Knap()
            while True:
                if len(memo_knap_approx) > 10000:
                    memo_knap_approx = set()
                resulting_knap = swap_out_classes(knap)
                if resulting_knap.score > knap.score:
                    print 'Improved: {} -> {}'.format(knap.score, resulting_knap.score)
                    knap = resulting_knap
                    print_knap(knap)
                else:
                    if knap.score > best_knap.score:
                        best_knap = knap
                    break
            memo_knap_approx = set()
            print_knap(knap)
            items_chosen = [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]
            write_output('{}/{}.out'.format(folder_name, testcase_name), items_chosen)
            # return [knap_item['name'] for cls in knap.items for knap_item in knap.items[cls]]
        except TimeoutException as e:
            print e
            print 'Tried to shotgun {} times'.format(shotgun_tries)
            if knap.score > best_knap.score:
                best_knap = knap
            print_knap(best_knap)
            return [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]
        except KeyboardInterrupt:
            print 'Tried to shotgun {} times'.format(shotgun_tries)
            if knap.score > best_knap.score:
                best_knap = knap
            print_knap(best_knap)
            return [knap_item['name'] for cls in best_knap.items for knap_item in best_knap.items[cls]]
        finally:
            signal.alarm(0)
        shotgun_tries += 1



if __name__ == '__main__':
    USE_LP = True
    START_FROM_OLD = True

    for i in range(1, 21 + 1):
        if i != 3:
            continue
        testcase_name = 'problem{}'.format(i)
        print testcase_name
        folder_name = 'submission12'
        print folder_name
        P, M, N, C, items, constraints = read_input('in/{}.in'.format(testcase_name))
        previous_chosen_items = read_output('{}/{}.out'.format(folder_name, testcase_name))
        # previous_chosen_items = []
        items_chosen = solve(P, M, N, C, items, constraints, previous_chosen_items, USE_LP, START_FROM_OLD)
        write_output('{}/{}.out'.format(folder_name, testcase_name), items_chosen)