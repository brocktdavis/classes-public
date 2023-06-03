import funcs
import numpy as np
import cv2

def getMatrix(img1,img2,blur=9):
    
    img1=img1.copy()
    img2=img2.copy()
    
    #Makes ORB: A keypoint detector
    orb=cv2.ORB()
    
    #Blurs to detect more prominent features
    img1=cv2.GaussianBlur(img1,(blur,blur),0,borderType=cv2.BORDER_REFLECT_101)
    img2=cv2.GaussianBlur(img2,(blur,blur),0,borderType=cv2.BORDER_REFLECT_101)
    
    #Get keypoints and descriptions of each
    kp1,des1=orb.detectAndCompute(img1, None)
    kp2,des2=orb.detectAndCompute(img2, None)

    #Match two sets of keypoints together
    bf=cv2.BFMatcher()
    matches=bf.knnMatch(des1,des2,k=2)

    good=[]
    for m,n in matches:
	#Change this  vvv value to get better or worse matches, comparatively
        if m.distance<0.7*n.distance:
            good.append(m)
        
    matches=good
    
    src_pts=[]
    dst_pts=[]

    #Put matches in respective lists
    for match in matches:
        src_pts.append(kp1[match.queryIdx].pt)
        dst_pts.append(kp2[match.trainIdx].pt)
        
    #Turn lists into numpy arrays to be able to findHomography
    src_pts=np.array(src_pts)
    dst_pts=np.array(dst_pts)
    
    #Find M between points
    M,mask=cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,10.0)
    
    return M

def getMatricies(imgs,blur):
    
    matricies=[]
    
    for i in range(len(imgs)):
	if i>0:
	    M=getMatrix(imgs[i-1],imgs[i],blur)
	    #print "got here"
	    matricies.append(M)
    return matricies

def getLists(num,blur):
    
    if blur<1:
        print "Non valid blur"
        raise
    
    try:
	imgs=[]

	#read in all images
	for i in range(num):
	    imgs.append(cv2.imread("input/image%i.png"%(i+1)))
	    
	matricies=getMatricies(imgs,blur)
	
	return imgs,matricies
	
    except:
	print "Trying with %i blur instead"%(blur-2)
	return getLists(num,blur-2)

def combineImgs(img1,img2,M,count):
    
    h,w=img1.shape[:2]
    
    corners=np.float32([[[0,0],[w,0],[0,h],[w,h]]])
    corners=cv2.perspectiveTransform(corners,M)
    corners=corners[0]
    minX=min(corners[:,0])
    maxX=max(corners[:,0])
    minY=min(corners[:,1])
    maxY=max(corners[:,1])
    
    M2=np.float32([[1, 0,-minX if minX<0 else 0],
		   [0, 1,-minY if minY<0 else 0],
		   [0, 0, 1]])
		   
    warp_dim_x=int(abs(minX) + (abs(maxX) if maxX > w else w))
    warp_dim_y=int(abs(minY) + (abs(maxY) if maxY > h else h))
    
    img1_warp=cv2.warpPerspective(img1,M2.dot(M),(warp_dim_x, warp_dim_y))
    img2_warp=cv2.warpPerspective(img2,M2,(warp_dim_x, warp_dim_y))
    mask=np.float32(img1_warp>0)
    kernel=np.float32([[1,1,1]])
    mask=cv2.erode(mask,kernel)
    out=mask*img1_warp+(1-mask)*img2_warp
    out=funcs.normalize_pic(out)
    
    funcs.save(out,('output/prog_%d.png'%count))
    out=autoCrop(out)
    return out

def cutSide(img):
    
    img=img.copy()
    while True:
	if np.count_nonzero(img[:,-1])==0:
	    img=img[:,:-1]
	else:
	    while True:
		if np.count_nonzero(img[:,0])==0:
		    img=img[:,1:]
		else:
		    return img

def autoCrop(img):
    
    img=cutSide(img)
    h,w=img.shape[:2]
    cutoff=w*3*.9
    
    while True:
	if np.count_nonzero(img[0])<cutoff:
	    img=img[1:]
	else:
	    while True:
		if np.count_nonzero(img[-1])<cutoff:
		    img=img[:-1]
		else:
		    return img

num=int(raw_input("# of images: "))
blur=int(raw_input("blur: "))


imgs,matricies=getLists(num,blur)

count=0
while len(imgs)>1:
    new_imgs=[]
    length=len(imgs)
    print length
    for i in range(0,length,2):
	if i==length-1:
	    new_imgs.append(imgs[i])
	else:
	    new_img=combineImgs(imgs[i],imgs[i+1],matricies[i],count)
	    count=count+1
	    new_imgs.append(new_img)
	
    
    imgs=list(new_imgs)
    matricies=getMatricies(imgs,blur)

out=imgs[0]
out=autoCrop(out)
funcs.save(imgs[0],"output/pic1.png")
