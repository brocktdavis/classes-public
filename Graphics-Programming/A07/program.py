import cv2
import numpy as np
import funcs
import struct

def save_file(img,filename):
    f=file(filename+".ext","wb")
    flat=img.ravel().copy()
    shape=img.shape
    shape=list(shape)
    if len(shape)==2:
	shape.append(1)
    h,w,d=shape
    f.write(struct.pack("HHB",h,w,d))
    for num in flat:
	f.write(struct.pack("B",num))
    f.close()
    
def read_file(filename):
    f=file(filename+".ext","rb")
    h,w,d=struct.unpack("HHB",f.read(5))
    shape=(h,w,d)
    nums=[]
    for i in range(h*w*d):
	nums.append(struct.unpack("B",f.read(1)))
    img=np.uint8(nums)
    img=img.reshape(shape)
    return img
    

    

