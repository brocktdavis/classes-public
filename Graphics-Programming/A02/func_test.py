import cv2
import numpy as np
import funcs


img1=cv2.imread('image1.png')
img2=cv2.imread('image2.png')
img3=cv2.imread('image3.png')

def test_funcs(img1,img2):
    img3=img2
    
    funcs.show(img1)
    funcs.save(img1,filename='out_1.png')

    #funcs.getWidth funcs.getHeight
    print funcs.getWidth(img1),funcs.getHeight(img1)

    #funcs.mix
    mixed_pics=funcs.mix(img1,img3,.5)
    funcs.show(mixed_pics)

    #funcs.tint
    tinted_pic=funcs.tint(img1,(0,0,255),.5)
    funcs.show(tinted_pic)

    #funcs.grayscale
    gray_pic=funcs.grayscale(img1)
    funcs.show(gray_pic)
    gray_pic_2=funcs.grayscale(img1,two_dimensional=True)
    funcs.show(gray_pic_2)

    #funcs.rotate
    img_rotate=funcs.rotate(img1,180)
    funcs.show(img_rotate)


def mix13(x):
    cv2.imshow('image',funcs.mix(img1,img3,x/100.0))

cv2.namedWindow('image')
cv2.createTrackbar('Scale','image',0,100,mix13)
cv2.imshow('image',img1)
cv2.waitKey(0)

def tint1(x):
    cv2.imshow('image',funcs.tint(img1,(0,128,255),x/100.0))

cv2.namedWindow('image')
cv2.createTrackbar('Scale','image',0,100,tint1)
cv2.imshow('image',img1)
cv2.waitKey(0)

def rotate1(x):
    cv2.imshow('image',funcs.rotate(img1,x))

cv2.namedWindow('image')
cv2.createTrackbar('Scale','image',0,360,rotate1)
cv2.imshow('image',img1)
cv2.waitKey(0)
