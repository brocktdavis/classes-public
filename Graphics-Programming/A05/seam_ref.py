import cv2
import numpy as np
import funcs

def getSeam(img,blur=1,debug=False,attractor=np.zeroes(img.shape)):
    
    img_original=img.copy()
    img=img.copy()
    img=funcs.grayscale(img,True)
    img=np.float32(cv2.GaussianBlur(img,(blur,blur),0))
    derivative_kernel=np.float32([[-1,0,1],[-2,0,2],[-1,0,1]])
    Ix=cv2.filter2D(img, -1, derivative_kernel)
    Iy=cv2.filter2D(img, -1, derivative_kernel.T)
    path=Ix**2+Iy**2
    path=path**.5
    
    path+=(atrractor*100.0)
    
    if debug:
	pic_1_c_0=path.copy()
	funcs.save(pic_1_c_0,"output/pic_1_a.png")

    kernel=np.float32([[1,1,1]])
    h,w=img.shape[:2]
    for i in range(1,h):
	row=path[i-1,:]
	row=cv2.erode(row,kernel.T)
	path[i:i+1,:]+=row.T
    if debug:
	pic_1_c_1=path.copy()
	funcs.save(pic_1_c_1,"output/pic_1_b.png")
    x=np.argmin(path[-1])
    y=h-1
    seam=[]
    seam.append(x)
    while y>0:
	y-=1
	minX=seam[0]-1
	maxX=seam[0]+2
	x=np.argmin(path[y,max(minX,0):min(maxX,w-1)])+x
	if x!=0:
	    x-=1
	seam.insert(0,x)
    if debug:
	funcs.save(showSeam(pic_1_c_0,seam),"output/pic_1_c_1.png")
	funcs.save(showSeam(pic_1_c_1,seam),"output/pic_1_c_2.png")
	funcs.save(showSeam(img_original,seam),"output/pic_1_c_0.png")
    return seam
  
def showSeam(img,seam,color=(0,255,0)):
    
    img=funcs.normalize_pic(img)
    if len(img.shape)==2:
	out=np.dstack((img,img,img))
    else:
	out=img.copy()
    for i in range(img.shape[0]):
	out[i,seam[i]]=np.array(color)
    return out
    
def removeSeam(img,seam,attractor=np.zeroes(img.shape)):
    
    if len(img.shape)==2:
	h,w=img.shape
	out=np.zeros((h,w-1),dtype=np.uint8)
    elif len(img.shape)==3:
	h,w,d=img.shape
	out=np.zeros((h,w-1,d),dtype=np.uint8)
    for i in range(h):
	row=img[i]
	row=np.concatenate((row[:seam[i]],row[seam[i]+1:]),axis=0)
	out[i]=row
    return out

def carveSeams(img,seams,show=False):
    
    h,w=img.shape[:2]
    seam_map=np.tile(np.arange(w),(h,1))
    for i in range(seams):
	seam=getSeam(img)
	seam_map=removeSeam(seam_map,seam)
	img=removeSeam(img,seam)
	if show:
	    funcs.show(img,False)
    return img,seam_map

def carveHorizSeams(img,seams,show=False):
    img=funcs.trans_img(img)
    img,seam_map=carveSeams(img,seams,show)
    return funcs.trans_img(img),seam_map

def retarget(img,(w,h),show=False):
    
    img=img.copy()
    h_img,w_img=img.shape[:2]
    dh=h_img-h
    dw=w_img-w
    seam_map=np.tile(np.arange(w_img),(h_img,1))
    
    if dh<0 or dw<0:
	return img,seam_map
	
    for i in range(max(dh,dw)):
	if i<dw:
	    
	    seam=getSeam(img)
	    
	    img=removeSeam(img,seam)
	    
	    seam_map=removeSeam(seam_map,seam)
	    
	if i<dh:
	    
	    trans_img=funcs.trans_img(img)
	    trans_map=funcs.trans_img(seam_map)
	    
	    seam=getSeam(trans_img)
	    
	    trans_img=removeSeam(trans_img,seam)
	    trans_map=removeSeam(trans_map,seam)
	    
	    img=funcs.trans_img(trans_img)
	    seam_map=funcs.trans_img(trans_map)
    
    return img,seam_map

#Read Image
img1=cv2.imread("image1.png")

#pic_1_a,pic_1_b,pic_1_c_0,pic_1_c_1,pic_1_c_2
seam1=getSeam(img1,1,debug=True)

#pic_1_d
pic_1_d=removeSeam(img1,seam1)
funcs.save(pic_1_d,"output/pic_1_d.png")

#pic_1_e
pic_1_e,_=carveSeams(img1,120)
funcs.save(pic_1_e,"output/pic_1_e.png")   

#pic_2_a
trans_img=funcs.trans_img(img1)
pic_2_a=showSeam(trans_img,getSeam(trans_img))
pic_2_a=funcs.trans_img(pic_2_a)
funcs.save(pic_2_a,"output/pic_2_a.png")

#pic_2_b
pic_2_b,_=carveHorizSeams(img1,120)
funcs.save(pic_2_b,"output/pic_2_b.png")

#pic_3_a
pic_3_b_3,_=retarget(img1,(640,640))
pic_3_b_2,_=retarget(pic_3_b_3,(640,480))
pic_3_b_1,_=retarget(pic_3_b_2,(320,320))
pic_3_b_0,_=retarget(pic_3_b_1,(320,240))

funcs.save(pic_3_b_0,"output/pic_3_b_0.png")
funcs.save(pic_3_b_1,"output/pic_3_b_1.png")
funcs.save(pic_3_b_2,"output/pic_3_b_2.png")
funcs.save(pic_3_b_3,"output/pic_3_b_3.png")
