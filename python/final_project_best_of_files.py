from utilities import *

from calc_output_score import calc_score


def best_of_submissions(testcase_name, folders):
    P, M, N, C, items, constraints = read_input('in/{}.in'.format(testcase_name))
    best_chosen_item_names = read_output('{}/{}.out'.format(folders[0] ,testcase_name))
    best_score = calc_score(P, M, N, C, items, constraints, best_chosen_item_names)

    for folder in folders[1:]:
        chosen_item_names = read_output('{}/{}.out'.format(folder, testcase_name))
        score = calc_score(P, M, N, C, items, constraints, chosen_item_names)
        if score > best_score:
            best_score = score
            best_chosen_item_names = chosen_item_names
    return best_chosen_item_names


if __name__ == '__main__':
    for i in range(1, 21 + 1):
        testcase_name = 'problem{}'.format(i)
        print testcase_name
        chosen_items = best_of_submissions(testcase_name, ['submission12_java3',
                                                           'submission11'])
        write_output('submission_final/{}.out'.format(testcase_name), chosen_items)