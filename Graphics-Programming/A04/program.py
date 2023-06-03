import cv2
import numpy as np
import funcs

def getSeam(img,blur=1):
    
    img=img.copy()
    img=funcs.grayscale(img,True)
    img=np.float32(cv2.GaussianBlur(img,(blur,blur),0))
    derivative_kernel=np.float32([[-1,0,1],[-2,0,2],[-1,0,1]])
    Ix=cv2.filter2D(img, -1, derivative_kernel)
    Iy=cv2.filter2D(img, -1, derivative_kernel.T)
    path=Ix**2+Iy**2
    path=path**.5

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
	minX=seam[0]-1
	maxX=seam[0]+2
	x=np.argmin(path[y,max(minX,0):min(maxX,w-1)])+x
	if x!=0:
	    x-=1
	seam.insert(0,x)
    return seam
  
def showSeam(img,seam):
    
    h,w=img.shape[:2]
    out=img.copy()
    for i in range(h):
	out[i,seam[i]]=255
    return out
    
def removeSeam(img,seam):
    
    if len(img.shape)==2:
	h,w=img.shape
	out=np.zeros((h,w-1),dtype=np.uint8)
    elif len(img.shape)==3:
	h,w,d=img.shape
	out=np.zeros((h,w-1,d),dtype=np.uint8)
    for i in range(h):
	row=img[i]
	row=np.concatenate((row[:seam[i]],row[seam[i]+1:]),axis=0)
	out[i]=row
    return out

def carveSeams(img,seams,show=False):
    
    h,w=img.shape[:2]
    seam_map=np.tile(np.arange(w),(h,1))
    for i in range(seams):
	seam=getSeam(img)
	seam_map=removeSeam(seam_map,seam)
	img=removeSeam(img,seam)
	if show:
	    funcs.show(img,False)
    return img,seam_map


img1=cv2.imread('image1_small.png')
seam1=getSeam(img1)
print "show seam"
img1=showSeam(img1,seam1)
funcs.show(img1)

img3=cv2.imread('image1_small.png')
img3=img3
print "carve seam"
img3,_=carveSeams(img3,400,True)
funcs.show(img3)
    
