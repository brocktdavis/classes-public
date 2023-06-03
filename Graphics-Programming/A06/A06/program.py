import numpy as np
import cv2
from scipy import ndimage
import time


#Set up window
#cv2.namedWindow("webcam", cv2.WINDOW_AUTOSIZE)
cv2.namedWindow("webcam", cv2.WND_PROP_FULLSCREEN)
cv2.setWindowProperty("webcam",cv2.WND_PROP_FULLSCREEN,1)

#Set up webcam
cap = cv2.VideoCapture(0)

#Doesn't work without this
cap.read()

#Get height/width of image and get grid
ret,frame=cap.read()
h,w=frame.shape[:2]
y,x=np.mgrid[0:h,0:w]
x=np.float32(x)
y=np.float32(y)

#Enumerate variables
line_0="0: Normal  S: Save  Esc: Exit"
line_1="1: Ghost  2: Invert  3: Strobe  4: Threshold"
line_2="5: Outline  6: Kaleidoscope  7: Wave  8: Shuffle"
key=ord("0")
prev_key=ord("0")
save=False

#Get time
last_time=time.time()

#Get stuff for save function
seed=int(last_time%100000000)
saves=[]

"""Set up for functions"""

#Make array of last 30 frames so that you can have delay
last30=[]
last30.append(frame)

#Kaleidascope Setup
polar=np.zeros((h,w,2),dtype=np.float32)
x_dist=np.abs(x-np.ones((h,w))*w/2)
y_dist=np.abs(y-np.ones((h,w))*h/2)
polar[:,:,0]=(x_dist**2+y_dist**2)**.5
polar[:,:,1]=np.minimum(np.arctan2(y_dist,x_dist),np.arctan2(x_dist,y_dist))
new_rect=np.ones((h,w))
x_coord=polar[:,:,0]*np.cos(polar[:,:,1])
y_coord=polar[:,:,0]*np.sin(polar[:,:,1])

#Shuffle Setup
x_shuff=x.copy()
y_shuff=y.copy()
x_shuff[:h/2,:]=(x_shuff[:h/2,:]+(w/2))%w
y_shuff[:,:w/2]=(y_shuff[:,:w/2]+(h/2))%h
x_shuff[h/2:,:]=(x_shuff[h/2:,:]-(w/2))%w
y_shuff[:,w/2:]=(y_shuff[:,w/2:]-(h/2))%h

#Wave Setup
x_wave=(x+3*np.sin(2*np.pi*(x)/(w/8)))
y_wave=(y+5*np.sin(2*np.pi*(x)/(h/4)))

#Increment
i=0
while True:
    
    #Get time benchmark data
    #Used for debug
    current_time=time.time()
    diff_time=current_time-last_time
    fps=1/diff_time
    
    #Read in frame
    ret, frame = cap.read()
    
    #Manage list of last frames
    last30.append(frame)
    if len(last30)>30:
        last30.pop(0)
    
    #Get user input key
    get=cv2.waitKey(1)
    if get!=-1:
        key=get
        
    #Exit
    if key==27:
        break
        
    #Make it so that it will save after changing the frame
    if key==ord("s") or key==ord("S"):
        save=True
        key=prev_key    
    
    #Normal
    elif key==ord("0"):
        out=frame.copy()
        
    #Ghost
    elif key==ord("1"):
        #Middle half of current img and 30 frames ago
        current=frame[:,w/4:3*w/4,:]
        ghost=last30[0][:,w/4:3*w/4,:]
        out=np.hstack((current,ghost))
        
    #Negative
    elif key==ord("2"):
        out=255-frame
        
    #Strobe
    elif key==ord("3"):
        
        if (i%6)/2==0:
            out=np.dstack((255-frame[:,:,0],frame[:,:,1],frame[:,:,2]))
        elif (i%6)/2==1:
            out=np.dstack((frame[:,:,0],255-frame[:,:,1],frame[:,:,2]))
        else:
            out=np.dstack((frame[:,:,0],frame[:,:,1],255-frame[:,:,2]))
    
    #Threshold
    if key==ord("4"):
        threshold=abs(256-(i%511))
        out=frame.copy()
        out[out<=threshold]=0
        out[out>threshold]=255
        
    #Outline
    elif key==ord("5"):
        frame=np.float32(cv2.GaussianBlur(frame,(5,5),0))
        derivative_kernel=np.float32([[-1,0,1],[-2,0,2],[-1,0,1]])
        Ix=cv2.filter2D(frame, -1, derivative_kernel)
        Iy=cv2.filter2D(frame, -1, derivative_kernel.T)
        out=(Ix**2+Iy**2)**.5
        out-=np.min(out)
        out/=np.max(out)
        out*=255.999999
        out=np.uint8(out)
    
    #Kaleidoscope
    elif key==ord("6"):
        out=cv2.remap(frame,x_coord,y_coord,cv2.INTER_CUBIC)
    
    #Wave
    elif key==ord("7"):
        out=cv2.remap(frame,x_wave,y_wave,cv2.INTER_CUBIC)
    
    #Shuffle
    elif key==ord("8"):
        out=cv2.remap(frame,x_shuff,y_shuff,cv2.INTER_CUBIC)
    
    #Save
    if save:
        saves.append(out)
        out=np.ones((h,w))*255
        save=False
    
    #Draw filters on image
    cv2.putText(out, line_0, (0,h-55),0, 0.8, (255,255,255),2)
    cv2.putText(out, line_1, (0,h-30),0, 0.8, (255,255,255),2)
    cv2.putText(out, line_2, (0,h-5),0, 0.8, (255,255,255),2)
    
    #Show frame
    cv2.imshow("webcam",out)
    
    i+=1
    prev_key=key
    last_time=current_time

for i in range(len(saves)):
    cv2.imwrite("%i_out_%i.png"%(seed,i),saves[i])
    
cap.release()
cv2.destroyAllWindows()
