import cv2
import numpy as np
import funcs
import random,struct
from BitWriter import BitFile


def save(img,filename):
    f=BitFile(filename+".swrd","wb")
    flat=1* img.ravel()
    shape=img.shape
    shape=list(shape)
    if len(shape)==2:
        shape.append(1)
    h,w,d=shape
    f.write(h,16)
    f.write(w,16)
    f.write(d,8)
    for num in flat:
        f.write(num/64,2)
    f.close()

def openImg(filename):
    f=BitFile(filename+".swrd","rb")
    h=f.read(16)
    w=f.read(16)
    d=f.read(8)
    print h,w,d
    shape=(h,w,d)
    nums=[]
    failcount=0
    for i in range(h*w*d):
        try:
            nums.append(f.read(2)*64+32)
        except:
            failcount+=1
    print failcount
    img=np.uint8(nums)
    img=img.reshape(shape)
    f.close()
    #img+=16*np.random.rand(*shape)
    return np.uint8(img)






img=cv2.imread("image1.png")

funcs.show(img)
save(img,"test_bitwriting")

img2=openImg("test_bitwriting")
cv2.imwrite("test2.png",img2)
funcs.show(img2)

#img2=flat.reshape((500,333,3))
#funcs.show(img2)
