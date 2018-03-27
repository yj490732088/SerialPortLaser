package com.zeng.serialport.ui;

import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;//GUi之前要吧这两个都引进来

import com.zeng.serialport.exception.NoSuchPort;
import com.zeng.serialport.exception.NotASerialPort;
import com.zeng.serialport.exception.PortInUse;
import com.zeng.serialport.exception.SendDataToSerialPortFailure;
import com.zeng.serialport.exception.SerialPortOutputStreamCloseFailure;
import com.zeng.serialport.exception.SerialPortParameterFailure;
import com.zeng.serialport.exception.TooManyListeners;
import com.zeng.serialport.manage.SerialPortManager;
import com.zeng.serialport.utils.ByteUtils;
import com.zeng.serialport.utils.ShowUtils;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
/**
 * 主界面
 * 
 * @author zeng
 */
public class MainFrame extends JFrame implements ActionListener {
	
	private byte serialPortBackBytes[] = null;
	
	/* ******** 指令 ******** */
	private final static String ORDER_B1 = "0x24";			// $
	private final static String ORDER_B2_SEND = "0x04";		// 发射
	private final static String ORDER_B2_FUNC = "0x05";		// 功能切换
	private final static String ORDER_B2_INC = "0x06";		// 按键 +
	private final static String ORDER_B2_DEC = "0x07";		// 按键 -
	private final static String ORDER_B2_RECOVERY = "0x08";	// 恢复出厂化
	private final static String ORDER_B2_QUERY = "0x09";	// 查询
	private final static String ORDER_B2_TEST = "0x0a";		// 测试
	private final static String ORDER_B3_DOWN = "0x01";		// 01-按下
	private final static String ORDER_B3_UP = "0x02";		// 02-松开
	private final static String ORDER_B4_SEND = "0x40";		// 发射
	private final static String ORDER_B4_FUNC = "0x41";		// 功能切换
	private final static String ORDER_B4_INC = "0x42";		// 按键 + 
	private final static String ORDER_B4_DEC = "0x43";		// 按键 -
	private final static String ORDER_B4_RECOVERY = "0x44";	// 恢复出厂化
	private final static String ORDER_B4_QUERY = "0x45";	// 查询
	private final static String ORDER_B4_TEST = "0x46";		// 测试
	private final static String ORDER_B5 = "0x23";			// #
	// 按键指令
	private final static String ORDER_SEND_DOWN[] = {ORDER_B1, ORDER_B2_SEND, ORDER_B3_DOWN, ORDER_B4_SEND, ORDER_B5};				// 发射 按下
	private final static String ORDER_SEND_UP[] = {ORDER_B1, ORDER_B2_SEND, ORDER_B3_UP, ORDER_B4_SEND, ORDER_B5};					// 发射 松开
	private final static String ORDER_FUNC_DOWN[] = {ORDER_B1, ORDER_B2_FUNC, ORDER_B3_DOWN, ORDER_B4_FUNC, ORDER_B5};				// 功能切换 按下
	private final static String ORDER_FUNC_UP[] = {ORDER_B1, ORDER_B2_FUNC, ORDER_B3_UP, ORDER_B4_FUNC, ORDER_B5};					// 功能切换 松开
	private final static String ORDER_INC_DOWN[] = {ORDER_B1, ORDER_B2_INC, ORDER_B3_DOWN, ORDER_B4_INC, ORDER_B5};					// 按键 + 按下
	private final static String ORDER_INC_UP[] = {ORDER_B1, ORDER_B2_INC, ORDER_B3_UP, ORDER_B4_INC, ORDER_B5};						// 按键 + 松开
	private final static String ORDER_DEC_DOWN[] = {ORDER_B1, ORDER_B2_DEC, ORDER_B3_DOWN, ORDER_B4_DEC, ORDER_B5};					// 按键 - 按下
	private final static String ORDER_DEC_UP[] = {ORDER_B1, ORDER_B2_DEC, ORDER_B3_UP, ORDER_B4_DEC, ORDER_B5};						// 按键 - 松开
	private final static String ORDER_RECOVERY_DOWN[] = {ORDER_B1, ORDER_B2_RECOVERY, ORDER_B3_DOWN, ORDER_B4_RECOVERY, ORDER_B5};	// 恢复出厂化 按下
	private final static String ORDER_RECOVERY_UP[] = {ORDER_B1, ORDER_B2_RECOVERY, ORDER_B3_UP, ORDER_B4_RECOVERY, ORDER_B5};		// 恢复出厂化 松开
	private final static String ORDER_QUERY_DOWN[] = {ORDER_B1, ORDER_B2_QUERY, ORDER_B3_DOWN, ORDER_B4_QUERY, ORDER_B5};			// 查询 按下
	private final static String ORDER_QUERY_UP[] = {ORDER_B1, ORDER_B2_QUERY, ORDER_B3_UP, ORDER_B4_QUERY, ORDER_B5};				// 查询 松开
	private final static String ORDER_TEST_DOWN[] = {ORDER_B1, ORDER_B2_TEST, ORDER_B3_DOWN, ORDER_B4_TEST, ORDER_B5};				// 测试 按下
	private final static String ORDER_TEST_UP[] = {ORDER_B1, ORDER_B2_TEST, ORDER_B3_UP, ORDER_B4_TEST, ORDER_B5};					// 测试 松开
	/* ******** 控件 ******** */
	// 控件尺寸
	final static int FRAME_L_D_R_PADDING = 3;	// 窗口左、下、右border
	final static int FRAME_U_PADDING = 25;		// 窗口上border
	final static int PORTRAIT_MARGIN =20;	// 控件横向margin
	final static int LANDSCAPE_MARGIN = 10;	// 控件纵向margin
	final static int BTN_WIDTH = 90;	// Button
	final static int BTN_HEIGHT = 30;
	final static int LABEL_WIDTH = 40;	//Label
	final static int LABEL_HEIGHT = 20;
	final static int COMBO_BOX_WIDTH = 80;	//ComboBox
	final static int COMBO_BOX_HEIGHT = 20;	
	final static int CONTROL_BROAD_INFO_TEXT_AREA_WIDTH = 350;	// 控制板信息 TextArea
	final static int CONTROL_BROAD_INFO_TEXT_AREA_HEIGHT = 40;
	final static int SERIAL_PORT_SETTING_PANEL_WIDTH = 500;		// 串口设置 Panel
	final static int SERIAL_PORT_SETTING_PANEL_HEIGHT = 60;
	final static int CONTROL_BROAD_INFO_PANEL_WIDTH = SERIAL_PORT_SETTING_PANEL_WIDTH;	// 控制板信息 Panel
	final static int CONTROL_BROAD_INFO_PANEL_HEIGHT = 80;
	final static int OPERA_BTN_PANEL_WIDTH = SERIAL_PORT_SETTING_PANEL_WIDTH;	// 操作区域 Panel
	final static int OPERA_BTN_PANEL_HEIGHT = 300;
	final static int LOG_PANEL_WIDTH = 300;		// Log Panel
	final static int LOG_PANEL_HEIGHT = SERIAL_PORT_SETTING_PANEL_HEIGHT + CONTROL_BROAD_INFO_PANEL_HEIGHT + OPERA_BTN_PANEL_HEIGHT + PORTRAIT_MARGIN*2;
	final static int CONFIGURE_PANEL_WIDTH = SERIAL_PORT_SETTING_PANEL_WIDTH + LOG_PANEL_WIDTH + LANDSCAPE_MARGIN*3;	// Configure Panel
	final static int CONFIGURE_PANEL_HEIGHT = SERIAL_PORT_SETTING_PANEL_HEIGHT + CONTROL_BROAD_INFO_PANEL_HEIGHT + OPERA_BTN_PANEL_HEIGHT + PORTRAIT_MARGIN*4 - 10;	
	final static int OK_HELP_CLOSE_PANEL_WIDTH = CONFIGURE_PANEL_WIDTH;	// 确定、Help、关闭 Panel
	final static int OK_HELP_CLOSE_PANEL_HEIGHT = 60;
	final static int FRAME_WIDTH =SERIAL_PORT_SETTING_PANEL_WIDTH + LOG_PANEL_WIDTH + FRAME_L_D_R_PADDING*2 + LANDSCAPE_MARGIN*5;	// Frame
	final static int FRAME_HEIGHT = CONFIGURE_PANEL_HEIGHT + OK_HELP_CLOSE_PANEL_HEIGHT + PORTRAIT_MARGIN*3 + FRAME_U_PADDING + FRAME_L_D_R_PADDING + 10;
	// 按钮
	private JButton SerialPorSwitchtBtn; // 串口开关
	private JButton clearLogBtn; // 清除log
	private JButton sendBtn; // 发射
	private JButton funcBtn; // 功能切换
	private JButton incBtn; // 按键 +
	private JButton decBtn; // 按键 -
	private JButton recoveryBtn; // 恢复出厂化
	private JButton queryBtn; // 查询
	private JButton testBtn; // 测试
	private JButton okBtn; // 确定
	private JButton helpBtn; // Help
	private JButton closeWindowBtn; // 关闭
	// 文本域
	private JScrollPane logScrollPane;
	private JTextArea logTextArea; // 日志打印区域
	private JScrollPane controlBroadInfoScrollPane;
	private JTextArea controlBroadInfoTextArea; // 控制板信息文本域
	// 面板
	private JPanel configurePanel; // Configure区域
	private JPanel serialPortPanel; // COM区域
	private JPanel controlBroadInfoPanel; // 控制板信息区域
	private JPanel operaBtnPanel; // 操作按钮区域
	private JPanel logPanel; // log区域
	private JPanel okHelpClosePanel; // log区域
	// Label
	private JLabel commLabel;	// 串口 label
	private JLabel baudrateLabel;	// 波特率 label
	// ComboBox
	private JComboBox<String> commChoice;
	private JComboBox<String> baudrateChoice;
	/* ******** 串口 ******** */
	private List<String> commList = null;	// 串口列表
	private SerialPort serialPort;	// 串口

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame();
			}
		});
	}

	public MainFrame() {
		init();
	}

	private void init() {		
		initObjs(); // 初始化对象
		initComponents();	// 初始化组件
		initData();	//初始化数据
		actionListener(); // add action listener
	}

	/**
	 * Init objects
	 */
	private void initObjs() {
		// 按钮
		SerialPorSwitchtBtn = new JButton("Open"); // 串口开关
		clearLogBtn = new JButton("Clear"); // 清除log
		sendBtn = new JButton("发射"); // 发射
		funcBtn = new JButton("功能键"); // 功能切换
		incBtn = new JButton("+"); // 按键 +
		decBtn = new JButton("-"); // 按键 -
		recoveryBtn = new JButton("Recovery"); // 恢复出厂化
		queryBtn = new JButton("查询"); // 查询
		testBtn = new JButton("测试"); // 测试
		okBtn = new JButton("确定"); // 确定
		helpBtn = new JButton("Help"); // Help
		closeWindowBtn = new JButton("关闭"); // 关闭
		// 文本域
		logTextArea = new JTextArea(); // 日志打印区域
		logTextArea.setEditable(false); // 不可编辑
		logTextArea.setLineWrap(true); // 自动换行
		logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setBackground(Color.WHITE);
		controlBroadInfoTextArea = new JTextArea(); // 控制板信息文本域
		controlBroadInfoTextArea.setEditable(false); // 不可编辑
		controlBroadInfoTextArea.setLineWrap(true); // 自动换行
		controlBroadInfoScrollPane = new JScrollPane(controlBroadInfoTextArea);
		controlBroadInfoScrollPane.setBackground(Color.WHITE);
		// 面板
		configurePanel = new JPanel(); // Configure面板
		serialPortPanel = new JPanel(); // COM面板
		controlBroadInfoPanel = new JPanel(); // 控制板信息面板
		operaBtnPanel = new JPanel(); // 操作按钮面板
		logPanel = new JPanel(); // log面板
		okHelpClosePanel = new JPanel(); // 确定、Help、关闭 面板
		// Label
		commLabel = new JLabel("串口");
		baudrateLabel = new JLabel("波特率");
		// ComboBox
		commChoice = new JComboBox<String>();
		baudrateChoice = new JComboBox<String>();
	}

	/**
	 * Init components
	 */
	private void initComponents() {
		placeComponentsToThis(); // this 放置组件
		placeComponentsToConfigurePanel(); // configurePanel面板 放置组件
		placeComponentsToSerialPortSettingPanel(); // COM面板
		placeComponentsToControlBroadPanel(); // 控制板信息面板 放置组件
		placeComponentsToOperaPanel(); // 操作按钮面板 放置组件
		placeComponentsToLogPanel(); // log面板 放置组件
		placeComponentsToOkHelpClosePanel(); // 确定、Help、关闭 面板 放置组件
	}
	
	/**
	 * Init data
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		commList = SerialPortManager.findPort();
		// 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : commList) {
				commChoice.addItem(s);
			}
		}

		baudrateChoice.addItem("9600");
		baudrateChoice.addItem("19200");
		baudrateChoice.addItem("38400");
		baudrateChoice.addItem("57600");
		baudrateChoice.addItem("115200");
	}

	/**
	 * Place components to 串口设置 面板
	 */
	private void placeComponentsToSerialPortSettingPanel() {
		int btn_landscape_margin = (SERIAL_PORT_SETTING_PANEL_WIDTH - (LANDSCAPE_MARGIN*2 + LABEL_WIDTH + COMBO_BOX_WIDTH)*2 - BTN_WIDTH)/2;
			
		serialPortPanel.setLayout(null);

		commLabel.setForeground(Color.gray);
		commChoice.setFocusable(false);
		baudrateLabel.setForeground(Color.gray);
		baudrateChoice.setFocusable(false);
		
		commLabel.setBounds(LANDSCAPE_MARGIN, (SERIAL_PORT_SETTING_PANEL_HEIGHT-LABEL_HEIGHT)/2+5, LABEL_WIDTH, LABEL_HEIGHT);
		commChoice.setBounds(LANDSCAPE_MARGIN*2+LABEL_WIDTH, (SERIAL_PORT_SETTING_PANEL_HEIGHT-COMBO_BOX_HEIGHT)/2+5, COMBO_BOX_WIDTH, COMBO_BOX_HEIGHT);
		baudrateLabel.setBounds(LANDSCAPE_MARGIN*3+LABEL_WIDTH+COMBO_BOX_WIDTH, (SERIAL_PORT_SETTING_PANEL_HEIGHT-LABEL_HEIGHT)/2+5, LABEL_WIDTH, LABEL_HEIGHT);
		baudrateChoice.setBounds(LANDSCAPE_MARGIN*4+LABEL_WIDTH*2+COMBO_BOX_WIDTH, (SERIAL_PORT_SETTING_PANEL_HEIGHT-COMBO_BOX_HEIGHT)/2+5, COMBO_BOX_WIDTH, COMBO_BOX_HEIGHT);
		SerialPorSwitchtBtn.setBounds(SERIAL_PORT_SETTING_PANEL_WIDTH-btn_landscape_margin-BTN_WIDTH, (SERIAL_PORT_SETTING_PANEL_HEIGHT-BTN_HEIGHT)/2+3, BTN_WIDTH, BTN_HEIGHT);
		
		serialPortPanel.add(commLabel);
		serialPortPanel.add(commChoice);
		serialPortPanel.add(baudrateLabel);
		serialPortPanel.add(baudrateChoice);
		serialPortPanel.add(SerialPorSwitchtBtn, BorderLayout.NORTH);	
	}

	/**
	 * Place components to 控制板信息 面板
	 */
	private void placeComponentsToControlBroadPanel() {	
		controlBroadInfoPanel.setLayout(null);

		controlBroadInfoScrollPane.setBounds(LANDSCAPE_MARGIN, (CONTROL_BROAD_INFO_PANEL_HEIGHT-CONTROL_BROAD_INFO_TEXT_AREA_HEIGHT)/2+5, CONTROL_BROAD_INFO_TEXT_AREA_WIDTH, CONTROL_BROAD_INFO_TEXT_AREA_HEIGHT);
		queryBtn.setBounds((CONTROL_BROAD_INFO_PANEL_WIDTH+LANDSCAPE_MARGIN+CONTROL_BROAD_INFO_TEXT_AREA_WIDTH-BTN_WIDTH)/2, (CONTROL_BROAD_INFO_PANEL_HEIGHT-BTN_HEIGHT)/2+5, BTN_WIDTH, BTN_HEIGHT);

		controlBroadInfoPanel.add(controlBroadInfoScrollPane, BorderLayout.NORTH);
		controlBroadInfoPanel.add(queryBtn, BorderLayout.NORTH);		
	}

	/**
	 * Place components to 操作按钮 面板
	 */
	private void placeComponentsToOperaPanel() {
		int landscape_margin = (OPERA_BTN_PANEL_WIDTH - BTN_WIDTH * 3) / 4;
		int portrait_margin = (OPERA_BTN_PANEL_HEIGHT - BTN_HEIGHT * 2) / 3;

		operaBtnPanel.setLayout(null); 

		funcBtn.setBounds(landscape_margin, portrait_margin, BTN_WIDTH, BTN_HEIGHT);
		sendBtn.setBounds(landscape_margin, portrait_margin*2 + BTN_HEIGHT, BTN_WIDTH, BTN_HEIGHT);
		decBtn.setBounds(landscape_margin*2+BTN_WIDTH, portrait_margin, BTN_WIDTH, BTN_HEIGHT);
		incBtn.setBounds(landscape_margin*2+BTN_WIDTH, portrait_margin*2 + BTN_HEIGHT, BTN_WIDTH, BTN_HEIGHT);
		testBtn.setBounds(landscape_margin*3+BTN_WIDTH*2, portrait_margin, BTN_WIDTH, BTN_HEIGHT);
		recoveryBtn.setBounds(landscape_margin*3+BTN_WIDTH*2, portrait_margin*2 + BTN_HEIGHT, BTN_WIDTH, BTN_HEIGHT);

		operaBtnPanel.add(funcBtn, BorderLayout.NORTH);
		operaBtnPanel.add(sendBtn, BorderLayout.NORTH);
		operaBtnPanel.add(decBtn, BorderLayout.NORTH);
		operaBtnPanel.add(incBtn, BorderLayout.NORTH);
		operaBtnPanel.add(testBtn, BorderLayout.NORTH);
		operaBtnPanel.add(recoveryBtn, BorderLayout.NORTH);
	}

	/**
	 * Place components to log 面板
	 */
	private void placeComponentsToLogPanel() {
		logPanel.setLayout(null);

		clearLogBtn.setBounds((LOG_PANEL_WIDTH - BTN_WIDTH) / 2, PORTRAIT_MARGIN, BTN_WIDTH, BTN_HEIGHT);
		logScrollPane.setBounds(LANDSCAPE_MARGIN, PORTRAIT_MARGIN + BTN_HEIGHT + PORTRAIT_MARGIN/2, 
				LOG_PANEL_WIDTH - PORTRAIT_MARGIN, LOG_PANEL_HEIGHT  - BTN_HEIGHT -PORTRAIT_MARGIN*2);

		logPanel.add(clearLogBtn, BorderLayout.NORTH);
		logPanel.add(logScrollPane, BorderLayout.NORTH);
	}

	/**
	 * Place components to Configure 面板
	 */
	private void placeComponentsToConfigurePanel() {
		configurePanel.setLayout(null);
		
		serialPortPanel.setBounds(LANDSCAPE_MARGIN, PORTRAIT_MARGIN, SERIAL_PORT_SETTING_PANEL_WIDTH, SERIAL_PORT_SETTING_PANEL_HEIGHT);
		controlBroadInfoPanel.setBounds(LANDSCAPE_MARGIN, PORTRAIT_MARGIN*2 + SERIAL_PORT_SETTING_PANEL_HEIGHT, CONTROL_BROAD_INFO_PANEL_WIDTH, CONTROL_BROAD_INFO_PANEL_HEIGHT);
		operaBtnPanel.setBounds(LANDSCAPE_MARGIN, PORTRAIT_MARGIN*3 + SERIAL_PORT_SETTING_PANEL_HEIGHT +CONTROL_BROAD_INFO_PANEL_HEIGHT, OPERA_BTN_PANEL_WIDTH, OPERA_BTN_PANEL_HEIGHT);
		logPanel.setBounds(LANDSCAPE_MARGIN*2+ SERIAL_PORT_SETTING_PANEL_WIDTH, PORTRAIT_MARGIN, LOG_PANEL_WIDTH, LOG_PANEL_HEIGHT);

		serialPortPanel.setBorder(BorderFactory.createTitledBorder("串口设置"));
		controlBroadInfoPanel.setBorder(BorderFactory.createTitledBorder("控制板信息"));
		operaBtnPanel.setBorder(BorderFactory.createTitledBorder("操作区域"));
		logPanel.setBorder(BorderFactory.createTitledBorder("LOG"));

		configurePanel.add(serialPortPanel, BorderLayout.NORTH);
		configurePanel.add(controlBroadInfoPanel, BorderLayout.NORTH);
		configurePanel.add(operaBtnPanel, BorderLayout.NORTH);
		configurePanel.add(logPanel, BorderLayout.NORTH);
	}

	/**
	 * Place components to 确定、Help、关闭 面板
	 */
	private void placeComponentsToOkHelpClosePanel() {
		int landscape_margin = (OK_HELP_CLOSE_PANEL_WIDTH - BTN_WIDTH * 3) / 4;
		int portrait_margin = (OK_HELP_CLOSE_PANEL_HEIGHT - BTN_HEIGHT) / 2;
		
		okHelpClosePanel.setLayout(null);

		okBtn.setBounds(landscape_margin, portrait_margin, BTN_WIDTH, BTN_HEIGHT);
		helpBtn.setBounds(landscape_margin * 2 + BTN_WIDTH, portrait_margin, BTN_WIDTH, BTN_HEIGHT);
		closeWindowBtn.setBounds(landscape_margin * 3 + BTN_WIDTH * 2, portrait_margin, BTN_WIDTH, BTN_HEIGHT);
		
		okHelpClosePanel.add(okBtn, BorderLayout.NORTH);
		okHelpClosePanel.add(helpBtn, BorderLayout.NORTH);
		okHelpClosePanel.add(closeWindowBtn, BorderLayout.NORTH);
	}

	/**
	 * Place components to this
	 */
	private void placeComponentsToThis() {
		//init Frame
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setTitle("Serial_Port_Laser_V1.03");
		this.setResizable(false);	// 禁止窗口最大化
		this.setLocationRelativeTo(null); // 窗口于屏幕居中
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);

		configurePanel.setBounds(LANDSCAPE_MARGIN, PORTRAIT_MARGIN, CONFIGURE_PANEL_WIDTH, CONFIGURE_PANEL_HEIGHT);
		okHelpClosePanel.setBounds(LANDSCAPE_MARGIN, PORTRAIT_MARGIN + CONFIGURE_PANEL_HEIGHT +PORTRAIT_MARGIN , 
										OK_HELP_CLOSE_PANEL_WIDTH, OK_HELP_CLOSE_PANEL_HEIGHT);
		configurePanel.setBorder(BorderFactory.createTitledBorder("Configure"));
		// okHelpClosePanel.setBorder(BorderFactory.createTitledBorder("okHelpClosePanel"));

		this.add(configurePanel, BorderLayout.NORTH);
		this.add(okHelpClosePanel, BorderLayout.NORTH);
		this.setVisible(true);
	}

	/**
	 * Add action listener
	 */
	private void actionListener() {
		SerialPorSwitchtBtn.addActionListener(this);
		clearLogBtn.addActionListener(this);
		sendBtn.addActionListener(this);
		funcBtn.addActionListener(this);
		incBtn.addActionListener(this);
		decBtn.addActionListener(this);
		recoveryBtn.addActionListener(this);
		queryBtn.addActionListener(this);
		testBtn.addActionListener(this);
		okBtn.addActionListener(this);
		helpBtn.addActionListener(this);
		closeWindowBtn.addActionListener(this);
	}

	/**
	 * 添加日志
	 */
	private void addToLog(boolean isRefreshLog, String logStr) {
		if (isRefreshLog) {
			logTextArea.setText("\n" + logStr + "\n");
		} else {
			logTextArea.append("\n" + logStr + "\n");
		}
		logScrollPane.getViewport().setViewPosition(new Point(0, logScrollPane.getVerticalScrollBar().getMaximum()));	// 滚动显示最后的log
	}

	/**
	 * 清除日志
	 */
	private void clearLog() {
		addToLog(false, "清除log ===");
		logTextArea.setText("");
	}
	
	/**
	 * 打开串口
	 */
	private void openSerialPort(){
		addToLog(false, "打开串口 ===");
		if (null != serialPort) {
			JOptionPane.showMessageDialog(null, "已有串口打开，如需打开新串口，请先关闭原串口！", "温馨提示", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// 获取串口名称
		String commName = (String) commChoice.getSelectedItem();
		// 获取波特率
		int baudrate = 9600;
		String bps = (String) baudrateChoice.getSelectedItem();
		baudrate = Integer.parseInt(bps);

		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				serialPort = SerialPortManager.openPort(commName, baudrate);
				if (serialPort != null) {
					addToLog(false, "串口已打开");
					SerialPorSwitchtBtn.setText("Close");
					commChoice.setEnabled(false);
					baudrateChoice.setEnabled(false);
				}
			} catch (SerialPortParameterFailure e) {
				e.printStackTrace();
			} catch (NotASerialPort e) {
				e.printStackTrace();
			} catch (NoSuchPort e) {
				e.printStackTrace();
			} catch (PortInUse e) {
				e.printStackTrace();
				ShowUtils.warningMessage("串口已被占用！");
				serialPort = null;
			}
		}

		try {
			SerialPortManager.addListener(serialPort, new SerialListener());
		} catch (TooManyListeners e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭串口
	 */
	private void closeSerialPort(){	
		if (null == serialPort) {
			JOptionPane.showMessageDialog(null, "没有打开的串口！", "温馨提示", JOptionPane.ERROR_MESSAGE);
			return;
		}
		addToLog(false, "关闭串口 ===");
		SerialPortManager.closePort(serialPort);
		serialPort = null;
		SerialPorSwitchtBtn.setText("Open");
		commChoice.setEnabled(true);
		baudrateChoice.setEnabled(true);
		addToLog(false, "串口已关闭");
	}
	
	private void sendOrders(boolean isRequestRespond, String orders_down[], String orders_up[]) {
		serialPortBackBytes = null;
		try {  
			for (String order : orders_down) {
//				System.out.println(order + " ");
				sendData(order);
		        Thread.sleep(100); 
			}
            Thread.sleep(200); 
			for (String order : orders_up) {
//				System.out.println(order + " ");
				sendData(order);
		        Thread.sleep(100); 
			} 
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }

		if (isRequestRespond && null == serialPortBackBytes) {
			addToLog(false, "操作失败！");
		}
	}

	/**
	 * 确定
	 */
	private void okBtnClick(){
		addToLog(false, "确定 ===");		
	}

	/**
	 * Help
	 */
	private void help(){
		addToLog(false, "Help ===");
	}

	/**
	 * 关闭
	 */
	private void closeBtnClick(){
		addToLog(false, "关闭 ===");
		System.exit(0);
	}	 

	/**
	 * 发射
	 */
	private void sendBtnClick(){
		System.out.println("======== 发射 ========");
		addToLog(false, "发射 ===");
		sendOrders(false, ORDER_SEND_DOWN, ORDER_SEND_UP);
	}

	/**
	 * 功能切换
	 */
	private void funcBtnClick(){
		System.out.println("======== 功能切换 ========");
		addToLog(false, "功能切换 ===");
		sendOrders(true, ORDER_FUNC_DOWN, ORDER_FUNC_UP);
	}

	/**
	 * 按键 +
	 */
	private void incBtnClick(){
		System.out.println("======== 按键 + ========");
		addToLog(false, "按键 + ===");
		sendOrders(true, ORDER_INC_DOWN, ORDER_INC_UP);
	}

	/**
	 * 按键 -
	 */
	private void decBtnClick(){
		System.out.println("======== 按键 - ========");
		addToLog(false, "按键 - ===");
		sendOrders(true, ORDER_DEC_DOWN, ORDER_DEC_UP);
	}

	/**
	 * 恢复出厂化
	 */
	private void recoveryBtnClick(){
		System.out.println("======== 恢复出厂化 ========");
		addToLog(false, "恢复出厂化 ===");
		sendOrders(false, ORDER_RECOVERY_DOWN, ORDER_RECOVERY_UP);
	}

	/**
	 * 查询
	 */
	private void queryBtnClick(){
		System.out.println("======== 查询 ========");
		addToLog(false, "查询===");
		sendOrders(true, ORDER_QUERY_DOWN, ORDER_QUERY_UP);
		if (null == serialPortBackBytes) {
			controlBroadInfoTextArea.setText("查询失败，请重试");
		} else {
			controlBroadInfoTextArea.setText(ByteUtils.byteArrayToHexString(serialPortBackBytes, true));
		}
	}

	/**
	 * 测试
	 */
	private void testBtnClick(){
		System.out.println("======== 测试 ========");
		addToLog(false, "测试===");
		sendOrders(false, ORDER_TEST_DOWN, ORDER_TEST_UP);
	}
	
	/**
	 * Action performed
	 */
	public void actionPerformed(ActionEvent e) {
		Object operaObj = e.getSource();
		if (SerialPorSwitchtBtn == operaObj) { 
			if (null == serialPort) {
				openSerialPort();	// 打开串口
			} else {
				closeSerialPort();	// 关闭串口
			}
		} else if (clearLogBtn == operaObj) { // 清除log
			clearLog();
		} else if (okBtn == operaObj) { // 确定
			okBtnClick();
		} else if (helpBtn == operaObj) { // Help
			help();
		} else if (closeWindowBtn == operaObj) { // 关闭
			closeBtnClick();
		} else { 
			if (null == serialPort) {
				JOptionPane.showMessageDialog(null, "请先打开串口！", "温馨提示", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (sendBtn == operaObj) { // 发射
				sendBtnClick();
			} else if (funcBtn == operaObj) { // 功能切换
				funcBtnClick();
			} else if (incBtn == operaObj) { // 按键 +
				incBtnClick();
			} else if (decBtn == operaObj) { // 按键 -
				decBtnClick();
			} else if (recoveryBtn == operaObj) { // 恢复出厂化
				recoveryBtnClick();
			} else if (queryBtn == operaObj) { // 查询
				queryBtnClick();
			} else if (testBtn == operaObj) { // 测试
				testBtnClick();
			}
		}
	}
	
	/**
	 * 发送数据
	 */
	private void sendData(String data) {
		try {
			SerialPortManager.sendToPort(serialPort, ByteUtils.hexStr2Byte(data));
		} catch (SendDataToSerialPortFailure e) {
			e.printStackTrace();
		} catch (SerialPortOutputStreamCloseFailure e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serial Listener Class
	 */
	private class SerialListener implements SerialPortEventListener {
		/**
		 * 处理监控到的串口事件
		 */
		public void serialEvent(SerialPortEvent serialPortEvent) {

			switch (serialPortEvent.getEventType()) {
				case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
					byte[] data = serialPortBackBytes = null;
					try {
						if (serialPort == null) {
							ShowUtils.errorMessage("串口对象为空！监听失败！");
						} else {
							// 读取串口数据
							serialPortBackBytes = data = SerialPortManager.readFromPort(serialPort);
							System.out.println("data == " + data);
							addToLog(false, ByteUtils.byteArrayToHexString(data, true));
						}
					} catch (Exception e) {
						ShowUtils.errorMessage(e.toString());
						// 发生读取错误时显示错误信息后退出系统
						System.exit(0);
					}
					break;
				case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				case SerialPortEvent.CTS: // 3 清除待发送数据
				case SerialPortEvent.DSR: // 4 待发送数据准备好了
				case SerialPortEvent.RI: // 5 振铃指示
				case SerialPortEvent.CD: // 6 载波检测
				case SerialPortEvent.OE: // 7 溢位（溢出）错误
				case SerialPortEvent.PE: // 8 奇偶校验错误
				case SerialPortEvent.FE: // 9 帧错误
					break;
				case SerialPortEvent.BI: // 10 通讯中断
					ShowUtils.errorMessage("与串口设备通讯中断");
					break;
	
			}
		}
	}
}








