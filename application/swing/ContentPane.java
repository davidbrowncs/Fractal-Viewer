
package swing;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import math.FractalCalculator;
import net.miginfocom.swing.MigLayout;
import util.DataObserver;
import util.DataSource;
import fractals.FractalType;

public class ContentPane extends JPanel {

	private static final long serialVersionUID = -7756613728210123704L;
	private FractalPanel fractalPanel1;
	private FractalPanel fractalPanel2;
	private JPanel fractalPanel;
	private JPanel juliaPanel;
	private JTextField textField;

	public ContentPane() {
		setLayout(new MigLayout("insets 3", "[grow]", "[][grow,grow]"));

		JPanel panel_1 = new JPanel();
		add(panel_1, "cell 0 0,growx");
		panel_1.setLayout(new BorderLayout());

		JMenuBar menuBar = new JMenuBar();
		panel_1.add(menuBar, BorderLayout.CENTER);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmSaveImage = new JMenuItem("Save Image");
		mnFile.add(mntmSaveImage);
		mntmSaveImage.addActionListener(e -> {
			List<FractalDrawer> drawers = new ArrayList<>();
			drawers.add(fractalPanel1.getDrawer());
			drawers.add(fractalPanel2.getDrawer());
			new SaveImageMenu(drawers);
		});

		JSplitPane splitPane = new JSplitPane();
		add(splitPane, "cell 0 1,grow");

		DefaultListModel<String> model = new DefaultListModel<String>();
		List<String> nameList = new ArrayList<>();
		for (int i = 0; i < FractalType.values().length; i++) {
			nameList.add(FractalType.values()[i].getName());
		}
		Collections.sort(nameList);
		nameList.forEach(e -> model.addElement(e));

		JList<String> list = new JList<>(model);
		list.addListSelectionListener(e -> {
			FractalType fractal = FractalType.getFractalType(list.getSelectedValue());
			setFractal(fractal);
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scrollPane);

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new MigLayout("", "[grow,fill][grow,fill]", "[222px,grow,fill][]"));

		fractalPanel = new JPanel();
		fractalPanel.setFocusable(true);
		fractalPanel.setLayout(new BorderLayout());
		panel.add(fractalPanel, "cell 0 0,grow");

		juliaPanel = new JPanel();
		juliaPanel.setFocusable(true);
		juliaPanel.setLayout(new BorderLayout());
		panel.add(juliaPanel, "cell 1 0,grow");

		addParentAndJulia(FractalType.MANDELBROT);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, "cell 0 1,grow");
		panel_2.setLayout(new MigLayout("", "[][][grow,center][grow,center][grow,center][grow,center][grow,fill]", "[][]"));

		JLabel label = new JLabel("Fractal iterations");
		panel_2.add(label, "cell 0 0,alignx center");

		JLabel lblMinReal = new JLabel("Min Real:");
		panel_2.add(lblMinReal, "cell 2 0,alignx center");

		JLabel lblMaxReal = new JLabel("Max Real:");
		panel_2.add(lblMaxReal, "cell 3 0,alignx center");

		JLabel lblMinImaginary = new JLabel("Min Imaginary:");
		panel_2.add(lblMinImaginary, "cell 4 0,alignx center");

		JLabel lblMaxImaginary = new JLabel("Max Imaginary:");
		panel_2.add(lblMaxImaginary, "cell 5 0,alignx center");

		JSlider slider_2 = new JSlider(JSlider.HORIZONTAL, 10, 1000, 50);
		slider_2.setMajorTickSpacing(1000);
		slider_2.setMinorTickSpacing(100);
		slider_2.addChangeListener((e) -> {
			if (!slider_2.getValueIsAdjusting()) {
				fractalPanel1.getFractalCalculator().setMaxIterations(slider_2.getValue());
				textField.setText(Integer.toString(slider_2.getValue()));
				repaint();
			}
		});
		panel_2.add(slider_2, "cell 0 1");

		NumberFormat f = NumberFormat.getIntegerInstance();
		NumberFormatter form = new NumberFormatter(f);
		form.setValueClass(Integer.class);
		form.setMaximum(1000);
		form.setMinimum(10);
		f.setGroupingUsed(false);
		textField = new JFormattedTextField(form);
		textField.setText("50");
		panel_2.add(textField, "cell 1 1,growx");
		textField.setColumns(10);
		textField.addActionListener((e) -> {
			try {
				int val = Integer.parseInt(textField.getText());
				fractalPanel1.getFractalCalculator().setMaxIterations(val);
				repaint();
				slider_2.setValue(val);
			} catch (Exception e1) {}
		});

		FractalCalculator c = fractalPanel1.getFractalCalculator();
		ObservingTextField minRealTextField_1 = new ObservingTextField(() -> {
			return c.getXMin();
		}, d -> {
			c.setXMin(d);
			repaint();
		});
		c.addDataObserver(minRealTextField_1);
		panel_2.add(minRealTextField_1, "cell 2 1,growx");

		ObservingTextField maxRealTextField_2 = new ObservingTextField(() -> {
			return c.getXMax();
		}, d -> {
			c.setXMax(d);
			repaint();
		});
		c.addDataObserver(maxRealTextField_2);
		panel_2.add(maxRealTextField_2, "cell 3 1,growx");

		ObservingTextField minImTextField_3 = new ObservingTextField(() -> {
			return c.getYMin();
		}, d -> {
			c.setYMin(d);
			repaint();
		});
		c.addDataObserver(minImTextField_3);
		panel_2.add(minImTextField_3, "cell 4 1,growx");

		ObservingTextField maxImTextField_4 = new ObservingTextField(() -> {
			return c.getYMax();
		}, d -> {
			c.setYMax(d);
			repaint();
		});
		c.addDataObserver(maxImTextField_4);
		panel_2.add(maxImTextField_4, "cell 5 1,growx");

		JButton button = new JButton("Choose fractal color");
		panel_2.add(button, "cell 6 1,alignx left");

		JPanel panel_3 = new JPanel();
		panel.add(panel_3, "cell 1 1,grow");
		panel_3.setLayout(new MigLayout("", "[][][grow,center][grow,center][grow,center][grow,center][grow,fill]", "[][]"));

		JLabel label_1 = new JLabel("Julia Iterations");
		panel_3.add(label_1, "cell 0 0");

		JLabel lblMinReal_1 = new JLabel("Min Real:");
		panel_3.add(lblMinReal_1, "cell 2 0");

		JLabel lblMaxReal_1 = new JLabel("Max Real:");
		panel_3.add(lblMaxReal_1, "cell 3 0");

		JLabel lblMinImaginary_1 = new JLabel("Min Imaginary:");
		panel_3.add(lblMinImaginary_1, "cell 4 0");

		JLabel lblMaxImaginary_1 = new JLabel("Max Imaginary:");
		panel_3.add(lblMaxImaginary_1, "cell 5 0");

		JFormattedTextField formattedTextField = new JFormattedTextField(form);
		formattedTextField.setText("50");
		formattedTextField.setColumns(10);
		panel_3.add(formattedTextField, "cell 1 1,growx");

		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 10, 1000, 50);
		slider.addChangeListener((e) -> {
			if (!slider.getValueIsAdjusting()) {
				fractalPanel2.getFractalCalculator().setMaxIterations(slider.getValue());
				formattedTextField.setText(Integer.toString(slider.getValue()));
				repaint();
			}
		});
		slider.setMinorTickSpacing(100);
		slider.setMajorTickSpacing(1000);
		panel_3.add(slider, "cell 0 1");

		formattedTextField.addActionListener((e) -> {
			try {
				int val = Integer.parseInt(formattedTextField.getText());
				fractalPanel2.getFractalCalculator().setMaxIterations(val);
				repaint();
				slider.setValue(val);
			} catch (Exception e1) {}
		});

		FractalCalculator c1 = fractalPanel2.getFractalCalculator();
		ObservingTextField minRealJTextField_5 = new ObservingTextField(() -> {
			return c1.getXMin();
		}, d -> {
			System.out.println("Setting x min: " + d);
			c1.setXMin(d);
			repaint();
		});
		c1.addDataObserver(minRealJTextField_5);
		panel_3.add(minRealJTextField_5, "cell 2 1,growx");

		ObservingTextField maxRealJTextField_6 = new ObservingTextField(() -> {
			return c1.getXMax();
		}, d -> {
			System.out.println("Setting x max: " + d);
			c1.setXMax(d);
			repaint();
		});
		c1.addDataObserver(maxRealJTextField_6);
		panel_3.add(maxRealJTextField_6, "cell 3 1,growx");

		ObservingTextField minImJTextField_7 = new ObservingTextField(() -> {
			return c1.getYMin();
		}, d -> {
			System.out.println("Setting y min: " + d);
			c1.setYMin(d);
			repaint();
		});
		c1.addDataObserver(minImJTextField_7);
		panel_3.add(minImJTextField_7, "cell 4 1,growx");

		ObservingTextField maxImJTextField_8 = new ObservingTextField(() -> {
			return c1.getYMax();
		}, d -> {
			System.out.println("Setting y max: " + d);
			c1.setYMax(d);
			repaint();
		});
		c1.addDataObserver(maxImJTextField_8);
		panel_3.add(maxImJTextField_8, "cell 5 1,growx");

		JButton button_1 = new JButton("Choose Julia color");
		panel_3.add(button_1, "cell 6 1");
	}

	private void setFractal(FractalType type) {
		fractalPanel1.setFractal(type.getFractal());
		fractalPanel2.setFractal(type.getFractal().getJulia());
		revalidate();
		repaint();
	}

	private class ObservingTextField extends JFormattedTextField implements DataObserver {
		private static final long serialVersionUID = 7411333623278398592L;
		private Supplier<Double> supply;

		private ObservingTextField(Supplier<Double> supply, Consumer<Double> consumer) {
			this.supply = supply;
			addActionListener(e -> {
				try {
					double d = Double.parseDouble(this.getText());
					consumer.accept(d);
				} catch (NumberFormatException e1) {
					System.out.println("Could not parse field");
				}
			});
		}

		@Override
		public void updated(DataSource source) {
			DecimalFormat df = new DecimalFormat("#.####");
			this.setText(df.format(supply.get()));
		}
	}

	private void addParentAndJulia(FractalType type) {
		UserMouseListener listener = new UserMouseListener();
		if (fractalPanel1 != null) {
			fractalPanel.remove(fractalPanel1);
		}
		fractalPanel1 = FractalPanelGenerator.newFractalPanel(type.getFractal(), listener);
		listener.setFractalCalculator(fractalPanel1.getFractalCalculator());
		fractalPanel1.addMouseMotionListener(listener);
		fractalPanel1.addMouseListener(listener);
		fractalPanel1.addMouseListener(fractalPanel1.getRectangleDrawer());
		fractalPanel1.addMouseMotionListener(fractalPanel1.getRectangleDrawer());
		fractalPanel.add(fractalPanel1, BorderLayout.CENTER);

		if (fractalPanel2 != null) {
			juliaPanel.remove(fractalPanel2);
		}
		fractalPanel2 = FractalPanelGenerator.newFractalPanel(type.getFractal().getJulia(), listener);
		fractalPanel2.addMouseListener(fractalPanel2.getRectangleDrawer());
		fractalPanel2.addMouseMotionListener(fractalPanel2.getRectangleDrawer());
		listener.addUpdateListener(fractalPanel2);
		juliaPanel.add(fractalPanel2, BorderLayout.CENTER);

		revalidate();
		repaint();
	}

}
