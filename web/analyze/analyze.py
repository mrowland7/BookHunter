import sys
import os
import csv
import re

item_map_g = None
item_info_g = None
lookup_info_g = None

def main():
    if len(sys.argv) != 3:
        print "usage: python analyze.py curr_sorted prev_sorted"
    else:
        item_map, item_info, lookup_info = gather_data(open(sys.argv[1]), open(sys.argv[2]))
        get_input(item_map, item_info, lookup_info) 

def init():
    path1 = os.path.join(os.path.split(__file__)[0], '../data/data_curr_sorted.csv')
    path2 = os.path.join(os.path.split(__file__)[0], '../data/data_prev_sorted.csv')
    return gather_data(open(path1), open(path2))

def gather_data(curr_file, prev_file):
    curr_csv = csv.reader(curr_file)
    prev_csv = csv.reader(prev_file)
    #map from checkout ids to item ids in them
    checkouts_to_items = {}
    #map from ids to checkouts they are in
    items_to_checkouts = {}
    #map from item id to call num, title, author, published
    item_info = {}
    #map from call numbers and ISBNs to list of item id 
    lookup_info = {}

    curr_checkout_id = 1

    checkouts_to_items, items1, item_info1, lookup_info1, curr_checkout_id = process_csv(curr_csv, curr_checkout_id, 6)
    checkouts2, items2, item_info2, lookup_info2, curr_checkout_id = process_csv(prev_csv, curr_checkout_id, 8)

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
    #same for lookup info 
    for x in lookup_info1:
        lookup_info[x] = lookup_info1[x]
    for x in lookup_info2:
        if x in lookup_info:
            lookup_info[x] += lookup_info2[x]
        else: 
            lookup_info[x] = lookup_info2[x]
    #not distinct, must check before combining (but don't need to join them)
    for x in item_info1:
        item_info[x.encode("utf-8")] = item_info1[x]
    for x in item_info2:
        if x not in item_info:
            item_info[x.encode("utf-8")] = item_info2[x]

    #go from items -> checkouts, checkouts -> items to items -> items
    item_map = {}
    for item in items_to_checkouts:
        item_map[item] = []
        for checkout in items_to_checkouts[item]:
            if checkout in checkouts_to_items:
                item_map[item] += checkouts_to_items[checkout]


    #print some interesting information 
    #print "there are", len(checkouts_to_items), "different checkouts"
    #b = len([x for x in checkouts_to_items if len(checkouts_to_items[x]) >= 12])
    #print "there are", b, "big checkouts"
    #c = [x for x in items_to_checkouts if len(items_to_checkouts[x]) >= 2]
    #print "there are", len(c), "items appearing in at least two checkouts"
    #print "there are", len(lookup_info), "different call numbers / isbns"
    # sample = 501
    # print "sample first checkout neighbors:", [item_info[x][1][1] for x in checkouts_to_items[items_to_checkouts[c[sample]][0]]]
    # print "sample second checkout neighbors:", [item_info[x][1][1] for x in checkouts_to_items[items_to_checkouts[c[sample]][1]]]
    # print "hmm:", get_matches(c[sample], checkouts_to_items, items_to_checkouts, item_info)
    return item_map, item_info, lookup_info

def process_csv(a_csv, curr_checkout_id, person_index):
    checkouts = {}  
    items = {}
    item_info = {}
    lookup_info = {}
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
        

        #construct dictionaries of call no / isbn -> item ids
        clean_call_no = cleanup_call_no(call_no)
        clean_isbns = cleanup_isbn(isbn_dirty)
        if barcode not in lookup_info:
            lookup_info[barcode] = [item_id]
        else:
            lookup_info[barcode] += [item_id]
        if clean_call_no not in lookup_info:
            lookup_info[clean_call_no] = [item_id]
        else:
            lookup_info[clean_call_no] += [item_id]
        for x in cleanup_isbn(isbn_dirty):
            if x not in lookup_info:
                lookup_info[x] = [item_id]
            else:
                lookup_info[x] += [item_id]

        #add items to dictionary 
        if item_id not in item_info:
            #item_info[item_id] = (call_no, cite_info, (all_time_checkouts, recent_checkouts), isbn_dirty)
            clean_title = cite_info[1]
            if clean_title[-1] == ":" or clean_title[-1] == "/":
                clean_title = clean_title[:-1].strip()
            item_info[item_id] = {"call_no": call_no, "author": cite_info[0], "title": clean_title,
                "pub_place": cite_info[2], "publisher": cite_info[3], "year": cite_info[4],
                "all_time_checkouts": all_time_checkouts, "recent_checkouts":recent_checkouts, 
                "isbn_dirty":isbn_dirty}
        #add checkout information
        if item_id not in items:
            items[item_id] = [curr_checkout_id]
        else:
            items[item_id].append(curr_checkout_id)

        #if there's no data for the person, stop
        if curr_person == "":
            break
        #compile a set of checkouts
        if curr_person == prev_person:    
            curr_checkout.add(item_id)
        else:
            prev_person = curr_person
            checkouts[curr_checkout_id] = frozenset(curr_checkout)
            curr_checkout_id += 1
            curr_checkout = set()
            curr_checkout.add(item_id)

    return checkouts, items, item_info, lookup_info, curr_checkout_id

#returns all of the items a particular item has been checked out with
# #functionalprogramming
def get_matches(item, im, ii):
    return [ii[x] for x in im[item]]

#returns the call number in a standardized format
#remove spaces, send everything to lower case
def cleanup_call_no(call_no):
    return "".join(call_no.lower().split())

#returns a list of isbns in a standardized format
#isbns are horrible in the data: split on everything, use things that are 10 or 13 digits
def cleanup_isbn(isbn_dirty):
    tokens = isbn_dirty.split()
    only_digits = map(lambda x: re.sub("[^0-9]","",x), tokens)
    isbns = [x for x in only_digits if len(x) == 10 or len(x) == 13]
    return isbns 

def get_input(im, ii, li):
    #read from standard in
    print "Ctrl-d to exit."
    while True:
        try:
            search = raw_input("Enter an id to find matches for: ")
        except EOFError:
            print ""
            break
        matches = get_recs(search, im, ii, li)
        print "possible matches:"
        pretty_matches = get_pretty_matches(matches)
        for x in pretty_matches:
            print x

def get_pretty_matches(matches):
    return [x[1] for x in matches]

def get_recs(search_term, item_map, item_info, lookup_info):
    print "search term is >", search_term, "<"
    search_term_call_no = cleanup_call_no(search_term)
    ids = []
    if search_term in lookup_info:
        ids += lookup_info[search_term]
    if search_term_call_no in lookup_info:
        ids += lookup_info[search_term_call_no]
    matches = []
    for item_id in ids:
        matches += get_matches(item_id, item_map, item_info)
    return matches
   
def get_info(num, item_map, item_info, lookup_info):
    new_num = cleanup_call_no(num)
    if new_num not in lookup_info:
        return new_num, " isn't there, but there are ", len(item_info), "things there"
    return item_info[lookup_info[new_num][0]]
if __name__ == "__main__":
    main()
