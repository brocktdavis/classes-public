import cv2
import numpy as np


img=cv2.imread("Untitled.bmp",0)
img/=128
data=img.ravel()
nums=[]
current=data[0]
count=0
for d in data:
    if d==current:
        count+=1
    else:
        nums.append(count)
        current=d
        count=1
nums.append(count)
nums=np.int16(nums)
print np.sum(nums<(2**3))
print np.sum(nums<(2**7))-np.sum(nums<(2**3))
#print np.max(nums)


