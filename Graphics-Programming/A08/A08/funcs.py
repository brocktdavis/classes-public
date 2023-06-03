import cv2
import numpy as np

def show(img,wait=True):
    img=normalize_pic(img)
    cv2.imshow('image',img)
    if wait:
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        cv2.waitKey(1)

def save(img,filename='out'):
    img=normalize_pic(img)
    cv2.imwrite(filename,img)

def normalize_pic(img):
    if img.dtype==np.uint8: 
        return img
    elif np.max(img)<=1 and np.min(img)>=0:
        img=np.uint8(img*255)
        return img
    elif np.max(img)<=255 and np.min(img)>=0:
        img=np.uint8(img)
        return img
    else:
        img=img*1.0
        img-=np.min(img)
        img/=np.max(img)
        img*=255.99999
        img=np.uint8(img)
        return img

def trans_img(img):
    img=img.copy()
    if len(img.shape)==3:
        return np.dstack((img[:,:,0].T,img[:,:,1].T,img[:,:,2].T))
    else:
        return img.T

"""0<percent<1#s
   Must be same size"""
def mix(img1,img2,percent):
    
    p=1.0*percent
    img=img2*p+img1*(1-p)
    return np.uint8(img)

"""0<percent<1"""
def tint(img,color,percent):
    return mix(img,np.array(color),percent)

def grayscale(img,two_dimensional=False):
    img=img.copy()
    img=img[:,:,0]*.1+img[:,:,1]*.7+img[:,:,2]*.2
    
    if two_dimensional:
        img=img[:,:,None]

    img=np.uint8(img)
    return img

def rotate(img,degrees,resize_window=True):
    img=img.copy()
    angle=np.deg2rad(degrees)
    h,w=img.shape[:2]
    M=np.float64([[np.cos(angle),-np.sin(angle),0],
                 [np.sin(angle),np.cos(angle),0],
                 [0,0,1]])
    
    if resize_window:
        corners=np.float64([[0,0,1],[w,0,1],[w,h,1],[0,h,1]])
        for i in range(corners.shape[0]):
            corners[i]=M.dot(corners[i])
            
        tx,ty,_=np.amin(corners,axis=0)
        w,h,_=np.amax(corners,axis=0)-np.amin(corners,axis=0)
        M[0,2]=-tx
        M[1,2]=-ty
        w,h=int(w),int(h)
    
    img=cv2.warpPerspective(img,M,(w,h))
    
    return img
   

