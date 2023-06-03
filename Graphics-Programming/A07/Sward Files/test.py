import cv2
import numpy as np
import funcs
import random,struct


def save(img,filename):
    f=file(filename+".swrd","wb")
    flat=1* img.ravel()
    shape=img.shape
    shape=list(shape)
    if len(shape)==2:
        shape.append(1)
    h,w,d=shape
    f.write(struct.pack("HHB",h,w,d))
    for num in flat:
        f.write(struct.pack("B",num))
    f.close()

def openImg(filename):
    f=file(filename+".swrd","rb")
    h,w,d=struct.unpack("HHB",f.read(5))
    shape=(h,w,d)
    nums=[]
    for i in range(h*w*d):
        nums.append(struct.unpack("B",f.read(1)))
    img=np.uint8(nums)
    img=img.reshape(shape)
    #img+=16*np.random.rand(*shape)
    return np.uint8(img)






img=cv2.imread("image1.png")

#funcs.show(img)
save(img,"test2")

img2=openImg("test2")
cv2.imwrite("test2.png",img2)
funcs.show(img2)

#img2=flat.reshape((500,333,3))
#funcs.show(img2)
