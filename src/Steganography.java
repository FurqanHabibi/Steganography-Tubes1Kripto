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
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("PNG Files", "png");
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
						stegoImage = ImageIO.read(file);
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
						coverImage = ImageIO.read(file);
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
			// calculate psnr
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
					Files.write(file.toPath(), stegoFile);
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
	    	randomInts[i] = i;
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
				n = randomInts[i];
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
	    	randomInts[i] = i;
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
			n = randomInts[i];
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
		return 5;
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
				ch[i] = pixels[i].getBlue();
			} else if(layer == 2){
				ch[i] = pixels[i].getGreen();
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
	
	private int setEmbeddedPixel(int pixel, int[] bits, int k){
		for(int i=0;i<k;i++){
			if(bits[i] == 1){
				pixel |= 1 << i;
			} else {
				pixel &= ~(1 << i);
			}
			
		}
		return pixel;
	}
	
	// k bit is embedded into the origin pixel, resulting in LSB pixel.
	// return modified LSB pixel
	private int getModifiedPixel(int LSBPixel, int originPixel, int k){
		int diff = Math.abs(LSBPixel - originPixel);
		
		// change MSB of embedded pixel to 1
		int modifiedPixelOne = (LSBPixel | (1 << (k+1)));
		int modifiedPixelZero = (LSBPixel & ~(1 << (k+1)));
		
		int oneDiff = Math.abs(modifiedPixelOne - originPixel);
		int zeroDiff = Math.abs(modifiedPixelZero - originPixel);
		
		if(oneDiff < diff && oneDiff < zeroDiff){
			return modifiedPixelOne;
		} else if(zeroDiff < diff && zeroDiff < oneDiff){
			return modifiedPixelZero;
		} else {
			return LSBPixel;
		}		
	}
	
	private int[] readjustPixelLayer(int[] pixelComp, int k){ 
		int[] readjustPixelComp = new int[4];
		float min3 = Float.MAX_VALUE;
		float D = getAvgDiff(pixelComp);
		
		for(int i=-1;i<2;i++){
			int[] tempPixel = new int[4];
			for(int j=0;j<4;j++){
				tempPixel[j] = pixelComp[j] + i * ((int) Math.pow(2, k));
			}
			
			float tempD = getAvgDiff(tempPixel);
			if(checkLevel(D) == checkLevel(tempD) && !isErrorBlock(tempPixel)){
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
	
	private boolean xinLiaoEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di encode, tulis juga errornya kenapa di errorMessage
		
		byte[] header = (fileName+"#"+Integer.toString(stegoBytes.length)+"#").getBytes(StandardCharsets.UTF_8);
		byte[] embeddedStegoBytes = Arrays.copyOf(header, header.length + stegoBytes.length);
		
		System.arraycopy(stegoBytes, 0, embeddedStegoBytes, header.length, stegoBytes.length);
		embeddedStegoBytes = encryptVigenere(embeddedStegoBytes, key);
		
		// convert bytes to bits
	    int stegoBitsLength = embeddedStegoBytes.length * 8;
		int[] embeddedStegoBits = new int[stegoBitsLength];
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
							// embed
							// use low k
							if(getAvgDiff(pixels, k) < getThreshold()){
								for(int m=0;m<4;m++){
									int[] embed = new int[getLowK()];
									for(int n=embeddedBitIndex;n<embeddedBitIndex+getLowK();n++){
										embed[n-embeddedBitIndex] = embeddedStegoBits[embeddedBitIndex];
									}
									embeddedBitIndex += getLowK();
									embeddedLayerPixels[k][m] = setEmbeddedPixel(getBlockLayer(pixels, k)[m], embed, getLowK());
									
									// apply modified LSB subtitution method
									embeddedLayerPixels[k][m] = getModifiedPixel(embeddedLayerPixels[k][m], getBlockLayer(pixels, k)[m], getLowK());
								}
								
								// readjust
								embeddedLayerPixels[k] = readjustPixelLayer(embeddedLayerPixels[k], getLowK());
							} else { // use high k
								for(int m=0;m<4;m++){
									int[] embed = new int[getHighK()];
									for(int n=embeddedBitIndex;n<embeddedBitIndex+getHighK();n++){
										embed[n-embeddedBitIndex] = embeddedStegoBits[embeddedBitIndex];
									}
									embeddedBitIndex += getHighK();
									embeddedLayerPixels[k][m] = setEmbeddedPixel(getBlockLayer(pixels, k)[m], embed, getHighK());
									
									// apply modified LSB subtitution method
									embeddedLayerPixels[k][m] = getModifiedPixel(embeddedLayerPixels[k][m], getBlockLayer(pixels, k)[m], getHighK());
								}
															
								// readjust
								embeddedLayerPixels[k] = readjustPixelLayer(embeddedLayerPixels[k], getHighK());
							}
						}
					}
					
					// put into stego images
					// for each pixel, create RGB value
					
					int posX[] = new int[]{j,j+1,j,j+1};
					int posY[] = new int[]{i,i,i+1,i+1};
					
					for(int m=0;m<4;m++){
						Color pixel = new Color(embeddedLayerPixels[0][m], embeddedLayerPixels[1][m], embeddedLayerPixels[2][m]);
						stegoImage.setRGB(posX[m], posY[m], pixel.getRGB());
					}
					
				} else {
					
				}				
			}
		}
		
		return true;
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
		return false;
	}
	
	private boolean gandharbaEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di-encode, tulis juga errornya kenapa di errorMessage
		return false;
	}
	
	private boolean gandharbaDecode(BufferedImage stegoImage, String key) {
		// tulis image hasil decode di coverImage
		// tulis byte[] hasil decode di stegoFile
		// tulis nama file di lblFileName.setText()
		// return true kalo bisa di-decode, false kalo ga bisa
		// kalo ga bisa di-decode, tulis juga errornya kenapa di errorMessage
		return false;
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
	
	public Image scaleImage(BufferedImage bi, int width, int height) {
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
