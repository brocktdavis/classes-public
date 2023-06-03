import cv2
import numpy as np

def show(img,wait=True):
    if img.dtype==np.uint8: 
        cv2.imshow('image',img)
    elif np.max(img)<=1 and np.min(img)>=0:
        img=np.uint8(img*255)
        cv2.imshow('image',img)
    elif np.max(img)<=255 and np.min(img)>=0:
        img=np.uint8(img)
        cv2.imshow('image',img)
    else:
        img=img*1.0
        img-=np.min(img)
        img/=np.max(img)
        img*=255.999999
        img=np.uint8(img)
        cv2.imshow('image',img)
    if wait:
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        cv2.waitKey(1)


def drawStar(img,(x,y),r,n=5,rotation=0,point_angle=36,color=(0,0,0)):
    h,w=img.shape[:2]
    for j in range(max(0,y-r),min(h,y+r)):
        for i in range(max(0,x-r),min(w,x+r)):
            #make sure point is inside a bounding circle
            if (x-i)**2+(y-j)**2<=r*r:
                #get angle of the given point
                a=math.degrees(math.atan2(j-y,i-x))
                #adjust so that a point is always pointing up
                a+=90
                #rotate to suit user
                a-=rotation
                #rotate by half a tine to help the the point orientation
                a+=180.0/n
                #bound answers to 0-360
                a%=360
                #figure out where you would in any given tine.
                #some number between 0 and 360/n
                a=a%(360.0/n)
                #find distance from center to point
                rp=math.sqrt((x-i)**2+(y-j)**2)
                #find new point with the modified angle
                xp=rp*math.cos(math.radians(a-180.0/n))
                yp=rp*math.sin(math.radians(a-180.0/n))
                #find the angle of the point with the tip of star
                a2=math.degrees(math.atan2(-yp,r-xp))%360
                #anything in the point_angle range needs to be colored
                if a2<point_angle/2 or a2>360-point_angle/2:
                    img[j,i]=color

def getWidth(img):
    return img.shape[1]

def getHeight(img):
    return img.shape[0]

def fillCircle(img,(x,y),r,color):
    r2=r*r
    h,w=img.shape[:2]
    for row in range(max(0,y-r),min(y+r,h)):   
        for col in range(max(0,x-r),min(x+r,w)):
            d2=(col-w/2)**2+(row-h/2)**2
            if d2<r2:
                img[row,col]=color
    return img
