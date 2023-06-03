import cv2
import numpy as np
import glob,funcs

def save_imgs(imgs,stem):
    
    i=0
    max_num=len(imgs)-1
    digits=np.ceil(np.log10(max_num))
    for img in imgs:
	if i:
	    power=np.ceil(np.log10(i)+0.00000001)
	else:
	    power=1
	num_of_zeros=digits-power
	leading_zeros="0"*int(num_of_zeros)
	cv2.imwrite("%s%s%i.png"%(stem,leading_zeros,i),img)
	i+=1

def start_end_frame(imgs):

    length=len(imgs)
    print length
    """Raw diff img"""
    diffs=np.zeros((length,length),dtype=np.float32)
    for i in range(length):
	print i
	for j in range(i,length):
	    
	    img1=imgs[i]
	    img2=imgs[j]
	    diff=(np.float32(img1)-img2)**2
	    diffs[i,j]+=np.sum(diff)
	    diffs[j,i]+=np.sum(diff)
    print "diffs done"
    out1=diffs.copy()

    """Blur and window"""
    diag_kernel=np.float32([[1,0,0,0,0],
			    [0,4,0,0,0],
			    [0,0,6,0,0],
			    [0,0,0,4,0],
			    [0,0,0,0,1]])
    diag_kernel/=np.sum(diag_kernel)

    max_diff=np.max(diffs)

    for i in range(length):
	for j in range(length):
	    #Discount anything less than 30 frames away
	    if np.abs(i-j)<30:
		diffs[i,j]+=max_diff


    diffs=cv2.filter2D(diffs,-1,diag_kernel,borderType=cv2.BORDER_REFLECT)
    diffs=diffs[1:-1,1:-1]

    out2=diffs.copy()

    coord=np.argmin(diffs)
    frame1=(coord//(length-2))+1
    frame2=(coord%(length-2))+1
    
    start=min(frame1,frame2)
    end=max(frame1,frame2)
    
    print start,end
    
    return out1,out2,start,end

"""Candle"""

candle_names=glob.glob("candle_in\*.png")
candle_names=sorted(candle_names)
candle_imgs=[]
for img in candle_names:
    candle_imgs.append(cv2.imread(img))

out1,out2,start,end=start_end_frame(candle_imgs)

candle_out=candle_imgs[start:end]

save_imgs(candle_out,"candle_out\\")
funcs.save(out1,"candle_out\out1.png")
funcs.save(out2,"candle_out\out2.png")

"""Personal"""

printer_names=glob.glob("printer_in\*.png")
printer_names=sorted(printer_names)
printer_imgs=[]
for img in printer_names:
    printer_imgs.append(cv2.imread(img))

out3,out4,startp,endp=start_end_frame(printer_imgs)

printer_out=printer_imgs[startp:endp]

save_imgs(printer_out,"printer_out\\")
funcs.save(out3,"printer_out\out3.png")
funcs.save(out4,"printer_out\out4.png")
