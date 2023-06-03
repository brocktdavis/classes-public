import numpy as np
import cv2
import funcs


img=cv2.imread("image.png",0)
img=np.float32(cv2.GaussianBlur(img,(5,5),0))
derivative_kernel=np.float32([[-1,0,1],[-2,0,2],[-1,0,1]])
Ix=cv2.filter2D(img, -1, derivative_kernel)
Iy=cv2.filter2D(img, -1, derivative_kernel.T)
I=Ix**2+Iy**2
funcs.show(I**.5)

path=I*1
kernel=np.float32([[1,1,1]])
h,w=img.shape[:2]
for i in range(1,h):
    row=path[i-1,:]
    row=cv2.erode(row,kernel.T)
    path[i:i+1,:]+=row.T
x=np.argmin(path[-1])
y=h-1
seam=[]
seam.append(x)
while y>0:
    y-=1
    x=x+1 or x or x-1
    seam.insert(x,0)
print seam

funcs.show(path)
