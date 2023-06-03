import numpy as np
import cv2
import funcs

def greyscale(img):
    img=img.copy()
    img=img[:,:,0]*.1+img[:,:,1]*.7+img[:,:,2]*.2
    img=img[:,:,None]
    img=np.uint8(img)
    return img
    
"""0<percent<1#s
   Must be same size"""
def mix(img1,img2,percent):
    
    p=1.0*percent
    img=img2*p+img1*(1-p)
    return np.uint8(img)
    
def blackWhite(img,threshold=128):
    img=img.copy()
    img=greyscale(img)
    img[img<=threshold]=0
    img[img>threshold]=255
    return img
    
def desaturate(img,percent=.5):
    img=img.copy()
    gray_img=greyscale(img)
    img=mix(img,gray_img,percent)
    return img
    
def contrast(img,factor=1):

    img=img*1.0

    #Get dist from 128
    dist_img=img-128.0

    #Multiply dist by factor
    dist_img*=factor

    #Cap Distance
    dist_img[dist_img<-128]=-128
    dist_img[dist_img>127]=127

    #Add distance to blank 128 canvas
    blank=np.ones(img.shape)*128.0
    img=blank+dist_img
    img=np.uint8(img)
    return img

img1=cv2.imread('input/image1.png')
img2=cv2.imread('input/image2.png')
img3=cv2.imread('input/image3.png')

#greyscale
pic_1_a=greyscale(img1)
cv2.imwrite('output/pic_1_a.png',pic_1_a)

#threshold
pic_1_b=blackWhite(img1)
cv2.imwrite('output/pic_1_b.png',pic_1_b)

#desat
for i in range(0,11):
    percent=i/10.0
    desat_img=desaturate(img1,percent)
    cv2.imwrite('output/pic_1_c_%i.png'%i,desat_img)

#contrast
for i in range(0,11):
    factor=.5+(i/10.0)
    contrast_img=contrast(img1,factor)
    cv2.imwrite('output/pic_1_d_%i.png'%i,contrast_img)
    
#y-axis mirror
h,w=img2.shape[:2]
M=np.array([[-1,0,w],
            [0,1,0],
            [0,0,1]],dtype=np.float32)
pic_2_a=cv2.warpPerspective(img2,M,(w,h))
cv2.imwrite('output/pic_2_a.png',pic_2_a)

#30deg about lower-right corner
h,w=img2.shape[:2]
theta=np.radians(-30)
M1=np.array([[1,0,-w],
            [0,1,-h],
            [0,0,1]],dtype=np.float32)
M2=np.array([[np.cos(theta),np.sin(theta),0],
            [-np.sin(theta),np.cos(theta),0],
            [0,0,1]],dtype=np.float32)
M3=np.array([[1,0,w],
            [0,1,h],
            [0,0,1]],dtype=np.float32)
M=M3.dot(M2).dot(M1)
pic_2_b=cv2.warpPerspective(img2,M,(w,h))
cv2.imwrite('output/pic_2_b.png',pic_2_b)

#Map to cube
dst_img=img3.copy()

#Points
h,w=img1.shape[:2]
orig_tri=np.array([[0,0],[w,0],[w,h],[0,h]],dtype=np.float32)
tri1_prime=np.array([[1,19],[171,84],[180,560],[21,380]],dtype=np.float32)
tri2_prime=np.array([[173,84],[598,50],[563,464],[182,561]],dtype=np.float32)

#Get matracies
M1=cv2.getPerspectiveTransform(orig_tri,tri1_prime)
M2=cv2.getPerspectiveTransform(orig_tri,tri2_prime)

#Translate two pics
img1_perspective=cv2.warpPerspective(img1,M1,(w,h+100))
img2_perspective=cv2.warpPerspective(img2,M2,(w,h+100))

#Replace points
hp,wp=img3.shape[:2]
for i in range(hp):
    for j in range(wp):
        try:
            if img1_perspective[i,j,0]!=0:
                dst_img[i,j,:]=img1_perspective[i,j,:]
            if img2_perspective[i,j,0]!=0:
                dst_img[i,j,:]=img2_perspective[i,j,:]
        except:
            None

cv2.imwrite('output/pic_2_c.png',dst_img)


