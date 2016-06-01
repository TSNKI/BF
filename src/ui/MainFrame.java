package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import helper.CodeFile;
import rmi.RemoteHelper;

class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String fileName;
	private CodeFile codeFile;

	private JTextArea codeArea = new JTextArea(20, 50);
	private JTextArea paramArea = new JTextArea(4, 25);
	private JTextArea resultArea = new JTextArea(4, 25);

	public MainFrame(String username) {
		// 创建窗体
		this.username = username;
		JFrame frame = new JFrame(this.username + "-" + this.fileName);
		frame.setLayout(new BorderLayout());

		MyMenuBar menuBar = new MyMenuBar();
		menuBar.setColor(Color.gray);

		// 添加"File"菜单
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		// "New"子菜单
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO "New"菜单监听
			}
		});
		fileMenu.add(newMenuItem);
		// "Open"子菜单
		JMenu openMenu = new JMenu("Open");
		fileMenu.add(openMenu);
		// "Open"下二级子菜单（文件列表）
		String[] fileList = null;
		try {
			// 读取文件列表
			fileList = RemoteHelper.getInstance().getIOService().readFileList(username).split("\n");
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		for (int i = 0; i < fileList.length; i++) {
			JMenuItem file = new JMenuItem(fileList[i]);
			file.addActionListener(new openFileActionListener());
		}

		// "Save"子菜单
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String code = codeArea.getText();
				codeFile.addCode(code);
				try {
					RemoteHelper.getInstance().getIOService().writeFile(codeFile.fileContent(), username, fileName);
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			}
		});
		fileMenu.add(saveMenuItem);
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO "Exit"菜单监听，待添加提示保存
				frame.dispose();
			}
		});
		fileMenu.add(exitMenuItem);

		// 添加"Run"菜单
		JMenu runMenu = new JMenu("Run");
		menuBar.add(runMenu);
		JMenuItem executeMenuItem = new JMenuItem("Execute");
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
		menuBar.add(versionMenu);
		// TODO 实现版本功能

		// 用户账户
		menuBar.add(Box.createHorizontalGlue());
		JMenu accountMenu = new JMenu(this.username);
		menuBar.add(accountMenu);
		JMenuItem logoutMenuItem = new JMenuItem("log out");
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

		/**
		 * TODO 需美化代码区域 可能有的提示信息
		 */

		codeArea.setLineWrap(true);
		codeArea.setMargin(new Insets(10, 10, 10, 10));
		codeArea.setBackground(Color.LIGHT_GRAY);
		JScrollPane codeScroller = new JScrollPane(codeArea);
		codeScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		codeScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.add(codeScroller, BorderLayout.CENTER);

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

	// "Open"的子菜单的监听
	class openFileActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
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
