import cv2
import funcs
import numpy as np

def drawMatches(img1, kp1, img2, kp2, matches):
    
    # Create a new output image that concatenates the two images together
    # (a.k.a) a montage
    rows1 = img1.shape[0]
    cols1 = img1.shape[1]
    rows2 = img2.shape[0]
    cols2 = img2.shape[1]

    out = np.zeros((max([rows1,rows2]),cols1+cols2,3), dtype='uint8')

    # Place the first image to the left
    out[:rows1,:cols1] = np.dstack([img1[:,:,0], img1[:,:,1], img1[:,:,2]])

    # Place the next image to the right of it
    out[:rows2,cols1:] = np.dstack([(img2[:,:,0]), (img2[:,:,1]), (img2[:,:,2])])

    # For each pair of points we have between both images
    # draw circles, then connect a line between them
    for mat in matches:

        # Get the matching keypoints for each of the images
        img1_idx = mat.queryIdx
        img2_idx = mat.trainIdx

        # x - columns
        # y - rows
        (x1,y1) = kp1[img1_idx].pt
        (x2,y2) = kp2[img2_idx].pt

        # Draw a small circle at both co-ordinates
        # radius 4
        # colour blue
        # thickness = 1
        cv2.circle(out, (int(x1),int(y1)), 4, (255, 0, 0), 1)   
        cv2.circle(out, (int(x2)+cols1,int(y2)), 4, (255, 0, 0), 1)

        # Draw a line in between the two points
        # thickness = 1
        # colour blue
        cv2.line(out, (int(x1),int(y1)), (int(x2)+cols1,int(y2)), (255, 0, 0), 1)

    # Also return the image if you'd like a copy
    return out

def draw_mathces(img1,img2):
    orb=cv2.ORB()
    
    img1=cv2.GaussianBlur(img1,(11,11),0,borderType=cv2.BORDER_REFLECT_101)
    img2=cv2.GaussianBlur(img2,(11,11),0,borderType=cv2.BORDER_REFLECT_101)
    
    kp1,des1=orb.detectAndCompute(img1, None)
    kp2,des2=orb.detectAndCompute(img2, None)

    bf=cv2.BFMatcher()
    matches=bf.knnMatch(des1,des2,k=2)

    good=[]
    for m,n in matches:
        if m.distance<0.7*n.distance:
            good.append(m)
        
    matches=good

    src_pts=[]
    dst_pts=[]

    for match in matches:
        src_pts.append(kp1[match.queryIdx].pt)
        dst_pts.append(kp2[match.trainIdx].pt)
        
    src_pts=np.array(src_pts)
    dst_pts=np.array(dst_pts)
    
    M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,10.0)
    h,w=img1.shape[:2]
    img1_transformed=cv2.warpPerspective(img1, M, (w,h))
    print "Calculated Matrix: "+str(M)    
    return drawMatches(img1,kp1,img2,kp2,matches),M

def getBoundingRectangle(w,h,M):
    p1 = M.dot(np.float32([[0], [0], [1]]))
    p2 = M.dot(np.float32([[w], [0], [1]]))
    p3 = M.dot(np.float32([[w], [h], [1]]))
    p4 = M.dot(np.float32([[0], [h], [1]]))
    print p1,p2,p3,p4
    return p1[0, 0] / p1[2, 0], p2[1, 0] / p2[2, 0], p3[0, 0] / p3[2, 0], p4[1, 0] / p4[2, 0]  # min x, min y, max x, max y


##Part 2##
pic1=cv2.imread('input/pic1.png')
pic2=cv2.imread('input/pic2.png')

pic_2_a_1=mark_features(pic1,blur=15)
cv2.imwrite('output/pic_2_a_1.png',pic_2_a_1)
pic_2_a_2=mark_features(pic2,blur=15)
cv2.imwrite('output/pic_2_a_2.png',pic_2_a_2)

pic_2_b,M2=draw_mathces(pic1,pic2)
cv2.imwrite('output/pic_2_b.png',pic_2_b)

h2,w2=pic2.shape[:2]
pic_2_d=cv2.warpPerspective(pic1,M2,(w2,h2))
cv2.imwrite('output/pic_2_d.png',pic_2_d)

pic_2_e=np.float32(funcs.grayscale(pic2))-funcs.grayscale(pic_2_d)
pic_2_e-=np.min(pic_2_e)
pic_2_e/=np.max(pic_2_e)
pic_2_e*=255.999999
pic_2_e=np.uint8(pic_2_e)
cv2.imwrite('output/pic_2_e.png',pic_2_e)     

minX,minY,maxX,maxY=getBoundingRectangle(w2, h2, M2)
M3=np.float32([[1, 0, -minX if minX < 0 else 0],
               [0, 1, -minY if minY < 0 else 0],
               [0, 0, 1]])
warp_dim_x = int((abs(minX) if minX < 0 else 0) + (abs(maxX) if maxX > w2 else w2))
warp_dim_y = int((abs(minY) if minY < 0 else 0) + (abs(maxY) if maxY > h2 else h2))
pic1_warp = cv2.warpPerspective(pic1, M3.dot(M2), (warp_dim_x, warp_dim_y))
pic2_warp = cv2.warpPerspective(pic2, M3, (warp_dim_x, warp_dim_y))
pic_2_f = np.maximum(pic1_warp, pic2_warp)
cv2.imwrite("output/pic_2_f.png",pic_2_f)
