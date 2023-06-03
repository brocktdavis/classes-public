import cv2
import numpy as np

def make_flag(size):
    
    #Colors
    red=(48,12,198)
    white=(255,255,255)
    blue=(118,39,0)
    
    #Array
    height=size
    width=height*2
    y_int=(size/30.0)/(np.cos(np.arctan(.5)))
    array_size=(height,width,3)
    img=np.zeros(array_size,dtype=np.uint8)
    
    #Background
    img[:,:,:]=blue
    
    #Diagonals
    for y in xrange(height):
        
        #Debug
        if y%100==0:
            print y
            
        for x in xrange(width):
            
            #White Diagonals
            if y>.5*x-(y_int*3) and y<.5*x+(y_int*3):
                img[y,x]=white
            if y>-.5*x+(height-(y_int*3)) and y<-.5*x+(height+(y_int*3)):
                img[y,x]=white
                
            #Top Left Red
            if x<width/2 and y>.5*x and y<.5*x+(y_int*2):
                img[y,x]=red
                
            #Bottom Left Red
            if x<width/2 and y>-.5*x+height and y<-.5*x+(height+(y_int*2)):
                img[y,x]=red
                
            #Bottom Right Red
            if x>width/2 and y>.5*x-(y_int*2) and y<.5*x:
                img[y,x]=red
                
            #Top Right Red
            if x>width/2 and y>-.5*x+(height-(y_int*2)) and y<-.5*x+height:
                img[y,x]=red
    
    
    #White Cross
    img[size/3:-size/3,:,:]=white
    img[:,size*5/6:-size*5/6,:]=white
    
    #Red Cross
    img[size*2/5:-size*2/5,:,:]=red
    img[:,size*9/10:-size*9/10,:]=red

    #Write
    cv2.imwrite('output.png',img)

make_flag(900)