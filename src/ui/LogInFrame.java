package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.PseudoColumnUsage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

import rmi.RemoteHelper;

public class LogInFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;

	public LogInFrame() {

		JFrame frame = new JFrame("log in");
		frame.setLayout(new BorderLayout());

		JLabel northBlank = new JLabel(" ");
		JLabel southBlank = new JLabel(" ");
		JLabel eastBlank = new JLabel("            ");
		JLabel westBlank = new JLabel("            ");

		frame.add(southBlank, BorderLayout.SOUTH);
		frame.add(northBlank, BorderLayout.NORTH);
		frame.add(eastBlank, BorderLayout.EAST);
		frame.add(westBlank, BorderLayout.WEST);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(4, 1));

		JPanel userNamePanel = new JPanel();
		userNamePanel.setLayout(new GridLayout(1, 2));
		JLabel userNameLabel = new JLabel("    User name:");
		userNamePanel.add(userNameLabel);
		JTextField userNameTextArea = new JTextField(1);
		userNameTextArea.setBackground(Color.WHITE);
		userNameTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
		userNamePanel.add(userNameTextArea);
		centerPanel.add(userNamePanel);

		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout(new GridLayout(1, 2));
		JLabel passwordLabel = new JLabel("    password:");
		passwordPanel.add(passwordLabel);
		JTextField passwordTextArea = new JTextField(1);
		passwordTextArea.setBackground(Color.WHITE);
		passwordTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
		passwordPanel.add(passwordTextArea);
		centerPanel.add(passwordPanel);

		JPanel warningPanel = new JPanel();
		JLabel warningLabel = new JLabel();
		warningLabel.setForeground(Color.RED);
		warningPanel.add(warningLabel);
		centerPanel.add(warningPanel);

		JPanel operatePanel = new JPanel();
		operatePanel.setLayout(new GridLayout());
		// 左侧空白
		JLabel leftLabel = new JLabel();
		operatePanel.add(leftLabel);
		// 登陆按钮
		JButton logInButton = new JButton("log in");
		logInButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				username = userNameTextArea.getText();
				password = passwordTextArea.getText();
				try {
					if (RemoteHelper.getInstance().getUserService().login(username, password)) {
						frame.dispose();
						new MainFrame(username);
					} else {
						warningLabel.setText("Username or password is wrong!");
					}
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			}
		});
		operatePanel.add(logInButton);
		// 中间空白
		JLabel middleLabel = new JLabel();
		operatePanel.add(middleLabel);
		// 注册按钮
		JButton registerButton = new JButton("register");
		registerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		operatePanel.add(registerButton);
		// 右侧空白
		JLabel rightLabel = new JLabel();
		operatePanel.add(rightLabel);
		// 将操作面板加入中央面板
		centerPanel.add(operatePanel);

		frame.add(centerPanel, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(400, 150);
		frame.setLocation(400, 200);
		frame.setResizable(false);
		frame.setVisible(true);

	}

}

class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;

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
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		fileMenu.add(newMenuItem);
		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		fileMenu.add(openMenuItem);
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String code = codeArea.getText();
				try {
					// TODO 需要改变传入的参数
					RemoteHelper.getInstance().getIOService().writeFile(code, "admin", "code");
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		fileMenu.add(saveMenuItem);
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

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
				// TODO Auto-generated method stub
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
		JMenu accountMenu = new JMenu(this.username);// TODO 账户名
		menuBar.add(accountMenu);
		JMenuItem logoutMenuItem = new JMenuItem("log out");
		logoutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 登出
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
		 * TODO 需美化代码区域 1、文件名称 2、可能有的提示信息
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