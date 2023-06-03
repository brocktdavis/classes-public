import struct

class BitWriter:
    
    """VARS"""
    #f - file
    #buffer - bits of info to be written to file
    #bitsInBuffer - size of buffer in bits
    
    """Creates file that writes binary"""
    def __init__(self, filename):
	
	self.f=file(filename,'wb')
	self.buffer=0
	self.bitsInBuffer=0
    
    """   """
    def write(self, num, numOfBits):
	
	for i in range(numOfBits):
            bit=num%2
            num/=2
            self.buffer*=2
            self.buffer+=bit
            self.bitsInBuffer+=1
            self._writeByte()
	   
	    
    def _writeByte(self):
	
	if self.bitsInBuffer==8:
	   self.f.write(struct.pack("B",self.buffer))
	   self.buffer=0
	   self.bitsInBuffer=0 
	   
	   
    def close(self):
	
	leftover=self.bitsInBuffer
	if leftover:
                self.f.write(struct.pack("B",self.buffer*(2**(8-leftover))))
	self.f.close()

class BitReader:
    
    """VARS"""
    #f - file
    #buffer - bits of info to be written to file
    #bitsInBuffer - size of buffer in bits
    
    """Creates file that writes binary"""
    def __init__(self, filename):
	
	self.f=file(filename,'rb')
	self.buffer=0
	self.bitsInBuffer=0
	
    def read(self, numOfBits):
        
	"""
        remainder=numOfBits-self.bitsInBuffer
	if remainder>0:
	    
	    #Calculates num of bytes to read in
	    bytesNeeded=remainder//8 if remainder%8==0 else remainder//8+1
	    
	    #Reads in bytes for each needed
	    string="B"*bytesNeeded
	    self.bitsInBuffer+=8*bytesNeeded
	    data=struct.unpack(string, self.f.read(bytesNeeded))
	    i=0
	    for num in data:
		self.buffer*=256
		self.buffer+=data[i]*(256**(i+1))
		i+=1
	"""
	
	while(self.bitsInBuffer-numOfBits<0):
            self.bitsInBuffer+=8
            self.buffer*=256
            data=self.f.read(1)
            #print repr(data)
            self.buffer+=struct.unpack("B",data)[0]
        num=0
        for i in range(numOfBits):
            q=2**(self.bitsInBuffer-1)
            #print q,self.buffer
            bit=1 if q<=self.buffer else 0
            if bit==1:
                self.buffer-=q
            self.bitsInBuffer-=1
            num+=bit*(2**i)
        return num
	
    def close(self):
	self.f.close()
