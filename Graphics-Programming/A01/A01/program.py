import cv2
import numpy as np

#returns new union jack flag
def get_flag(size):
    
    #Colors
    red=(43,20,207)
    white=(255,255,255)
    blue=(125,36,0)
    
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
            if x<=width/2 and y>=-.5*x+height and y<=-.5*x+(height+(y_int*2)):
                img[y,x]=red
                
            #Bottom Right Red
            if x>width/2 and y>.5*x-(y_int*2) and y<.5*x:
                img[y,x]=red
                
            #Top Right Red
            if x>width/2 and y>=-.5*x+(height-(y_int*2)) and y<-.5*x+height:
                img[y,x]=red
    
    
    #White Cross
    img[size/3:-size/3,:,:]=white
    img[:,size*5/6:-size*5/6,:]=white
    
    #Red Cross
    img[size*2/5:-size*2/5,:,:]=red
    img[:,size*9/10:-size*9/10,:]=red
    
    #Return
    return img

#gets values between 0 and 255 from a pic
def normalize_pic(pic):
	
	#Makes all elements positive
	min_val=np.min(pic)
	pic-=min_val
	
	#Scales each element to be between 0 and almost 256
	max_val=np.max(pic)
	pic*=255/max_val
	
	pic=np.uint8(pic)

"""Pt0: Read input images"""
image1=cv2.imread('input/image1.png')
image2=cv2.imread('input/image2.png')
real_flag=cv2.imread('input/flag.png')


"""Pt1: Colors"""
#Switch R and G or image1
red=image1[:,:,2]
green=image1[:,:,1]
pic_1_a=image1.copy()
pic_1_a[:,:,1],pic_1_a[:,:,2]=red,green
cv2.imwrite('output/pic_1_a.png',pic_1_a)

#image2 B monochrome
pic_1_b=image2[:,:,0]
cv2.imwrite('output/pic_1_b.png',pic_1_b)

#invert G image1
pic_1_c=image1.copy()
pic_1_c[:,:,1]=255-pic_1_c[:,:,1]
cv2.imwrite('output/pic_1_c.png',pic_1_c)

#Add 100 to val
pic_1_d=image2.copy()
pic_1_d+=100
pic_1_d[pic_1_d<100]=255
cv2.imwrite('output/pic_1_d.png',pic_1_d)


"""Pt2: Copy & Paste"""
#Replace center window G w/ 255
pic_2_a=image1.copy()
window_width=pic_2_a.shape[1]/2-50
window_height=pic_2_a.shape[0]/2-50
window=pic_2_a[window_height:-window_height,window_width:-window_width,:]
window[:,:,1]=255
cv2.imwrite('output/pic_2_a.png',pic_2_a)

#Paste center image1 on center image2
window_image=image1.copy()
window_2=window_image[window_height:-window_height,window_width:-window_width,:].copy()
pic_2_b=image2.copy()
window_width_2=pic_2_b.shape[1]/2-50
window_height_2=pic_2_b.shape[0]/2-50
pic_2_b[window_height_2:-window_height_2,window_width_2:-window_width_2,:]=window_2
cv2.imwrite('output/pic_2_b.png',pic_2_b)

"""Pt4: Flag"""
#Make flag and save it
my_flag=get_flag(300)
cv2.imwrite('output/pic_4_a.png',my_flag)

#Get difference between flags and normalize
diff_flag=real_flag-my_flag
normalize_pic(diff_flag)
cv2.imwrite('output/pic_4_b.png',diff_flag)
