import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.midi.MidiChannel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import StegoClass.GandharbaStegano;
public class Steganography {

	private JFrame frame;
	private JTextField txtKey;
	private JComboBox comboBox;
	private JButton btnOpenCoverImage;
	private JButton btnSaveCoverImage;
	private JButton btnOpenStegoImage;
	private JButton btnSaveStegoImage;
	private JLabel lblCoverImage;
	private JLabel lblStegoImage;
	private JButton btnEncode;
	private JButton btnDecode;
	private JButton btnOpenStegoFile;
	private JLabel lblFileName;
	private JLabel lblCapacity;
	private JLabel lblPsnrValue;
	
	private JFileChooser imageChooser;
	private JFileChooser fileChooser;
	private BufferedImage coverImage;
	private BufferedImage stegoImage;
	private byte[] stegoFile;
	private String errorMessage;
	
	private GandharbaStegano GGS;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Steganography window = new Steganography();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Steganography() {
		initialize();
		
		// set file choosers
		imageChooser = new JFileChooser();
		String[] Ext = {"bmp"};
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("PNG & BMP Files", Ext);
		imageChooser.setFileFilter(imageFilter);
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Stego File");
		
		// set button listeners
		btnOpenCoverImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = imageChooser.getSelectedFile();
					try {
						coverImage = ImageIO.read(file);
						stegoImage = new BufferedImage(coverImage.getWidth(), coverImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					lblCoverImage.setIcon(new ImageIcon(scaleImage(coverImage, lblCoverImage.getWidth(), lblCoverImage.getHeight())));
				}
			}
		});
		btnSaveCoverImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = imageChooser.getSelectedFile();
					try {
						ImageIO.write(coverImage, "png", file);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnOpenStegoImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = imageChooser.getSelectedFile();
					try {
						stegoImage = ImageIO.read(file);
						coverImage = new BufferedImage(stegoImage.getWidth(), stegoImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					lblStegoImage.setIcon(new ImageIcon(scaleImage(stegoImage, lblStegoImage.getWidth(), lblStegoImage.getHeight())));
				}
			}
		});
		btnSaveStegoImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = imageChooser.getSelectedFile();
					try {
						ImageIO.write(stegoImage, "png", file);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnOpenStegoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					lblFileName.setText(file.getName());
					try {
						stegoFile = Files.readAllBytes(file.toPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnEncode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				encode();
			}
		});
		btnDecode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				decode();
			}
		});
		
		// set image listener
		lblCoverImage.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e)	{
		    	if (coverImage!=null) {
			    	JFrame frame1 = new JFrame("Cover Image");
			    	frame1.setBounds(0, 0, 640, 720);
			    	frame1.setLocationRelativeTo(lblCoverImage);
					JPanel contentPane = new JPanel();
					contentPane.setLayout(new BorderLayout(0, 0));
					frame1.setContentPane(contentPane);
					frame1.setVisible(true);
					
					JLabel lblNewCoverImage = new JLabel("");
					lblNewCoverImage.setIcon(new ImageIcon(scaleImage(coverImage, contentPane.getWidth()-10, contentPane.getHeight()-10)));
					
					JScrollPane scrollPane = new JScrollPane(lblNewCoverImage);
					contentPane.add(scrollPane, BorderLayout.CENTER);
		    	}
				
		    }
		});
		lblStegoImage.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e)	{
		    	if (stegoImage!=null) {
			    	JFrame frame1 = new JFrame("Stego Image");
			    	frame1.setBounds(0, 0, 640, 720);
			    	frame1.setLocationRelativeTo(lblStegoImage);
			    	JPanel contentPane = new JPanel();
					contentPane.setLayout(new BorderLayout(0, 0));
					frame1.setContentPane(contentPane);
					frame1.setVisible(true);
					
					JLabel lblNewStegoImage = new JLabel("");
					lblNewStegoImage.setIcon(new ImageIcon(scaleImage(stegoImage, contentPane.getWidth()-10, contentPane.getHeight()-10)));
					
					JScrollPane scrollPane = new JScrollPane(lblNewStegoImage);
					contentPane.add(scrollPane, BorderLayout.CENTER);
		    	}
		    }
		}); 
	}
	
	private void encode() {
		boolean success = false;
		errorMessage = "";
		if (coverImage==null) {
			errorMessage = "Cover image belum dipilih!";
		}
		else if (stegoFile==null) {
			errorMessage = "Stego file belum dipilih!";
		}
		else if (txtKey.getText().equals("")) {
			errorMessage = "Key belum dimasukkan!";
		}
		else {
			if (comboBox.getSelectedItem().toString().equals("LSB Standard")) {
				success = standardEncode(coverImage, stegoFile, txtKey.getText(), lblFileName.getText());
			}
			else if (comboBox.getSelectedItem().toString().equals("LSB Xin Liao")) {
				/*
				int[] bits = new int[]{0,0,0};
				int res = setEmbeddedPixel(150, bits, 3);
				System.out.println(Integer.toBinaryString(150));
				System.out.println(Integer.toBinaryString(res));
				
				
				Color[] colors = new Color[4];
				
				colors[0] = new Color(7286575);
				colors[1] = new Color(142, 194, 68);
				colors[2] = new Color(123, 196, 75);
				colors[3] = new Color(164, 170, 95);
				
				System.out.println(getBlockLayer(colors, 0)[0]);
				
				
				int[] block = new int[]{139, 146, 137, 142};
				ArrayList<Integer> bits1, bits2, bits3, bits4;
				
				bits1 = new ArrayList<Integer>(Arrays.asList(0,0,0));
				bits2 = new ArrayList<Integer>(Arrays.asList(1,1,1));
				bits3 = new ArrayList<Integer>(Arrays.asList(1,1,1));
				bits4 = new ArrayList<Integer>(Arrays.asList(1,0,1));
				
				float D = getAvgDiff(block);
				
				System.out.println("After embedding: " + Arrays.toString(block));
				
				System.out.println("D: " + D);
				if(D > getThreshold()){
					System.out.println("high level");
					block[0] = setEmbeddedPixel(block[0], bits1, getHighK());
					block[1] = setEmbeddedPixel(block[1], bits2, getHighK());
					block[2] = setEmbeddedPixel(block[2], bits3, getHighK());
					block[3] = setEmbeddedPixel(block[3], bits4, getHighK());
					
					System.out.println("After embedding: " + Arrays.toString(block));
					
					System.out.println("Bits before modif:");
					System.out.println(Integer.toBinaryString(block[1]));
					System.out.println(Integer.toBinaryString(block[2]));
					
					//block[0] = getModifiedPixel(block[0], 139, getHighK());
					block[1] = getModifiedPixel(block[1], 146, getHighK());
					block[2] = getModifiedPixel(block[2], 137, getHighK());
					//block[3] = getModifiedPixel(block[3], 142, getHighK());
										
					System.out.println("After modification: " + Arrays.toString(block));
					System.out.println("Bits:");
					System.out.println(Integer.toBinaryString(block[1]));
					System.out.println(Integer.toBinaryString(block[2]));
					
					block = readjustPixelLayer(block, getHighK(), D);
					
					System.out.println("After readjustment: " + Arrays.toString(block));
				} else {
					System.out.println("low level");
					block[0] = setEmbeddedPixel(block[0], bits1, getLowK());
					block[1] = setEmbeddedPixel(block[1], bits2, getLowK());
					block[2] = setEmbeddedPixel(block[2], bits3, getLowK());
					block[3] = setEmbeddedPixel(block[3], bits4, getLowK());
				}
				*/
				
				success = xinLiaoEncode(coverImage, stegoFile, txtKey.getText(), lblFileName.getText());
			}
			else { // comboBox.getSelectedItem().toString().equals("LSB Gandharba Swain")
				success = gandharbaEncode(coverImage, stegoFile, txtKey.getText(), lblFileName.getText());
			}
		}
		
		if (!success) {
			JOptionPane.showMessageDialog(frame, errorMessage);
		}
		else {
			lblStegoImage.setIcon(new ImageIcon(scaleImage(stegoImage, lblStegoImage.getWidth(), lblStegoImage.getHeight())));
//			int[] pixelArray = new int[stegoImage.getWidth()*stegoImage.getHeight()];
//			Color c = Color.BLACK;
//			for (int i=0; i<pixelArray.length; i++) {
//				pixelArray[i] = c.getRGB();
//			}
//			stegoImage.setRGB(0, 0, stegoImage.getWidth(), stegoImage.getHeight(), pixelArray, 0, stegoImage.getWidth());
			lblPsnrValue.setText(PSNR(coverImage, stegoImage)+"");
		}
	}
	
	private void decode() {
		boolean success = false;
		errorMessage = "";
		if (stegoImage==null) {
			errorMessage = "Stego image belum dipilih!";
		}
		else if (txtKey.getText().equals("")) {
			errorMessage = "Key belum dimasukkan!";
		}
		else {
			String key = txtKey.getText();
			if (comboBox.getSelectedItem().toString().equals("LSB Standard")) {
				success = standardDecode(stegoImage, key);
			}
			else if (comboBox.getSelectedItem().toString().equals("LSB Xin Liao")) {
				/*int[] bits = getEmbeddedPixel(58, getHighK());
				for(int i=0;i<getHighK();i++){
					System.out.println(bits[i]);
				}*/
				success = xinLiaoDecode(stegoImage, key);
			}
			else { // comboBox.getSelectedItem().toString().equals("LSB Gandharba Swain")
				success = gandharbaDecode(stegoImage, key);
			}
		}
		if (!success) {
			JOptionPane.showMessageDialog(frame, errorMessage);
		}
		else {
			fileChooser.setSelectedFile(new File(lblFileName.getText()));
			if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try {
					StandardOpenOption[] Opt = {StandardOpenOption.CREATE_NEW};
					Files.write(file.toPath(), stegoFile,Opt);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			lblCoverImage.setIcon(new ImageIcon(scaleImage(coverImage, lblCoverImage.getWidth(), lblCoverImage.getHeight())));
		}
	}
	
	private boolean standardEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di encode, tulis juga errornya kenapa di errorMessage

		// insert file properties, encrypt
		byte[] fileProp = (fileName+"#"+Integer.toString(stegoBytes.length)+"#").getBytes(StandardCharsets.UTF_8);
		byte[] dirtyStegoBytes = Arrays.copyOf(fileProp, fileProp.length+stegoBytes.length);
		System.arraycopy(stegoBytes, 0, dirtyStegoBytes, fileProp.length, stegoBytes.length);
		//String dirtyStegoString = new String(dirtyStegoBytes, StandardCharsets.UTF_8);
		//System.out.println(dirtyStegoString);
		dirtyStegoBytes = encryptVigenere(dirtyStegoBytes, key);
		
		// calculate capacity
		int width = coverImage.getWidth();
		int height = coverImage.getHeight();
		int maxCapacity = (width*height*3)/8;
		
		// get pixel array
		int[] pixelArray = new int[width*height];
		coverImage.getRGB(0, 0, width, height, pixelArray, 0, width);
		
		// get random number generator
		int seed = 0;
		for  (int i=0; i<key.length(); i++) {
			seed += (int)key.charAt(i);
		}
		seed = seed % (width*height);
		Random rand = new Random(seed);
		Integer[] randomInts = new Integer[width*height];
//		System.out.println("seed : "+seed+" length : "+randomInts.length);
	    for (int i = 0; i < randomInts.length; i++) {
	    	randomInts[i] = Integer.valueOf(i);
	    }
//	    for (int i = 0; i < 10; i++) {
//	    	System.out.println(randomInts[i]);
//	    }
	    Collections.shuffle(Arrays.asList(randomInts), rand);
//	    for (int i = 0; i < 10; i++) {
//	    	System.out.println(randomInts[i]);
//	    }
		
		// convert bytes to bits
	    int stegoBitsLength = dirtyStegoBytes.length*8;
	    stegoBitsLength+=(3-(stegoBitsLength%3));
		int[] stegoBits = new int[stegoBitsLength];
		for (int i=0; i<stegoBits.length; i++) {
			stegoBits[i] = 0;
		}
		for (int i=0; i<dirtyStegoBytes.length; i++) {
			stegoBits[i*8] = (dirtyStegoBytes[i]>>7)&1;
			stegoBits[(i*8)+1] = (dirtyStegoBytes[i]>>6)&1;
			stegoBits[(i*8)+2] = (dirtyStegoBytes[i]>>5)&1;
			stegoBits[(i*8)+3] = (dirtyStegoBytes[i]>>4)&1;
			stegoBits[(i*8)+4] = (dirtyStegoBytes[i]>>3)&1;
			stegoBits[(i*8)+5] = (dirtyStegoBytes[i]>>2)&1;
			stegoBits[(i*8)+6] = (dirtyStegoBytes[i]>>1)&1;
			stegoBits[(i*8)+7] = (dirtyStegoBytes[i])&1;
		}
		
		// embed
		if (dirtyStegoBytes.length<maxCapacity) {
			int n, r, g, b;
			Color c;
			for (int i=0; i<(int)(stegoBits.length/3); i++) {
				n = randomInts[i].intValue();
				c = new Color(pixelArray[n]);
				
				r = c.getRed();
				if (stegoBits[i*3]==1) {
					r |= 1;
				}
				else {
					r &= ~(1);
				}
				
				g = c.getGreen();
				if (stegoBits[(i*3)+1]==1) {
					g |= 1;
				}
				else {
					g &= ~(1);
				}
				
				b = c.getBlue();
				if (stegoBits[(i*3)+2]==1) {
					b |= 1;
				}
				else {
					b &= ~(1);
				}

				//System.out.println(n+" "+stegoBits[i*3]+" "+stegoBits[(i*3)+1]+" "+stegoBits[(i*3)+2]);
				
				pixelArray[n] = new Color(r, g, b).getRGB();
			}
			
			// write stuffs
			stegoImage.setRGB(0, 0, width, height, pixelArray, 0, width);
			lblCapacity.setText(maxCapacity+" bytes");
			
			return true;
		}
		else {
			errorMessage = "Stego File melebihi kapasitas!\nKapasistas maksimum : " + maxCapacity + " bytes";
			return false;
		}
	}
	
	private boolean standardDecode(BufferedImage stegoImage, String key) {
		// tulis image hasil decode di coverImage
		// tulis byte[] hasil decode di stegoFile
		// tulis nama file di lblFileName.setText()
		// return true kalo bisa di-decode, false kalo ga bisa
		// kalo ga bisa di-decode, tulis juga errornya kenapa di errorMessage
		
		int width = stegoImage.getWidth();
		int height = stegoImage.getHeight();
		
		// get pixel array
		int[] pixelArray = new int[width*height];
		stegoImage.getRGB(0, 0, width, height, pixelArray, 0, width);
		
		// get random number generator
		int seed = 0;
		for  (int i=0; i<key.length(); i++) {
			seed += (int)key.charAt(i);
		}
		seed = seed % (width*height);
		Random rand = new Random(seed);
		Integer[] randomInts = new Integer[width*height];
//		System.out.println("seed : "+seed+" length : "+randomInts.length);
	    for (int i = 0; i < randomInts.length; i++) {
	    	randomInts[i] = Integer.valueOf(i);
	    }
//	    for (int i = 0; i < 10; i++) {
//	    	System.out.println(randomInts[i]);
//	    }
	    Collections.shuffle(Arrays.asList(randomInts), rand);
//	    for (int i = 0; i < 10; i++) {
//	    	System.out.println(randomInts[i]);
//	    }
		
		// extract bits
		int[] stegoBits = new int[width*height*3];
		int n, r, g, b;
		Color c;
		for (int i=0; i<(width*height); i++) {
			n = randomInts[i].intValue();
			c = new Color(pixelArray[n]);
			r = c.getRed();
			stegoBits[i*3] = r&1;
			g = c.getGreen();
			stegoBits[(i*3)+1] = g&1;
			b = c.getBlue();
			stegoBits[(i*3)+2] = b&1;
//			if (i<10) {
//				System.out.println("############"+n+" "+stegoBits[i*3]+" "+stegoBits[(i*3)+1]+" "+stegoBits[(i*3)+2]);
//			}
		}
		
		// convert to bytes, decrypt
		byte[] dirtyStegoBytes = new byte[(int)stegoBits.length/8];
		for (int i=0; i<dirtyStegoBytes.length; i++) {
			for (int j=0; j<8; j++) {
				if (stegoBits[(i*8)+j]==1) { // bit==1
					dirtyStegoBytes[i] |= (1 << (7-j));
				}
				else {						// bit==0
					dirtyStegoBytes[i] &= ~(1 << (7-j));
				}
			}
		}
		dirtyStegoBytes = decryptVigenere(dirtyStegoBytes, key);
		
		// extract properties
		String dirtyStegoString = new String(dirtyStegoBytes, StandardCharsets.UTF_8);
		//System.out.println("###################\n"+dirtyStegoString);
		int firstFound = dirtyStegoString.indexOf('#');
		String fileName = dirtyStegoString.substring(0, firstFound);
		//System.out.println("nama : "+fileName);
		int bytesLength = Integer.parseInt(dirtyStegoString.substring(firstFound+1, dirtyStegoString.indexOf('#', firstFound+1)));
		
		// get clean stegobytes
		byte[] fileProp = (fileName+"#"+Integer.toString(bytesLength)+"#").getBytes(StandardCharsets.UTF_8);
		byte[] stegoBytes = new byte[bytesLength];
		System.arraycopy(dirtyStegoBytes, fileProp.length, stegoBytes, 0, bytesLength);
		
		// write stuffs
		coverImage = stegoImage;
		lblFileName.setText(fileName);
		stegoFile = stegoBytes;
		
		return true;
	}
	
	private int getLowK(){
		return 2;
	}
	
	private int getHighK(){
		return 3;
	}
	
	private int getThreshold(){
		return 12;
	}
	
	private int getMinPixel(Color pixels[], int layer){
		int ch[] = getBlockLayer(pixels, layer);
		
		int ymin = ch[0];
		for(int i=1;i<4;i++){
			if(ch[i] < ymin){
				ymin = ch[i];
			}
		}
		
		return ymin;
	}
	
	private int getMaxPixel(Color pixels[], int layer){
		int ch[] = getBlockLayer(pixels, layer);
		
		int ymax = ch[0];
		for(int i=1;i<4;i++){
			if(ch[i] > ymax){
				ymax = ch[i];
			}
		}
		
		return ymax;
	}
	
	//layer 0,1,2: r,g,b
	private int[] getBlockLayer(Color pixels[], int layer){
		int ch[] = new int[4];
		for(int i=0;i<4;i++){
			if(layer == 0){
				ch[i] = pixels[i].getRed();
			} else if(layer == 1){
				ch[i] = pixels[i].getGreen();
			} else if(layer == 2){
				ch[i] = pixels[i].getBlue();
			} 
		}
		return ch;
	}
	
	private float getAvgDiff(int[] ch){		
		int ymin = Integer.MAX_VALUE;
		for(int i=0;i<4;i++){
			if(ch[i] < ymin)
				ymin = ch[i];
		}
		
		float result = 0f;
		for(int i=0;i<4;i++){
			result += (ch[i] - ymin);
		}
		
		result /= 3;
		
		return result;
	}
	
	
	private float getAvgDiff(Color[] pixels, int layer){
		
		int ch[] = getBlockLayer(pixels, layer);
		int ymin = getMinPixel(pixels, layer);
		
		float result = 0f;
		for(int i=0;i<4;i++){
			result += (ch[i] - ymin);
		}
		
		result /= 3;
		
		return result;
	}
	
	private int setEmbeddedPixel(int pixel, ArrayList<Integer> bits, int k){
		for(int i=0;i<k;i++){
			if(bits.get(i) == 1){
				pixel |= 1 << i;
			} else {
				pixel &= ~(1 << i);
			}
			
		}
		return pixel;
	}
	
	private int[] getEmbeddedPixel(int pixel, int k){
		int[] bits = new int[k];
		for(int i=k-1;i>=0;i--){
			bits[i] = (pixel >> i) & 1;
		}
		return bits;
	}
	
	// k bit is embedded into the origin pixel, resulting in LSB pixel.
	// return modified LSB pixel
	private int getModifiedPixel(int LSBPixel, int originPixel, int k){
		int diff = Math.abs(LSBPixel - originPixel);
		
		// get unembedded parts. 8 - k
		int unembeddedBits = LSBPixel >> k;
		int embeddedBits = LSBPixel & ((1 << k) - 1);
		
		//System.out.println("Unembedded: " + Integer.toBinaryString(unembeddedBits));
		//System.out.println("  Embedded: " + Integer.toBinaryString(embeddedBits));
		
		// opsi 1: + 1
		int unembeddedBits1 = unembeddedBits + 1;
		int valueAdd = (unembeddedBits1 << k) + embeddedBits;
		
		//System.out.println("     Add 1: " + Integer.toBinaryString(valueAdd));
		
		// opsi 2: - 1
		int unembeddedBits0 = unembeddedBits - 1;
		int valueDec = (unembeddedBits0 << k) + embeddedBits;
		
		//System.out.println("     Dec 1: " + Integer.toBinaryString(valueDec));
		
		if(Math.abs(valueAdd - originPixel) < diff && Math.abs(valueAdd - originPixel) < Math.abs(valueDec - originPixel)){
			return valueAdd;
		} else if(Math.abs(valueDec - originPixel) < diff && Math.abs(valueDec - originPixel) < Math.abs(valueAdd - originPixel)){
			return valueDec;
		} else {
			return LSBPixel;
		}
	}
	
	private int[] readjustPixelLayer(int[] pixelComp, int k, float D){ 
		int[] readjustPixelComp = new int[4];
		float min3 = Float.MAX_VALUE;
		
		for(int i=-1;i<2;i++){
			int[] tempPixel = new int[4];
			boolean isNegative = false;
			
			for(int j=0;j<4;j++){
				tempPixel[j] = pixelComp[j] + i * ((int) Math.pow(2, k));
				if(tempPixel[j] < 0){
					isNegative = true;
				}
			}
			
			float tempD = getAvgDiff(tempPixel);
			if(checkLevel(D) == checkLevel(tempD) && !isErrorBlock(tempPixel) && !isNegative){
				// calculate temp min3, compare with current. if temp is less, set temp as current
				float tempMin3 = 0;
				for(int j=0;j<4;j++){
					tempMin3 += (tempPixel[j] - readjustPixelComp[j]) * (tempPixel[j] - readjustPixelComp[j]);
				}
				
				if(tempMin3 < min3){
					min3 = tempMin3;
					for(int j=0;j<4;j++){
						readjustPixelComp[j] = tempPixel[j];
					}
				}
			}
		}
		
		return readjustPixelComp;
	}
	
	// 0: low, 1: high
	int checkLevel(float D){
		return (D < getThreshold() ? 0 : 1);
	}
	
	private boolean isErrorBlock(int[] ch){
		float D = getAvgDiff(ch);
		float T = getThreshold();
		int ymax = Integer.MIN_VALUE;
		int ymin = Integer.MAX_VALUE;
		
		for(int i=0;i<4;i++){
			if(ch[i] > ymax){
				ymax = ch[i];
			}
			
			if(ch[i] < ymin){
				ymin = ch[i];
			}
		}
		
		if(D <= T && ((ymax - ymin) > 2*T + 2))
			return true;	
		return false;
	}
	
	private boolean isErrorBlock(Color[] pixels, int layer){
		float D = getAvgDiff(pixels, layer);
		float T = getThreshold();
		int ymax = getMaxPixel(pixels, layer);
		int ymin = getMinPixel(pixels, layer);
		
		if(D <= T && ((ymax - ymin) > 2*T + 2))
			return true;	
		return false;
	}
	
	private int getXinLiaoMaxBytesCapacity(){
		int byteCapacity = 0;
		
		for(int i=0;i<coverImage.getHeight();i += 2){
			for(int j=0;j<coverImage.getWidth();j += 2){
				Color pixels[] = new Color[4];
				
				if(j + 1 < coverImage.getWidth() && i + 1 < coverImage.getHeight()){
					//System.out.println("Processing " + j + " " + i);
					pixels[0] = new Color(coverImage.getRGB(j, i));
					pixels[1] = new Color(coverImage.getRGB(j+1, i));
					pixels[2] = new Color(coverImage.getRGB(j, i+1));
					pixels[3] = new Color(coverImage.getRGB(j+1, i+1));
					
					/*
					for(int z=0;z<4;z++){
						System.out.println(pixels[z].getRed() + " " + pixels[z].getGreen() + " " + pixels[z].getBlue());
					}
					System.out.println(); */
					
					//for each layer: R, G, B & which pixel left top, right top, left bottom, right bottom
					int[][] embeddedLayerPixels = new int[3][4];
					for(int k=0;k<3;k++){				
						// default if error block
						for(int m=0;m<4;m++){
							embeddedLayerPixels[k][m] = getBlockLayer(pixels, k)[m];
						}
						
						// not an error block and there is something left to embed
						if(!isErrorBlock(pixels, k)){
							if(getAvgDiff(pixels, k) <= getThreshold()){
								byteCapacity += 4 * getLowK();
							} else { // use high k
								byteCapacity += 4 * getHighK();
							}
						}
					}
				}
			}
		}
		
		return byteCapacity/8;
			
	}
	
	private boolean xinLiaoEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di encode, tulis juga errornya kenapa di errorMessage
		
		System.out.println("stegobytes length: " + stegoBytes.length);
		//System.out.println("length: " + Integer.toString(stegoBytes.length));
		
		byte[] header = (fileName+"#"+Integer.toString(stegoBytes.length)+"#").getBytes(StandardCharsets.UTF_8);
		
		//System.out.println("Header: " + Arrays.toString(header));
		
		byte[] embeddedStegoBytes = Arrays.copyOf(header, header.length + stegoBytes.length);
		
		
		System.arraycopy(stegoBytes, 0, embeddedStegoBytes, header.length, stegoBytes.length);
		embeddedStegoBytes = encryptVigenere(embeddedStegoBytes, key);
		
		//System.out.println("Header: " + Arrays.toString(embeddedStegoBytes));
		
		
		// convert bytes to bits
	    int stegoBitsLength = embeddedStegoBytes.length * 8;
		int[] embeddedStegoBits = new int[stegoBitsLength];
		int maxBytesCapacity = getXinLiaoMaxBytesCapacity();
		lblCapacity.setText(String.valueOf(maxBytesCapacity) + " bytes");
		
		for (int i=0; i<embeddedStegoBits.length; i++) {
			embeddedStegoBits[i] = 0;
		}
		for (int i=0; i<embeddedStegoBytes.length; i++) {
			embeddedStegoBits[i*8] = (embeddedStegoBytes[i]>>7)&1;
			embeddedStegoBits[(i*8)+1] = (embeddedStegoBytes[i]>>6)&1;
			embeddedStegoBits[(i*8)+2] = (embeddedStegoBytes[i]>>5)&1;
			embeddedStegoBits[(i*8)+3] = (embeddedStegoBytes[i]>>4)&1;
			embeddedStegoBits[(i*8)+4] = (embeddedStegoBytes[i]>>3)&1;
			embeddedStegoBits[(i*8)+5] = (embeddedStegoBytes[i]>>2)&1;
			embeddedStegoBits[(i*8)+6] = (embeddedStegoBytes[i]>>1)&1;
			embeddedStegoBits[(i*8)+7] = (embeddedStegoBytes[i])&1;
		}
		
		//System.out.println("Bit version: " + Arrays.toString(embeddedStegoBits) + "\n\n");
		
		if(embeddedStegoBytes.length <= maxBytesCapacity){
			int embeddedBitIndex = 0;
			for(int i=0;i<coverImage.getHeight();i += 2){
				for(int j=0;j<coverImage.getWidth();j += 2){
					
					Color pixels[] = new Color[4];
					
					if(j + 1 < coverImage.getWidth() && i + 1 < coverImage.getHeight()){
						//System.out.println("Processing " + j + " " + i);
						pixels[0] = new Color(coverImage.getRGB(j, i));
						pixels[1] = new Color(coverImage.getRGB(j+1, i));
						pixels[2] = new Color(coverImage.getRGB(j, i+1));
						pixels[3] = new Color(coverImage.getRGB(j+1, i+1));
						
						/*
						for(int z=0;z<4;z++){
							System.out.println(pixels[z].getRed() + " " + pixels[z].getGreen() + " " + pixels[z].getBlue());
						}
						System.out.println(); */
						
						//for each layer: R, G, B & which pixel left top, right top, left bottom, right bottom
						int[][] embeddedLayerPixels = new int[3][4];
						for(int k=0;k<3;k++){				
							// default if error block
							for(int m=0;m<4;m++){
								embeddedLayerPixels[k][m] = getBlockLayer(pixels, k)[m];
							}
							
							// not an error block and there is something left to embed
							if(!isErrorBlock(pixels, k) && embeddedBitIndex < stegoBitsLength){
								if(j == 120 && i == 0){
									//System.out.println("is not error block");
								}
								// embed
								// use low k
								if(getAvgDiff(pixels, k) <= getThreshold()){
									for(int m=0;m<4;m++){
										ArrayList<Integer> embed = new ArrayList<Integer>();
										int takenLength = (embeddedBitIndex+getLowK() < embeddedStegoBits.length ? embeddedBitIndex+getLowK() : embeddedStegoBits.length);
										for(int n=embeddedBitIndex;n<takenLength;n++){
											embed.add(embeddedStegoBits[n]);
										}
										
										embeddedBitIndex += embed.size();
										
										
										
										if(j == 36 && i == 0 && k == 2){
											System.out.println("Embedding bits: " + embed.toString() + ", initial D: " + getAvgDiff(embeddedLayerPixels[k]));
											System.out.println("Initial Elmt: " + Arrays.toString(embeddedLayerPixels[k]));
										}
										
										System.out.println("Saving " + embed.toString() + " in (" + j + ", " + i + ")");
										
										//System.out.println("origin block: " + Arrays.toString(embeddedLayerPixels[k]));
										
										embeddedLayerPixels[k][m] = setEmbeddedPixel(getBlockLayer(pixels, k)[m], embed, embed.size());
										
										if(j == 36 && i == 0 && k == 2){
											System.out.println("Embedded Elmt: " + Arrays.toString(embeddedLayerPixels[k]));
										}
										
										//System.out.println("embedded block: " + Arrays.toString(embeddedLayerPixels[k]));
										
										// apply modified LSB subtitution method
										embeddedLayerPixels[k][m] = getModifiedPixel(embeddedLayerPixels[k][m], getBlockLayer(pixels, k)[m], getLowK());
										
										if(j == 36 && i == 0 && k == 2){
											System.out.println("Modified Elmt: " + Arrays.toString(embeddedLayerPixels[k]));
										}
										
										//System.out.println("modified block: " + Arrays.toString(embeddedLayerPixels[k]));
									}
									
									// readjust
									embeddedLayerPixels[k] = readjustPixelLayer(embeddedLayerPixels[k], getLowK(), getAvgDiff(pixels, k));
									
									if(j == 36 && i == 0 && k == 2){
										System.out.println("Readjusted Elmt: " + Arrays.toString(embeddedLayerPixels[k]) + ", final D: " + getAvgDiff(embeddedLayerPixels[k]));
									}
									
									//System.out.println("readjusted block: " + Arrays.toString(embeddedLayerPixels[k]));
									
								} else { // use high k
									for(int m=0;m<4;m++){
														
										ArrayList<Integer> embed = new ArrayList<Integer>();
										int takenLength = (embeddedBitIndex+getHighK() < embeddedStegoBits.length ? embeddedBitIndex+getHighK() : embeddedStegoBits.length);
										
										for(int n=embeddedBitIndex;n<takenLength;n++){
											//System.out.println("n: " + n + ", stego length: " + embeddedStegoBits.length);
											embed.add(embeddedStegoBits[n]);
										}
										
										embeddedBitIndex += embed.size();
										
										if(j == 120 && i == 0 && m == 3 && k == 2){
											System.out.println("origin: " + Arrays.toString(embeddedLayerPixels[2]));
										}
										
										System.out.println("Saving " + embed.toString() + " in (" + j + ", " + i + ")");
										
										embeddedLayerPixels[k][m] = setEmbeddedPixel(getBlockLayer(pixels, k)[m], embed, embed.size());
										
										if(j == 120 && i == 0 && m == 3 && k == 2){
											System.out.println("embedded: " + Arrays.toString(embeddedLayerPixels[2]));
										}
										
										// apply modified LSB subtitution method
										embeddedLayerPixels[k][m] = getModifiedPixel(embeddedLayerPixels[k][m], getBlockLayer(pixels, k)[m], getHighK());
										
										if(j == 120 && i == 0 && m == 3 && k == 2){
											System.out.println("modified: " + Arrays.toString(embeddedLayerPixels[2]));
										}
									}
																
									// readjust
									embeddedLayerPixels[k] = readjustPixelLayer(embeddedLayerPixels[k], getHighK(), getAvgDiff(pixels, k));
									
									if(j == 120 && i == 0 && k == 2){
										System.out.println("readjusted: " + Arrays.toString(embeddedLayerPixels[2]));
									}
								}
							} else {
								if(j == 120 && i == 0){
									//System.out.println("is error block");
								}
							}
						}
						
						// put into stego images
						// for each pixel, create RGB value
						
						int posX[] = new int[]{j,j+1,j,j+1};
						int posY[] = new int[]{i,i,i+1,i+1};
						
						for(int m=0;m<4;m++){							
							Color pixel = null;
							
							pixel = new Color(embeddedLayerPixels[0][m], embeddedLayerPixels[1][m], embeddedLayerPixels[2][m]);
							
							stegoImage.setRGB(posX[m], posY[m], pixel.getRGB());
						}
						
					} else {
						
					}				
				}
			}
			
			return true;
		} else {
			errorMessage = "Stego File melebihi kapasitas!\nKapasitas maksimum : " + maxBytesCapacity + " bytes. Kapasitas file: " + stegoBitsLength;
			return false;
		}
	}
	
	//isinya y0-y3
	private float avgDiffValue(int[] y){
		int ymin = y[0];
		for(int i=1;i<4;i++){
			if(y[i] < ymin){
				ymin = y[i];
			}
		}
		
		float d = 0;
		
		for(int i=0;i<4;i++){
			d += (y[i] - ymin);
		}
		
		d /= 3;
		
		return d;
	}
	
	private boolean xinLiaoDecode(BufferedImage stegoImage, String key) {
		// tulis image hasil decode di coverImage
		// tulis byte[] hasil decode di stegoFile
		// tulis nama file di lblFileName.setText()
		// return true kalo bisa di-decode, false kalo ga bisa
		// kalo ga bisa di-decode, tulis juga errornya kenapa di errorMessage
		
		//for each layer: R, G, B & which pixel left top, right top, left bottom, right bottom
		int[] resultBits = new int[stegoImage.getWidth() * stegoImage.getHeight() * getHighK() * 8];
		byte[] result = new byte[stegoImage.getWidth() * stegoImage.getHeight() * getHighK()]; //max possible embedded message
		int resultBitIndex = 0;
		
		for(int i=0;i<stegoImage.getHeight();i += 2){
			for(int j=0;j<stegoImage.getWidth();j += 2){
				Color pixels[] = new Color[4];
				
				if(j + 1 < stegoImage.getWidth() && i + 1 < stegoImage.getHeight()){
					//System.out.println("Processing " + j + " " + i);
					pixels[0] = new Color(stegoImage.getRGB(j, i));
					pixels[1] = new Color(stegoImage.getRGB(j+1, i));
					pixels[2] = new Color(stegoImage.getRGB(j, i+1));
					pixels[3] = new Color(stegoImage.getRGB(j+1, i+1));

					for(int k=0;k<3;k++){		
						float D = getAvgDiff(pixels, k);
						if(D <= getThreshold()){
							if(!isErrorBlock(pixels, k)){
								for(int m=0;m<4;m++){
									int bits[] = new int[getLowK()];
									bits = getEmbeddedPixel(getBlockLayer(pixels, k)[m], getLowK());
									
									System.out.println("Loading " + Arrays.toString(bits) +  " in (" + j + ", " + i + ")");
									
									for(int n=0;n<getLowK();n++){
										resultBits[resultBitIndex++] = bits[n];
									}
								}
							}
						} else {
							if(!isErrorBlock(pixels, k)){
								for(int m=0;m<4;m++){
									int bits[] = new int[getHighK()];
									bits = getEmbeddedPixel(getBlockLayer(pixels, k)[m], getHighK());
									
									System.out.println("Loading " + Arrays.toString(bits) + " in (" + j + ", " + i + ")");
									
									for(int n=0;n<getHighK();n++){
										resultBits[resultBitIndex++] = bits[n];
									}
								}
							}
						}
					}
					
				}
			}
		}
		
		// convert to bytes, decrypt
		for (int i=0; i<result.length; i++) {
			for (int j=0; j<8; j++) {
				if (resultBits[(i*8)+j]==1) { // bit==1
					result[i] |= (1 << (7-j));
				}
				else {						// bit==0
					result[i] &= ~(1 << (7-j));
				}
			}
		}
		
		//System.out.println("Bit version: " + Arrays.toString(resultBits) + "\n\n");
		
		/*
		for(int i=0;i<90;i++){
			String s1 = String.format("%8s", Integer.toBinaryString(result[i] & 0xFF)).replace(' ', '0');
			System.out.println(s1);
		} 
		*/
				
		result = decryptVigenere(result, key);
		
		// extract properties
		String resultString = new String(result, StandardCharsets.UTF_8);
		//System.out.println("result string: " + resultString);
		int firstFound = resultString.indexOf('#');
		String fileName = resultString.substring(0, firstFound);
		//System.out.println("nama : "+ fileName);
		int bytesLength = Integer.parseInt(resultString.substring(firstFound+1, resultString.indexOf('#', firstFound+1)));
		
		// get clean stegobytes
		byte[] fileProp = (fileName+"#"+Integer.toString(bytesLength)+"#").getBytes(StandardCharsets.UTF_8);
		byte[] stegoBytes = new byte[bytesLength];
		System.arraycopy(result, fileProp.length, stegoBytes, 0, bytesLength);
		
		// write stuffs
		coverImage = stegoImage;
		lblFileName.setText(fileName);
		stegoFile = stegoBytes;
		
		return true;
	}
	
	private boolean gandharbaEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di-encode, tulis juga errornya kenapa di errorMessage
		boolean ret=false;
		GGS.ColorImage = true; //Color Image?
		GGS.setImage(coverImage);
		//sisipkan filename dan ukuran file
		String FileInfo = fileName + "#"+ Integer.toString(stegoBytes.length)+"#";
		byte[] NewStegoBytes = new byte[stegoBytes.length+FileInfo.length()];
		System.arraycopy(FileInfo.getBytes(), 0, NewStegoBytes, 0, FileInfo.length());
		
		byte[] EncryptedBytes = encryptVigenere(stegoBytes,key);
		System.arraycopy(EncryptedBytes,0,NewStegoBytes,FileInfo.length(),EncryptedBytes.length);
		System.out.println("Full Message : "+new String(NewStegoBytes));
		
		if (GGS.ColorImage==false){
			GGS.ImageData = GGS.processImage(GGS.img,false);
			GGS.calculateCapacity(GGS.ImgWidth, GGS.ImgHeight, GGS.ImageData);
			lblCapacity.setText(Integer.toString(GGS.ImgCapacity)+" (bits)");
			//Generate Seed
			int Seed = 0;
			for(int i=0;i<key.length();i++){
				Seed+=key.charAt(i);
			}
			ret = GGS.embedMessage(NewStegoBytes, Seed, GGS.ImageData, GGS.ImgWidth, GGS.ImgHeight);
			int[] TempImageData = new int[GGS.StegoImageData.length];
			this.stegoImage = new BufferedImage(GGS.ImgWidth,GGS.ImgHeight,BufferedImage.TYPE_INT_ARGB);
			for(int i=0;i<GGS.StegoImageData.length;i++){
				//System.out.println("TempImage : "+TempImageData[i]);
				int row = i/256;
				int col =i%256;
				int temp = GGS.StegoImageData[i];
				Color C = new Color(temp,temp,temp);
				TempImageData[i]=C.getRGB();
				this.stegoImage.setRGB(col,row,C.getRGB());
				if (i==25768){
					//System.out.println("Steg lagi : "+temp+" "+C.getRGB()+" "+this.stegoImage.getRGB(col,row));
					
				}
			}
			GGS.stegoimg = this.stegoImage;
		}
		else{
			GGS.ColorImageData=GGS.processImage(GGS.img,true);
			GGS.calculateCapacity(GGS.ImgWidth*3, GGS.ImgHeight, GGS.ColorImageData);
			lblCapacity.setText(Integer.toString(GGS.ImgCapacity)+" (bits)");
			//Generate Seed
			int Seed = 0;
			for(int i=0;i<key.length();i++){
				Seed+=key.charAt(i);
			}
			ret = GGS.embedMessage(NewStegoBytes, Seed, GGS.ColorImageData, GGS.ImgWidth*3, GGS.ImgHeight);
			this.stegoImage = new BufferedImage(GGS.ImgWidth,GGS.ImgHeight,BufferedImage.TYPE_INT_ARGB);
			for(int i=0;i<GGS.ColorImageData.length/3;i++){
				int idx = (i/GGS.ImgWidth)*(GGS.ImgWidth*3) + (i%GGS.ImgWidth);
				int red =  GGS.ColorImageData[idx];
				int green = GGS.ColorImageData[idx+GGS.ImgWidth];
				int blue = GGS.ColorImageData[idx+GGS.ImgWidth*2];
				
				int row = i/GGS.ImgWidth;
				int col =i%GGS.ImgWidth;
				int temp = GGS.StegoImageData[i];
				Color C = new Color(red,green,blue);
				this.stegoImage.setRGB(col,row,C.getRGB());
			}
		
			GGS.stegoimg = this.stegoImage;
		}
		return ret;
	}
	
	private boolean gandharbaDecode(BufferedImage stegoImage, String key) {
		// tulis image hasil decode di coverImage
		// tulis byte[] hasil decode di stegoFile
		// tulis nama file di lblFileName.setText()
		// return true kalo bisa di-decode, false kalo ga bisa
		// kalo ga bisa di-decode, tulis juga errornya kenapa di errorMessage
		GGS.ColorImage = true;
		GGS.setStegoImage(stegoImage);
		boolean ret = false;
		int Seed = 0;
		for(int i=0;i<key.length();i++){
			Seed+=key.charAt(i);
		}
		if(GGS.ColorImage==false){
			GGS.setStegoImage(GGS.stegoimg);
			GGS.StegoImageData = GGS.processImage(GGS.stegoimg, false);
			System.out.println("Coba Stego Image : "+GGS.StegoImageData[25768]);
			ret=GGS.extractMessage(Seed, GGS.StegoImageData, GGS.ImgWidth, GGS.ImgHeight);
			for(int i=0;i<GGS.MessageBytes.length;i++){
				System.out.print((char)GGS.MessageBytes[i]);
			}
			this.lblFileName.setText(GGS.StegoFileName);
			this.stegoFile = new byte[GGS.MessageBytes.length];
			System.arraycopy(GGS.MessageBytes, 0, this.stegoFile, 0, GGS.MessageBytes.length);
		}
		else{
			GGS.setStegoImage(GGS.stegoimg);
			GGS.StegoImageData = GGS.processImage(GGS.stegoimg, true);
			ret=GGS.extractMessage(Seed, GGS.StegoImageData, GGS.ImgWidth*3, GGS.ImgHeight);
			GGS.MessageBytes = decryptVigenere(GGS.MessageBytes,key);
			for(int i=0;i<GGS.MessageBytes.length;i++){
				System.out.print((char)GGS.MessageBytes[i]);
			}
			this.lblFileName.setText(GGS.StegoFileName);
			this.stegoFile = new byte[GGS.MessageBytes.length];
			System.arraycopy(GGS.MessageBytes, 0, this.stegoFile, 0, GGS.MessageBytes.length);
			for(int i=0;i<GGS.MessageBytes.length;i++){
				System.out.print((char)stegoFile[i]);
			}
		}
		return ret;
	}
	
	private byte[] encryptVigenere(byte[] plaintext, String key) {
		byte[] ciphertext = new byte[plaintext.length];
		int i;
		for (i=0; i<plaintext.length; i++) {
			int curKey = (int)(key.charAt(i%key.length()));
			int curPlain = (int)(plaintext[i]);
			ciphertext[i] = (byte)((curPlain+curKey)%256);
		}
		return ciphertext;
	}
	
	private byte[] decryptVigenere(byte[] ciphertext, String key) {
		byte[] plaintext = new byte[ciphertext.length];
		int i;
		for (i=0; i<ciphertext.length; i++) {
			int curKey = (int)(key.charAt(i%key.length()));
			int curCipher = (int)(ciphertext[i]);
			plaintext[i] = (byte)((((curCipher-curKey)%256) < 0) ? ((curCipher-curKey)%256)+256 : ((curCipher-curKey)%256));
		}
		return plaintext;
	}
	
	private Image scaleImage(BufferedImage bi, int width, int height) {
        int newWidth = bi.getWidth();
        int newHeight = bi.getHeight();
        if (bi.getWidth() > width) {
        	newWidth = width;
        	newHeight = (newWidth * bi.getHeight()) / bi.getWidth();
        }
        if (newHeight > height) {
        	newHeight = height;
            newWidth = (newHeight * bi.getWidth()) / bi.getHeight();
        }
		return bi.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	}
	
	private double PSNR (BufferedImage coverImage, BufferedImage stegoImage) {
		int width = coverImage.getWidth();
		int height = coverImage.getHeight();
		int[] pixelArrayCover = new int[width*height];
		coverImage.getRGB(0, 0, width, height, pixelArrayCover, 0, width);
		int[] pixelArrayStego = new int[width*height];
		stegoImage.getRGB(0, 0, width, height, pixelArrayStego, 0, width);
		
		double red = 0;
		double green = 0;
		double blue = 0;
		Color c1, c2;
		for (int i=0; i<width*height; i++) {
			c1 = new Color (pixelArrayCover[i]);
			c2 = new Color (pixelArrayStego[i]);
			red += Math.pow((c1.getRed() - c2.getRed()), 2);
			green += Math.pow((c1.getGreen() - c2.getGreen()), 2);
			blue += Math.pow((c1.getBlue() - c2.getBlue()), 2);
		}
		double rms = (red+green+blue)/(width*height*3);
		return (10 * (Math.log10(Math.pow(255, 2)/rms)));
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GGS = new GandharbaStegano();
		
		frame = new JFrame("Steganography");
		frame.setBounds(0, 0, 1000, 650);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblStegoTechnique = new JLabel("Stego Technique :");
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"LSB Standard", "LSB Xin Liao", "LSB Gandharba Swain"}));
		
		btnOpenCoverImage = new JButton("Open Cover-Image");
		
		btnSaveCoverImage = new JButton("Save Cover-Image");
		
		btnOpenStegoImage = new JButton("Open Stego-Image");
		
		btnSaveStegoImage = new JButton("Save Stego-Image");
		
		JPanel panelCoverImage = new JPanel();
		panelCoverImage.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JPanel panelStegoImage = new JPanel();
		panelStegoImage.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		btnOpenStegoFile = new JButton("Open Stego-File");
		
		lblFileName = new JLabel("File name will be shown here");
		
		JLabel lblStegokey = new JLabel("Stego-Key :");
		
		txtKey = new JTextField();
		txtKey.setText("key");
		txtKey.setColumns(10);
		
		btnEncode = new JButton("Encode ->");
		
		btnDecode = new JButton("<- Decode");
		
		JPanel panelProperties = new JPanel();
		panelProperties.setBorder(new TitledBorder(null, "Properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panelProperties, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnOpenStegoFile)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblFileName))
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(lblStegoTechnique)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
											.addGroup(groupLayout.createSequentialGroup()
												.addComponent(btnOpenCoverImage)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnSaveCoverImage))
											.addComponent(panelCoverImage, GroupLayout.PREFERRED_SIZE, 388, GroupLayout.PREFERRED_SIZE))
										.addGap(33)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
											.addComponent(btnEncode)
											.addGroup(groupLayout.createSequentialGroup()
												.addGap(17)
												.addComponent(lblStegokey))
											.addComponent(txtKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(btnDecode))
										.addGap(26)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
											.addGroup(groupLayout.createSequentialGroup()
												.addComponent(btnOpenStegoImage)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnSaveStegoImage))
											.addComponent(panelStegoImage, GroupLayout.PREFERRED_SIZE, 394, GroupLayout.PREFERRED_SIZE)))))
							.addGap(33))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(27)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblStegoTechnique)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(30)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnOpenStegoImage)
										.addComponent(btnSaveStegoImage))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panelStegoImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnOpenCoverImage)
										.addComponent(btnSaveCoverImage))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panelCoverImage, GroupLayout.PREFERRED_SIZE, 339, GroupLayout.PREFERRED_SIZE)))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnOpenStegoFile)
								.addComponent(lblFileName)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(174)
							.addComponent(lblStegokey)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnEncode, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnDecode, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addGap(138)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panelProperties, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(35, Short.MAX_VALUE))
		);
		
		JLabel lblMaximumCapacity = new JLabel("- Maximum capacity :");
		
		JLabel lblPsnr = new JLabel("- PSNR :");
		
		lblCapacity = new JLabel("capacity");
		
		lblPsnrValue = new JLabel("psnr value");
		GroupLayout gl_panelProperties = new GroupLayout(panelProperties);
		gl_panelProperties.setHorizontalGroup(
			gl_panelProperties.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelProperties.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelProperties.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelProperties.createSequentialGroup()
							.addComponent(lblMaximumCapacity)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblCapacity))
						.addGroup(gl_panelProperties.createSequentialGroup()
							.addComponent(lblPsnr)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblPsnrValue)))
					.addContainerGap(781, Short.MAX_VALUE))
		);
		gl_panelProperties.setVerticalGroup(
			gl_panelProperties.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelProperties.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_panelProperties.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMaximumCapacity)
						.addComponent(lblCapacity))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelProperties.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPsnr)
						.addComponent(lblPsnrValue))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		panelProperties.setLayout(gl_panelProperties);
		panelStegoImage.setLayout(new BorderLayout(0, 0));
		
		lblStegoImage = new JLabel("");
		panelStegoImage.add(lblStegoImage);
		panelCoverImage.setLayout(new BorderLayout(0, 0));
		
		lblCoverImage = new JLabel("");
		panelCoverImage.add(lblCoverImage);
		frame.getContentPane().setLayout(groupLayout);
	}
}
