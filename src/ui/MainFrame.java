package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import helper.CodeFile;
import rmi.RemoteHelper;

class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean isSaved = false;

	private String username;
	private String fileName;
	private CodeFile codeFile;
	private JMenu openMenu;

	private JLabel codeInfoLabel;
	private JTextArea codeArea = new JTextArea(20, 50);
	private JTextArea paramArea = new JTextArea(4, 25);
	private JTextArea resultArea = new JTextArea(4, 25);

	public MainFrame(String username) {
		// 创建窗体
		this.username = username;
		JFrame frame = new JFrame(this.username);
		frame.setLayout(new BorderLayout());

		MyMenuBar menuBar = new MyMenuBar();
		menuBar.setColor(Color.gray);

		// 添加"File"菜单
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		// 设置快捷键
		fileMenu.setMnemonic('F');
		// "New"子菜单
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		// 设置快捷键
		newMenuItem.setMnemonic('N');
		newMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new NewFrame();
			}
		});
		fileMenu.add(newMenuItem);
		// "Open"子菜单
		openMenu = new JMenu("Open");
		fileMenu.add(openMenu);
		// 设置快捷键
		openMenu.setMnemonic('O');
		// "Open"下二级子菜单（文件列表）
		String[] fileList = null;
		try {
			// 读取文件列表
			fileList = RemoteHelper.getInstance().getIOService().readFileList(username).split("\n");
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		JMenuItem[] files = new JMenuItem[fileList.length];
		for (int i = 0; i < fileList.length; i++) {
			files[i] = new JMenuItem(fileList[i]);
			openMenu.add(files[i]);
			files[i].addActionListener(new OpenFileActionListener());
		}

		// "Save"子菜单
		JMenuItem saveMenuItem = new JMenuItem("Save");
		// 设置快捷键
		saveMenuItem.setMnemonic('S');
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String code = codeArea.getText();
				codeFile.addCode(code);
				try {
					RemoteHelper.getInstance().getIOService().writeFile(codeFile.fileContent(), username, fileName);
					codeInfoLabel.setText("Code - " + fileName + " - " + codeFile.getLatestVersion());
					isSaved = true;
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			}
		});
		fileMenu.add(saveMenuItem);
		// "Exit"子菜单
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		// 设置快捷键
		exitMenuItem.setMnemonic('E');
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_MASK));
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isSaved) {
					frame.dispose();
				} else {
					int option = JOptionPane.showConfirmDialog(frame, "Code has been changed. Save?", "Not saved",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					switch (option) {
					case JOptionPane.YES_OPTION:
						String code = codeArea.getText();
						codeFile.addCode(code);
						try {
							RemoteHelper.getInstance().getIOService().writeFile(codeFile.fileContent(), username,
									fileName);
							codeInfoLabel.setText("Code - " + fileName + " - " + codeFile.getLatestVersion());
							isSaved = true;
						} catch (RemoteException re) {
							re.printStackTrace();
						}
						frame.dispose();
						break;
					case JOptionPane.NO_OPTION:
						frame.dispose();
						break;
					case JOptionPane.CANCEL_OPTION:
						break;
					}
				}
			}
		});
		fileMenu.add(exitMenuItem);

		// 添加"Run"菜单
		JMenu runMenu = new JMenu("Run");
		// 设置快捷键
		runMenu.setMnemonic('R');
		menuBar.add(runMenu);
		JMenuItem executeMenuItem = new JMenuItem("Execute");
		// 设置快捷键
		executeMenuItem.setMnemonic('E');
		executeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, InputEvent.CTRL_MASK));
		executeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String code = codeArea.getText();
				String param = paramArea.getText();
				try {
					resultArea.setText(RemoteHelper.getInstance().getExecuteService().execute(code, param));
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			}
		});
		runMenu.add(executeMenuItem);

		// 添加"Version"菜单
		JMenu versionMenu = new JMenu("Version");
		// 设置快捷键
		versionMenu.setMnemonic('V');
		menuBar.add(versionMenu);
		// "Version"子菜单
		JMenuItem versionMenuItem = new JMenuItem("Choose version...");
		// 设置快捷键
		versionMenuItem.setMnemonic('C');
		versionMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new VersionFrame();
			}

		});
		versionMenu.add(versionMenuItem);

		// 用户账户
		menuBar.add(Box.createHorizontalGlue());
		JMenu accountMenu = new JMenu(this.username);
		menuBar.add(accountMenu);
		JMenuItem logoutMenuItem = new JMenuItem("Log out");
		logoutMenuItem.setMnemonic('L');
		logoutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		logoutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					RemoteHelper.getInstance().getUserService().logout("admin");
					frame.dispose();
					new LogInFrame();
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			}
		});
		accountMenu.add(logoutMenuItem);

		// 菜单栏
		frame.setJMenuBar(menuBar);

		JPanel codePanel = new JPanel();
		codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
		codeInfoLabel = new JLabel("Code");
		// 将提示加入代码区面板
		codePanel.add(codeInfoLabel);
		// 设置代码区
		codeArea.setLineWrap(true);
		codeArea.setMargin(new Insets(10, 10, 10, 10));
		codeArea.setBackground(Color.LIGHT_GRAY);
		codeArea.getDocument().addDocumentListener(new TextChangedListener());
		// 滚动
		JScrollPane codeScroller = new JScrollPane(codeArea);
		codeScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		codeScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// 将代码区加入代码面板
		codePanel.add(codeScroller);
		// 将代码面板加入中部面板
		frame.add(codePanel, BorderLayout.CENTER);

		// 参数+结果
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(1, 2));

		// 输入参数
		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		JLabel paramPromptLabel = new JLabel("Params");
		// 将提示加入输入面板
		paramPanel.add(paramPromptLabel);
		// 设置输入区域
		paramArea.setLineWrap(true);
		paramArea.setMargin(new Insets(10, 10, 10, 10));
		paramArea.setBackground(Color.white);
		// 滚动
		JScrollPane paramScroller = new JScrollPane(paramArea);
		paramScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paramScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// 将输入区域加入输入面板
		paramPanel.add(paramScroller);
		// 将输入面板加入南部面板左边
		southPanel.add(paramPanel);

		// 显示结果
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		JLabel resultPromptLabel = new JLabel("Result");
		// 将提示加入结果面板
		resultPanel.add(resultPromptLabel);
		// 设置结果区域
		resultArea.setLineWrap(true);
		resultArea.setEditable(false);
		resultArea.setMargin(new Insets(10, 10, 10, 10));
		resultArea.setBackground(Color.white);
		// 滚动
		JScrollPane resultScroller = new JScrollPane(resultArea);
		resultScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		resultScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// 将结果区域加入结果面板
		resultPanel.add(resultScroller);
		// 将结果面板加入南部面板右边
		southPanel.add(resultPanel);

		// 将南部面板加入主窗体
		frame.add(southPanel, BorderLayout.SOUTH);

		// 设置主窗体属性
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLocation(400, 200);
		frame.setVisible(true);
	}

	// 版本选择窗口
	class VersionFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		VersionFrame() {
			JFrame versionFrame = new JFrame("Choose version...");
			JPanel versionPanel = new JPanel();
			versionPanel.setLayout(new GridLayout(3, 1));

			JPanel versionChoosePanel = new JPanel();
			JLabel promptLabel = new JLabel("choose:");
			JComboBox<String> versionComboBox = new JComboBox<>(new VersionComboBox());
			versionChoosePanel.add(promptLabel);
			versionChoosePanel.add(versionComboBox);
			versionPanel.add(versionChoosePanel);

			versionPanel.add(new JLabel(" "));

			JPanel confirmPanel = new JPanel();
			confirmPanel.setLayout(new GridLayout(1, 3));
			JLabel leftLabel = new JLabel(" ");
			JLabel rightLabel = new JLabel(" ");
			JButton confirmButton = new JButton("Confirm");
			confirmButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String version = (String) versionComboBox.getSelectedItem();
					codeInfoLabel.setText("Code - " + fileName + " - " + version);
					codeArea.setText(codeFile.getCode(version));
					isSaved = true;
					versionFrame.dispose();
				}

			});
			confirmPanel.add(leftLabel);
			confirmPanel.add(confirmButton);
			confirmPanel.add(rightLabel);
			versionPanel.add(confirmPanel);

			versionFrame.add(versionPanel);
			versionFrame.setSize(270, 120);
			versionFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			versionFrame.setLocation(400, 200);
			versionFrame.setVisible(true);
		}
	}

	// 版本选择下拉表单
	class VersionComboBox extends AbstractListModel<String> implements ComboBoxModel<String> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String selectedItem = null;
		String[] versionList = codeFile.getVersionList();

		@Override
		public int getSize() {
			return versionList.length;
		}

		@Override
		public String getElementAt(int index) {
			return versionList[index];
		}

		@Override
		public void setSelectedItem(Object anItem) {
			selectedItem = (String) anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

	}

	// "New"菜单按下的弹出窗口
	class NewFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		NewFrame() {
			JFrame newFrame = new JFrame();
			JPanel newPanel = new JPanel();
			newPanel.setLayout(new GridLayout(3, 1));

			JPanel inputPanel = new JPanel();
			JLabel promptLabel = new JLabel("Name:");
			inputPanel.add(promptLabel);
			JTextField inputField = new JTextField(15);
			inputPanel.add(inputField);
			newPanel.add(inputPanel);

			JLabel prompt = new JLabel(" ");
			newPanel.add(prompt);

			JPanel confirmPanel = new JPanel();
			confirmPanel.setLayout(new GridLayout(1, 3));
			JLabel leftLabel = new JLabel(" ");
			JLabel rightLabel = new JLabel(" ");
			JButton confirmButton = new JButton("Confirm");
			confirmButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (inputField.getText().equals("")) {
						prompt.setText("Please input your filename!");
					} else {
						fileName = inputField.getText();
						codeInfoLabel.setText("Code - " + fileName); // 改变代码区名字

						codeFile = new CodeFile(fileName); // 实例化新的代码文件对象

						// 向"Open"菜单下添加新的子菜单
						JMenuItem file = new JMenuItem(fileName);
						openMenu.add(file);
						file.addActionListener(new OpenFileActionListener());

						isSaved = false;

						// 关闭本窗口
						newFrame.dispose();
					}
				}

			});
			confirmPanel.add(leftLabel);
			confirmPanel.add(confirmButton);
			confirmPanel.add(rightLabel);
			newPanel.add(confirmPanel);

			newFrame.add(newPanel);
			newFrame.setSize(270, 120);
			newFrame.setLocation(450, 250);
			newFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			newFrame.setVisible(true);
		}
	}

	// "Open"的子菜单的监听
	class OpenFileActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String file = e.getActionCommand();
			String fileContent = null;
			try {
				fileContent = RemoteHelper.getInstance().getIOService().readFile(username, file);
			} catch (RemoteException re) {
				re.printStackTrace();
			}
			codeFile = new CodeFile(file, fileContent);
			codeArea.setText(codeFile.getLatestCode());
			fileName = file;
			codeInfoLabel.setText("Code - " + fileName + " - " + codeFile.getLatestVersion());
			isSaved = true;
		}

	}

	// 代码区文本更新监听
	class TextChangedListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			isSaved = false;
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			isSaved = false;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			isSaved = false;
		}

	}
}

class MyMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Color bgColor = Color.WHITE;

	public void setColor(Color color) {
		bgColor = color;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

	}
}
