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
	// ---------------�ļ��˵�
	JMenuItem mFile_New, mFile_Open, mFile_Save, mFile_ASave, mFile_Print, mFile_Exit;
	// ---------------�༭�˵�
	JMenuItem mEdit_Undo, mEdit_Cut, mEdit_Copy, mEdit_Paste, mEdit_Del, mEdit_Search, mEdit_SearchNext, mEdit_Replace, mEdit_Turnto, mEdit_SelectAll, mEdit_TimeDate;
	// ---------------��ʽ�˵�
	JCheckBoxMenuItem formatMenu_LineWrap;
	JMenu formatMenu_Color;
	JMenuItem formatMenu_Font, formatMenu_Color_FgColor, formatMenu_Color_BgColor;
	// ---------------�鿴�˵�
	JCheckBoxMenuItem viewMenu_Status;
	// ---------------�����˵�
	JMenuItem mHelp_HelpTopics, mHelp_About;
	// ---------------�����˵����˵���
	JPopupMenu popupMenu;
	JMenuItem popupMenu_Undo, popupMenu_Cut, popupMenu_Copy, popupMenu_Paste, popupMenu_Delete, popupMenu_SelectAll;
	// ---------------��������ť
	JButton newButton, openButton, saveButton, saveAsButton, printButton, undoButton, redoButton, cutButton, copyButton, pasteButton, deleteButton, searchButton, timeButton, fontButton, boldButton,
			italicButton, fgcolorButton, bgcolorButton, helpButton;
	// �ı��༭����
	static JTextArea Text;
	// ״̬����ǩ
	JLabel statusLabel1, statusLabel2, statusLabel3;
	JToolBar statusBar;
	// ---------------ϵͳ������
	Toolkit toolKit = Toolkit.getDefaultToolkit();
	Clipboard clipBoard = toolKit.getSystemClipboard();
	// ---------------������������������
	protected UndoManager undo = new UndoManager();
	protected UndoableEditListener undoHandler = new UndoHandler();
	// ----------------��������
	boolean isNewFile = true; // �Ƿ����ļ�(δ�������)
	File currentFile; // ��ǰ�ļ���
	String oldValue; // ��ű༭��ԭ�������ݣ����ڱȽ��ı��Ƿ��иĶ�
	JButton fontOkButton; // �����������"ȷ��"��ť
	// ----------------���ñ༭��Ĭ������
	protected Font defaultFont = new Font("����", Font.PLAIN, 12);
	GregorianCalendar time = new GregorianCalendar();
	int hour = time.get(Calendar.HOUR_OF_DAY);
	int min = time.get(Calendar.MINUTE);
	int second = time.get(Calendar.SECOND);
	File saveFileName = null, fileName = null;

	public Notepad4() {
		super("���±�");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Container container = getContentPane();
		// System.out.println(Text.getDragEnabled()); //֧���Զ��Ϸ�
		JScrollPane scroll = new JScrollPane(Text);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Text.setWrapStyleWord(true); // ���õ�����һ�в�������ʱ����
		Text.setLineWrap(true);
		Text.setFont(defaultFont); // ���ñ༭��Ĭ������
		Text.setBackground(Color.white); // ���ñ༭��Ĭ�ϱ���ɫ
		Text.setForeground(Color.black); // ���ñ༭��Ĭ��ǰ��ɫ
		oldValue = Text.getText(); // ��ȡԭ�ı��༭��������
		// --------------------------�༭��ע���¼�����
		Text.getDocument().addUndoableEditListener(undoHandler); // ��Ӹ���֪ͨ�κθ��ĵĳ���������
		Text.getDocument().addDocumentListener(this); // ��Ӹ���֪ͨ�κθ��ĵ��ĵ�������
		JMenuBar MenuBar = new JMenuBar();
		mFile = new JMenu("�ļ�(F)", true); // �����˵�
		mEdit = new JMenu("�༭(E)", true);
		mMode = new JMenu("��ʽ(O)", true);
		mView = new JMenu("�鿴(V)", true);
		mHelp = new JMenu("����(H)", true);
		mEdit.addActionListener(new ActionListener() // ע���¼�����
				{
					public void actionPerformed(ActionEvent e) {
						checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
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
		// --------------�ļ��˵�
		mFile_New = new JMenuItem("�½�(N)", 'N');
		mFile_Open = new JMenuItem("��(O)", 'O');
		mFile_Save = new JMenuItem("����(S)", 'S');
		mFile_ASave = new JMenuItem("���Ϊ(A)", 'A');
		mFile_Print = new JMenuItem("��ӡ(P)", 'P');
		mFile_Exit = new JMenuItem("�˳�(X)", 'X');
		mFile_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mFile_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mFile_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mFile_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		mFile_New.addActionListener(this); // ע���¼�����
		mFile_Open.addActionListener(this);
		mFile_Save.addActionListener(this);
		mFile_ASave.addActionListener(this);
		mFile_Print.addActionListener(this);
		mFile_Exit.addActionListener(this);
		mFile.add(mFile_New); // ��Ӳ˵���
		mFile.add(mFile_Open);
		mFile.add(mFile_Save);
		mFile.add(mFile_ASave);
		mFile.addSeparator(); // ��ӷָ���
		mFile.add(mFile_Print);
		mFile.addSeparator(); // ��ӷָ���
		mFile.add(mFile_Exit);

		// --------------�༭�˵�
		mEdit_Undo = new JMenuItem("����(U)", 'U');
		mEdit_Cut = new JMenuItem("����(T)", 'T');
		mEdit_Copy = new JMenuItem("����(C)", 'C');
		mEdit_Paste = new JMenuItem("ճ��(P)", 'P');
		mEdit_Del = new JMenuItem("ɾ��(L)", 'L');
		mEdit_Search = new JMenuItem("����(F)", 'F');
		mEdit_SearchNext = new JMenuItem("������һ��(N)", 'N');
		mEdit_Replace = new JMenuItem("�滻(R)", 'R');
		mEdit_Turnto = new JMenuItem("ת��(G)", 'G');
		mEdit_SelectAll = new JMenuItem("ȫѡ(A)", 'A');
		mEdit_TimeDate = new JMenuItem("ʱ��/����(D)", 'D');
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
		mEdit_Undo.addActionListener(this); // ע���¼�����
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
		mEdit.add(mEdit_Undo); // ��Ӳ˵���
		mEdit.addSeparator(); // ��ӷָ���
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

		// --------------��ʽ�˵�
		formatMenu_LineWrap = new JCheckBoxMenuItem("�Զ�����(W)");
		formatMenu_LineWrap.setMnemonic('W');
		formatMenu_LineWrap.setState(true);
		formatMenu_Font = new JMenuItem("����(F)", 'F');
		formatMenu_Color = new JMenu("��ɫ");
		formatMenu_Color_FgColor = new JMenuItem("������ɫ");
		formatMenu_Color_BgColor = new JMenuItem("������ɫ");
		formatMenu_LineWrap.addActionListener(this); // ע���¼�����
		formatMenu_Font.addActionListener(this);
		formatMenu_Color_FgColor.addActionListener(this);
		formatMenu_Color_BgColor.addActionListener(this);
		mMode.add(formatMenu_LineWrap); // ��Ӳ˵���
		mMode.addSeparator();
		mMode.add(formatMenu_Font);
		mMode.add(formatMenu_Color);
		formatMenu_Color.add(formatMenu_Color_FgColor);
		formatMenu_Color.add(formatMenu_Color_BgColor);

		// --------------�鿴�˵�
		viewMenu_Status = new JCheckBoxMenuItem("״̬��(S)");
		viewMenu_Status.setMnemonic('S');
		viewMenu_Status.setState(true);
		viewMenu_Status.addActionListener(this);
		mView.add(viewMenu_Status);

		// --------------�����˵�
		mHelp_HelpTopics = new JMenuItem("����(H)", 'H');
		mHelp_About = new JMenuItem("����(A)", 'A');
		mHelp_HelpTopics.addActionListener(this);
		mHelp_About.addActionListener(this);
		mHelp.add(mHelp_HelpTopics);
		mHelp.addSeparator(); // ��ӷָ���
		mHelp.add(mHelp_About);

		// -------------------�����Ҽ������˵�
		popupMenu = new JPopupMenu();
		popupMenu_Undo = new JMenuItem("����(U)", 'U');
		popupMenu_Cut = new JMenuItem("����(T)", 'T');
		popupMenu_Copy = new JMenuItem("����(C)", 'C');
		popupMenu_Paste = new JMenuItem("ճ��(P)", 'P');
		popupMenu_Delete = new JMenuItem("ɾ��(D)", 'D');
		popupMenu_SelectAll = new JMenuItem("ȫѡ(A)", 'A');

		popupMenu_Undo.setEnabled(false); // ����ѡ���ʼ��Ϊ������

		// ---------------���Ҽ��˵���Ӳ˵���ͷָ���
		popupMenu.add(popupMenu_Undo);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_Cut);
		popupMenu.add(popupMenu_Copy);
		popupMenu.add(popupMenu_Paste);
		popupMenu.add(popupMenu_Delete);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_SelectAll);
		// --------------------�Ҽ��˵�ע���¼�
		popupMenu_Undo.addActionListener(this);
		popupMenu_Cut.addActionListener(this);
		popupMenu_Copy.addActionListener(this);
		popupMenu_Paste.addActionListener(this);
		popupMenu_Delete.addActionListener(this);
		popupMenu_SelectAll.addActionListener(this);
		// --------------------�ı��༭��ע���Ҽ��˵��¼�
		Text.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				checkForTriggerEvent(e);
			}

			public void mouseReleased(MouseEvent e) {
				checkForTriggerEvent(e);

			}

			private void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger())
					popupMenu.show(e.getComponent(), e.getX(), e.getY());// ����������ߵ�����ռ��е�λ��
																			// X��Y
																			// ��ʾ�����˵���
				else {
					statusLabel3.setText("��ǰ�����������: " + getlineNumber());
				}
				checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
				Text.requestFocus(); // �༭����ȡ����
			}
		});

		// ----------------------------����������
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
		// -----------------------------------ע�Ṥ������ť�¼�
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
		// ------------------------���ð�ť��ʾ����
		newButton.setToolTipText("�½�");
		openButton.setToolTipText("��");
		saveButton.setToolTipText("����");
		saveAsButton.setToolTipText("���Ϊ");
		printButton.setToolTipText("��ӡ");
		undoButton.setToolTipText("����");
		cutButton.setToolTipText("����");
		copyButton.setToolTipText("����");
		pasteButton.setToolTipText("ճ��");
		deleteButton.setToolTipText("ɾ����ѡ");
		searchButton.setToolTipText("�������滻");
		timeButton.setToolTipText("����ʱ��/����");
		fontButton.setToolTipText("��������");
		boldButton.setToolTipText("����");
		italicButton.setToolTipText("б��");
		fgcolorButton.setToolTipText("����������ɫ");
		bgcolorButton.setToolTipText("���ñ�����ɫ");
		helpButton.setToolTipText("����");
		// ���ó��������������С����ơ�ճ����ɾ���ȹ�������ť������ʱ��ͼƬ(��ɫ)
		undoButton.setDisabledIcon(new ImageIcon("Icons/undo1.gif"));
		cutButton.setDisabledIcon(new ImageIcon("Icons/cut1.gif"));
		copyButton.setDisabledIcon(new ImageIcon("Icons/copy1.gif"));
		pasteButton.setDisabledIcon(new ImageIcon("Icons/paste1.gif"));
		deleteButton.setDisabledIcon(new ImageIcon("Icons/delete1.gif"));
		// ------------------------�򹤾�����Ӱ�ť
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

		// --------------------------------------��������ӹ�����
		container.add(toolBar, BorderLayout.NORTH);
		// -----------------------------------���������״̬��

		statusBar = new JToolBar();

		statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		statusLabel1 = new JLabel("��F1��ȡ����                ");
		statusLabel2 = new JLabel("    ��ǰʱ�䣺" + hour + ":" + min + ":" + second);
		statusLabel3 = new JLabel("    ��ǰ�����������" + getlineNumber());
		statusBar.add(statusLabel1);
		statusBar.addSeparator();
		statusBar.add(statusLabel2);
		statusBar.addSeparator();
		statusBar.add(statusLabel3);
		container.add(statusBar, BorderLayout.SOUTH);
		statusBar.setVisible(true);
		// ------------------------------------�ı�������������Ĭ��ͼ��
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image image = tk.createImage("Icons/notepad.gif");
		this.setIconImage(image);
		this.setJMenuBar(MenuBar); // �򴰿���Ӳ˵���
		container.add(scroll, BorderLayout.CENTER); // ����������ı��༭��
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

			int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���档�Ƿ񱣴�?", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION);

			if (saveChoose == JOptionPane.YES_OPTION) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("ȷ��");
				fileChooser.setDialogTitle("���Ϊ");

				int result = fileChooser.showSaveDialog(this);

				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel1.setText("��û��ѡ���κ��ļ�");
					return;
				}

				saveFileName = fileChooser.getSelectedFile();

				if (saveFileName == null || saveFileName.getName().equals(""))
					JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
				else {
					saveFile();
					Text.setText("");
					this.setTitle("�½��ı�");
					statusLabel1.setText("���½��ı�");
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

		// ճ�����ܿ������ж�
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
			pos = s.indexOf('\12', pos); // ���� \n ���ڵ�λ��
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
		final JDialog findDialog = new JDialog(this, "�������滻", true);
		Container con = findDialog.getContentPane();
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel searchContentLabel = new JLabel("��������(N) :");
		JLabel replaceContentLabel = new JLabel("�滻Ϊ(P)�� :");
		final JTextField findText = new JTextField(22);
		final JTextField replaceText = new JTextField(22);
		final JCheckBox matchcase = new JCheckBox("���ִ�Сд");
		ButtonGroup bGroup = new ButtonGroup();
		final JRadioButton up = new JRadioButton("����(U)");
		final JRadioButton down = new JRadioButton("����(D)");
		down.setSelected(true);
		bGroup.add(up);
		bGroup.add(down);
		JButton searchNext = new JButton("������һ��(F)");
		JButton replace = new JButton("�滻(R)");
		final JButton replaceAll = new JButton("ȫ���滻(A)");
		searchNext.setPreferredSize(new Dimension(110, 22));
		replace.setPreferredSize(new Dimension(110, 22));
		replaceAll.setPreferredSize(new Dimension(110, 22));
		// "�滻"��ť���¼�����
		replace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (replaceText.getText().length() == 0 && Text.getSelectedText() != null)
					Text.replaceSelection("");
				if (replaceText.getText().length() > 0 && Text.getSelectedText() != null)
					Text.replaceSelection(replaceText.getText());
			}
		});

		// "�滻ȫ��"��ť���¼�����
		replaceAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Text.setCaretPosition(0); // �����ŵ��༭����ͷ
				int a = 0, b = 0, replaceCount = 0;

				if (findText.getText().length() == 0) {
					JOptionPane.showMessageDialog(findDialog, "����д��������!", "��ʾ", JOptionPane.WARNING_MESSAGE);
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
							JOptionPane.showMessageDialog(findDialog, "�Ҳ��������ҵ�����!", "���±�", JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(findDialog, "�ɹ��滻" + replaceCount + "��ƥ������!", "�滻�ɹ�", JOptionPane.INFORMATION_MESSAGE);
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
		}); /* "�滻ȫ��"��ť���¼�������� */

		// "������һ��"��ť�¼�����
		searchNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int a = 0, b = 0;
				int FindStartPos = Text.getCaretPosition();
				String str1, str2, str3, str4, strA, strB;
				str1 = Text.getText();
				str2 = str1.toLowerCase();
				str3 = findText.getText();
				str4 = str3.toLowerCase();
				// "���ִ�Сд"��CheckBox��ѡ��
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
					JOptionPane.showMessageDialog(null, "�Ҳ��������ҵ�����!", "���±�", JOptionPane.INFORMATION_MESSAGE);
				}

			}
		});/* "������һ��"��ť�¼�������� */
		// "ȡ��"��ť���¼�����
		JButton cancel = new JButton("ȡ��");
		cancel.setPreferredSize(new Dimension(110, 22));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findDialog.dispose();
			}
		});

		// ����"�������滻"�Ի���Ľ���
		JPanel bottomPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel topPanel = new JPanel();

		JPanel direction = new JPanel();
		direction.setBorder(BorderFactory.createTitledBorder("���� "));
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

		// ����"�������滻"�Ի���Ĵ�С���ɸ��Ĵ�С(��)��λ�úͿɼ���
		findDialog.setSize(410, 210);
		findDialog.setResizable(false);
		findDialog.setLocation(230, 280);
		findDialog.setVisible(true);
	}

	/* ����mySearch()���� */

	// ʵ��ActionListener���¼�������public void actionPerformed(ActionEvent e)
	public void actionPerformed(ActionEvent e) {
		// �½�
		if (e.getActionCommand().equals("�½�(N)") || e.getSource() == newButton)
		// ������� Event �Ķ���
		// if(e.getSource()==mFile_New||e.getSource()==newButton)
		{
			Text.requestFocus();
			String currentValue = Text.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;

			if (isTextChange) {

				int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���档�Ƿ񱣴�?", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION);

				if (saveChoose == JOptionPane.YES_OPTION) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("ȷ��");
					fileChooser.setDialogTitle("���Ϊ");
					int result = fileChooser.showSaveDialog(this);
					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel1.setText("��û��ѡ���κ��ļ�");
						return;
					}
					saveFileName = fileChooser.getSelectedFile();
					if (saveFileName == null || saveFileName.getName().equals(""))
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
					else {
						saveFile();
						Text.setText("");
						this.setTitle("�½��ı�");
						statusLabel1.setText("���½��ı�");
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					Text.replaceRange("", 0, Text.getText().length());
					statusLabel1.setText("���½��ļ�");
					this.setTitle("�ޱ��� - ���±�");
					isNewFile = true;
					undo.discardAllEdits(); // �������е�"����"����
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
				statusLabel1.setText("���½��ļ�");
				this.setTitle("�ޱ��� - ���±�");
				isNewFile = true;
				undo.discardAllEdits();
				mEdit_Undo.setEnabled(false);
				popupMenu_Undo.setEnabled(false);
				undoButton.setEnabled(false);
				oldValue = Text.getText();
			}
		}// �½��������

		// ��
		else if (e.getActionCommand().equals("��(O)") || e.getSource() == openButton) {
			Text.requestFocus();
			String currentValue = Text.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;

			if (isTextChange) {

				int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���档�Ƿ񱣴�?", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION);

				if (saveChoose == JOptionPane.YES_OPTION) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("ȷ��");
					fileChooser.setDialogTitle("���Ϊ");

					int result = fileChooser.showSaveDialog(this);

					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel1.setText("��û��ѡ���κ��ļ�");
						return;
					}

					saveFileName = fileChooser.getSelectedFile();

					if (saveFileName == null || saveFileName.getName().equals(""))
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
					else {
						saveFile();
						isNewFile = false;
						currentFile = saveFileName;
						oldValue = Text.getText();
						this.setTitle(saveFileName.getName() + "  - ���±�");
						statusLabel1.setText("����ǰ���ļ�:" + saveFileName.getAbsoluteFile());
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					String str = null;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("ȷ��");
					fileChooser.setDialogTitle("���ļ�");
					int result = fileChooser.showOpenDialog(this);
					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel1.setText("��û��ѡ���κ��ļ�");
						return;
					}
					fileName = fileChooser.getSelectedFile();
					if (fileName == null || fileName.getName().equals(""))
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
					else {
						try {
							FileReader fr = new FileReader(fileName);
							BufferedReader bfr = new BufferedReader(fr);
							Text.setText("");
							while ((str = bfr.readLine()) != null) {// ÿ�ζ�ȡһ�У�ֱ���ļ�����
								Text.append(str + "\15\12");
							}// endwhile
							this.setTitle(fileName.getName() + "  - ���±�");
							statusLabel1.setText("����ǰ���ļ�:" + fileName.getAbsoluteFile());
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
				fileChooser.setApproveButtonText("ȷ��");
				fileChooser.setDialogTitle("���ļ�");
				int result = fileChooser.showOpenDialog(this);
				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel1.setText("����û��ѡ���κ��ļ�");
					return;
				}
				fileName = fileChooser.getSelectedFile();
				if (fileName == null || fileName.getName().equals(""))
					JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
				else {
					try {
						FileReader fr = new FileReader(fileName);
						BufferedReader bfr = new BufferedReader(fr);
						Text.setText("");
						while ((str = bfr.readLine()) != null) {// ÿ�ζ�ȡһ�У�ֱ���ļ�����
							Text.append(str + "\15\12");
						}// endwhile

						this.setTitle(fileName.getName() + "  - ���±�");
						statusLabel1.setText("����ǰ���ļ�:" + fileName.getAbsoluteFile());
						fr.close();
						isNewFile = false;
						currentFile = fileName;
						oldValue = Text.getText();
					} catch (IOException ioException) {
					}
				}

			}
		}// "��"�������

		// ����
		else if (e.getSource() == mFile_Save || e.getSource() == saveButton) {
			Text.requestFocus();
			if (isNewFile) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setApproveButtonText("ȷ��");
				fileChooser.setDialogTitle("���Ϊ");
				int result = fileChooser.showSaveDialog(this);
				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel1.setText("����û��ѡ���κ��ļ�");
					return;
				}
				saveFileName = fileChooser.getSelectedFile();
				if (saveFileName == null || saveFileName.getName().equals(""))
					JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
				else {
					saveFile();
					isNewFile = false;
					currentFile = saveFileName;
					oldValue = Text.getText();
					this.setTitle(saveFileName.getName() + "  - ���±�");
					statusLabel1.setText("����ǰ���ļ�:" + saveFileName.getAbsoluteFile());
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
		}// "����"�������

		// ���Ϊ
		else if (e.getSource() == mFile_ASave || e.getSource() == saveAsButton) {
			Text.requestFocus();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setApproveButtonText("ȷ��");
			fileChooser.setDialogTitle("���Ϊ");
			int result = fileChooser.showSaveDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				statusLabel1.setText("����û��ѡ���κ��ļ�");
				return;
			}
			saveFileName = fileChooser.getSelectedFile();
			if (saveFileName == null || saveFileName.getName().equals(""))
				JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
			else {
				saveFile();
				isNewFile = false;
				currentFile = saveFileName;
				oldValue = Text.getText();
				this.setTitle(saveFileName.getName() + "  - ���±�");
				statusLabel1.setText("����ǰ���ļ�:" + saveFileName.getAbsoluteFile());
			}

		}// "���Ϊ"�������

		// ��ӡ
		else if (e.getSource() == mFile_Print || e.getSource() == printButton) {
			Text.requestFocus();
			JOptionPane.showMessageDialog(this, "�˹�����δ���!", "��ʾ", JOptionPane.WARNING_MESSAGE);
		}

		// �˳�
		else if (e.getSource() == mFile_Exit) {
			int exitChoose = JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�˳�ô��", "�˳���ʾ", JOptionPane.OK_CANCEL_OPTION);
			if (exitChoose == JOptionPane.OK_OPTION) {
				checkText();
			} else {
				return;
			}
		}

		// ����
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

		// ����
		else if (e.getSource() == mEdit_Cut || e.getSource() == popupMenu_Cut || e.getSource() == cutButton) {
			Text.requestFocus();
			String text = Text.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			Text.replaceRange("", Text.getSelectionStart(), Text.getSelectionEnd());
			checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
		}

		// ����
		else if (e.getSource() == mEdit_Copy || e.getSource() == popupMenu_Copy || e.getSource() == copyButton) {
			Text.requestFocus();
			String text = Text.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
		}

		// ճ��
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
			checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
		}

		// ɾ��
		else if (e.getSource() == mEdit_Del || e.getSource() == popupMenu_Delete || e.getSource() == deleteButton) {
			Text.requestFocus();
			Text.replaceRange("", Text.getSelectionStart(), Text.getSelectionEnd());
			checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
		}

		// ����
		else if (e.getSource() == mEdit_Search || e.getSource() == searchButton) {
			Text.requestFocus();
			if (e.getSource() == searchButton) {
				Text.requestFocus();
				Text.setCaretPosition(0);
			}
			mySearch();
		}

		// ������һ��(�˹�����δ�ܺܺ�ʵ�֣����Ծ����ò��ҹ���������)
		else if (e.getSource() == mEdit_SearchNext) {
			mySearch();
		}

		// �滻(����ҹ��ܼ�����һ����)
		else if (e.getSource() == mEdit_Replace) {
			mySearch();
		}

		// ת��
		else if (e.getSource() == mEdit_Turnto) {
			final JDialog gotoDialog = new JDialog(this, "ת��������");
			JLabel gotoLabel = new JLabel("����(L):");
			final JTextField linenum = new JTextField(5);
			linenum.setText("1");
			linenum.selectAll();

			JButton okButton = new JButton("ȷ��");
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int totalLine = Text.getLineCount();
					int[] lineNumber = new int[totalLine + 1];
					String s = Text.getText();
					int pos = 0, t = 0;

					while (true) {
						pos = s.indexOf('\12', pos);
						// System.out.println("����pos:"+pos);
						if (pos == -1)
							break;
						lineNumber[t++] = pos++;
					}

					int gt = 1;
					try {
						gt = Integer.parseInt(linenum.getText());
					} catch (NumberFormatException efe) {
						JOptionPane.showMessageDialog(null, "����������!", "��ʾ", JOptionPane.WARNING_MESSAGE);
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

			JButton cancelButton = new JButton("ȡ��");
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

		}// "ת��"�������

		// ����ʱ������
		else if (e.getSource() == mEdit_TimeDate || e.getSource() == timeButton) {
			Text.requestFocus();
			SimpleDateFormat currentDateTime = new SimpleDateFormat("HH:mm yyyy-MM-dd");
			Text.insert(currentDateTime.format(new Date()), Text.getCaretPosition());
		}

		// ȫѡ
		else if (e.getSource() == popupMenu_SelectAll || e.getSource() == mEdit_SelectAll) {
			Text.selectAll();
		}

		// �Զ�����
		else if (e.getSource() == formatMenu_LineWrap) {
			if (formatMenu_LineWrap.getState()) {
				Text.setLineWrap(true);
			} else
				Text.setLineWrap(false);
		}

		// ��������
		else if (e.getSource() == formatMenu_Font || e.getSource() == fontButton) {
			Text.requestFocus();
			new MyFont();
		}

		// ����������ɫ(ǰ��ɫ)
		else if (e.getSource() == formatMenu_Color_FgColor || e.getSource() == fgcolorButton) {
			Text.requestFocus();
			Color color = JColorChooser.showDialog(this, "����������ɫ", Color.black);
			if (color != null) {
				Text.setForeground(color);
			} else
				return;
		}

		// ���ñ༭��������ɫ
		else if (e.getSource() == formatMenu_Color_BgColor || e.getSource() == bgcolorButton) {
			Text.requestFocus();
			Color color = JColorChooser.showDialog(this, "���ı�����ɫ", Color.white);
			if (color != null) {
				Text.setBackground(color);
			} else
				return;
		}

		// ����״̬���ɼ���
		else if (e.getSource() == viewMenu_Status) {
			if (viewMenu_Status.getState())
				statusBar.setVisible(true);

			else
				statusBar.setVisible(false);

		}

		// ��������
		else if (e.getSource() == mHelp_HelpTopics || e.getSource() == helpButton) {
			JOptionPane.showMessageDialog(this, "���±�֧�������ı���ȡ\n" + "���ڶԱ��벻��Ϥ������\n��ʱδ���б���ת��\n", "��������", JOptionPane.INFORMATION_MESSAGE);
		}

		// ����
		else if (e.getSource() == mHelp_About) {
			JOptionPane.showMessageDialog(this, "       VXBB�ļ��±�\n" + "       QQ��491697374\n" + "     JAVAͼ�ν�����ϰ\n", "���ڼ��±�", JOptionPane.INFORMATION_MESSAGE);
		}

		// ������"����"��ť�¼�����
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

		// ������"б��"��ť�¼�����
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

	}/* ����actionPerformed()���� */

	class Clock extends Thread { // ģ��ʱ��
		public void run() {
			while (true) {
				GregorianCalendar time = new GregorianCalendar();
				int hour = time.get(Calendar.HOUR_OF_DAY);
				int min = time.get(Calendar.MINUTE);
				int second = time.get(Calendar.SECOND);
				statusLabel2.setText("    ��ǰʱ�䣺" + hour + ":" + min + ":" + second);
				try {
					Thread.sleep(950);
				} catch (InterruptedException exception) {
				}

			}
		}
	}

	// ���������������MyFont
	class MyFont implements ActionListener {
		final JDialog fontDialog;
		final JTextField tfFont, tfSize, tfStyle;
		final int fontStyleConst[] = { Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD + Font.ITALIC };
		final JList listStyle, listFont, listSize;
		JLabel sample;
		JPanel pane1, pane2, pane3, pane4;

		// ���캯��MyFont
		public MyFont() {

			fontDialog = new JDialog(Notepad4.this, "��������", true);
			Container con = fontDialog.getContentPane();
			con.setLayout(new BoxLayout(con, BoxLayout.Y_AXIS));
			pane1 = new JPanel();
			pane2 = new JPanel();
			pane3 = new JPanel();
			pane4 = new JPanel();
			Font currentFont = Text.getFont();

			JLabel lblFont = new JLabel("����(F):");
			JLabel lblStyle = new JLabel("����(Y):");
			JLabel lblSize = new JLabel("��С(S):");

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
				tfStyle.setText("����");
			else if (currentFont.getStyle() == Font.BOLD)
				tfStyle.setText("����");
			else if (currentFont.getStyle() == Font.ITALIC)
				tfStyle.setText("б��");
			else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC))
				tfStyle.setText("��б��");

			tfFont.selectAll();
			tfStyle.setPreferredSize(new Dimension(200, 20));
			tfSize = new JTextField(7);
			tfSize.setText(currentFont.getSize() + "");
			tfSize.selectAll();
			tfSize.setPreferredSize(new Dimension(200, 20));

			final String fontStyle[] = { "����", "����", "б��", "��б��" };
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
			fontOkButton = new JButton("ȷ��");
			fontOkButton.addActionListener(this);
			JButton cancelButton = new JButton("ȡ��");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fontDialog.dispose();
				}
			});

			sample = new JLabel(" ���±� ");
			sample.setHorizontalAlignment(SwingConstants.CENTER);
			sample.setPreferredSize(new Dimension(150, 31));

			JPanel samplePanel = new JPanel();
			samplePanel.setBorder(BorderFactory.createTitledBorder("ʾ��"));
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
		}// ���캯������

		// ����ʾ����ʾ������ͷ���С��
		public void updateSample() {
			Font sampleFont = new Font(tfFont.getText(), fontStyleConst[listStyle.getSelectedIndex()], Integer.parseInt(tfSize.getText()));
			sample.setFont(sampleFont);
		}// End method updateSample

		// �����ı��༭��������
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

	// ʵ���˽ӿ�UndoableListener����UndoHandler
	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent uee) {
			undo.addEdit(uee.getEdit());
		}
	}

	public static void main(String s[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Text = new JTextArea();
		Text.setDragEnabled(true); // ֧���Զ��Ϸ�
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
