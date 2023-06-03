import cv2
import numpy as np
import funcs

cv2.namedWindow("webcam",cv2.WND_PROP_FULLSCREEN)
cv2.setWindowProperty("webcam",cv2.WND_PROP_FULLSCREEN,1)

cap=cv2.VideoCapture(0)

ret,frame=cap.read()
lastFrame=funcs.grayscale(frame)


"""
while True:
    ret,frame=cap.read()
    frame=funcs.grayscale(frame)
    output=np.uint8(np.abs(lastFrame*1.0-frame))
    lastFrame=frame
    
    output=np.uint8((output>50)*255.0)
    cv2.imshow("webcam",output)
    key=cv2.waitKey(1)
    if key==27:
	break

cap.release()
cv2.destroyAllWindows()
"""
