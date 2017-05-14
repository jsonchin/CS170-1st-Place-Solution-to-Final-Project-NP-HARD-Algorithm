import time

def print_knap(knap, print_items=False):
    print 'Cost: {}'.format(knap.cost), 'Weight: {}'.format(knap.weight), 'Score: {}'.format(knap.score)
    if print_items:
        print [knap_item['name'] for cls in knap.items for knap_item in knap.items[cls]]

# from stackoverflow
def time_it(f):
    def wrapper(*args):
        time1 = time.time()
        result = f(*args)
        time2 = time.time()
        time_delta = (time2-time1)/60.0
        minutes = int(time_delta)
        seconds = int((time_delta - minutes) * 60)
        print '{} minutes, {} seconds'.format(minutes, seconds)
        return result
    return wrapper

def read_input(filename):
    """
    P: float can carry pounds
    M: float can spend dollars
    N: integer number of items to choose from
    C: integer number of constraints
    items: list of tuples
    constraints: list of sets
    """
    with open(filename) as f:
        P = float(f.readline())
        M = float(f.readline())
        N = int(f.readline())
        C = int(f.readline())
        items = []
        constraints = []
        for i in range(N):
            name, cls, weight, cost, val = f.readline().split(";")
            items.append((name, int(cls), float(weight), float(cost), float(val)))
        for i in range(C):
            constraint = set(eval(f.readline()))
            constraints.append(constraint)
    return P, M, N, C, items, constraints

def read_output(filename):
    with open(filename) as f:
        names = []
        for line in f:
            names.append(line.strip())
        return names

def write_output(filename, items_chosen):
    with open(filename, "w") as f:
        for i in items_chosen:
            f.write("{0}\n".format(i))