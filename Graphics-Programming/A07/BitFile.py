import numpy as np
import struct

#HUFFMAN ENCODDING

class BitFile:
    
    #Vars
    #f - file
    #buffer - up to 1 byte of info to be written to file
    #bitsInBuffer - size of buffer in bits up to 8 bits
    
    def __init__(self,filename,rights):
	
	self.f=file(filename,rights)
	self.buffer=0
	self.bitsInBuffer=0
	
    def write(self,num,numOfBits):
	
	for i in range(numOfBits):
	    bit=num%2
	    num/=2
	    self.buffer*=2
	    self.buffer+=bit
	    self.bitsInBuffer+=1
	    self._houseKeeping()
	
    def _houseKeeping(self):
	
	if self.bitsInBuffer==8:
	   self.f.write(struct.pack("B",self.buffer))
	   self.buffer=0
	   self.bitsInBuffer=0 
	
    def close(self):
	try:
	    if self.bitsInBuffer:
		self.f.write(struct.pack("B",self.buffer*(2**(8-self.bitsInBuffer))))
	except:
	    pass
	self.f.close()

    def read(self,numOfBits):
	
	"""Can be calculated"""
	while(self.bitsInBuffer-numOfBits<0):
	    self.bitsInBuffer+=8
	    self.buffer*=256
	    self.buffer+=struct.unpack("B",self.f.read(1))[0]
	
	#0111010100
	num=0
	for i in range(numOfBits):
	    q=2**(self.bitsInBuffer-1)
	    bit=1 if q>=self.buffer else 0
	    if bit==1:
		self.buffer-=q
	    self.bitsInBuffer-=1
	    num+=bit*(2**i)
	    
	return num


f=BitFile("test.output","wb")
f.write(7,3)
f.write(0,4)
f.write(4,4)
f.close()

f2=BitFile("test.output","rb")
print f2.read(3)
print f2.read(4)
print f2.read(4)
f2.close()

