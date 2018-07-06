import csv
# premodification of raw data
with open('train.csv') as f:
    reader = csv.reader(f)
    c = open("train_new.csv", "w")
    writer = csv.writer(c)
    for row in reader:
        if reader.line_num<=1000:
            if reader.line_num != 1:
                # separate date and time
                ilist=list(row)
                if ilist[9] == 'N':  # filter all 'Y' flag
                    rlist = []
                    rlist.append(ilist[0])  # id if necessary
                    rlist.append(ilist[1])  # vender id
                    pdt = ilist[2].split(' ')
                    rlist.append(pdt[0])
                    rlist.append(pdt[1])
                    ddt = ilist[3].split(' ')  # dropoff
                    rlist.append(ddt[0])
                    rlist.append(ddt[1])
                    rlist.append(ilist[4])  # head count
                    rlist.append(ilist[5])  # plo
                    rlist.append(ilist[6])  # pla
                    rlist.append(ilist[7])  # dlo
                    rlist.append(ilist[8])  # dla
                    rlist.append(ilist[10])  # duration
                    writer.writerow(rlist)
    c.close()
