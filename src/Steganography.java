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

import javax.imageio.ImageIO;
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
	
	private byte[] dirtyStegoFile;
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
						System.out.println(stegoFile.length);
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
			    	JFrame frame1 = new JFrame("Cover Image");
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
		System.out.println("Test");
		boolean success = false;
		errorMessage = "";
		if (coverImage==null) {
			errorMessage = "Cover image belum dipilih!";
		}
		else if (stegoFile==null) {
			errorMessage = "File message belum dipilih!";
		}
		else if (txtKey.getText().equals("")) {
			errorMessage = "Key belum dimasukkan!";
		}
		else {
			//System.out.println(comboBox.getSelectedItem().toString());
			if (comboBox.getSelectedItem().toString().equals("LSB Standard")) {
				success = standardEncode(coverImage, stegoFile, txtKey.getText(), lblFileName.getText());
			}
			else if (comboBox.getSelectedItem().toString().equals("LSB Xin Liao")) {
				success = xinLiaoEncode(coverImage, stegoFile, txtKey.getText(), lblFileName.getText());
			}
			else { // comboBox.equals("LSB Gandharba Swain")
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
			if (comboBox.equals("LSB Standard")) {
				success = standardDecode(coverImage, key);
			}
			else if (comboBox.equals("LSB Xin Liao")) {
				success = xinLiaoDecode(coverImage, key);
			}
			else { // comboBox.equals("LSB Gandharba Swain")
				success = gandharbaDecode(coverImage, key);
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
		return false;
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
	
	private boolean xinLiaoEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di encode, tulis juga errornya kenapa di errorMessage
		
		System.out.println("Encode!");
		
		for(int i=0;i<coverImage.getWidth();i++){
			for(int j=0;j<coverImage.getHeight();j++){
				Color color = new Color(coverImage.getRGB(i, j));
				System.out.println(color.getRed() + " " + color.getBlue() + " " + color.getBlue());
			}
		}
		
		
		return false;
	}
	
	private boolean gandharbaEncode(BufferedImage coverImage, byte[] stegoBytes, String key, String fileName) {
		// tulis image hasil encode di stegoImage
		// tulis max capacity di lblCapacity.setText(text)
		// return true kalo bisa di-encode, false kalo ga bisa
		// kalo ga bisa di-encode, tulis juga errornya kenapa di errorMessage
		return false;
	}
	
	private boolean standardDecode(BufferedImage stegoImage, String key) {
		// tulis image hasil decode di coverImage
		// tulis byte[] hasil decode di stegoFile
		// tulis nama file di lblFileName.setText()
		// return true kalo bisa di-decode, false kalo ga bisa
		// kalo ga bisa di-decode, tulis juga errornya kenapa di errorMessage
		return false;
	}
	
	private boolean xinLiaoDecode(BufferedImage stegoImage, String key) {
		// tulis image hasil decode di coverImage
		// tulis byte[] hasil decode di stegoFile
		// tulis nama file di lblFileName.setText()
		// return true kalo bisa di-decode, false kalo ga bisa
		// kalo ga bisa di-decode, tulis juga errornya kenapa di errorMessage
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
