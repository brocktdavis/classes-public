
for i in range(1, 20):
    
    for j in range(i + 1, 20):
        
        f = file(str(i) + "_" + str(j) + ".bat", 'w')
        f.write("start java Fighter " + str(i))
        f.write("\n")
        f.write("start java Fighter " + str(j))
        f.close()
