
package swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import math.FractalCalculator;
import net.miginfocom.swing.MigLayout;

public class SaveImageMenu extends JFrame {

	private static final long serialVersionUID = 6912621256386165914L;
	private JPanel contentPane;
	private JFormattedTextField textField;
	private JFormattedTextField textField_1;

	JFormattedTextField realMinTextField;
	JFormattedTextField realMaxTextField;
	JFormattedTextField imaginaryMinTextField;
	JFormattedTextField imaginaryMaxTextField;

	private String[] options = { "16:9", "16:10", "4:3" };
	private float[] ratios = { 16 / 9, 16 / 10, 4 / 3 };
	private int selectedRatio = 0;

	public SaveImageMenu(List<FractalDrawer> drawers) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][grow]", "[][][][][][][][][][grow,top]"));

		setType(JFrame.Type.UTILITY);
		setUndecorated(true);

		JLabel lblWidthOfImage = new JLabel("Width of image (pixels): ");
		contentPane.add(lblWidthOfImage, "cell 0 0,alignx trailing");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		NumberFormat f = NumberFormat.getIntegerInstance();
		NumberFormatter form = new NumberFormatter(f);
		form.setValueClass(Integer.class);
		form.setMinimum(1);
		f.setGroupingUsed(false);

		textField = new JFormattedTextField(form);
		textField.setText(Integer.toString((int) screenSize.getWidth()));
		contentPane.add(textField, "cell 1 0,growx");
		textField.setColumns(10);

		JLabel lblHeightOfImage = new JLabel("Height of image (pixels): ");
		contentPane.add(lblHeightOfImage, "cell 0 1,alignx trailing");

		textField_1 = new JFormattedTextField(form);
		textField_1.setText(Integer.toString((int) screenSize.getHeight()));
		contentPane.add(textField_1, "cell 1 1,growx");
		textField_1.setColumns(10);

		JCheckBox chckbxPreserveAspectRatio = new JCheckBox("Preserve Aspect Ratio?");
		contentPane.add(chckbxPreserveAspectRatio, "cell 0 2");

		 JComboBox<String> comboBox = new JComboBox<String>(options);
//		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setSelectedIndex(selectedRatio);
		contentPane.add(comboBox, "cell 1 2,growx");

		JLabel lblRealMinimum = new JLabel("Real minimum: ");
		contentPane.add(lblRealMinimum, "cell 0 3,alignx trailing");

		DecimalFormat df = new DecimalFormat("#.####");

		realMinTextField = new JFormattedTextField();
		realMinTextField.setText(df.format(drawers.get(0).getFractalCalculator().getXMin()));
		contentPane.add(realMinTextField, "cell 1 3,growx");

		JLabel lblRealMaximum = new JLabel("Real maximum: ");
		contentPane.add(lblRealMaximum, "cell 0 4,alignx trailing");

		realMaxTextField = new JFormattedTextField();
		realMaxTextField.setText(df.format(drawers.get(0).getFractalCalculator().getXMax()));
		contentPane.add(realMaxTextField, "cell 1 4,growx");

		JLabel lblImaginaryMinimum = new JLabel("Imaginary minimum: ");
		contentPane.add(lblImaginaryMinimum, "cell 0 5,alignx trailing");

		imaginaryMinTextField = new JFormattedTextField();
		imaginaryMinTextField.setText(df.format(drawers.get(0).getFractalCalculator().getYMin()));
		contentPane.add(imaginaryMinTextField, "cell 1 5,growx");

		JLabel lblImaginaryMaximum = new JLabel("Imaginary maximum: ");
		contentPane.add(lblImaginaryMaximum, "cell 0 6,alignx trailing");

		imaginaryMaxTextField = new JFormattedTextField();
		imaginaryMaxTextField.setText(df.format(drawers.get(0).getFractalCalculator().getYMax()));
		contentPane.add(imaginaryMaxTextField, "cell 1 6,growx");

		JRadioButton rdbtnFractalPanel = new JRadioButton("Fractal Panel");
		contentPane.add(rdbtnFractalPanel, "cell 0 7 2 1,alignx center");

		JRadioButton rdbtnJuliaPanel = new JRadioButton("Julia Panel");
		contentPane.add(rdbtnJuliaPanel, "cell 0 8 2 1,alignx center");

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnFractalPanel);
		group.add(rdbtnJuliaPanel);

		rdbtnFractalPanel.setSelected(true);

		JButton btnSaveImage = new JButton("Save Image");
		btnSaveImage.addActionListener(e -> {
			saveImage(drawers, rdbtnFractalPanel.isSelected());
		});
		contentPane.add(btnSaveImage, "cell 0 9,alignx right");

		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(e -> {
			this.dispose();
		});
		contentPane.add(btnExit, "cell 1 9");

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void saveImage(List<FractalDrawer> drawers, boolean fractalPanelSelected) {
		int w = 0;
		int h = 0;
		try {
			w = Integer.parseInt(textField.getText());
			h = Integer.parseInt(textField_1.getText());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(this, "Width or height not a valid number");
			return;
		}
		if (w < 1 || h < 1) {
			JOptionPane.showMessageDialog(this, "Width and height must be greater or equal to 1");
			return;
		}

		double xMin;
		double xMax;
		double yMin;
		double yMax;
		try {
			xMin = Double.parseDouble(realMinTextField.getText());
			xMax = Double.parseDouble(realMaxTextField.getText());
			yMin = Double.parseDouble(imaginaryMinTextField.getText());
			yMax = Double.parseDouble(imaginaryMaxTextField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Real/Imaginary fields must be valid");
			return;
		}

		final int width = w;
		final int height = h;

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (.jpg, .jpeg, .png)", "jpg", "jpeg", "png");
		chooser.setFileFilter(filter);
		final FractalDrawer drawer = fractalPanelSelected ? drawers.get(0) : drawers.get(1);
		int result = chooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			System.out.println("Writing file");
			File file = chooser.getSelectedFile();
			FractalCalculator c = drawer.getFractalCalculator().clone();
			new Thread(() -> {
				BufferedImage img = drawer.getImage(c, width, height, xMin, xMax, yMin, yMax);
				System.out.println("image null : " + img == null);
				String fileName = file.toString();
				String pattern = "((^|, )(\\.png|\\.jpg|\\.jpeg))";
				if (!Pattern.compile(pattern).matcher(fileName).find()) {
					if (Pattern.compile("\\..*").matcher(fileName).find()) {
						JOptionPane.showMessageDialog(this, "Wrong file type");
						saveImage(drawers, fractalPanelSelected);
						return;
					} else {
						fileName += ".png";
					}
				}

				File newFile = new File(fileName);
				if (newFile.exists()) {
					int choice = JOptionPane.showConfirmDialog(this, "That file already exists, overwrite it?",
							"Confirmation", JOptionPane.YES_NO_OPTION);
					if (choice != JOptionPane.YES_OPTION) {
						if (choice == JOptionPane.NO_OPTION) {
							saveImage(drawers, fractalPanelSelected);
							return;
						} else {
							return;
						}
					}
				}

				System.out.println("File to write to: " + newFile);
				String type = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toUpperCase();
				System.out.println("Type: " + type);
				try {
					System.out.println("Image written successfully " + ImageIO.write(img, type, newFile));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}).start();
			this.dispose();
		}
	}

	private float getRatio(String selected) {
		for (int i = 0; i < ratios.length; i++) {
			if (options[i].equals(selected)) {
				return ratios[i];
			}
		}
		return -1f;
	}

}
