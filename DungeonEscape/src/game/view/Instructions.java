package game.view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Instructions extends JPanel {
	
	private JButton back;
	private JTextArea text;
	
	private WindowManager manager;
	
	private int width, height;
	
	public static void addImageToButton(JButton button, String imageLocation){
		Image image;
		try {
			image = ImageIO.read(new File(imageLocation));
			ImageIcon icon1 = new ImageIcon(image.getScaledInstance(
					button.getWidth(),button.getHeight(),Image.SCALE_DEFAULT)); 
			button.setIcon(icon1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Instructions(WindowManager manager){
		this.manager = manager;
		width = manager.getWidth();
		height = manager.getHeight();
		
		setLayout(null);
		back= new JButton();
		back.setBounds((int) (0.02 * width),(int)(0.03*height),
				(int) (0.10 * width),(int)(0.06*height));
		back.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("MenuPanel");
			}
		});
		add(back);
		
		text= new JTextArea();
		text.setBounds((int) (0.2 * width),(int)(0.1*height),
				(int) (0.6 * width),(int)(0.8*height));
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("TheWildEscape/src/resources/instructions.txt"));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    text.setText(everything);
		} catch(IOException e){
		    e.printStackTrace();
		}
		
		add(text);
		addImageToButton(back,"TheWildEscape/src/resources/buttons/back.png");
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			BufferedImage bg = ImageIO.read(new File("TheWildEscape/src/resources/backgrounds/B1.png")); 
			width= this.getWidth();
			height=this.getHeight();
			g.drawImage(bg, 0, 0, width, height, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		back.setBounds((int) (0.02 * width),(int)(0.03*height),
				(int) (0.10 * width),(int)(0.06*height));
		text.setBounds((int) (0.2 * width),(int)(0.1*height),
				(int) (0.6 * width),(int)(0.8*height));
		
		addImageToButton(back,"TheWildEscape/src/resources/buttons/back.png");
	}
}
