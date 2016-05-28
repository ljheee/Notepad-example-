package com.ljheee.ui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.io.*;
import javax.swing.undo.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import java.util.List;

public class Notepad4 extends JFrame implements ActionListener, DocumentListener {
	JMenu mFile, mEdit, mMode, mView, mHelp;
	// ---------------文件菜单
	JMenuItem mFile_New, mFile_Open, mFile_Save, mFile_ASave, mFile_Print, mFile_Exit;
	// ---------------编辑菜单
	JMenuItem mEdit_Undo, mEdit_Cut, mEdit_Copy, mEdit_Paste, mEdit_Del, mEdit_Search, mEdit_SearchNext, mEdit_Replace, mEdit_Turnto, mEdit_SelectAll, mEdit_TimeDate;
	// ---------------格式菜单
	JCheckBoxMenuItem formatMenu_LineWrap;
	JMenu formatMenu_Color;
	JMenuItem formatMenu_Font, formatMenu_Color_FgColor, formatMenu_Color_BgColor;
	// ---------------查看菜单
	JCheckBoxMenuItem viewMenu_Status;
	// ---------------帮助菜单
	JMenuItem mHelp_HelpTopics, mHelp_About;
	// ---------------弹出菜单级菜单项
	JPopupMenu popupMenu;
	JMenuItem popupMenu_Undo, popupMenu_Cut, popupMenu_Copy, popupMenu_Paste, popupMenu_Delete, popupMenu_SelectAll;
	// ---------------工具栏按钮
	JButton newButton, openButton, saveButton, saveAsButton, printButton, undoButton, redoButton, cutButton, copyButton, pasteButton, deleteButton, searchButton, timeButton, fontButton, boldButton,
			italicButton, fgcolorButton, bgcolorButton, helpButton;
	// 文本编辑区域
	static JTextArea Text;
	// 状态栏标签
	JLabel statusLabel1, statusLabel2, statusLabel3;
	JToolBar statusBar;
	// ---------------系统剪贴板
	Toolkit toolKit = Toolkit.getDefaultToolkit();
	Clipboard clipBoard = toolKit.getSystemClipboard();
	// ---------------创建撤消操作管理器
	protected UndoManager undo = new UndoManager();
	protected UndoableEditListener undoHandler = new UndoHandler();
	// ----------------其它变量
	boolean isNewFile = true; // 是否新文件(未保存过的)
	File currentFile; // 当前文件名
	String oldValue; // 存放编辑区原来的内容，用于比较文本是否有改动
	JButton fontOkButton; // 字体设置里的"确定"按钮
	// ----------------设置编辑区默认字体
	protected Font defaultFont = new Font("宋体", Font.PLAIN, 12);
	GregorianCalendar time = new GregorianCalendar();
	int hour = time.get(Calendar.HOUR_OF_DAY);
	int min = time.get(Calendar.MINUTE);
	int second = time.get(Calendar.SECOND);
	File saveFileName = null, fileName = null;

	public Notepad4() {
		super("记事本");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Container container = getContentPane();
		// System.out.println(Text.getDragEnabled()); //支持自动拖放
		JScrollPane scroll = new JScrollPane(Text);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Text.setWrapStyleWord(true); // 设置单词在一行不足容纳时换行
		Text.setLineWrap(true);
		Text.setFont(defaultFont); // 设置编辑区默认字体
		Text.setBackground(Color.white); // 设置编辑区默认背景色
		Text.setForeground(Color.black); // 设置编辑区默认前景色
		oldValue = Text.getText(); // 获取原文本编辑区的内容
		// --------------------------编辑区注册事件监听
		Text.getDocument().addUndoableEditListener(undoHandler); // 添加负责通知任何更改的撤消侦听器
		Text.getDocument().addDocumentListener(this); // 添加负责通知任何更改的文档侦听器
		JMenuBar MenuBar = new JMenuBar();
		mFile = new JMenu("文件(F)", true); // 创建菜单
		mEdit = new JMenu("编辑(E)", true);
		mMode = new JMenu("格式(O)", true);
		mView = new JMenu("查看(V)", true);
		mHelp = new JMenu("帮助(H)", true);
		mEdit.addActionListener(new ActionListener() // 注册事件监听
				{
					public void actionPerformed(ActionEvent e) {
						checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
					}
				});
		mFile.setMnemonic('F');
		mEdit.setMnemonic('E');
		mMode.setMnemonic('O');
		mView.setMnemonic('V');
		mHelp.setMnemonic('H');
		MenuBar.add(mFile);
		MenuBar.add(mEdit);
		MenuBar.add(mMode);
		MenuBar.add(mView);
		MenuBar.add(mHelp);
		// --------------文件菜单
		mFile_New = new JMenuItem("新建(N)", 'N');
		mFile_Open = new JMenuItem("打开(O)", 'O');
		mFile_Save = new JMenuItem("保存(S)", 'S');
		mFile_ASave = new JMenuItem("另存为(A)", 'A');
		mFile_Print = new JMenuItem("打印(P)", 'P');
		mFile_Exit = new JMenuItem("退出(X)", 'X');
		mFile_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mFile_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mFile_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mFile_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		mFile_New.addActionListener(this); // 注册事件监听
		mFile_Open.addActionListener(this);
		mFile_Save.addActionListener(this);
		mFile_ASave.addActionListener(this);
		mFile_Print.addActionListener(this);
		mFile_Exit.addActionListener(this);
		mFile.add(mFile_New); // 添加菜单项
		mFile.add(mFile_Open);
		mFile.add(mFile_Save);
		mFile.add(mFile_ASave);
		mFile.addSeparator(); // 添加分割线
		mFile.add(mFile_Print);
		mFile.addSeparator(); // 添加分割线
		mFile.add(mFile_Exit);

		// --------------编辑菜单
		mEdit_Undo = new JMenuItem("撤消(U)", 'U');
		mEdit_Cut = new JMenuItem("剪切(T)", 'T');
		mEdit_Copy = new JMenuItem("复制(C)", 'C');
		mEdit_Paste = new JMenuItem("粘贴(P)", 'P');
		mEdit_Del = new JMenuItem("删除(L)", 'L');
		mEdit_Search = new JMenuItem("查找(F)", 'F');
		mEdit_SearchNext = new JMenuItem("查找下一个(N)", 'N');
		mEdit_Replace = new JMenuItem("替换(R)", 'R');
		mEdit_Turnto = new JMenuItem("转到(G)", 'G');
		mEdit_SelectAll = new JMenuItem("全选(A)", 'A');
		mEdit_TimeDate = new JMenuItem("时间/日期(D)", 'D');
		mEdit_Cut.setEnabled(false);
		mEdit_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mEdit_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mEdit_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mEdit_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mEdit_Del.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		mEdit_Search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		mEdit_SearchNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		mEdit_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
		mEdit_Turnto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		mEdit_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mEdit_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mEdit_Undo.addActionListener(this); // 注册事件监听
		mEdit_Cut.addActionListener(this);
		mEdit_Copy.addActionListener(this);
		mEdit_Paste.addActionListener(this);
		mEdit_Del.addActionListener(this);
		mEdit_Search.addActionListener(this);
		mEdit_SearchNext.addActionListener(this);
		mEdit_Replace.addActionListener(this);
		mEdit_Turnto.addActionListener(this);
		mEdit_SelectAll.addActionListener(this);
		mEdit_TimeDate.addActionListener(this);
		mEdit.add(mEdit_Undo); // 添加菜单项
		mEdit.addSeparator(); // 添加分割线
		mEdit.add(mEdit_Cut);
		mEdit.add(mEdit_Copy);
		mEdit.add(mEdit_Paste);
		mEdit.add(mEdit_Del);
		mEdit.addSeparator();
		mEdit.add(mEdit_Search);
		mEdit.add(mEdit_SearchNext);
		mEdit.add(mEdit_Replace);
		mEdit.add(mEdit_Turnto);
		mEdit.addSeparator();
		mEdit.add(mEdit_SelectAll);
		mEdit.add(mEdit_TimeDate);

		// --------------格式菜单
		formatMenu_LineWrap = new JCheckBoxMenuItem("自动换行(W)");
		formatMenu_LineWrap.setMnemonic('W');
		formatMenu_LineWrap.setState(true);
		formatMenu_Font = new JMenuItem("字体(F)", 'F');
		formatMenu_Color = new JMenu("颜色");
		formatMenu_Color_FgColor = new JMenuItem("字体颜色");
		formatMenu_Color_BgColor = new JMenuItem("背景颜色");
		formatMenu_LineWrap.addActionListener(this); // 注册事件监听
		formatMenu_Font.addActionListener(this);
		formatMenu_Color_FgColor.addActionListener(this);
		formatMenu_Color_BgColor.addActionListener(this);
		mMode.add(formatMenu_LineWrap); // 添加菜单项
		mMode.addSeparator();
		mMode.add(formatMenu_Font);
		mMode.add(formatMenu_Color);
		formatMenu_Color.add(formatMenu_Color_FgColor);
		formatMenu_Color.add(formatMenu_Color_BgColor);

		// --------------查看菜单
		viewMenu_Status = new JCheckBoxMenuItem("状态栏(S)");
		viewMenu_Status.setMnemonic('S');
		viewMenu_Status.setState(true);
		viewMenu_Status.addActionListener(this);
		mView.add(viewMenu_Status);

		// --------------帮助菜单
		mHelp_HelpTopics = new JMenuItem("帮助(H)", 'H');
		mHelp_About = new JMenuItem("关于(A)", 'A');
		mHelp_HelpTopics.addActionListener(this);
		mHelp_About.addActionListener(this);
		mHelp.add(mHelp_HelpTopics);
		mHelp.addSeparator(); // 添加分割线
		mHelp.add(mHelp_About);

		// -------------------创建右键弹出菜单
		popupMenu = new JPopupMenu();
		popupMenu_Undo = new JMenuItem("撤消(U)", 'U');
		popupMenu_Cut = new JMenuItem("剪切(T)", 'T');
		popupMenu_Copy = new JMenuItem("复制(C)", 'C');
		popupMenu_Paste = new JMenuItem("粘贴(P)", 'P');
		popupMenu_Delete = new JMenuItem("删除(D)", 'D');
		popupMenu_SelectAll = new JMenuItem("全选(A)", 'A');

		popupMenu_Undo.setEnabled(false); // 撤消选项初始设为不可用

		// ---------------向右键菜单添加菜单项和分隔符
		popupMenu.add(popupMenu_Undo);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_Cut);
		popupMenu.add(popupMenu_Copy);
		popupMenu.add(popupMenu_Paste);
		popupMenu.add(popupMenu_Delete);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_SelectAll);
		// --------------------右键菜单注册事件
		popupMenu_Undo.addActionListener(this);
		popupMenu_Cut.addActionListener(this);
		popupMenu_Copy.addActionListener(this);
		popupMenu_Paste.addActionListener(this);
		popupMenu_Delete.addActionListener(this);
		popupMenu_SelectAll.addActionListener(this);
		// --------------------文本编辑区注册右键菜单事件
		Text.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkForTriggerEvent(e);

			}

			private void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger())
					popupMenu.show(e.getComponent(), e.getX(), e.getY());// 在组件调用者的坐标空间中的位置
																			// X、Y
																			// 显示弹出菜单。
				else {
					statusLabel3.setText("当前光标所在行数: " + getlineNumber());
				}
				checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
				Text.requestFocus(); // 编辑区获取焦点
			}
		});

		// ----------------------------创建工具栏
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

		Icon newIcon = new ImageIcon("Icons/new.gif");
		Icon openIcon = new ImageIcon("Icons/open.gif");
		Icon saveIcon = new ImageIcon("Icons/save.gif");
		Icon saveAsIcon = new ImageIcon("Icons/saveas.gif");
		Icon printIcon = new ImageIcon("Icons/print.gif");
		Icon undoIcon = new ImageIcon("Icons/undo.gif");
		Icon cutIcon = new ImageIcon("Icons/cut.gif");
		Icon copyIcon = new ImageIcon("Icons/copy.gif");
		Icon pasteIcon = new ImageIcon("Icons/paste.gif");
		Icon deleteIcon = new ImageIcon("Icons/delete.gif");
		Icon searchIcon = new ImageIcon("Icons/search.gif");
		Icon timeIcon = new ImageIcon("Icons/time.gif");
		Icon fontIcon = new ImageIcon("Icons/font.gif");
		Icon boldIcon = new ImageIcon("Icons/bold.gif");
		Icon italicIcon = new ImageIcon("Icons/italic.gif");
		Icon bgcolorIcon = new ImageIcon("Icons/bgcolor.gif");
		Icon fgcolorIcon = new ImageIcon("Icons/fgcolor.gif");
		Icon helpIcon = new ImageIcon("Icons/help.gif");

		newButton = new JButton(newIcon);
		openButton = new JButton(openIcon);
		saveButton = new JButton(saveIcon);
		saveAsButton = new JButton(saveAsIcon);
		printButton = new JButton(printIcon);
		undoButton = new JButton(undoIcon);
		undoButton.setEnabled(false);
		cutButton = new JButton(cutIcon);
		cutButton.setEnabled(false);
		copyButton = new JButton(copyIcon);
		copyButton.setEnabled(false);
		pasteButton = new JButton(pasteIcon);
		pasteButton.setEnabled(false);
		deleteButton = new JButton(deleteIcon);
		deleteButton.setEnabled(false);
		searchButton = new JButton(searchIcon);
		timeButton = new JButton(timeIcon);
		fontButton = new JButton(fontIcon);
		boldButton = new JButton(boldIcon);
		italicButton = new JButton(italicIcon);
		fgcolorButton = new JButton(fgcolorIcon);
		bgcolorButton = new JButton(bgcolorIcon);
		helpButton = new JButton(helpIcon);

		newButton.setPreferredSize(new Dimension(22, 22));
		openButton.setPreferredSize(new Dimension(22, 22));
		saveButton.setPreferredSize(new Dimension(22, 22));
		saveAsButton.setPreferredSize(new Dimension(22, 22));
		printButton.setPreferredSize(new Dimension(22, 22));
		undoButton.setPreferredSize(new Dimension(22, 22));
		cutButton.setPreferredSize(new Dimension(22, 22));
		copyButton.setPreferredSize(new Dimension(22, 22));
		pasteButton.setPreferredSize(new Dimension(22, 22));
		deleteButton.setPreferredSize(new Dimension(22, 22));
		searchButton.setPreferredSize(new Dimension(22, 22));
		timeButton.setPreferredSize(new Dimension(22, 22));
		fontButton.setPreferredSize(new Dimension(22, 22));
		boldButton.setPreferredSize(new Dimension(22, 22));
		italicButton.setPreferredSize(new Dimension(22, 22));
		fgcolorButton.setPreferredSize(new Dimension(22, 22));
		bgcolorButton.setPreferredSize(new Dimension(22, 22));
		helpButton.setPreferredSize(new Dimension(22, 22));
		// -----------------------------------注册工具栏按钮事件
		newButton.addActionListener(this);
		openButton.addActionListener(this);
		saveButton.addActionListener(this);
		saveAsButton.addActionListener(this);
		printButton.addActionListener(this);
		undoButton.addActionListener(this);
		cutButton.addActionListener(this);
		copyButton.addActionListener(this);
		pasteButton.addActionListener(this);
		deleteButton.addActionListener(this);
		searchButton.addActionListener(this);
		timeButton.addActionListener(this);
		fontButton.addActionListener(this);
		boldButton.addActionListener(this);
		italicButton.addActionListener(this);
		fgcolorButton.addActionListener(this);
		bgcolorButton.addActionListener(this);
		helpButton.addActionListener(this);
		// ------------------------设置按钮提示文字
		newButton.setToolTipText("新建");
		openButton.setToolTipText("打开");
		saveButton.setToolTipText("保存");
		saveAsButton.setToolTipText("另存为");
		printButton.setToolTipText("打印");
		undoButton.setToolTipText("撤消");
		cutButton.setToolTipText("剪切");
		copyButton.setToolTipText("复制");
		pasteButton.setToolTipText("粘贴");
		deleteButton.setToolTipText("删除所选");
		searchButton.setToolTipText("查找与替换");
		timeButton.setToolTipText("插入时间/日期");
		fontButton.setToolTipText("设置字体");
		boldButton.setToolTipText("粗体");
		italicButton.setToolTipText("斜体");
		fgcolorButton.setToolTipText("设置字体颜色");
		bgcolorButton.setToolTipText("设置背景颜色");
		helpButton.setToolTipText("帮助");
		// 设置撤消、重做、剪切、复制、粘贴、删除等工具栏按钮不可用时的图片(灰色)
		undoButton.setDisabledIcon(new ImageIcon("Icons/undo1.gif"));
		cutButton.setDisabledIcon(new ImageIcon("Icons/cut1.gif"));
		copyButton.setDisabledIcon(new ImageIcon("Icons/copy1.gif"));
		pasteButton.setDisabledIcon(new ImageIcon("Icons/paste1.gif"));
		deleteButton.setDisabledIcon(new ImageIcon("Icons/delete1.gif"));
		// ------------------------向工具栏添加按钮
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(saveAsButton);
		toolBar.add(printButton);
		toolBar.add(undoButton);
		toolBar.add(cutButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.add(deleteButton);
		toolBar.add(searchButton);
		toolBar.add(timeButton);
		toolBar.add(fontButton);
		toolBar.add(boldButton);
		toolBar.add(italicButton);
		toolBar.add(fgcolorButton);
		toolBar.add(bgcolorButton);
		toolBar.add(helpButton);

		// --------------------------------------向容器添加工具栏
		container.add(toolBar, BorderLayout.NORTH);
		// -----------------------------------创建和添加状态栏

		statusBar = new JToolBar();

		statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		statusLabel1 = new JLabel("按F1获取帮助                ");
		statusLabel2 = new JLabel("    当前时间：" + hour + ":" + min + ":" + second);
		statusLabel3 = new JLabel("    当前光标所在行数" + getlineNumber());
		statusBar.add(statusLabel1);
		statusBar.addSeparator();
		statusBar.add(statusLabel2);
		statusBar.addSeparator();
		statusBar.add(statusLabel3);
		container.add(statusBar, BorderLayout.SOUTH);
		statusBar.setVisible(true);
		// ------------------------------------改变标题栏窗口左侧默认图标
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image image = tk.createImage("Icons/notepad.gif");
		this.setIconImage(image);
		this.setJMenuBar(MenuBar); // 向窗口添加菜单条
		container.add(scroll, BorderLayout.CENTER); // 向容器添加文本编辑区
		this.pack();
		this.setSize(890, 630);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		checkMenuItemEnabled();
		Text.requestFocus();
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				checkText();
			}
		});
		Clock clock = new Clock();
		clock.start();
	}

	public void checkText() {
		Text.requestFocus();
		String currentValue = Text.getText();
		boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
		if (isTextChange) {

			int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存。是否保存?", "提示", JOptionPane.YES_NO_CANCEL_OPTION);

			if (saveChoose == JOptionPane.YES_OPTION) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("确定");
				fileChooser.setDialogTitle("另存为");

				int result = fileChooser.showSaveDialog(this);

				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel1.setText("您没有选择任何文件");
					return;
				}

				saveFileName = fileChooser.getSelectedFile();

				if (saveFileName == null || saveFileName.getName().equals(""))
					JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
				else {
					saveFile();
					Text.setText("");
					this.setTitle("新建文本");
					statusLabel1.setText("　新建文本");
				}
			}

			else if (saveChoose == JOptionPane.NO_OPTION) {
				System.exit(0);
			}

			else if (saveChoose == JOptionPane.CANCEL_OPTION) {
				Text.requestFocus();
			}
		}

		else if (!isTextChange) {
			System.exit(0);
		}
	}

	public void checkMenuItemEnabled() {
		String selectText = Text.getSelectedText();

		if (selectText == null) {
			mEdit_Cut.setEnabled(false);
			popupMenu_Cut.setEnabled(false);
			cutButton.setEnabled(false);
			mEdit_Copy.setEnabled(false);
			popupMenu_Copy.setEnabled(false);
			copyButton.setEnabled(false);
			mEdit_Del.setEnabled(false);
			popupMenu_Delete.setEnabled(false);
			deleteButton.setEnabled(false);
		} else {
			mEdit_Cut.setEnabled(true);
			popupMenu_Cut.setEnabled(true);
			cutButton.setEnabled(true);
			mEdit_Copy.setEnabled(true);
			popupMenu_Copy.setEnabled(true);
			copyButton.setEnabled(true);
			mEdit_Del.setEnabled(true);
			popupMenu_Delete.setEnabled(true);
			deleteButton.setEnabled(true);
		}

		// 粘贴功能可用性判断
		Transferable contents = clipBoard.getContents(this);
		if (contents == null) {
			mEdit_Paste.setEnabled(false);
			popupMenu_Paste.setEnabled(false);
			pasteButton.setEnabled(false);
		} else {
			mEdit_Paste.setEnabled(true);
			popupMenu_Paste.setEnabled(true);
			pasteButton.setEnabled(true);
		}

	}

	public int getlineNumber() {
		int totalLine = Text.getLineCount();
		int[] lineNumber = new int[totalLine + 1];
		int pos = 0, t = 0, num = 0, i = 0;
		String s = Text.getText();
		while (true) {
			pos = s.indexOf('\12', pos); // 返回 \n 所在的位置
			if (pos == -1)
				break;
			lineNumber[t++] = pos++;
		}
		if (Text.getCaretPosition() <= lineNumber[0])
			num = 1;
		else {
			if (Text.getCaretPosition() > lineNumber[Text.getLineCount() - 1])
				num = Text.getLineCount();
			for (i = 0; i < totalLine + 1; i++) {
				if (Text.getCaretPosition() <= lineNumber[i]) {
					num = i + 1;
					break;
				} else
					continue;
			}
		}
		return num;
	}

	public void saveFile() {
		try {
			FileWriter fw = new FileWriter(saveFileName);
			fw.write(Text.getText());
			fw.close();
		} catch (Exception e) {
		}
	}

	public void mySearch() {
		final JDialog findDialog = new JDialog(this, "查找与替换", true);
		Container con = findDialog.getContentPane();
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel searchContentLabel = new JLabel("查找内容(N) :");
		JLabel replaceContentLabel = new JLabel("替换为(P)　 :");
		final JTextField findText = new JTextField(22);
		final JTextField replaceText = new JTextField(22);
		final JCheckBox matchcase = new JCheckBox("区分大小写");
		ButtonGroup bGroup = new ButtonGroup();
		final JRadioButton up = new JRadioButton("向上(U)");
		final JRadioButton down = new JRadioButton("向下(D)");
		down.setSelected(true);
		bGroup.add(up);
		bGroup.add(down);
		JButton searchNext = new JButton("查找下一个(F)");
		JButton replace = new JButton("替换(R)");
		final JButton replaceAll = new JButton("全部替换(A)");
		searchNext.setPreferredSize(new Dimension(110, 22));
		replace.setPreferredSize(new Dimension(110, 22));
		replaceAll.setPreferredSize(new Dimension(110, 22));
		// "替换"按钮的事件处理
		replace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (replaceText.getText().length() == 0 && Text.getSelectedText() != null)
					Text.replaceSelection("");
				if (replaceText.getText().length() > 0 && Text.getSelectedText() != null)
					Text.replaceSelection(replaceText.getText());
			}
		});

		// "替换全部"按钮的事件处理
		replaceAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Text.setCaretPosition(0); // 将光标放到编辑区开头
				int a = 0, b = 0, replaceCount = 0;

				if (findText.getText().length() == 0) {
					JOptionPane.showMessageDialog(findDialog, "请填写查找内容!", "提示", JOptionPane.WARNING_MESSAGE);
					findText.requestFocus(true);
					return;
				}
				while (a > -1) {

					int FindStartPos = Text.getCaretPosition();
					String str1, str2, str3, str4, strA, strB;
					str1 = Text.getText();
					str2 = str1.toLowerCase();
					str3 = findText.getText();
					str4 = str3.toLowerCase();

					if (matchcase.isSelected()) {
						strA = str1;
						strB = str3;
					} else {
						strA = str2;
						strB = str4;
					}

					if (up.isSelected()) {
						if (Text.getSelectedText() == null) {
							a = strA.lastIndexOf(strB, FindStartPos - 1);
						} else {
							a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
						}
					} else if (down.isSelected()) {
						if (Text.getSelectedText() == null) {
							a = strA.indexOf(strB, FindStartPos);
						} else {
							a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
						}

					}

					if (a > -1) {
						if (up.isSelected()) {
							Text.setCaretPosition(a);
							b = findText.getText().length();
							Text.select(a, a + b);
						} else if (down.isSelected()) {
							Text.setCaretPosition(a);
							b = findText.getText().length();
							Text.select(a, a + b);
						}
					} else {
						if (replaceCount == 0) {
							JOptionPane.showMessageDialog(findDialog, "找不到您查找的内容!", "记事本", JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(findDialog, "成功替换" + replaceCount + "个匹配内容!", "替换成功", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					if (replaceText.getText().length() == 0 && Text.getSelectedText() != null) {
						Text.replaceSelection("");
						replaceCount++;
					}
					if (replaceText.getText().length() > 0 && Text.getSelectedText() != null) {
						Text.replaceSelection(replaceText.getText());
						replaceCount++;
					}
				}// end while
			}
		}); /* "替换全部"按钮的事件处理结束 */

		// "查找下一个"按钮事件处理
		searchNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int a = 0, b = 0;
				int FindStartPos = Text.getCaretPosition();
				String str1, str2, str3, str4, strA, strB;
				str1 = Text.getText();
				str2 = str1.toLowerCase();
				str3 = findText.getText();
				str4 = str3.toLowerCase();
				// "区分大小写"的CheckBox被选中
				if (matchcase.isSelected()) {
					strA = str1;
					strB = str3;
				} else {
					strA = str2;
					strB = str4;
				}

				if (up.isSelected()) {
					if (Text.getSelectedText() == null) {
						a = strA.lastIndexOf(strB, FindStartPos - 1);
					} else {
						a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
					}
				} else if (down.isSelected()) {
					if (Text.getSelectedText() == null) {
						a = strA.indexOf(strB, FindStartPos);
					} else {
						a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
					}

				}
				if (a > -1) {
					if (up.isSelected()) {
						Text.setCaretPosition(a);
						b = findText.getText().length();
						Text.select(a, a + b);
					} else if (down.isSelected()) {
						Text.setCaretPosition(a);
						b = findText.getText().length();
						Text.select(a, a + b);
					}
				} else {
					JOptionPane.showMessageDialog(null, "找不到您查找的内容!", "记事本", JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});/* "查找下一个"按钮事件处理结束 */
		// "取消"按钮及事件处理
		JButton cancel = new JButton("取消");
		cancel.setPreferredSize(new Dimension(110, 22));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findDialog.dispose();
			}
		});

		// 创建"查找与替换"对话框的界面
		JPanel bottomPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel topPanel = new JPanel();

		JPanel direction = new JPanel();
		direction.setBorder(BorderFactory.createTitledBorder("方向 "));
		direction.add(up);
		direction.add(down);
		direction.setPreferredSize(new Dimension(170, 60));
		JPanel replacePanel = new JPanel();
		replacePanel.setLayout(new GridLayout(2, 1));
		replacePanel.add(replace);
		replacePanel.add(replaceAll);

		topPanel.add(searchContentLabel);
		topPanel.add(findText);
		topPanel.add(searchNext);
		centerPanel.add(replaceContentLabel);
		centerPanel.add(replaceText);
		centerPanel.add(replacePanel);
		bottomPanel.add(matchcase);
		bottomPanel.add(direction);
		bottomPanel.add(cancel);

		con.add(topPanel);
		con.add(centerPanel);
		con.add(bottomPanel);

		// 设置"查找与替换"对话框的大小、可更改大小(否)、位置和可见性
		findDialog.setSize(410, 210);
		findDialog.setResizable(false);
		findDialog.setLocation(230, 280);
		findDialog.setVisible(true);
	}

	/* 方法mySearch()结束 */

	// 实现ActionListener的事件处理方法public void actionPerformed(ActionEvent e)
	public void actionPerformed(ActionEvent e) {
		// 新建
		if (e.getActionCommand().equals("新建(N)") || e.getSource() == newButton)
		// 最初发生 Event 的对象
		// if(e.getSource()==mFile_New||e.getSource()==newButton)
		{
			Text.requestFocus();
			String currentValue = Text.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;

			if (isTextChange) {

				int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存。是否保存?", "提示", JOptionPane.YES_NO_CANCEL_OPTION);

				if (saveChoose == JOptionPane.YES_OPTION) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("确定");
					fileChooser.setDialogTitle("另存为");
					int result = fileChooser.showSaveDialog(this);
					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel1.setText("您没有选择任何文件");
						return;
					}
					saveFileName = fileChooser.getSelectedFile();
					if (saveFileName == null || saveFileName.getName().equals(""))
						JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
					else {
						saveFile();
						Text.setText("");
						this.setTitle("新建文本");
						statusLabel1.setText("　新建文本");
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					Text.replaceRange("", 0, Text.getText().length());
					statusLabel1.setText("　新建文件");
					this.setTitle("无标题 - 记事本");
					isNewFile = true;
					undo.discardAllEdits(); // 撤消所有的"撤消"操作
					mEdit_Undo.setEnabled(false);
					popupMenu_Undo.setEnabled(false);
					undoButton.setEnabled(false);
					redoButton.setEnabled(false);
					oldValue = Text.getText();
				} else if (saveChoose == JOptionPane.CANCEL_OPTION) {
					return;
				}
			} else {
				// Text.replaceRange("", 0, Text.getText().length());
				Text.setText("");
				statusLabel1.setText("　新建文件");
				this.setTitle("无标题 - 记事本");
				isNewFile = true;
				undo.discardAllEdits();
				mEdit_Undo.setEnabled(false);
				popupMenu_Undo.setEnabled(false);
				undoButton.setEnabled(false);
				oldValue = Text.getText();
			}
		}// 新建处理结束

		// 打开
		else if (e.getActionCommand().equals("打开(O)") || e.getSource() == openButton) {
			Text.requestFocus();
			String currentValue = Text.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;

			if (isTextChange) {

				int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存。是否保存?", "提示", JOptionPane.YES_NO_CANCEL_OPTION);

				if (saveChoose == JOptionPane.YES_OPTION) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("确定");
					fileChooser.setDialogTitle("另存为");

					int result = fileChooser.showSaveDialog(this);

					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel1.setText("您没有选择任何文件");
						return;
					}

					saveFileName = fileChooser.getSelectedFile();

					if (saveFileName == null || saveFileName.getName().equals(""))
						JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
					else {
						saveFile();
						isNewFile = false;
						currentFile = saveFileName;
						oldValue = Text.getText();
						this.setTitle(saveFileName.getName() + "  - 记事本");
						statusLabel1.setText("　当前打开文件:" + saveFileName.getAbsoluteFile());
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					String str = null;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("确定");
					fileChooser.setDialogTitle("打开文件");
					int result = fileChooser.showOpenDialog(this);
					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel1.setText("您没有选择任何文件");
						return;
					}
					fileName = fileChooser.getSelectedFile();
					if (fileName == null || fileName.getName().equals(""))
						JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
					else {
						try {
							FileReader fr = new FileReader(fileName);
							BufferedReader bfr = new BufferedReader(fr);
							Text.setText("");
							while ((str = bfr.readLine()) != null) {// 每次读取一行，直到文件结束
								Text.append(str + "\15\12");
							}// endwhile
							this.setTitle(fileName.getName() + "  - 记事本");
							statusLabel1.setText("　当前打开文件:" + fileName.getAbsoluteFile());
							fr.close();
							isNewFile = false;
							currentFile = fileName;
							oldValue = Text.getText();
						} catch (IOException ioException) {
						}
					}
				} else {
					return;
				}
			}

			else {
				String str = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("确定");
				fileChooser.setDialogTitle("打开文件");
				int result = fileChooser.showOpenDialog(this);
				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel1.setText("　您没有选择任何文件");
					return;
				}
				fileName = fileChooser.getSelectedFile();
				if (fileName == null || fileName.getName().equals(""))
					JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
				else {
					try {
						FileReader fr = new FileReader(fileName);
						BufferedReader bfr = new BufferedReader(fr);
						Text.setText("");
						while ((str = bfr.readLine()) != null) {// 每次读取一行，直到文件结束
							Text.append(str + "\15\12");
						}// endwhile

						this.setTitle(fileName.getName() + "  - 记事本");
						statusLabel1.setText("　当前打开文件:" + fileName.getAbsoluteFile());
						fr.close();
						isNewFile = false;
						currentFile = fileName;
						oldValue = Text.getText();
					} catch (IOException ioException) {
					}
				}

			}
		}// "打开"处理结束

		// 保存
		else if (e.getSource() == mFile_Save || e.getSource() == saveButton) {
			Text.requestFocus();
			if (isNewFile) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("确定");
				fileChooser.setDialogTitle("另存为");
				int result = fileChooser.showSaveDialog(this);
				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel1.setText("　您没有选择任何文件");
					return;
				}
				saveFileName = fileChooser.getSelectedFile();
				if (saveFileName == null || saveFileName.getName().equals(""))
					JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
				else {
					saveFile();
					isNewFile = false;
					currentFile = saveFileName;
					oldValue = Text.getText();
					this.setTitle(saveFileName.getName() + "  - 记事本");
					statusLabel1.setText("　当前打开文件:" + saveFileName.getAbsoluteFile());
				}
			} else {
				try {
					FileWriter fw = new FileWriter(currentFile);
					BufferedWriter bfw = new BufferedWriter(fw);
					bfw.write(Text.getText(), 0, Text.getText().length());
					bfw.flush();
					fw.close();
				} catch (IOException ioException) {
				}
			}
		}// "保存"处理结束

		// 另存为
		else if (e.getSource() == mFile_ASave || e.getSource() == saveAsButton) {
			Text.requestFocus();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setApproveButtonText("确定");
			fileChooser.setDialogTitle("另存为");
			int result = fileChooser.showSaveDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				statusLabel1.setText("　您没有选择任何文件");
				return;
			}
			saveFileName = fileChooser.getSelectedFile();
			if (saveFileName == null || saveFileName.getName().equals(""))
				JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
			else {
				saveFile();
				isNewFile = false;
				currentFile = saveFileName;
				oldValue = Text.getText();
				this.setTitle(saveFileName.getName() + "  - 记事本");
				statusLabel1.setText("　当前打开文件:" + saveFileName.getAbsoluteFile());
			}

		}// "另存为"处理结束

		// 打印
		else if (e.getSource() == mFile_Print || e.getSource() == printButton) {
			Text.requestFocus();
			JOptionPane.showMessageDialog(this, "此功能尚未添加!", "提示", JOptionPane.WARNING_MESSAGE);
		}

		// 退出
		else if (e.getSource() == mFile_Exit) {
			int exitChoose = JOptionPane.showConfirmDialog(this, "确定要退出么？", "退出提示", JOptionPane.OK_CANCEL_OPTION);
			if (exitChoose == JOptionPane.OK_OPTION) {
				checkText();
			} else {
				return;
			}
		}

		// 撤消
		else if (e.getSource() == mEdit_Undo || e.getSource() == popupMenu_Undo || e.getSource() == undoButton) {
			Text.requestFocus();
			if (undo.canUndo()) {
				try {
					undo.undo();

				} catch (CannotUndoException ex) {
					System.out.println("Unable to undo: " + ex);
					ex.printStackTrace();
				}

				if (!undo.canUndo()) {
					mEdit_Undo.setEnabled(false);
					popupMenu_Undo.setEnabled(false);
					undoButton.setEnabled(false);

				}
			}
		}

		// 剪切
		else if (e.getSource() == mEdit_Cut || e.getSource() == popupMenu_Cut || e.getSource() == cutButton) {
			Text.requestFocus();
			String text = Text.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			Text.replaceRange("", Text.getSelectionStart(), Text.getSelectionEnd());
			checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
		}

		// 复制
		else if (e.getSource() == mEdit_Copy || e.getSource() == popupMenu_Copy || e.getSource() == copyButton) {
			Text.requestFocus();
			String text = Text.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
		}

		// 粘贴
		else if (e.getSource() == mEdit_Paste || e.getSource() == popupMenu_Paste || e.getSource() == pasteButton) {
			Text.requestFocus();
			Transferable contents = clipBoard.getContents(this);
			if (contents == null)
				return;
			String text;
			text = "";

			try {
				text = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception exception) {
			}

			Text.replaceRange(text, Text.getSelectionStart(), Text.getSelectionEnd());
			checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
		}

		// 删除
		else if (e.getSource() == mEdit_Del || e.getSource() == popupMenu_Delete || e.getSource() == deleteButton) {
			Text.requestFocus();
			Text.replaceRange("", Text.getSelectionStart(), Text.getSelectionEnd());
			checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
		}

		// 查找
		else if (e.getSource() == mEdit_Search || e.getSource() == searchButton) {
			Text.requestFocus();
			if (e.getSource() == searchButton) {
				Text.requestFocus();
				Text.setCaretPosition(0);
			}
			mySearch();
		}

		// 查找下一个(此功能尚未能很好实现，所以就先用查找功能来代替)
		else if (e.getSource() == mEdit_SearchNext) {
			mySearch();
		}

		// 替换(与查找功能集成在一起了)
		else if (e.getSource() == mEdit_Replace) {
			mySearch();
		}

		// 转到
		else if (e.getSource() == mEdit_Turnto) {
			final JDialog gotoDialog = new JDialog(this, "转到下列行");
			JLabel gotoLabel = new JLabel("行数(L):");
			final JTextField linenum = new JTextField(5);
			linenum.setText("1");
			linenum.selectAll();

			JButton okButton = new JButton("确定");
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int totalLine = Text.getLineCount();
					int[] lineNumber = new int[totalLine + 1];
					String s = Text.getText();
					int pos = 0, t = 0;

					while (true) {
						pos = s.indexOf('\12', pos);
						// System.out.println("引索pos:"+pos);
						if (pos == -1)
							break;
						lineNumber[t++] = pos++;
					}

					int gt = 1;
					try {
						gt = Integer.parseInt(linenum.getText());
					} catch (NumberFormatException efe) {
						JOptionPane.showMessageDialog(null, "请输入行数!", "提示", JOptionPane.WARNING_MESSAGE);
						linenum.requestFocus(true);
						return;
					}

					if (gt < 2 || gt >= totalLine) {
						if (gt < 2)
							Text.setCaretPosition(0);
						else
							Text.setCaretPosition(s.length());
					} else
						Text.setCaretPosition(lineNumber[gt - 2] + 1);

					gotoDialog.dispose();
				}

			});

			JButton cancelButton = new JButton("取消");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gotoDialog.dispose();
				}
			});

			Container con = gotoDialog.getContentPane();
			con.setLayout(new FlowLayout());
			con.add(gotoLabel);
			con.add(linenum);
			con.add(okButton);
			con.add(cancelButton);

			gotoDialog.setSize(200, 100);
			gotoDialog.setResizable(false);
			gotoDialog.setLocation(300, 280);
			gotoDialog.setVisible(true);

		}// "转到"处理结束

		// 插入时间日期
		else if (e.getSource() == mEdit_TimeDate || e.getSource() == timeButton) {
			Text.requestFocus();
			SimpleDateFormat currentDateTime = new SimpleDateFormat("HH:mm yyyy-MM-dd");
			Text.insert(currentDateTime.format(new Date()), Text.getCaretPosition());
		}

		// 全选
		else if (e.getSource() == popupMenu_SelectAll || e.getSource() == mEdit_SelectAll) {
			Text.selectAll();
		}

		// 自动换行
		else if (e.getSource() == formatMenu_LineWrap) {
			if (formatMenu_LineWrap.getState()) {
				Text.setLineWrap(true);
			} else
				Text.setLineWrap(false);
		}

		// 字体设置
		else if (e.getSource() == formatMenu_Font || e.getSource() == fontButton) {
			Text.requestFocus();
			new MyFont();
		}

		// 设置字体颜色(前景色)
		else if (e.getSource() == formatMenu_Color_FgColor || e.getSource() == fgcolorButton) {
			Text.requestFocus();
			Color color = JColorChooser.showDialog(this, "更改字体颜色", Color.black);
			if (color != null) {
				Text.setForeground(color);
			} else
				return;
		}

		// 设置编辑区背景颜色
		else if (e.getSource() == formatMenu_Color_BgColor || e.getSource() == bgcolorButton) {
			Text.requestFocus();
			Color color = JColorChooser.showDialog(this, "更改背景颜色", Color.white);
			if (color != null) {
				Text.setBackground(color);
			} else
				return;
		}

		// 设置状态栏可见性
		else if (e.getSource() == viewMenu_Status) {
			if (viewMenu_Status.getState())
				statusBar.setVisible(true);

			else
				statusBar.setVisible(false);

		}

		// 帮助主题
		else if (e.getSource() == mHelp_HelpTopics || e.getSource() == helpButton) {
			JOptionPane.showMessageDialog(this, "记事本支持拖入文本读取\n" + "由于对编码不熟悉保存文\n件时未进行编码转换\n", "帮助主题", JOptionPane.INFORMATION_MESSAGE);
		}

		// 关于
		else if (e.getSource() == mHelp_About) {
			JOptionPane.showMessageDialog(this, "       VXBB的记事本\n" + "       QQ：491697374\n" + "     JAVA图形界面练习\n", "关于记事本", JOptionPane.INFORMATION_MESSAGE);
		}

		// 工具栏"粗体"按钮事件处理
		else if (e.getSource() == boldButton) {
			Text.requestFocus();
			Font tempFont = Text.getFont();

			if (Text.getFont().getStyle() == Font.PLAIN) {
				tempFont = new Font(Text.getFont().getFontName(), Font.BOLD, Text.getFont().getSize());
			} else if (Text.getFont().getStyle() == Font.ITALIC) {
				tempFont = new Font(Text.getFont().getFontName(), Font.BOLD + Font.ITALIC, Text.getFont().getSize());
			} else if (Text.getFont().getStyle() == Font.BOLD) {
				tempFont = new Font(Text.getFont().getFontName(), Font.PLAIN, Text.getFont().getSize());
			} else if (Text.getFont().getStyle() == (Font.BOLD + Font.ITALIC)) {
				tempFont = new Font(Text.getFont().getFontName(), Font.ITALIC, Text.getFont().getSize());
			}

			Text.setFont(tempFont);
		}

		// 工具栏"斜体"按钮事件处理
		else if (e.getSource() == italicButton) {
			Text.requestFocus();
			Font tempFont = Text.getFont();

			if (Text.getFont().getStyle() == Font.PLAIN) {
				tempFont = new Font(Text.getFont().getFontName(), Font.ITALIC, Text.getFont().getSize());
			} else if (Text.getFont().getStyle() == Font.ITALIC) {
				tempFont = new Font(Text.getFont().getFontName(), Font.PLAIN, Text.getFont().getSize());
			} else if (Text.getFont().getStyle() == Font.BOLD) {
				tempFont = new Font(Text.getFont().getFontName(), Font.BOLD + Font.ITALIC, Text.getFont().getSize());
			} else if (Text.getFont().getStyle() == (Font.BOLD + Font.ITALIC)) {
				tempFont = new Font(Text.getFont().getFontName(), Font.BOLD, Text.getFont().getSize());
			}

			Text.setFont(tempFont);
		}

	}/* 方法actionPerformed()结束 */

	class Clock extends Thread { // 模拟时钟
		public void run() {
			while (true) {
				GregorianCalendar time = new GregorianCalendar();
				int hour = time.get(Calendar.HOUR_OF_DAY);
				int min = time.get(Calendar.MINUTE);
				int second = time.get(Calendar.SECOND);
				statusLabel2.setText("    当前时间：" + hour + ":" + min + ":" + second);
				try {
					Thread.sleep(950);
				} catch (InterruptedException exception) {
				}

			}
		}
	}

	// 用于设置字体的类MyFont
	class MyFont implements ActionListener {
		final JDialog fontDialog;
		final JTextField tfFont, tfSize, tfStyle;
		final int fontStyleConst[] = { Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD + Font.ITALIC };
		final JList listStyle, listFont, listSize;
		JLabel sample;
		JPanel pane1, pane2, pane3, pane4;

		// 构造函数MyFont
		public MyFont() {

			fontDialog = new JDialog(Notepad4.this, "字体设置", true);
			Container con = fontDialog.getContentPane();
			con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
			pane1 = new JPanel();
			pane2 = new JPanel();
			pane3 = new JPanel();
			pane4 = new JPanel();
			Font currentFont = Text.getFont();

			JLabel lblFont = new JLabel("字体(F):");
			JLabel lblStyle = new JLabel("字形(Y):");
			JLabel lblSize = new JLabel("大小(S):");

			lblStyle.setHorizontalAlignment(SwingConstants.CENTER);
			lblSize.setHorizontalAlignment(SwingConstants.CENTER);
			lblFont.setPreferredSize(new Dimension(91, 20));
			lblStyle.setPreferredSize(new Dimension(82, 20));
			lblSize.setPreferredSize(new Dimension(100, 20));
			tfFont = new JTextField(13);
			tfFont.setText(currentFont.getFontName());
			tfFont.selectAll();
			tfFont.setPreferredSize(new Dimension(200, 20));
			tfStyle = new JTextField(10);
			if (currentFont.getStyle() == Font.PLAIN)
				tfStyle.setText("常规");
			else if (currentFont.getStyle() == Font.BOLD)
				tfStyle.setText("粗体");
			else if (currentFont.getStyle() == Font.ITALIC)
				tfStyle.setText("斜体");
			else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC))
				tfStyle.setText("粗斜体");

			tfFont.selectAll();
			tfStyle.setPreferredSize(new Dimension(200, 20));
			tfSize = new JTextField(7);
			tfSize.setText(currentFont.getSize() + "");
			tfSize.selectAll();
			tfSize.setPreferredSize(new Dimension(200, 20));

			final String fontStyle[] = { "常规", "粗体", "斜体", "粗斜体" };
			listStyle = new JList(fontStyle);

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final String fontName[] = ge.getAvailableFontFamilyNames();
			int defaultFontIndex = 0;
			for (int i = 0; i < fontName.length; i++) {
				if (fontName[i].equals(currentFont.getFontName())) {
					defaultFontIndex = i;
					break;
				}
			}
			listFont = new JList(fontName);
			listFont.setSelectedIndex(defaultFontIndex);
			listFont.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listFont.setVisibleRowCount(7);
			listFont.setFixedCellWidth(99);
			listFont.setFixedCellHeight(20);
			listFont.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					tfFont.setText(fontName[listFont.getSelectedIndex()]);
					tfFont.requestFocus();
					tfFont.selectAll();
					updateSample();
				}
			});

			listStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (currentFont.getStyle() == Font.PLAIN)
				listStyle.setSelectedIndex(0);
			else if (currentFont.getStyle() == Font.BOLD)
				listStyle.setSelectedIndex(1);
			else if (currentFont.getStyle() == Font.ITALIC)
				listStyle.setSelectedIndex(2);
			else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC))
				listStyle.setSelectedIndex(3);

			listStyle.setVisibleRowCount(7);
			listStyle.setFixedCellWidth(85);
			listStyle.setFixedCellHeight(20);
			listStyle.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					tfStyle.setText(fontStyle[listStyle.getSelectedIndex()]);
					tfStyle.requestFocus();
					tfStyle.selectAll();
					updateSample();
				}
			});

			final String fontSize[] = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72" };
			listSize = new JList(fontSize);
			int defaultFontSizeIndex = 0;
			for (int i = 0; i < fontSize.length; i++) {
				if (fontSize[i].equals(currentFont.getSize() + "")) {
					defaultFontSizeIndex = i;
					break;
				}
			}
			listSize.setSelectedIndex(defaultFontSizeIndex);

			listSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listSize.setVisibleRowCount(7);
			listSize.setFixedCellWidth(50);
			listSize.setFixedCellHeight(20);
			listSize.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					tfSize.setText(fontSize[listSize.getSelectedIndex()]);
					tfSize.requestFocus();
					tfSize.selectAll();
					updateSample();
				}
			});
			fontOkButton = new JButton("确定");
			fontOkButton.addActionListener(this);
			JButton cancelButton = new JButton("取消");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fontDialog.dispose();
				}
			});

			sample = new JLabel(" 记事本 ");
			sample.setHorizontalAlignment(SwingConstants.CENTER);
			sample.setPreferredSize(new Dimension(150, 31));

			JPanel samplePanel = new JPanel();
			samplePanel.setBorder(BorderFactory.createTitledBorder("示例"));
			samplePanel.add(sample);

			pane1.add(lblFont);
			pane1.add(lblStyle);
			pane1.add(lblSize);
			pane2.add(tfFont);
			pane2.add(tfStyle);
			pane2.add(tfSize);

			pane3.add(new JScrollPane(listFont));
			pane3.add(new JScrollPane(listStyle));
			pane3.add(new JScrollPane(listSize));
			pane4.add(samplePanel);
			pane4.add(fontOkButton);
			pane4.add(cancelButton);
			con.add(pane1);
			con.add(pane2);
			con.add(pane3);
			con.add(pane4);
			updateSample();

			fontDialog.pack();
			fontDialog.setSize(400, 400);
			fontDialog.setLocation(200, 200);
			fontDialog.setResizable(false);
			fontDialog.setVisible(true);
		}// 构造函数结束

		// 更新示例显示的字体和风格大小等
		public void updateSample() {
			Font sampleFont = new Font(tfFont.getText(), fontStyleConst[listStyle.getSelectedIndex()], Integer.parseInt(tfSize.getText()));
			sample.setFont(sampleFont);
		}// End method updateSample

		// 设置文本编辑区的字体
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == fontOkButton) {
				Font tempFont = new Font(tfFont.getText(), fontStyleConst[listStyle.getSelectedIndex()], Integer.parseInt(tfSize.getText()));
				Text.setFont(tempFont);
				fontDialog.dispose();
			}
		}// End method actionPerformed
	}/* End of class MyFont */

	public void removeUpdate(DocumentEvent e) {
		mEdit_Undo.setEnabled(true);
		popupMenu_Undo.setEnabled(true);
		undoButton.setEnabled(true);
	}

	public void insertUpdate(DocumentEvent e) {
		mEdit_Undo.setEnabled(true);
		popupMenu_Undo.setEnabled(true);
		undoButton.setEnabled(true);
	}

	public void changedUpdate(DocumentEvent e) {
		mEdit_Undo.setEnabled(true);
		popupMenu_Undo.setEnabled(true);
		undoButton.setEnabled(true);
	}

	// End of DocumentListener

	// 实现了接口UndoableListener的类UndoHandler
	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent uee) {
			undo.addEdit(uee.getEdit());
		}
	}

	public static void main(String s[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Text = new JTextArea();
		Text.setDragEnabled(true); // 支持自动拖放
		Text.setTransferHandler(new FileTransferHandler(Text));
		new Notepad4();
	}
}

class FileTransferHandler extends TransferHandler {
	JTextArea Text;

	public FileTransferHandler(JTextArea Text) {
		this.Text = Text;
	}

	public boolean importData(JComponent c, Transferable t) {
		try {
			List files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
			addFilesToFilePathList(files);
			return true;
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}

	private void addFilesToFilePathList(List files) {
		for (Iterator iter = files.iterator(); iter.hasNext();) {
			File file = (File) iter.next();
			String str = null;
			try {
				FileReader fr = new FileReader(file);
				BufferedReader bfr = new BufferedReader(fr);
				Text.setText("");
				while ((str = bfr.readLine()) != null) {
					Text.append(str + "\15\12");
				}
			} catch (Exception b) {
			}
		}
	}
}
