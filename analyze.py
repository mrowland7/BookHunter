import sys
import csv

def main():
    gather_data(open(sys.argv[1]))


def gather_data(data_file):
    data_csv = csv.reader(data_file)
    both_count = 0

    #set of checkouts, which are sets of item_ids
    checkouts = set()
    #map from item id to call num, title, author, published
    items = {}
    #map from

    prev_person = None
    curr_checkout = set()
    for line in data_csv:
        #get various aspects of the data
        item_id = line[0]
        call_no = line[2]
        barcode = line[5]
        curr_out, curr_person = line[6],line[7]
        last_out, last_person = line[8],line[9]
        all_time_checkouts = line[11]
        recent_checkouts = line[13]
        cite_info = line[15:20]
        isbn_dirty = line[22]

        #add items to dictionary 
        items[item_id] = (call_no, cite_info, isbn_dirty)

        #compile a set of checkouts
        if curr_person == prev_person:    
            curr_checkout.add(item_id)
        else:
            prev_person = curr_person
            checkouts.add(frozenset(curr_checkout))
            curr_checkout = set()
            curr_checkout.add(item_id)


        if curr_out and last_out:
            both_count += 1
    print "there are", both_count, "books with both"
    print "there are", len(checkouts), "different checkouts"
    a = map(float, map(len, checkouts))
    print "avg checkout size:",sum(a) / len(a)



if __name__ == "__main__":
    main()
