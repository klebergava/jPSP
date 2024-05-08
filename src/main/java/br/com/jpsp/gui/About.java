package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;

public class About extends JFrame {
	private static final long serialVersionUID = -2283777512879950127L;
	private final JTextArea appInfo = new JTextArea("");
	private final JTextArea license = new JTextArea("");
	private final JTextArea readMe = new JTextArea("");
    // Criando o painel principal
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
	public About() {
		super(Strings.ABOUT);
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(Images.ABOUT_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mount(), "Center");

		setSize(800, 600);
		pack();

		setAlwaysOnTop(true);

		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				About.this.fillAppInfoContent();
				About.this.fillLicenseContent();
				About.this.fillReadmeContent();
			}
		});
	}

	/**
	 * 
	 */
	private void fillAppInfoContent() {
		appInfo.setEditable(false);
		StringBuffer content = new StringBuffer();

		content.append(Strings.APP_TITLE + "\n");
		content.append("Versão: " + Strings.VERSION + " - " + Strings.VERSION_DATE + "\n");

		File path = new File(".");
		content.append("\nDiretório da aplicação: " + path.getAbsoluteFile() + "\n");
		path = new File(FilesUtils.DATABASE_FILE_V1);
		content.append("Arquivo de dados: " + path.getAbsoluteFile() + "\n");
		path = new File(FilesUtils.USER_CONFIG_DATA_FILE);
		content.append("Arquivo de configurações: " + path.getAbsoluteFile() + "\n");

		content.append("\nNotas da versão 1.0:\n");
		content.append("\t- versão estável do aplicativo\n");
		
		content.append("\nNotas da versão 1.0RC5:\n");
		content.append("\t- jars atualizados para as versões mais novas\n");
		content.append("\t- projeto agora usa o maven\n");
		content.append("\t- implementada ordenação nos relatórios\n");
		content.append("\t- código do POI (Excel) refatorado\n");
		content.append("\t- inclusão do log4j\n");
		
		content.append("\nNotas da versão 1.0RC4:\n");
		content.append("\t- correções de bugs\n");
		content.append("\t- labels de telas parametrizados\n");
		content.append("\t- melhorias na interface\n");
		content.append("\t- implementação da funcionalidade 'Tempo gasto na atividade'\n");
		content.append("\t- refatoração da base de dados\n");
		content.append("\t- configurações agora são salvas em um arquivo, não mais no SQLite\n");
		content.append("\t- inclusão do campo 'Sistema' na atividade\n");
		content.append("\t- inclusão de telas de edição de atividade, descrição,\n\t  classificação e sistema\n");
		content.append("\t- remoção das sugestões de atividades/descrições em todas as telas de\n\t  edição de tarefas; alterado para combobox editável\n");

		
		content.append("\nNotas da versão 1.0RC3:\n");
		content.append("\t- correções de bugs\n");
		content.append("\t- telas de alteração e inclusão melhoradas\n");
		content.append("\t- incluído o nome do usuário no arquivo excel\n");
		content.append("\t- opções de filtro alteradas\n");
		content.append("\t- alteração do layout\n");
		content.append("\t- botão de ordenação removido, ordenação pode ser feita\n\t  na própria tabela\n");

		content.append("\nNotas da versão 1.0RC2:\n");
		content.append("\t- correções de bugs\n");
		content.append("\t- geração de relatório incluído na barra de menu\n");
		content.append("\t- sugestões de atividades/descrições em todas as telas de\n\t  edição de tarefas\n");
		content.append(
				"\t- iniciar automaticamente tarefa ao desbloquear o sistema\n\t  somente se a tarefa já estivesse rodando\n");
		content.append("\t- alteração desta tela\n");

		content.append("\nNotas da versão 1.0RC1:\n");
		content.append(
				"\t- todas as funcionalidades propostas estão implementadas\n\t  (inclusão, edição e remoção de atividades)\n");
		content.append("\t- permite fazer backup/restore da base de dados\n");
		content.append("\t- permite incluir/editar/excluir atividades\n");
		content.append("\t- utiliza o SQLite para armazenar as informações e as configurações\n");
		content.append("\t- utiliza o POI para gerar a planilha excel\n");
		content.append("\t- utiliza o JFreeChart para gerar os gráficos\n");
		content.append("\t- utiliza o JNA para capturar eventos do sistema operacional\n");

		content.append("\n --------------------------- 8< ---------------------------\n");

		content.append("\nVersão do Java: " + System.getProperty("java.version") + "\n");
		
		content.append("\nJars utilizados na aplicação:\n");
		
		List<String> jars = FilesUtils.readAppJARS();
		for (String jar : jars) {
			content.append("\t- " + jar + " \n");	
		}
		content.append("\n --------------------------- 8< ---------------------------\n");

		appInfo.setLineWrap(true);
		appInfo.setFont(Gui.COURIER_12);
		appInfo.setText(content.toString());
		appInfo.setCaretPosition(0);
	}
	
	/**
	 * 
	 */
	private void fillLicenseContent() {
		license.setEditable(false);
		license.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
		license.setLineWrap(true);
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(FilesUtils.GPL3_LICENCE_FILE);
		List<String> fileLines = FilesUtils.readTxtFile(is);
		license.setFont(Gui.COURIER_12);
		
		fileLines.forEach(line -> {
			license.append(line + "\n");
		});
		
		license.setCaretPosition(0);
	}
	
	/**
	 * 
	 */
	private void fillReadmeContent() {
		readMe.setEditable(false);
		readMe.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
		readMe.setLineWrap(true);
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(FilesUtils.README_FILE);
		List<String> fileLines = FilesUtils.readTxtFile(is);
		
		readMe.setFont(Gui.COURIER_12);
		
		fileLines.forEach(line -> {
			readMe.append(line + "\n");
		});
		
		readMe.setCaretPosition(0);
	}

	private JPanel mount() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(Gui.getLinedBorder("Sobre o jPSP", Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));
		main.setBackground(GuiSingleton.DEFAULT_BG_COLOR);

		JLabel label = new JLabel(Images.SPLASH_ICON);
		label.setAlignmentX(0.5F);
		main.add(label, "North");

		JScrollPane appInfoScroll = Gui.getDefaultScroll(appInfo);
		appInfoScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.ABOUT, appInfoScroll);
		
		JScrollPane licenseScroll = Gui.getDefaultScroll(license);
		licenseScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.GPL3, licenseScroll);
		
		JScrollPane readmeScroll = Gui.getDefaultScroll(readMe);
		readmeScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.README, readmeScroll);

		JButton close = new JButton("Sair", Images.EXIT);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				About.this.dispose();
			}
		});
		
		main.add(tabbedPane, "Center");
		main.add(close, "South");

		return main;
	}
}
