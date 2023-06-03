import numpy as np
import cv2
import funcs

"""Make it work on 3D images"""
def cross_correlate(img,kernel_size=3):
    #Initialize I1 and I2
    img=np.float32(img)
    kernel=np.ones((kernel_size,kernel_size),dtype=np.float32)/(kernel_size**2)
    
    #Add border
    hk,wk=kernel.shape
    img_border=cv2.copyMakeBorder(img,hk/2,hk/2,wk/2,wk/2,cv2.BORDER_REFLECT_101)
    
    if len(img.shape)==3:
	output=img.copy()
	for i in range(3):
	    output[:,:,i]=cv2.matchTemplate(img_border[:,:,i],kernel,method=cv2.TM_CCORR)
    else:
	output=cv2.matchTemplate(img_border,kernel,method=cv2.TM_CCORR)
	
    return output

img1=cv2.imread('image1.png',0)
img2=cv2.imread('image2.png',0)

orb=cv2.ORB()

kp1,des1=orb.detectAndCompute(img1,None)
kp2,des2=orb.detectAndCompute(img2,None)

bf=cv2.BFMatcher()
#matches=bf.match(des1,des2)
matches=bf.knnMatch(des1,des2,k=2)

good=[]
for m,n in matches:
    if m.distacnes<.75*n.distance:
	good.append(m)
	
matches=good
matches=sorted(matches,key=lambda x: x.distance)

src_pts=[]
dst_pts=[]
for match in matches:
    src_pts.append(kp1[match.queryIdx].pt)
    dst_pts.append(kp2[match.trainIdx].pt)
src_pts=np.array(src_pts)
dst_pts=np.array(dst_pts)

M,mask=cv2.findHomography(src_pts,dst_pts,cv2.RANSAC,5.0)
img1_transformed=cv2.warpPrespective(img1,M,img1.shape[::-1])

"""
img=cv2.imread('image1.png',0)
#funcs.show(img)
#img=cross_correlate(img,51)
#funcs.show(img)
img=cv2.GaussianBlur(img,(5,5),0,borderType=cv2.BORDER_REFLECT_101)
kernel=np.array([[0,0,0],[1,0,-1],[0,0,0]])
Ix=cv2.filter2D(img,-1,kernel,borderType=cv2.BORDER_REFLECT_101)
Iy=cv2.filter2D(img,-1,kernel.T,borderType=cv2.BORDER_REFLECT_101)

thing=cv2.cornerHarris(np.uint8(img),2,3,0.04)
""""""Dilate makes local maxes stick out""""""
thing_dilate=cv2.dilate(thing,np.ones((15,15)))
corners=thing_dilate==thing
corners[thing<.1*thing.max()]=0
funcs.show(thing)
funcs.show(thing_dilate)
funcs.show(corners)
xs,ys=corners
"""
