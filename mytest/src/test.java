import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;


public class test extends JFrame{

	private static final long serialVersionUID = 1L;

	private BufferedImage nowImg = null;  
	private BufferedImage resultImg = null;  

	public test() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		setResizable(false);
		setSize(437, 399);
		setLocation(500, 300);
		
		File file = new File(System.getProperty("user.dir") + "//src//test.png");
		try {
			nowImg = ImageIO.read(file);
			resultImg = nowImg;
		} catch (IOException e) {
			e.printStackTrace();
		}

		JPanel jp = new JPanel(){
			protected void paintComponent(Graphics g) {  
				Graphics2D g2 = (Graphics2D) g;  
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
				if (resultImg != null) {  
					g2.drawImage(resultImg, 0, 0, resultImg.getWidth(), resultImg.getHeight(), null);  
				}
			}
		};

		add(jp, BorderLayout.CENTER);
		
		new WaterFilter().filter(nowImg, resultImg);
		

		setVisible(true);

	}

	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new test();
	}
	
}
