package StegoClass;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;


public class GandharbaStegano {
	public byte[] MessageBytes;
	public BufferedImage img;
	public BufferedImage stegoimg;
	public int[] ImageData;
	public int[] ColorImageData;
	public int[] StegoImageData;
	public int ImgWidth;
	public int ImgHeight;
	public int ImgCapacity;
	public int Lower;
	public int Lowmid;
	public int Highmid;
	public int Higher;
	public boolean ColorImage;
	public String StegoFileName;
	public int StegoFileSize;
	public BufferedImage loadImage(String PathFile) throws Exception{
		BufferedImage img = ImageIO.read(new File(PathFile));
        return img;
	}
	public void setImage(BufferedImage _img){
		this.img = _img;
		ImgWidth = img.getWidth();
		ImgHeight = img.getHeight();
	}
	public void setStegoImage(BufferedImage _img){
		this.stegoimg = _img;
		ImgWidth = img.getWidth();
		ImgHeight = img.getHeight();
	}
	public int[] processImage(BufferedImage _img,boolean ColorImage){
		int[] _ImageData = _img.getRGB(0,0,_img.getWidth(),_img.getHeight(),null,0,_img.getWidth());
		if (ColorImage==false){
			for(int i=0;i<_ImageData.length;i++){
				_ImageData[i] = (_ImageData[i] & 0xff0000) >> 16;
			}
			return _ImageData;
		}
		else{
			int[][] rgbval = new int[3][_ImageData.length];
			int[] ColorImageData = new int[_ImageData.length*3];
			for(int i=0;i<_ImageData.length;i++){
				rgbval[0][i] = (_ImageData[i] & 0xff0000) >> 16; //red
				rgbval[1][i] = (_ImageData[i] & 0xff00) >> 8; //green
				rgbval[2][i] = _ImageData[i] & 0xff; //blue
			}
			for(int i=0;i<_img.getHeight();i++){
				System.arraycopy(rgbval[0],(i*_img.getWidth()),ColorImageData,(i*(_img.getWidth()*3)),_img.getWidth());
				System.arraycopy(rgbval[1],(i*_img.getWidth()),ColorImageData,(i*(_img.getWidth()*3)+_img.getWidth()),_img.getWidth());
				System.arraycopy(rgbval[2],(i*_img.getWidth()),ColorImageData,(i*(_img.getWidth()*3)+_img.getWidth()*2),_img.getWidth());
			}
			return ColorImageData;
		}
	}
	public int[] calculateCapacity(int ImgWidth,int ImgHeight,int[] ImageData){
		//Hitung kapasitas image dan sisipkan nilai level pada image
		int[] ProcessedData = new int[ImageData.length];
		System.arraycopy(ImageData,0,ProcessedData,0,ImageData.length);
		ImgCapacity=0;
		Lower = 0;
		Lowmid = 0;
		Highmid = 0;
		Higher = 0;
		for(int i=0;i<ImgHeight;i+=3){
			if (i+3>ImgHeight) break;
			for(int j=0;j<ImgWidth;j+=3){
				if (j+3>ImgWidth) break;
				int xmin = 1000;
				int d = 0;
				for(int k=0;k<3;k++){
					for(int l=0;l<3;l++){
						if (xmin > ProcessedData[(i+k)*ImgWidth+(j+l)]){
							xmin = ProcessedData[(i+k)*ImgWidth+(j+l)];
						}
					}
				}
				//System.out.println("xmin : "+xmin);
				for(int k=0;k<3;k++){
					for(int l=0;l<3;l++){
						d+=Math.abs(ProcessedData[(i+k)*ImgWidth+(j+l)]-xmin);
					}
				}
				d=d/8;
				int Level = 0;
				//System.out.print("Level : ");
				if (d<=7){
					//System.out.println("lower level");
					Level = 0;
					Lower ++;
				}
				else if (d>=8 && d<=15){
					//System.out.println("lower-middle level");
					Level = 1;
					Lowmid++;
				}
				else if (d>=16 && d<=31){
					//System.out.println("higher-middle level");
					Level = 2;
					Highmid++;
				}
				else{
					//System.out.println("higher level");
					Level = 3;
					Higher++;
				}
				ImgCapacity += (9*(Level+2)-2);
				ProcessedData[(2+i)*ImgWidth+(j+2)] = (ProcessedData[(2+i)*ImgWidth+(j+2)] & 0xFC) | Level;
				/*for(int k=0;k<3;k++){
					for(int l=0;l<3;l++){
						System.out.print(ProcessedData[(k+i)*ImgWidth+(j+l)]+" ");
					}
					System.out.println();
				}
				System.out.println();*/
			}
		}
		int TotalBlocks = Lower+Lowmid+Highmid+Higher;
		System.out.println("Matrices : "+(TotalBlocks)+" , Image Capacity : "+ImgCapacity+" (bits)");
		System.out.println("Lower : "+Lower+" , LowMid = "+Lowmid+" , Highmid : "+Highmid+" , Higher : "+Higher);
		return ProcessedData;
	}
	public int extractBits(int data,int pos,int n){
		int currbyte = (data<<pos) & 0xFF;
		currbyte = currbyte >> (8-(n));
		return currbyte;
	}
	public boolean embedMessage(byte[] MessageBytes,int Seed,int[] ImageData,int ImageWidth,int ImageHeight){
		//set Random variable 
		Random rand = new Random();
		Seed = Seed % (ImageWidth*ImageHeight);
		rand.setSeed(Seed);
		Integer[] randomInts = new Integer[ImageWidth*ImageHeight];

	    for (int i = 0; i < randomInts.length; i++) {
	    	randomInts[i] = Integer.valueOf(i);
	    }
	    Collections.shuffle(Arrays.asList(randomInts), rand);
	    
	    int ShuffIdx = 0;
	    
		int BitEmbedded=0; //Number of Bit Embedded on image
		System.out.println("Embedding Message , Width : "+ImageWidth);
		while(BitEmbedded < MessageBytes.length*8){
			int pos = randomInts[ShuffIdx].intValue(); // randomize position to embed bit
			ShuffIdx++;
			System.out.println("Pos : "+pos);
			//Get This pixel block level
			//Calculate block position so we can get this block level
			int posrow = pos/ImageWidth;
			int poscol = pos % ImageWidth;
			int blockrow = posrow/3;
			int blockcol = poscol/3;
			
			if (blockrow*3+2>=ImageHeight || blockcol*3+2>=ImageWidth){ //Unused block
				System.out.println("Unused block "+(blockrow*3+2)+" "+(blockcol*3+2));
			}
			else{
				int LevelPos = (blockrow*3+2)*ImageWidth+(blockcol*3+2);
				int Level = ImageData[LevelPos]&0x03; //Get block level
				//System.out.println("Pos : "+pos+" , Level : "+Level);
				int n = Level;
				if (LevelPos!=pos){ //if pos doesnt contain level information of corresponding block
					n+=2;
					if (MessageBytes[BitEmbedded/8]<0){ //Adjust Value to 0-255
						MessageBytes[BitEmbedded/8]+=256;
					}
					//System.out.println("Message Bytes : "+(char) MessageBytes[BitEmbedded/8]+" ImageData : "+ImageData[pos]);
					
					int currbyte = 0;
					if (n>(8-(BitEmbedded%8))){
						System.out.println("Overflow");
						int left,right;
						left = (8-(BitEmbedded%8));
						right = n-left;
						
						currbyte = extractBits(MessageBytes[BitEmbedded/8],(BitEmbedded%8),left);
						if ((BitEmbedded/8)+1 < MessageBytes.length){
							int currbyte1 = extractBits(MessageBytes[(BitEmbedded/8)+1],0,right);
							currbyte = (currbyte<<right) | currbyte1;
						}
					}
					else{
						currbyte = extractBits(MessageBytes[BitEmbedded/8],(BitEmbedded%8),n);
					}
					//Embedding bit message on image
					int temp = ImageData[pos];
					ImageData[pos]=(ImageData[pos]>>n)<<n;
					ImageData[pos]=(ImageData[pos])| currbyte;
					//System.out.println("currbyte : "+currbyte+" n : "+n);
					//System.out.println("Hasil Embedding : "+ImageData[pos]);
					BitEmbedded+=n;
					//Adjusting pixel value
					if (ImageData[pos] >= (temp + (1<<(n-1)) +1)){
						ImageData[pos]-=(1<<n);
					}
					else if (ImageData[pos] <= (temp - (1<<(n-1)) +1)){
						ImageData[pos]+=(1<<n);}
					//System.out.println("Hasil ReAdjust : "+ImageData[pos]);
					if (ImageData[pos]<0){
						ImageData[pos]+=(1<<n);}
					else if (ImageData[pos]>255){
						ImageData[pos]-=(1<<n);}
					//System.out.println("Hasil Bounding : "+ImageData[pos]);
					
				}
				else{
					System.out.println("Edge Matrix");
					if (MessageBytes[BitEmbedded/8]<0){
						MessageBytes[BitEmbedded/8]+=256;
					}
					System.out.println("Message Bytes : "+(char) MessageBytes[BitEmbedded/8]+" ImageData : "+ImageData[pos]);
					
					int currbyte = 0;
					if (n>(8-(BitEmbedded%8))){
						System.out.println();
						System.out.println("Overflow");
						int left,right;
						left = (8-(BitEmbedded%8));
						right = n-left;
						
						currbyte = extractBits(MessageBytes[BitEmbedded/8],(BitEmbedded%8),left);
						
						int currbyte1 = extractBits(MessageBytes[(BitEmbedded/8)+1],0,right);
						
						currbyte = (currbyte<<right) | currbyte1;
					}
					else{
						currbyte = extractBits(MessageBytes[BitEmbedded/8],(BitEmbedded%8),n);
					}
					
					int temp = ImageData[pos];
					ImageData[pos]=(ImageData[pos]>>(n+2))<<n;
					ImageData[pos]=(ImageData[pos])| currbyte;
					ImageData[pos]=(ImageData[pos]<<2) | Level;
					//System.out.println("currbyte : "+currbyte+" n : "+n);
					//System.out.println("Hasil Embedding : "+ImageData[pos]);
					BitEmbedded+=n;
					
					n+=2; //for adjusting only
					if (ImageData[pos] >= (temp + (1<<(n-1)) +1)){
						ImageData[pos]-=(1<<n);
					}
					else if (ImageData[pos] <= (temp - (1<<(n-1)) +1)){
						ImageData[pos]+=(1<<n);}
					//System.out.println("Hasil ReAdjust : "+ImageData[pos]);
					if (ImageData[pos]<0){
						ImageData[pos]+=(1<<n);}
					else if (ImageData[pos]>255){
						ImageData[pos]-=(1<<n);}
					//System.out.println("Hasil Bounding : "+ImageData[pos]);	
				}
			}
			//System.out.println();
		}
		this.StegoImageData = new int[ImageData.length];
		System.arraycopy(ImageData, 0, this.StegoImageData, 0, ImageData.length);
		System.out.println("Stego Image Data : "+this.StegoImageData[25768]);
		return true;
	}				
	public boolean extractMessage(int Seed,int[] ImageData,int ImageWidth,int ImageHeight){
		Random rand = new Random();
		Seed = Seed % (ImageWidth*ImageHeight);
		rand.setSeed(Seed);
		Integer[] randomInts = new Integer[ImageWidth*ImageHeight];

	    for (int i = 0; i < randomInts.length; i++) {
	    	randomInts[i] = Integer.valueOf(i);
	    }
	    Collections.shuffle(Arrays.asList(randomInts), rand);
	    
	    int ShuffIdx = 0;
		
		int BitExtracted=0; //Number of Bits Extracted
		int idx=0; //Idx Byte
		int TmpBit=0; //Temporary Bit Value
		int FileInfoSize=0;
		int MessageSize=ImageData.length*5; //File Size
		int MessageBitExtracted = 0;
		
		byte[] FileInfo = new byte[ImageData.length];
		byte[] Messages = new byte[ImageData.length];
		
		boolean FileNameFlag=false;
		boolean SizeFlag = false;
		boolean StartMessageFlag = false;
		boolean StopExtract = false;
		
		System.out.println();
		System.out.println("Seed for extractng message : "+Seed);
		while(StopExtract==false || FileNameFlag==false || SizeFlag==false){
			int pos = randomInts[ShuffIdx].intValue();
			ShuffIdx++;
			//System.out.println("TmpBit : "+TmpBit);
			//Get This pixel block level
			int posrow = pos/ImageWidth;
			int poscol = pos % ImageWidth;
			int blockrow = posrow/3;
			int blockcol = poscol/3;
			
			if (blockrow*3+2>=ImageHeight || blockcol*3+2>=ImageWidth){
				System.out.println("Unused BLOCKS "+(blockrow*3+2)+" "+(blockcol*3+2));
			}
			else{
				int LevelPos = (blockrow*3+2)*ImageWidth+(blockcol*3+2);
				int Level = ImageData[LevelPos]&0x03;
				
				//System.out.println("Pos : "+pos+" , Level : "+Level+" , Bit Extracted now : "+BitExtracted);
				int n = Level;
				int TempData = 0;
				if (LevelPos!=pos){
					n+=2;
					//System.out.println("ImageData : "+ImageData[pos]);
					TempData = extractBits(ImageData[pos],(8-n),n);
				}
				else{
					TempData = extractBits(ImageData[pos]>>2,(8-n),n);
				}
				if ((BitExtracted%8)+n >= 8){
					int LOIdx = 0;
					int LeftOver =0;
					if (BitExtracted+n > FileInfoSize*8+MessageSize*8 ){
						int OverIdx = BitExtracted+n - (FileInfoSize*8 + MessageSize*8);
						int Masker = (1<<(n-OverIdx))-1;
						TempData = TempData & (Masker);
						TmpBit=(TmpBit<<(n-OverIdx))|TempData;
					}
					else{
						TmpBit=(TmpBit<<n)|TempData;
						LOIdx =  (BitExtracted%8) + n - 8;
						LeftOver = TmpBit & ((1<<LOIdx) - 1);
					}
					char X = (char)(TmpBit>>LOIdx);
					System.out.println("X : "+X);
					
					if (FileNameFlag==false){
						FileInfo[idx] = (byte)(TmpBit >> LOIdx);
						if ((char)FileInfo[idx]=='#'){
							FileNameFlag = true;
							this.StegoFileName = new String(FileInfo);
							this.StegoFileName = StegoFileName.substring(0,idx);
							
						}
						idx++;
					}
					else if (SizeFlag==false){
						FileInfo[idx] = (byte)(TmpBit >> LOIdx);
						if ((char)FileInfo[idx]=='#'){
							SizeFlag = true;
							String StegoFileSizes = new String(FileInfo);
							FileInfoSize = idx+1;
							System.out.println("File Info Size : "+FileInfoSize+" bits "+StegoFileSizes);
							
							int BeginIdx = StegoFileName.length()+1;
							int EndIdx = idx;
							
							StegoFileSizes=StegoFileSizes.substring(BeginIdx, EndIdx);
							this.StegoFileSize = Integer.parseInt(StegoFileSizes);
							
							MessageSize = StegoFileSize;
							System.out.println("Stego File Name : "+StegoFileName);
							System.out.println("Stego File Size : "+StegoFileSize);
							System.out.println();
							idx=0;
							StartMessageFlag=true;
							System.out.println("Leftover : "+LeftOver);
						}
						else{
							idx++;
						}
					}
					else{
						Messages[idx] = (byte) (TmpBit >> LOIdx);
						//System.out.println("Bytes formed  : "+Messages[idx]);
						idx++;
					}
					TmpBit = LeftOver;
				}
				else{
					TmpBit=(TmpBit<<n)|TempData;
				}
				BitExtracted+=n;
				if (StartMessageFlag == true){
					//System.out.println("Start Extracting Message ");
					//System.out.println(BitExtracted+" "+(FileInfoSize*8)+" "+(MessageSize*8));
					if (BitExtracted > FileInfoSize*8+MessageSize*8 ){
						StopExtract=true;
					}
				}
			}
		}
		this.MessageBytes = new byte[MessageSize];
		System.arraycopy(Messages, 0, MessageBytes, 0, MessageSize);
		return true;
	}				

}

