import cv2
import numpy as np
from random import randint

in_img=cv2.imread('in.png')

margin=7630

out_img=in_img[7630-margin:,:]
cv2.imwrite('test2.png',out_img)


"""
img=np.zeros((100,100),dtype=np.uint8)

for col in range(100):
	for row in range(100):
		img[col,row]=randint(0,255)
		
cv2.imwrite('test.png',img)
"""
