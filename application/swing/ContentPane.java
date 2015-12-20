package swing;

import java.awt.BorderLayout;
import java.text.NumberFormat;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.NumberFormatter;

import net.miginfocom.swing.MigLayout;
import fractals.FractalType;

public class ContentPane extends JPanel {

	private static final long serialVersionUID = -7756613728210123704L;
	private FractalPanel fractalPanel1;
	private FractalPanel fractalPanel2;
	private JPanel fractalPanel;
	private JPanel juliaPanel;
	private JTextField textField;
	private JFormattedTextField textField_1;

	public ContentPane() {
		setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane splitPane = new JSplitPane();
		add(splitPane, "cell 0 0,grow");

		DefaultListModel<String> model = new DefaultListModel<String>();
		for (int i = 0; i < FractalType.values().length; i++) {
			model.addElement(FractalType.values()[i].getName());
		}

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
		panel.setLayout(new MigLayout("", "[166px,grow,fill]", "[222px,grow,fill][]"));
		
		fractalPanel = new JPanel();
		fractalPanel.setFocusable(true);
		fractalPanel.setLayout(new BorderLayout());
		panel.add(fractalPanel, "cell 0 0,grow");
		
		juliaPanel = new JPanel();
		juliaPanel.setFocusable(true);
		juliaPanel.setLayout(new BorderLayout());
		panel.add(juliaPanel, "cell 0 0,grow");
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, "cell 0 1,grow");
		panel_2.setLayout(new MigLayout("", "[][fill][grow][][fill][grow]", "[][]"));
		
		JLabel label = new JLabel("Fractal iterations");
		panel_2.add(label, "cell 0 0,alignx center");
		
		JLabel label_1 = new JLabel("Julia Iterations");
		panel_2.add(label_1, "cell 3 0,alignx center");
		
		JSlider slider_2 = new JSlider(JSlider.HORIZONTAL, 10, 1000, 50);
		slider_2.setMajorTickSpacing(1000);
		slider_2.setMinorTickSpacing(100);
		slider_2.addChangeListener( (e) -> {
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
		textField.addActionListener( (e) -> {
			try {
				int val = Integer.parseInt(textField.getText());
				fractalPanel1.getFractalCalculator().setMaxIterations(val);
				slider_2.setValue(val);
			} catch (Exception e1) {}
		});
		
		JButton button = new JButton("Choose fractal color");
		panel_2.add(button, "cell 2 1,alignx center");
		
		JSlider slider_3 = new JSlider(JSlider.HORIZONTAL, 10, 1000, 50);
		slider_3.setMajorTickSpacing(1000);
		slider_3.setMinorTickSpacing(100);
		slider_3.addChangeListener( (e) -> {
			if (!slider_3.getValueIsAdjusting()) {
				fractalPanel2.getFractalCalculator().setMaxIterations(slider_3.getValue());
				textField_1.setText(Integer.toString(slider_3.getValue()));
				repaint();
			}
		});
		panel_2.add(slider_3, "cell 3 1");
		
		textField_1 = new JFormattedTextField(form);
		textField_1.setText("50");
		panel_2.add(textField_1, "cell 4 1,growx");
		textField_1.setColumns(10);
		textField_1.addActionListener( (e) -> {
			try {
				int val = Integer.parseInt(textField_1.getText());
				fractalPanel2.getFractalCalculator().setMaxIterations(val);
				slider_3.setValue(val);
			} catch (Exception e1) {}
		});
		
		JButton button_1 = new JButton("Choose Julia color");
		panel_2.add(button_1, "cell 5 1,alignx center");

		addParentAndJulia(FractalType.MANDELBROT);
	}

	private void setFractal(FractalType type) {
		fractalPanel1.setFractal(type.getFractal());
		fractalPanel2.setFractal(type.getFractal().getJulia());
		revalidate();
		repaint();
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
