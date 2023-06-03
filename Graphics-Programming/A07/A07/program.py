from BitFile import BitReader,BitWriter
import numpy as np
import cv2,struct

def save(img, filename):
    
    #Make BitWriter
    f=BitWriter(filename)
    flat=img.copy().ravel()
    
    #Get shape data
    shape=list(img.shape)
    if len(shape)==2:
	shape.append(1)
    h,w,d=shape
    
    #Get data for run length encoding
    first=flat[0]
    current=first
    count=0
    run_lengths=[]
    for num in flat:
	if num==current:
	    count+=1
	else:
	    run_lengths.append(count)
	    current=num
	    count=1
    if count:
	run_lengths.append(count)
	
    run_lengths=np.array(run_lengths,dtype=np.uint32)
    
    #Calc the num of bits needed to encode majority of nums
    false_pow=1
    while (np.sum(run_lengths<(2**false_pow))<.6827*len(run_lengths)):
	false_pow+=1
	if false_pow==7:
	    break
    false_thresh=2**false_pow
    
	    
    #Calc the num of bits needed to encode
    true_pow=false_pow+1
    while (np.sum(run_lengths<((2**true_pow)+(2**false_pow)))<.9545*len(run_lengths)): 
	true_pow+=1
	if true_pow>15:
	    break
    true_thresh=2**true_pow
    
	
    #Write header
    f.write(h,16)
    f.write(w,16)
    f.write(d,8)
    f.write(first,1)
    f.write(false_pow,3)
    f.write(true_pow,4)
    
    
    for num in run_lengths:
	
	#If num is bigger than capacity of one nibble
	if num>=true_thresh+false_thresh:
	    while num>=true_thresh+false_thresh:

		#Take a bit off the num
		f.write(1,1)
		f.write(true_thresh-1,true_pow)
		
		#Make opposite color zero
		f.write(0,1)
		f.write(0,false_pow)
		
		#Subtract the bit from the num
		num-=(true_thresh+false_thresh-1)
	    
	if num<false_thresh:
	    f.write(0,1)
	    f.write(num,false_pow)
	elif num<true_thresh+false_thresh:
	    f.write(1,1)
	    f.write(num-false_thresh,true_pow)
	    
    
    f.close()

def open_img(filename):
    
    f=BitReader(filename)
    
    h=f.read(16)
    w=f.read(16)
    d=f.read(8)
    current=f.read(1)
    false_pow=f.read(3)
    true_pow=f.read(4)
    
    false_thresh=2**false_pow
    true_thresh=2**true_pow

    
    nums=[]
    
    while len(nums)<h*w*d:
	
	flag=f.read(1)
	
	if flag:
	    num=f.read(true_pow)+false_thresh
	else:
	    num=f.read(false_pow)
	    
	if current:
	    nums+=list(np.ones((num)))
	else:
	    nums+=list(np.zeros((num)))
	    
	current=not current
	
    img=np.uint8(nums)
    img=img.reshape((h,w,d))*255
    f.close()
    return img
    
while True:
    print "0: Convert standard images to .bdif images"
    print "1: Convert .bdif images to standard images"
    answer=raw_input("> ").strip()
    
    if answer=="0":
	
	in_file=raw_input("Input image: ")
	img=cv2.imread(in_file,0)
	img/=128
	    
	filename=raw_input("Save as: ")
	if not ".bdif"==filename[-5:]:
	    filename+=".bdif"
	save(img,filename)
    
    elif answer=="1":
	
	open_image=raw_input(".bdif image: ")
	if not ".bdif"==open_image[-5:]:
	    open_image+=".bdif"
	img=open_img(open_image)
	    
	filename=raw_input("Save as: ")
	if not "." in filename:
	    filename+=".png"
	cv2.imwrite(filename,img)
    
    elif answer=="exit":
	exit()
    
    else:
	print "Bad input. Try again"
	
    print ""
