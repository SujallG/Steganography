import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class Decryption extends JFrame implements ActionListener {
	JButton open = new JButton("Open"), decode = new JButton("Decode"), reset = new JButton("Reset");
	JTextArea message = new JTextArea(10, 3);
	BufferedImage image = null;
	JScrollPane imagePane = new JScrollPane();

	public Decryption() {
		super("Decode stegonographic message in image");
		assembleInterface();
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);

		open.setBackground(Color.black);
		open.setForeground(Color.black);
		open.setFont(new Font("Monaco", Font.BOLD, 20));

		decode.setBackground(Color.black);
		decode.setForeground(Color.black);
		decode.setFont(new Font("Monaco", Font.BOLD, 20));

		reset.setBackground(Color.black);
		reset.setForeground(Color.black);
		reset.setFont(new Font("Monaco", Font.BOLD, 20));
	}

	private void assembleInterface() {
		JPanel p = new JPanel(new FlowLayout());
		p.add(open);
		p.add(decode);
		p.add(reset);

		this.getContentPane().add(p, BorderLayout.NORTH);
		open.addActionListener(this);
		decode.addActionListener(this);
		reset.addActionListener(this);
		open.setMnemonic('O');
		decode.setMnemonic('D');
		reset.setMnemonic('R');

		p = new JPanel(new GridLayout(1, 1));
		p.add(new JScrollPane(message));
		message.setFont(new Font("Arial", Font.BOLD, 20));
		p.setBorder(BorderFactory.createTitledBorder("Decoded message"));
		message.setEditable(false);
		this.getContentPane().add(p, BorderLayout.SOUTH);

		imagePane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
		this.getContentPane().add(imagePane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent ae) {
		Object o = ae.getSource();
		if (o == open)
			openImage();
		else if (o == decode)
			decodeMessage();
		else if (o == reset)
			resetInterface();
	}

	private java.io.File showFileDialog(boolean open) {
		JFileChooser fc = new JFileChooser("Open an image");
		javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
			public boolean accept(java.io.File f) {
				String name = f.getName().toLowerCase();
				return f.isDirectory() || name.endsWith(".png") || name.endsWith(".bmp");
			}

			public String getDescription() {
				return "Image (*.png, *.bmp)";
			}
		};
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(ff);

		if (open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
			return fc.getSelectedFile();
		else if (!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)
			return fc.getSelectedFile();
		return null;
	}

	private void openImage() {
		java.io.File f = showFileDialog(true);
		try {
			image = ImageIO.read(f);
			JLabel l = new JLabel(new ImageIcon(image));
			imagePane.getViewport().add(l);
			this.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void decodeMessage() {
		if (image == null) {
			JOptionPane.showMessageDialog(null, "first open a picture");
			return;
		}

		int l = extractInteger(image, 0, 0);
		byte b[] = new byte[l];
		for (int i = 0; i < l; i++)
			b[i] = extractByte(image, i * 8 + 32, 0);
		message.setText(new String(b));
	}

	private int extractInteger(BufferedImage img, int start, int storageBit) {
		int mX = img.getWidth(), mY = img.getHeight(), sX = start / mY, sY = start - sX * mY, count = 0, l = 0;
		for (int i = sX; i < mX && count < 32; i++)
			for (int j = sY; j < mY && count < 32; j++)
				l = setBitValue(l, count++, getBitValue(img.getRGB(i, j), storageBit));

		return l;
	}

	private byte extractByte(BufferedImage img, int start, int storageBit) {
		int mX = img.getWidth(), mY = img.getHeight(), sX = start / mY, sY = start - sX * mY, count = 0;
		byte b = 0;

		for (int i = sX; i < mX && count < 8; i++)
			for (int j = sY; j < mY && count < 8; j++)
				b = (byte) setBitValue(b, count++, getBitValue(img.getRGB(i, j), storageBit));

		return b;
	}

	private void resetInterface() {
		message.setText("");
		imagePane.getViewport().removeAll();
		image = null;
		this.validate();
	}

	private int getBitValue(int n, int location) {
		return (n & (int) Math.round(Math.pow(2, location))) == 0 ? 0 : 1;
	}

	private int setBitValue(int n, int location, int bit) {
		int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
		if (bv == bit)
			return n;
		if (bv == 0 && bit == 1)
			n |= toggle;
		else if (bv == 1 && bit == 0)
			n ^= toggle;
		return n;
	}

	// public static void main(String arg[]) {
	// Decryption newClass = new Decryption();
	// }
}