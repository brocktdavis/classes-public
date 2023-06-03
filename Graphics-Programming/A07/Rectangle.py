
class Rectangle:
    def __init__(self,w,h):
	self.w=w
	self.h=h
	
    def area(self):
	return self.w*self.h
	
    def __str__(self):
	return "h:%f w:%f"%(self.h,self.w)
    
    def __add__(self,rect):
	return Rectangle(self.w+rect.w,self.h_rect.h)
	
	
rect=Rectangle(5,6)
print rect.area()
print rect
