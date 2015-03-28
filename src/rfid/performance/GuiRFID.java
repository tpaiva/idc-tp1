package rfid.performance;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.alien.enterpriseRFID.reader.AlienReaderException;

public class GuiRFID extends JFrame implements ActionListener, ItemListener {

	JLabel metricsLabel = new JLabel("Métrica: ", JLabel.LEFT);
	JLabel timeLabel = new JLabel("Tempo: ", JLabel.LEFT);
	JLabel numReadingsLabel = new JLabel("Núm. de Leituras: ", JLabel.LEFT);
	JLabel repetitionsLabel = new JLabel("Repetições: ", JLabel.LEFT);
	JComboBox metricsField = new JComboBox();
	JTextField timeField = new JTextField();
	JTextField numReadingsField = new JTextField();
	JTextField repetitionsField = new JTextField();
	JButton executionButton = new JButton("Executar");
	JTextArea resultsTextArea = new JTextArea();
	String selectedMetric = "";
	ReaderPerformanceTest performanceTest;

	public GuiRFID() {
		super("RFID");
		setSize(1000, 700); // (width, height)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen

		// ---- Configuração do Experimento

		metricsField.addItem("");
		metricsField.addItem("Taxa de Leitura");
		metricsField.addItem("Taxa de Sucesso");
		metricsField.addItem("Ambas");
		metricsField.setEditable(false);
		metricsField.addItemListener(this);

		executionButton.setPreferredSize(new Dimension(300, 50));
		executionButton.addActionListener(this);

		// -- Primeiro Painel

		JPanel panel1 = new JPanel();

		GroupLayout panel1Layout = new GroupLayout(panel1);
		panel1.setLayout(panel1Layout);

		panel1Layout.setAutoCreateGaps(true);
		panel1Layout.setAutoCreateContainerGaps(true);

		panel1Layout.setHorizontalGroup(panel1Layout
				.createSequentialGroup()
				.addGroup(
						panel1Layout.createParallelGroup()
								.addComponent(metricsLabel)
								.addComponent(timeLabel)
								.addComponent(numReadingsLabel)
								.addComponent(repetitionsLabel))
				.addGroup(
						panel1Layout.createParallelGroup()
								.addComponent(metricsField)
								.addComponent(timeField)
								.addComponent(numReadingsField)
								.addComponent(repetitionsField)));

		panel1Layout.setVerticalGroup(panel1Layout
				.createSequentialGroup()
				.addGroup(
						panel1Layout
								.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
								.addComponent(metricsLabel)
								.addComponent(metricsField))
				.addGroup(
						panel1Layout
								.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
								.addComponent(timeLabel)
								.addComponent(timeField))
				.addGroup(
						panel1Layout
								.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
								.addComponent(numReadingsLabel)
								.addComponent(numReadingsField))
				.addGroup(
						panel1Layout
								.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
								.addComponent(repetitionsLabel)
								.addComponent(repetitionsField)));

		// -- Painel de Preenchimento
		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize(new Dimension(300, 400));

		// -- Painel Esquerdo (Configurações do Experimento)
		JPanel leftPanel = new JPanel();

		TitledBorder leftPanelTitle = BorderFactory
				.createTitledBorder("Configurações do Experimento");
		leftPanelTitle.setTitleJustification(TitledBorder.CENTER);
		leftPanel.setBorder(leftPanelTitle);

		GroupLayout leftLayout = new GroupLayout(leftPanel);
		leftPanel.setLayout(leftLayout);

		leftLayout.setAutoCreateGaps(true);
		leftLayout.setAutoCreateContainerGaps(true);

		leftLayout.setHorizontalGroup(leftLayout.createSequentialGroup()
				.addGroup(
						leftLayout
								.createParallelGroup(
										GroupLayout.Alignment.LEADING)
								.addComponent(panel1,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(executionButton,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(emptyPanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)));

		leftLayout.setVerticalGroup(leftLayout
				.createSequentialGroup()
				.addComponent(panel1, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(executionButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(emptyPanel, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		// ---- Resultados

		resultsTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		resultsTextArea.setLineWrap(true);
		resultsTextArea.setWrapStyleWord(true);
		resultsTextArea.setEditable(false);

		JScrollPane resultsScroll = new JScrollPane(resultsTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		resultsScroll.setBorder(new EmptyBorder(5, 5, 5, 5));

		// -- Painel Direito (Resultados)
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(1, 1));

		rightPanel.setPreferredSize(new Dimension(600, 635));

		TitledBorder resultsTitle;
		resultsTitle = BorderFactory.createTitledBorder("Resultados");
		resultsTitle.setTitleJustification(TitledBorder.CENTER);
		rightPanel.setBorder(resultsTitle);

		rightPanel.add(resultsScroll);

		// --- Tela completa

		JPanel screenPanel = new JPanel();
		GroupLayout screenLayout = new GroupLayout(screenPanel);
		screenPanel.setLayout(screenLayout);
		screenPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		// (topo, esquerda, embaixo, direita)

		screenLayout.setAutoCreateGaps(true);
		screenLayout.setAutoCreateContainerGaps(true);

		screenLayout.setHorizontalGroup(screenLayout
				.createSequentialGroup()
				.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(rightPanel, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		screenLayout.setVerticalGroup(screenLayout.createSequentialGroup()
				.addGroup(
						screenLayout
								.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
								.addComponent(leftPanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(rightPanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)));

		add(screenPanel);

		setVisible(true);
		
		try {
			performanceTest = new ReaderPerformanceTest();
		} catch(AlienReaderException e) {
			System.out.println("Error: " + e.toString());
		}

	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (Exception exc) {
			// ignore error
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		// TODO Auto-generated method stub
		String answer;

		if (event.getStateChange() == ItemEvent.SELECTED) {
			selectedMetric = event.getItem().toString();
			System.out.println(selectedMetric);
		}

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub

		printExperimentsParameters();

		try {
			if (selectedMetric.equals("Taxa de Leitura")
					|| selectedMetric.equals("Ambas")) {
				long time = Long.valueOf(timeField.getText()).longValue();
		
				System.out.println("Calculo da Taxa de Leitura");
				//resultsTextArea.append("> Cálculo da Taxa de Leitura\n\n");
				
				HashMap<String, Double> tagReadRate = performanceTest.getIndividualReadRate(time);
				resultsTextArea.append(performanceTest.performanceToString(tagReadRate, "Taxa de Leitura", false));

			}
			
			if (selectedMetric.equals("Taxa de Sucesso")
					|| selectedMetric.equals("Ambas")) {
				int trials = Integer.parseInt(numReadingsField.getText());

				System.out.println("Calculo da Taxa de Leitura");
				//resultsTextArea.append("> Cálculo da Taxa de Sucesso\n\n");
				
				HashMap<String, Double> tagSuccessRate = performanceTest.getIndividualSuccessRate(trials);
				resultsTextArea.append(performanceTest.performanceToString(tagSuccessRate, "Taxa de Sucesso", true));

			}
		} catch(AlienReaderException e) {
			resultsTextArea.append("Error: " + e.toString());
		}
	}

	public void printExperimentsParameters() {
		if (!selectedMetric.equals("")) {
			resultsTextArea.append("--- Configuração do Experimento\n\n");
			resultsTextArea.append("Métrica: " + selectedMetric + '\n');
			if (selectedMetric.equals("Taxa de Leitura")
					|| selectedMetric.equals("Ambas"))
				resultsTextArea.append("Tempo: " + timeField.getText());
			if (selectedMetric.equals("Ambas"))
				resultsTextArea.append("\n");
			if (selectedMetric.equals("Taxa de Sucesso")
					|| selectedMetric.equals("Ambas"))
				resultsTextArea.append("Número de Leituras: "
						+ numReadingsField.getText());
			resultsTextArea.append("\nRepetições: "
					+ repetitionsField.getText() + "\n\n");
		} else
			resultsTextArea.append("(!) Definir parâmetros do experimento\n\n");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GuiRFID gui = new GuiRFID();
	}

}
