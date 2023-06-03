import struct,random

class Rectangle:
    def __init__(self,w,h):
        self.w=w
        self.h=h
    def area(self):
        return self.w*self.h
    def __str__(self):
        return "rectangle: width=%f height=%f"%(self.w,self.h)
    def __add__(self,rect):
        return Rectangle(self.w+rect.w,self.h+rect.h)
    

class BitFile:
    def __init__(self,filename,rights):
        self.f=file(filename,rights)
        self.buffer=0
        self.bitInBuffer=0
    def write(self,num,numOfBits):
        
        for i in range(numOfBits):
            bit=num%2
            num/=2
            self.buffer*=2
            self.buffer+=bit
            self.bitInBuffer+=1
            self._houseKeeping()
            
    def read(self,numOfBits):
        while(self.bitInBuffer-numOfBits<0):
            self.bitInBuffer+=8
            self.buffer*=256
            data=self.f.read(1)
            #print repr(data)
            self.buffer+=struct.unpack("B",data)[0]
        num=0
        for i in range(numOfBits):
            q=2**(self.bitInBuffer-1)
            #print q,self.buffer
            bit=1 if q<=self.buffer else 0
            if bit==1:
                self.buffer-=q
            self.bitInBuffer-=1
            num+=bit*(2**i)
        return num
    
    def _houseKeeping(self):
      
        if self.bitInBuffer==8:
            self.f.write(struct.pack("B",self.buffer))
            self.buffer=0
            self.bitInBuffer=0
            
            
    def close(self):
        try:
            if self.bitInBuffer:
                self.f.write(struct.pack("B",self.buffer*(2**(8-self.bitInBuffer))))
        except:
            pass
        self.f.close()
    


if __name__ == "__main__":

    file1=BitFile('test.brc','wb')
    file1.write(165918, 19)
    file1.write(12,8)
    file1.close()

    file2=BitFile('test.brc','rb')
    print file2.read(17)
    print file2.read(8)
    file2.close()
