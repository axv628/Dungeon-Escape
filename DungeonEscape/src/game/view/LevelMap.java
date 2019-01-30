package game.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LevelMap extends JPanel {
	private JButton level1;
	private JButton level2;
	private JButton level3;
	private JButton back1;
	private JLabel label;

	private WindowManager manager;
	private Log log;
	
	private int width, height;
	
	public LevelMap(WindowManager manager, Log log){
		this.manager = manager;
		this.log = log;
		width = manager.getWidth();
		height = manager.getHeight();
		
		setLayout(null);
		label=new JLabel("SELECT LEVEL");
		label.setBounds((int) (0.4*width) ,(int) (0.2*height),
				(int)( 0.2*width), (int)( 0.09*height));
		label.setFont(new Font("Algerian",Font.BOLD, 50));
		add(label);
		
		level1 =new JButton();
		level2= new JButton();
		level3= new JButton();
		back1= new JButton();
		
		level1.setBounds((int) (0.175 * width),(int)(0.45*height),
				(int) (0.15 * width),(int)(0.1*height));
		level2.setBounds((int) (0.425 * width),(int)(0.45*height),
				(int) (0.15 * width),(int)(0.1*height));
		level3.setBounds((int) (0.675 * width),(int)(0.45*height),
				(int) (0.15 * width),(int)(0.1*height));
		back1.setBounds((int) (0.02 * width),(int)(0.03*height),
				(int) (0.10 * width),(int)(0.06*height));
		
		level1.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		level2.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		level3.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		back1.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		
		
		add(level1);
		add(level2);
		add(level3);
		add(back1);
		
		level1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("1");
			}

		});
		
		level2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((log.getLevelsCompleted()[0])){
				manager.changeState("2");
				}
			}
			
		});
		
		level3.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((log.getLevelsCompleted()[1])){
				manager.changeState("3");
				}
			}
			
		});
		
		
		back1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("MenuPanel");
			}
			
		});
	}
	
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
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			BufferedImage bg = ImageIO.read(new File("TheWildEscape/src/resources/backgrounds/menu.png")); 
			width= this.getWidth();
			height=this.getHeight();
			g.drawImage(bg, 0, 0, width, height, null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		level1.setBounds((int) (0.175 * width),(int)(0.45 * height),
				(int) (0.15 * width),(int)(0.1 * height));
		level2.setBounds((int) (0.425 * width),(int)(0.45 * height),
				(int) (0.15 * width),(int)(0.1 * height));
		level3.setBounds((int) (0.675 * width),(int)(0.45 * height),
				(int) (0.15 * width),(int)(0.1 * height));
		back1.setBounds((int) (0.02 * width),(int)(0.03 * height),
				(int) (0.10 * width),(int)(0.06 * height));
		label.setBounds((int) (0.4 * width) ,(int) (0.2 * height),
				(int)( 700 ), (int)( 150 ));
		
		addImageToButton(level1,"TheWildEscape/src/resources/buttons/l1.png");
		addImageToButton(level2,"TheWildEscape/src/resources/buttons/l2.png");
		addImageToButton(level3,"TheWildEscape/src/resources/buttons/l3.png");
		addImageToButton(back1,"TheWildEscape/src/resources/buttons/back.png");
    }
}