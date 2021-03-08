package fr.lapstime.factiona.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.launcher.util.UsernameSaver;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener{
	
	private Image background = Swinger.getResource("background.png");
	
	private UsernameSaver saver = new UsernameSaver(Launcher.F_INFOS);
	
	private JTextField usernameField = new JTextField(saver.getUsername(""));
	private JPasswordField passwordField = new JPasswordField();
	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png")); 
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quit.png")); 
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hide.png")); 
	
	private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private JLabel infoLabel = new JLabel("Clique sur jouer !", SwingConstants.CENTER);
	
	public LauncherPanel() {
		this.setLayout(null);
		
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setForeground(Color.WHITE);
		usernameField.setFont(getFont().deriveFont(40F));
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setBounds(1029, 404, 450, 86);
		this.add(usernameField);
		
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setForeground(Color.WHITE);
		passwordField.setFont(getFont().deriveFont(40F));
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setBounds(1029, 593, 450, 86);
		this.add(passwordField);
		
		playButton.setBounds(1035, 766);
		playButton.addEventListener(this);
		this.add(playButton);
		
		quitButton.setBounds(1577, 2, 95, 97);
		quitButton.addEventListener(this);
		this.add(quitButton);
		
		hideButton.setBounds(1438, 2, 95, 97);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		progressBar.setBounds(0, 936, 1679, 51);
		this.add(progressBar);
		
		infoLabel.setBounds(0, 882, 1679, 44);
		infoLabel.setFont(passwordField.getFont());
		infoLabel.setForeground(Color.WHITE);
		this.add(infoLabel);
	}
	
	@Override
	public void onEvent(SwingerEvent e) {
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur, veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Launcher.auth(usernameField.getText(), passwordField.getText());
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter. " + e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						return;
					}
					
					try {
						Launcher.update();
					} catch (Exception e) {
						Launcher.interruptThread();
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de mettre à jour le jeu. " + e, "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						return;
					}
				}
			};
			t.start();
		} else if(e.getSource() == quitButton)
			System.exit(0);
		else if(e.getSource() == hideButton)
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	private void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {
		return progressBar;
	}
	
	public void setInfoText(String text) {
		infoLabel.setText(text);
	}
	
}
