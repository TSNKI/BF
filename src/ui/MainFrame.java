package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import rmi.RemoteHelper;

public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea codeArea;
	private JTextArea inputArea;
	private JLabel resultLabel;

	public MainFrame() {
		// 创建窗体
		JFrame frame = new JFrame("BF Client");
		frame.setLayout(new BorderLayout());

		MyMenuBar menuBar = new MyMenuBar();
		menuBar.setColor(Color.gray);

		// 添加"File"菜单
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem newMenuItem = new JMenuItem("New");
		fileMenu.add(newMenuItem);
		JMenuItem openMenuItem = new JMenuItem("Open");
		fileMenu.add(openMenuItem);
		JMenuItem saveMenuItem = new JMenuItem("Save");
		fileMenu.add(saveMenuItem);
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(exitMenuItem);

		// 添加"Run"菜单
		JMenu runMenu = new JMenu("Run");
		menuBar.add(runMenu);
		JMenuItem executeMenuItem = new JMenuItem("Execute");
		runMenu.add(executeMenuItem);

		// 添加"Version"菜单
		JMenu versionMenu = new JMenu("Version");
		menuBar.add(versionMenu);
		// TODO 实现版本功能

		// 用户账户
		menuBar.add(Box.createHorizontalGlue());
		JMenu accountMenu = new JMenu("Account");// TODO 账户名
		menuBar.add(accountMenu);
		JMenuItem logoutMenuItem = new JMenuItem("log out");
		accountMenu.add(logoutMenuItem);

		// 菜单栏
		frame.setJMenuBar(menuBar);

		newMenuItem.addActionListener(new MenuItemActionListener());
		openMenuItem.addActionListener(new MenuItemActionListener());
		saveMenuItem.addActionListener(new SaveActionListener());
		executeMenuItem.addActionListener(new MenuItemActionListener());
		logoutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 登出

			}
		});

		codeArea = new JTextArea();
		codeArea.setMargin(new Insets(10, 10, 10, 10));
		codeArea.setBackground(Color.LIGHT_GRAY);
		frame.add(codeArea, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));

		inputArea = new JTextArea();
		inputArea.setMargin(new Insets(10, 10, 10, 10));
		inputArea.setBackground(Color.LIGHT_GRAY);
		inputArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		panel.add(inputArea);

		// 显示结果
		resultLabel = new JLabel();
		resultLabel.setText("result");
		resultLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		panel.add(resultLabel);

		frame.add(panel, BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLocation(400, 200);
		frame.setVisible(true);
	}

	class MenuItemActionListener implements ActionListener {
		/**
		 * 子菜单响应事件
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("Open")) {
				codeArea.setText("Open");
			} else if (cmd.equals("Save")) {
				codeArea.setText("Save");
			} else if (cmd.equals("Run")) {
				resultLabel.setText("Hello, result");
			}
		}
	}

	class SaveActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String code = codeArea.getText();
			try {
				RemoteHelper.getInstance().getIOService().writeFile(code, "admin", "code");
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
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