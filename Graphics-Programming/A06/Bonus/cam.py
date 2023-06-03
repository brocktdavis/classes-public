import cv2
import numpy as np
import random
from scipy import ndimage

"""Draw a sprite on an img on an x,y position"""
def draw_sprite(img,(x,y),sprite,colors=None):
    
    img=img.copy()
    i_h,i_w=img.shape[:2]
    s_h,s_w=sprite.shape[:2]
    
    #If img is grayscale
    if len(img.shape)==2:
        
        #Get alpha channel
        img_alpha=np.ones((i_h,i_w),dtype=np.uint8)*255
        #Put the img together to have BGRA img
        img=cv2.merge((img,img,img,img_alpha))
        
    #If img is color (BGR) w/ no alpha
    elif img.shape[2]==3:
        
        #Get alpha channel for img
        img_alpha=np.ones((i_h,i_w),dtype=np.uint8)*255
        
        #Put img together to have BGRA img
        img=cv2.cvtColor(img,cv2.COLOR_BGR2BGRA)

     #If sprite is grayscale
    if len(sprite.shape)==2:
        
        #Get alpha channel
        sprite_alpha=np.ones((s_h,s_w),dtype=np.uint8)*255
        
        #Put the img together to have BGRA img
        sprite=cv2.merge((sprite,sprite,sprite))
        
    #If sprite is color (BGR) w/ no alpha
    if sprite.shape[2]==3:
        
        #Get alpha channel for sprite
        sprite_alpha=np.ones((s_h,s_w),dtype=np.uint8)*255
        
        """Remove specified colors from img"""
        for color in colors:
            
            #Turns input into numpy array
            color=np.array(color)
            #Gets locs of where the trans_color is present
            mask=sprite==color
            #Only gets where it matches the color excatly
            mask=np.amin(mask,axis=2)
            #Makes the locations have 0 alpha value
            sprite_alpha[mask]=0
        
        #Put img together to have BGRA img
        sprite=cv2.merge((sprite[:,:,0],sprite[:,:,1],sprite[:,:,2],sprite_alpha)) 
    
    #Make copy of zero img
    out=np.zeros((s_h+i_h,s_w+i_w,4),dtype=np.uint8)
    
    #Copy orig img to blank
    out[:i_h,:i_w,:]=img
    
    #Replace out with sprite, with alpha
    #Loop through eac color
    for c in range(0,3):
        
        out[y:y+s_h,x:x+s_w,c]=sprite[:,:,c]*(sprite[:,:,3]/255.0)+out[y:y+s_h,x:x+s_w,c]*(1.0-sprite[:,:,3]/255.0)
    
    #Return output image the size of the original img
    out=out[:i_h,:i_w,:]
    return out

#Get eyes img
eyes=cv2.imread("ghosty.png",cv2.IMREAD_UNCHANGED)
eyes[:,:,3]/=2


cv2.namedWindow("webcam", cv2.WND_PROP_FULLSCREEN)          
cv2.setWindowProperty("webcam", cv2.WND_PROP_FULLSCREEN,1)

#Set up webcam
cap = cv2.VideoCapture(0)

#Doesn't work without this
cap.read()

#Gets first frame
ret,frame=cap.read()

#Most recent frame is only the gray
lastFrame=cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
offset_x,offset_y=0,0

while True:
    
    #Read in frame
    ret, frame = cap.read()
    
    #convert to gray and detect lines
    gray=cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
    output=np.uint8(np.abs(np.int16(lastFrame)-gray))
    lastFrame=gray
    
    #Find center of movement in pic
    output=np.uint8((output>50)*255.0)
    y,x=ndimage.measurements.center_of_mass(output)
    
    #Current loc is based on past locations
    offset_x=int(offset_x*.95+x*0.05)
    offset_y=int(offset_y*.95+y*0.05)
    
    #try:
        #Draw ghost on center of movement
    frame=draw_sprite(frame,(offset_x,offset_y),eyes,[(255,255,255)])
    #except:
    #    pass
        
    cv2.imshow("webcam",frame)
    key=cv2.waitKey(1)
    if key==27:
        break

cap.release()
cv2.destroyAllWindows()

