import sys
import csv

def main():
    gather_data(open(sys.argv[1]), open(sys.argv[2]))


def gather_data(curr_file, prev_file):
    curr_csv = csv.reader(curr_file)
    prev_csv = csv.reader(prev_file)
    #map from checkout ids to item ids in them
    checkouts_to_items = {}
    #map from ids to checkouts they are in
    items_to_checkouts = {}
    #map from item id to call num, title, author, published
    item_info = {}

    curr_checkout_id = 1

    checkouts_to_items, items1, item_info1, curr_checkout_id = process_csv(curr_csv, curr_checkout_id, 6)
    checkouts2, items2, item_info2, curr_checkout_id = process_csv(prev_csv, curr_checkout_id, 8)

    #join dicts together
    #ok since they are distinct
    checkouts_to_items.update(checkouts2)
    #not distinct, must check before combining 
    for x in items1:
        items_to_checkouts[x] = items1[x]
    for x in items2:
        if x in items_to_checkouts:
            items_to_checkouts[x] += items2[x]
        else: 
            items_to_checkouts[x] = items2[x]
    #not distinct, must check before combining (but don't need to join them)
    for x in item_info1:
        item_info[x] = item_info1[x]
    for x in item_info2:
        if x not in item_info:
            item_info[x] = item_info2[x]


    #print some interesting information 
    print "there are", len(checkouts_to_items), "different checkouts"
    b = len([x for x in checkouts_to_items if len(checkouts_to_items[x]) >= 12])
    print "there are", b, "big checkouts"
    c = [x for x in items_to_checkouts if len(items_to_checkouts[x]) >= 2]
    print "there are", len(c), "items appearing in at least two checkouts"
    sample = 501
    print "sample first checkout neighbors:", [item_info[x][1][1] for x in checkouts_to_items[items_to_checkouts[c[sample]][0]]]
    print "sample second checkout neighbors:", [item_info[x][1][1] for x in checkouts_to_items[items_to_checkouts[c[sample]][1]]]

def process_csv(a_csv, curr_checkout_id, person_index):
    checkouts = {}
    items = {}
    item_info = {}
    prev_person = None
    curr_checkout = set()
    for line in a_csv:
        #get various aspects of the data
        item_id = line[0]
        call_no = line[2]
        barcode = line[5]
        curr_out, curr_person = line[person_index],line[person_index + 1]
        all_time_checkouts = line[11]
        recent_checkouts = line[13]
        cite_info = line[15:20]
        isbn_dirty = line[22]

        #add items to dictionary 
        item_info[item_id] = (call_no, cite_info, isbn_dirty)
        if item_id not in items:
            items[item_id] = [curr_checkout_id]
        else:
            items[item_id].append(curr_checkout_id)

        #compile a set of checkouts
        if curr_person == "":
            break
        if curr_person == prev_person:    
            curr_checkout.add(item_id)
        else:
            prev_person = curr_person
            checkouts[curr_checkout_id] = frozenset(curr_checkout)
            curr_checkout_id += 1
            curr_checkout = set()
            curr_checkout.add(item_id)

    return checkouts, items, item_info, curr_checkout_id
if __name__ == "__main__":
    main()
