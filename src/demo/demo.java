package demo;

import com.ioserver.bean.Struct_TagInfo;
import com.ioserver.bean.Struct_TagInfo_AddName;
import com.sun.jna.WString;
import demoFunction.classDemoClient;
import lk.loginForm.LoginForm;
import lk.loginForm.method.ExclImport;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;


public class demo {

    private JFrame frame;
    private JTextField text_ip;
    private JTextField text_port;
    int chooseSubscribe = 0;
    int chooseAsyncReadWrite = 0;
    int chooseSyncReadWrite = 0;
    int flagAsyncReadComplete = 0;
    int flagSubscribeAll = 0;//1订阅开始 2暂停 3同步下暂停
    int flagSyncReadComplete = 0;
    int chooseSyncRdBtn = 0;// 1,short 2,float 3,string
    int chosseAsyncRdBtn = 0;
    int flagConnect = 0;// 1为连接
    /*/flag 判断是同步读、显示全部、追加*/
    int flagSyncRead = 0;//1是同步读 2是显示全部 3是追加

    Vector<WString> vecAllTagName = new Vector<WString>();
    Map<WString, Vector<WString>> MapvecSubscribeTagsName = new HashMap<>();//带设备名的TagName
    List asynclist;
    List synclist;
    List subscrilist;
    WString subscri_device_list[] = {};//设备名字的获取
    WString subscri_device = null;


    private JTable table_subscribe;
    private JTable table;
    private JTable table_1;
    private JTextField text_syncReadTagName;
    private JTextField text_syncWriteTagName;
    private JTextField text_syncWriteTagValue;
    private JTextField text_asyncWriteTagName;
    private JTextField text_asyncWriteTagValue;
    private JTextField text_asyncReadTagName;
    private JTextField text_ReadTagName;//订阅下的TagName
    private JTextField text_WriteTagValue;//订阅下写操作的TagValue
    private JTextField text_WriteTagName;//订阅下写操作的TagName

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {

            UIManager.put("RootPane.setupButtonVisible", false);//隐藏设置
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();

        } catch (Exception e) {
            //TODO exception
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    demo window = new demo();
                    //***********************//
                    LoginForm loginForm = new LoginForm(window.frame);//登录界面的构造函数
                    //window.frame.setVisible(true);
                    //***********************//


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });
    }

    /**
     * Create the application.
     */
    public demo() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        classDemoClient client = new classDemoClient();

        /************************ 标签初始化 **************************/

        frame = new JFrame();
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(237, 240, 232));
        frame.setBounds(520, 200, 873, 609);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblNewLabel = new JLabel("IP\uFF1A");
        lblNewLabel.setBounds(12, 332, 40, 16);
        lblNewLabel.setFont(new Font("宋体", Font.BOLD, 15));

        JLabel lblNewLabel_1 = new JLabel("PORT\uFF1A");
        lblNewLabel_1.setBounds(12, 363, 63, 16);
        lblNewLabel_1.setFont(new Font("宋体", Font.BOLD, 15));

        text_ip = new JTextField();
        text_ip.setBounds(70, 329, 80, 22);
        text_ip.setText("127.0.0.1");
        text_ip.setColumns(10);

        text_port = new JTextField();
        text_port.setBounds(70, 360, 80, 22);
        text_port.setText("12380");
        text_port.setColumns(10);

        JLabel label = new JLabel("\u6307\u793A\u706F\uFF1A");//""含义是指示灯的unicode码
        label.setBounds(12, 290, 63, 16);
        label.setFont(new Font("宋体", Font.BOLD, 14));

        JLabel lamp = new JLabel();
        lamp.setBounds(87, 283, 30, 30);
        lamp.setOpaque(true);
        lamp.setHorizontalAlignment(SwingConstants.CENTER);
        //设置连接图标
        ImageIcon lampImg = new ImageIcon("C:\\Users\\Administrator\\Desktop\\JavaDemoCode(x64)\\src\\resources\\picture\\lampclose.jpg");
        lampImg.setImage(lampImg.getImage().getScaledInstance(lamp.getWidth(), lamp.getHeight(), Image.SCALE_DEFAULT));
        lamp.setIcon(lampImg);

        JLabel label_1 = new JLabel();
        label_1.setText("CISDI");
        label_1.setForeground(Color.WHITE);
        label_1.setFont(new Font("华文行楷", Font.BOLD, 56));
        label_1.setBounds(351, 0, 250, 60);
        frame.getContentPane().add(label_1);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(137, 140, 135));
        panel.setBounds(0, 58, 2120, 2);
        frame.getContentPane().add(panel);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel_1.setBounds(170, 60, 2, 2010);
        frame.getContentPane().add(panel_1);

        /// ---------------------订阅面板-------------------///

        JPanel panel_subscribe = new JPanel();
        panel_subscribe.setBackground(Color.WHITE);
        panel_subscribe.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_subscribe.setBounds(839, 217, 98, 95);
        frame.getContentPane().add(panel_subscribe);
        panel_subscribe.setVisible(false);
        panel_subscribe.setLayout(null);

        //lktodo:订阅 excl导入按钮添加
        JButton exclButton_subscription = new JButton();
        exclButton_subscription.setFont(new Font("华文仿宋", Font.BOLD, 15));
        exclButton_subscription.setBounds(570, 11, 110, 28);
        exclButton_subscription.setText("导入Excl");
        panel_subscribe.add(exclButton_subscription);
        exclButton_subscription.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                subscrilist=ExclImport.exclimport();
            }
        });

        String[] subscribHeadename = {"设备名", "标签名", "标签值", "时间", "质量情况", "标签ID"};

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 50, 509, 260);
        panel_subscribe.add(scrollPane);

        DefaultTableModel model_subscribe = new DefaultTableModel();

        table_subscribe = new JTable(model_subscribe) {
            //表格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model_subscribe.setColumnIdentifiers(subscribHeadename);

        table_subscribe.getColumnModel().getColumn(2).setPreferredWidth(30);//value
        table_subscribe.getColumnModel().getColumn(3).setPreferredWidth(120);// 时间列宽设置为100
        table_subscribe.getColumnModel().getColumn(4).setPreferredWidth(40);// 质量列宽设置为40
        table_subscribe.setCellSelectionEnabled(true);
        table_subscribe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//修改一条
        scrollPane.setViewportView(table_subscribe);
        table_subscribe.setBorder(new LineBorder(Color.black));
        // 首先通过表格对象 table 获取选择器模型
        ListSelectionModel selectionModel_subscribe = table_subscribe.getSelectionModel();
        selectionModel_subscribe.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table_subscribe.getSelectedRow(); // 获取选中的第一行
                if(selectedRow==-1){//持续刷新 结果错误返回
                    return;
                }
                System.out.println(selectedRow);
                text_WriteTagName.setText(table_subscribe.getValueAt(selectedRow,1).toString());

            }
        });




        /*/ -----------------------------------------------------/*/

        /// ---------------------异步读写面板-----------------///

        JPanel panel_AsyncReadWrite = new JPanel();
        panel_AsyncReadWrite.setBackground(Color.WHITE);
        panel_AsyncReadWrite.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_AsyncReadWrite.setBounds(806, 404, 131, 144);
        frame.getContentPane().add(panel_AsyncReadWrite);
        panel_AsyncReadWrite.setVisible(false);
        panel_AsyncReadWrite.setLayout(null);

        JLabel lblNewLabel_4 = new JLabel("异步读写");
        lblNewLabel_4.setForeground(Color.GRAY);
        lblNewLabel_4.setFont(new Font("宋体", Font.BOLD, 23));
        lblNewLabel_4.setBounds(299, 0, 114, 45);
        panel_AsyncReadWrite.add(lblNewLabel_4);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 40, 430, 236);
        panel_AsyncReadWrite.add(scrollPane_1);

        text_asyncReadTagName = new JTextField();
        text_asyncReadTagName.setBounds(48, 346, 201, 23);
        panel_AsyncReadWrite.add(text_asyncReadTagName);
        text_asyncReadTagName.setColumns(10);

        String[] Headname = {"TagName", "TagValue", "Time", "Quality", "Id"};
        DefaultTableModel model_asyncReadWrite = new DefaultTableModel();
        JTable table_asyncReadWrite = new JTable(model_asyncReadWrite) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model_asyncReadWrite.setColumnIdentifiers(Headname);
        //model_asyncReadWrite.addRow(Headname);基础数据

        scrollPane_1.setViewportView(table_asyncReadWrite);
        table_asyncReadWrite.setBorder(new LineBorder(Color.black));
        //lktodo 异步table修改
        table_asyncReadWrite.setCellSelectionEnabled(true);
        table_asyncReadWrite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//修改一条
        // 首先通过表格对象 table 获取选择器模型
        ListSelectionModel selectionModel = table_asyncReadWrite.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table_asyncReadWrite.getSelectedRow(); // 获取选中的第一行
                int selectedColumn = table_asyncReadWrite.getSelectedColumn(); // 获取选中的第一列
                int[] selectedRows = table_asyncReadWrite.getSelectedRows();// 获取选中的所有行
                int[] selectedColumns = table_asyncReadWrite.getSelectedColumns();
                text_asyncWriteTagName.setText(table_asyncReadWrite.getValueAt(selectedRow, 0).toString());
            }
        });


        /// -------------------------------------------------------///

        /// ----------------------同步读写-----------------------///

        JPanel panel_SyncReadWrite = new JPanel();
        panel_SyncReadWrite.setBackground(Color.WHITE);
        panel_SyncReadWrite.setBorder(new LineBorder(new Color(0, 0, 0)));

        panel_SyncReadWrite.setBounds(823, 82, 114, 103);
        frame.getContentPane().add(panel_SyncReadWrite);
        panel_SyncReadWrite.setVisible(false);
        panel_SyncReadWrite.setLayout(null);

        JLabel lblNewLabel_2 = new JLabel("同步读写");
        lblNewLabel_2.setBounds(262, 0, 101, 33);
        lblNewLabel_2.setForeground(Color.GRAY);
        lblNewLabel_2.setFont(new Font("宋体", Font.BOLD, 23));
        panel_SyncReadWrite.add(lblNewLabel_2);

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(12, 53, 453, 194);
        panel_SyncReadWrite.add(scrollPane_2);

        DefaultTableModel model_syncReadWrite = new DefaultTableModel();
        JTable table_syncReadWrite = new JTable(model_syncReadWrite);
        model_syncReadWrite.setColumnIdentifiers(Headname);
        //model_syncReadWrite.addRow(Headname);

        scrollPane_2.setViewportView(table_syncReadWrite);
        table_syncReadWrite.setBorder(new LineBorder(Color.black));


        text_syncReadTagName = new JTextField();
        text_syncReadTagName.setBounds(52, 344, 170, 22);
        panel_SyncReadWrite.add(text_syncReadTagName);
        text_syncReadTagName.setColumns(10);

        JLabel label_2 = new JLabel("\u8F93\u5165\u8BFB\u53D8\u91CF\uFF0C\u7528\",\"\u9694\u5F00\uFF1A");//输入读变量，用","隔开：
        label_2.setBounds(52, 304, 176, 16);
        panel_SyncReadWrite.add(label_2);

        JLabel label_3 = new JLabel("\u53D8\u91CF\u540D\uFF1A");
        label_3.setBounds(410, 307, 63, 16);
        panel_SyncReadWrite.add(label_3);

        text_syncWriteTagName = new JTextField();
        text_syncWriteTagName.setBounds(485, 304, 84, 22);
        panel_SyncReadWrite.add(text_syncWriteTagName);
        text_syncWriteTagName.setColumns(10);

        JLabel label_4 = new JLabel("\u53D8\u91CF\u503C\uFF1A");
        label_4.setBounds(410, 342, 63, 16);
        panel_SyncReadWrite.add(label_4);

        text_syncWriteTagValue = new JTextField();
        text_syncWriteTagValue.setBounds(485, 342, 84, 22);
        panel_SyncReadWrite.add(text_syncWriteTagValue);
        text_syncWriteTagValue.setColumns(10);

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.BLACK);
        panel_2.setBounds(327, 255, 2, 300);
        panel_SyncReadWrite.add(panel_2);

        JRadioButton rdbtnSyncShort = new JRadioButton("int");
        rdbtnSyncShort.setBounds(400, 375, 60, 25);
        panel_SyncReadWrite.add(rdbtnSyncShort);
        rdbtnSyncShort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseSyncRdBtn = 1;
            }
        });

        JRadioButton rdbtnSyncFloat = new JRadioButton("float");
        rdbtnSyncFloat.setBounds(460, 375, 80, 25);
        panel_SyncReadWrite.add(rdbtnSyncFloat);
        rdbtnSyncFloat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseSyncRdBtn = 2;
            }
        });

        JRadioButton rdbtnSyncString = new JRadioButton("string");
        rdbtnSyncString.setBounds(540, 375, 100, 25);
        panel_SyncReadWrite.add(rdbtnSyncString);
        rdbtnSyncString.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseSyncRdBtn = 3;
            }
        });


        /// --------------------------------------------------------///
        /********************** <标签初始化END> ****************************/

        /********************* 连接按钮 **************************/
        JButton btn_connect = new JButton("\u8FDE\u63A5");
        btn_connect.setBounds(12, 404, 70, 25);
        btn_connect.setBorder(null);
        btn_connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (client.funcConnect(text_ip.getText(), text_port.getText()) == 0) {
                    flagConnect = 1;
                } else {
                    flagConnect = 0;
                }
            }
        });
        /********************* < 连接按钮END> **************************/


        /********************* 断开连接按钮 **************************/
        JButton btn_disconnect = new JButton("\u65AD\u5F00");
        btn_disconnect.setBounds(90, 404, 70, 25);
        btn_disconnect.setBorder(null);
        btn_disconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lamp.getText() == "开") {
                    client.funcDisConnect();
                }
            }
        });
        /********************* < 断开连接按钮END> **************************/

        /************************ 订阅选项卡 ******************************/
        JButton btnSubscribeChoose = new JButton("订阅功能");
        btnSubscribeChoose.setBackground(new Color(137, 140, 135));
        btnSubscribeChoose.setBorder(new LineBorder(Color.WHITE));
        btnSubscribeChoose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel_subscribe.setBounds(168, 60, 1487, 1468);

                //同步操作的标签
                text_ReadTagName = new JTextField();
                text_ReadTagName.setBounds(52, 344, 170, 22);
                panel_subscribe.add(text_ReadTagName);
                text_ReadTagName.setColumns(10);

                JLabel label_2 = new JLabel("输入读变量，用\",\"隔开：");
                label_2.setBounds(52, 304, 176, 16);
                panel_subscribe.add(label_2);

                JLabel label_3 = new JLabel("变量名：");
                label_3.setBounds(410, 307, 63, 16);
                panel_subscribe.add(label_3);

                text_WriteTagName = new JTextField();
                text_WriteTagName.setBounds(485, 304, 84, 22);
                panel_subscribe.add(text_WriteTagName);
                text_WriteTagName.setColumns(10);

                JLabel label_4 = new JLabel("变量值");
                label_4.setBounds(410, 342, 63, 16);
                panel_subscribe.add(label_4);

                text_WriteTagValue = new JTextField();
                text_WriteTagValue.setBounds(485, 342, 84, 22);
                panel_subscribe.add(text_WriteTagValue);
                text_WriteTagValue.setColumns(10);

                JPanel panel_2 = new JPanel();
                panel_2.setBackground(Color.BLACK);
                panel_2.setBounds(327, 255, 2, 300);
                panel_subscribe.add(panel_2);

                JRadioButton rdbtnSyncShort = new JRadioButton("int");
                rdbtnSyncShort.setBounds(400, 375, 60, 25);
                panel_subscribe.add(rdbtnSyncShort);
                JRadioButton rdbtnSyncFloat = new JRadioButton("float");
                rdbtnSyncFloat.setBounds(460, 375, 80, 25);
                panel_subscribe.add(rdbtnSyncFloat);
                JRadioButton rdbtnSyncString = new JRadioButton("string");
                rdbtnSyncString.setBounds(540, 375, 100, 25);
                panel_subscribe.add(rdbtnSyncString);
                rdbtnSyncShort.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        chooseSyncRdBtn = 1;
                        rdbtnSyncFloat.setSelected(false);
                        rdbtnSyncString.setSelected(false);
                    }
                });
                rdbtnSyncFloat.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        chooseSyncRdBtn = 2;
                        rdbtnSyncString.setSelected(false);
                        rdbtnSyncShort.setSelected(false);
                    }
                });
                rdbtnSyncString.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        chooseSyncRdBtn = 3;
                        rdbtnSyncShort.setSelected(false);
                        rdbtnSyncFloat.setSelected(false);
                    }
                });

                ///////////同步写////////
                JButton btn_syncWrite = new JButton("\u540C\u6B65\u5199");
                btn_syncWrite.setBorder(null);
                btn_syncWrite.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int type = 0;
                        if (rdbtnSyncShort.isSelected() == true) {
                            type = 1;

                        }
                        if (rdbtnSyncFloat.isSelected() == true) {
                            type = 2;

                        }
                        if (rdbtnSyncString.isSelected() == true) {
                            type = 3;

                        }
                        client.funcSyncWrite(text_WriteTagName.getText(), text_WriteTagValue.getText(), type);
                    }
                });
                btn_syncWrite.setBounds(410, 418, 103, 25);
                panel_subscribe.add(btn_syncWrite);

                //todo：   订阅面板添加同步操作按钮
                JButton btn_syncReadWrite = new JButton("同步读");
                btn_syncReadWrite.setBorder(null);
                btn_syncReadWrite.setBounds(12, 391, 103, 25);
                panel_subscribe.add(btn_syncReadWrite);
                btn_syncReadWrite.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        model_subscribe.setRowCount(0);//表格清零
                        flagSyncReadComplete = 1;//同步读flag
                        flagSyncRead = 1;
                        if(flagSubscribeAll==3){//如果是订阅下同步暂停
                            flagSubscribeAll=1;
                        }
                    }
                });

                //lktodo:订阅同步显示按钮添加
                JButton Button_sync_show = new JButton();
                Button_sync_show.setBounds(120, 390, 153, 25);
                Button_sync_show.setText("显示导入的数据");
                panel_subscribe.add(Button_sync_show);
                Button_sync_show.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        if (subscrilist == null) {
                            return;
                        }
                        model_subscribe.setRowCount(0);//清零
                        flagSyncRead = 2;//同步显示
                        flagSyncReadComplete = 1;
                        flagSubscribeAll=1;//恢复订阅下全部显示
                    }
                });


                chooseSubscribe = 1;//订阅标签选选择
            }
        });
        btnSubscribeChoose.setBounds(0, 143, 170, 43);
        frame.getContentPane().add(btnSubscribeChoose);
        /************************ < 订阅选项卡END> ************************/

        /************************ 异步读写选项卡 ****************************/
        JButton btnAsyncReadWrite = new JButton("\u5F02\u6B65\u8BFB\u5199\u529F\u80FD");
        btnAsyncReadWrite.setBackground(new Color(137, 140, 135));
        btnAsyncReadWrite.setBorder(new LineBorder(Color.WHITE));
        btnAsyncReadWrite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel_AsyncReadWrite.setBounds(168, 60, 1487, 1468);
                chooseAsyncReadWrite = 1;
            }
        });
        btnAsyncReadWrite.setBounds(0, 101, 170, 43);
        btnAsyncReadWrite.setEnabled(false);
        frame.getContentPane().add(btnAsyncReadWrite);//显示异步按钮

        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel_3.setBounds(327, 275, 2, 300);
        panel_AsyncReadWrite.add(panel_3);

        JLabel label_5 = new JLabel("\u6570\u636E\u53D8\u91CF\u540D\u79F0\uFF0C\u7528\",\"\u5206\u9694\uFF1A");
        label_5.setBounds(47, 313, 185, 16);
        panel_AsyncReadWrite.add(label_5);

        JLabel label_6 = new JLabel("\u53D8\u91CF\u540D\uFF1A");
        label_6.setBounds(405, 313, 63, 16);
        panel_AsyncReadWrite.add(label_6);

        text_asyncWriteTagName = new JTextField();
        text_asyncWriteTagName.setBounds(480, 310, 103, 22);
        panel_AsyncReadWrite.add(text_asyncWriteTagName);
        text_asyncWriteTagName.setColumns(10);

        JLabel label_7 = new JLabel("\u53D8\u91CF\u503C\uFF1A");
        label_7.setBounds(405, 349, 63, 16);
        panel_AsyncReadWrite.add(label_7);

        text_asyncWriteTagValue = new JTextField();
        text_asyncWriteTagValue.setBounds(480, 346, 103, 22);
        panel_AsyncReadWrite.add(text_asyncWriteTagValue);
        text_asyncWriteTagValue.setColumns(10);

        JRadioButton rdbtnAsyncInt = new JRadioButton("int");
        rdbtnAsyncInt.setBounds(400, 390, 72, 25);
        panel_AsyncReadWrite.add(rdbtnAsyncInt);
        rdbtnAsyncInt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosseAsyncRdBtn = 1;
            }
        });

        JRadioButton rdbtnAsyncFloat = new JRadioButton("float");
        rdbtnAsyncFloat.setBounds(470, 390, 90, 25);
        panel_AsyncReadWrite.add(rdbtnAsyncFloat);
        rdbtnAsyncFloat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosseAsyncRdBtn = 2;
            }
        });

        JRadioButton rdbtnAsyncString = new JRadioButton("string");
        rdbtnAsyncString.setBounds(555, 390, 100, 25);
        panel_AsyncReadWrite.add(rdbtnAsyncString);
        rdbtnAsyncString.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chosseAsyncRdBtn = 3;
            }
        });
        /************************ <异步读写选项卡END> ***********************/

        /************************ 同步读写选项卡 ****************************/
        JButton btnSyncReadWrite = new JButton("\u540C\u6B65\u8BFB\u5199\u529F\u80FD");
        btnSyncReadWrite.setBackground(new Color(137, 140, 135));
        btnSyncReadWrite.setBorder(new LineBorder(Color.WHITE));
        btnSyncReadWrite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel_SyncReadWrite.setBounds(168, 60, 1487, 1468);
                chooseSyncReadWrite = 1;
            }
        });
        btnSyncReadWrite.setEnabled(false);
        btnSyncReadWrite.setBounds(0, 60, 170, 43);
        /************************ <同步读写选项卡END> ************************/

        /************************ 订阅全部变量 ****************************/
        JButton btnSubscribeAllTags = new JButton("订阅全部变量");
        btnSubscribeAllTags.setBounds(12, 5, 140, 30);
        panel_subscribe.add(btnSubscribeAllTags);
        btnSubscribeAllTags.addActionListener(new ActionListener() {
            //订阅全部变量后展示数据
            public void actionPerformed(ActionEvent e) {
                MapvecSubscribeTagsName = client.funcSubscribeAllTags_Device();
                Iterator<Map.Entry<WString, Vector<WString>>> entries = MapvecSubscribeTagsName.entrySet().iterator();
                int i = 0;//导入数组
                subscri_device_list = new WString[MapvecSubscribeTagsName.size()];
                while (entries.hasNext()) {
                    Map.Entry<WString, Vector<WString>> entry = entries.next();
                    subscri_device_list[i] = entry.getKey();
                    i++;
                }

                JLabel comboBox_device_label = new JLabel("按设备名查看：");//添加标签
                final JComboBox<WString> comboBox_device = new JComboBox<WString>(subscri_device_list);
                //添加下拉框、标签到订阅面板上
                panel_subscribe.add(comboBox_device);
                panel_subscribe.add(comboBox_device_label);
                comboBox_device_label.setVisible(true);
                comboBox_device.setVisible(true);
                comboBox_device.setBounds(120, 250, 120, 25);
                comboBox_device_label.setBounds(12, 250, 110, 25);
                //添加条目选中状态改变的监听器
                comboBox_device.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        // 只处理选中的状态
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            if(flagSubscribeAll==3){
                                flagSubscribeAll=1;//恢复同步暂停

                            }
                            System.out.println("选中: " + comboBox_device.getSelectedIndex() + " = " + comboBox_device.getSelectedItem());
                            subscri_device = (WString) comboBox_device.getSelectedItem();//选择的订阅设备名
                        }
                    }
                });
                subscri_device = subscri_device_list[0];//给定默认值


                flagSubscribeAll = 1;//订阅timer启动
            }
        });

        //暂停/继续刷新按钮
        JButton btn_subscribe_pause = new JButton("暂停/继续");
        btn_subscribe_pause.setBounds(160, 5, 100, 30);
        panel_subscribe.add(btn_subscribe_pause);
        btn_subscribe_pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(flagSubscribeAll==1){
                    flagSubscribeAll=2;//暂停table刷新
                    return;
                }
                if(flagSubscribeAll==2){
                    flagSubscribeAll=1;//继续table刷新
                    return;
                }
                if(flagSubscribeAll==3){
                    flagSubscribeAll=1;//恢复同步暂停，table继续全部刷新
                    return;
                }

            }
        });




        /************************ <订阅全部变量END> ************************/

        /************************ 异步读按钮 ****************************/
        JButton btn_asyncRead = new JButton("\u5F02\u6B65\u8BFB");
        btn_asyncRead.setBorder(null);
        btn_asyncRead.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                model_asyncReadWrite.setRowCount(0);
                String strAllTagName = text_asyncReadTagName.getText();
                String[] tagNames = strAllTagName.split(",");
                client.funcAsyncRead(tagNames);
                flagAsyncReadComplete = 1;
            }
        });
        btn_asyncRead.setBounds(51, 390, 103, 25);
        panel_AsyncReadWrite.add(btn_asyncRead);

        //lktodo:异步excl导入按钮添加
        JButton exclButton_async = new JButton();
        exclButton_async.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        exclButton_async.setFont(new Font("华文仿宋", Font.BOLD, 15));
        exclButton_async.setBounds(570, 11, 110, 28);
        exclButton_async.setText("导入Excl");
        panel_AsyncReadWrite.add(exclButton_async);
        exclButton_async.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                asynclist = ExclImport.exclimport();
                model_asyncReadWrite.setRowCount(0);
                String strAllTagName = asynclist.get(0).toString();//拼接字符串
                for (int i = 1; i < asynclist.size(); i++) {
                    strAllTagName = strAllTagName + "," + asynclist.get(i).toString();
                }

                String[] tagNames = strAllTagName.split(",");
                client.funcAsyncRead(tagNames);
                flagAsyncReadComplete = 1;
            }
        });

        //lktodo:异步显示按钮添加
        JButton Button_async_show = new JButton();
        Button_async_show.setBounds(170, 390, 103, 25);
        Button_async_show.setText("显示全部");
        panel_AsyncReadWrite.add(Button_async_show);
        Button_async_show.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                List tagNamelist = asynclist;
                model_asyncReadWrite.setRowCount(0);//清零原来的数据
                String strAllTagName = tagNamelist.get(0).toString();//拼接字符串
                for (int i = 1; i < tagNamelist.size(); i++) {
                    strAllTagName = strAllTagName + "," + tagNamelist.get(i).toString();
                }
                String[] tagNames = strAllTagName.split(",");
                client.funcAsyncRead(tagNames);
                flagAsyncReadComplete = 1;

            }
        });
        /************************ <异步读按钮END> ************************/

        /************************ 异步写按钮 ****************************/
        JButton btn_asyncWrite = new JButton("\u5F02\u6B65\u5199");
        btn_asyncWrite.setBorder(null);
        btn_asyncWrite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int type = 0;
                if (rdbtnAsyncInt.isSelected() == true) {
                    type = 1;
                }
                if (rdbtnAsyncFloat.isSelected() == true) {
                    type = 2;
                }
                if (rdbtnAsyncString.isSelected() == true) {
                    type = 3;
                }
                client.funcAsyncWrite(text_asyncWriteTagName.getText(), text_asyncWriteTagValue.getText(), type);
            }
        });
        btn_asyncWrite.setBounds(418, 422, 103, 25);
        panel_AsyncReadWrite.add(btn_asyncWrite);
        /************************ <异步写按钮END> ************************/

        /************************* 同步读变量 ************************/
//        JButton btn_syncReadWrite = new JButton("\u540C\u6B65\u8BFB");
//        btn_syncReadWrite.setBorder(null);
//        btn_syncReadWrite.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                model_syncReadWrite.setRowCount(0);
//                flagSyncReadComplete = 1;
//                flagSyncRead=1;
//
//            }
//        });
//        btn_syncReadWrite.setBounds(58, 391, 103, 25);
//        panel_SyncReadWrite.add(btn_syncReadWrite);
//
//
//        //lktodo:同步excl导入按钮添加
//        JButton exclButton_sync = new JButton();
//        exclButton_sync.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
//        exclButton_sync.setFont(new Font("华文仿宋", Font.BOLD, 15));
//        exclButton_sync.setBounds(570, 11, 110, 28);
//        exclButton_sync.setText("导入Excl");
//        panel_SyncReadWrite.add(exclButton_sync);
//        exclButton_sync.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                model_syncReadWrite.setRowCount(0);
//                synclist= ExclImport.exclimport();//同步读数据
//                flagSyncRead=2;
//                flagSyncReadComplete = 1;
//            }
//        });
//
//        //lktodo:同步显示按钮添加
//        JButton Button_sync_show = new JButton();
//        Button_sync_show.setBounds(170, 390, 103, 25);
//        Button_sync_show.setText("显示全部");
//        panel_SyncReadWrite.add(Button_sync_show);
//        Button_sync_show.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                if(synclist==null){
//                    return;
//                }
//                model_syncReadWrite.setRowCount(0);
//                flagSyncRead=2;
//                flagSyncReadComplete = 1;
//            }
//        });

        /************************ <同步读按钮END> ************************/

        /************************** 同步写按钮 *****************************/
//        JButton btn_syncWrite = new JButton("\u540C\u6B65\u5199");
//        btn_syncWrite.setBorder(null);
//        btn_syncWrite.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                int type = 0;
//                if (rdbtnSyncShort.isSelected() == true) {
//                    type = 1;
//                }
//                if (rdbtnSyncFloat.isSelected() == true) {
//                    type = 2;
//                }
//                if (rdbtnSyncString.isSelected() == true) {
//                    type = 3;
//                }
//                client.funcSyncWrite(text_syncWriteTagName.getText(), text_syncWriteTagValue.getText(), type);
//            }
//        });
//        btn_syncWrite.setBounds(410, 418, 103, 25);
//        panel_SyncReadWrite.add(btn_syncWrite);
        /************************ <同步写按钮END> **************************/

        /************************* 界面刷新定时器 ************************/

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                if (client.funcIsConnect() == 0 && flagConnect == 1) {
                    //lamp.setBackground(new Color(19,137,80));
                    //设置连接成功图标
                    ImageIcon lampImg = new ImageIcon("C:\\Users\\Administrator\\Desktop\\JavaDemoCode(x64)\\src\\resources\\picture\\lampopen.jpg");
                    lampImg.setImage(lampImg.getImage().getScaledInstance(lamp.getWidth(), lamp.getHeight(), Image.SCALE_DEFAULT));
                    lamp.setIcon(lampImg);
                } else {
                    lamp.setBackground(Color.white);
                    ImageIcon lampImg = new ImageIcon("C:\\Users\\Administrator\\Desktop\\JavaDemoCode(x64)\\src\\resources\\picture\\lampclose.jpg");
                    lampImg.setImage(lampImg.getImage().getScaledInstance(lamp.getWidth(), lamp.getHeight(), Image.SCALE_DEFAULT));
                    lamp.setIcon(lampImg);
                    //lamp.setText("关");
                }

                if (chooseAsyncReadWrite == 1) {
                    scrollPane_1.setBounds(10, 50, 670, 189);
                    btnSubscribeChoose.setBackground(new Color(45, 170, 240));
                    btnSubscribeChoose.repaint();
                    panel_subscribe.setVisible(false);
                    panel_subscribe.repaint();

                    btnSyncReadWrite.setBackground(new Color(45, 170, 240));
                    btnSyncReadWrite.repaint();
                    panel_SyncReadWrite.setVisible(false);
                    panel_SyncReadWrite.repaint();

                    btnAsyncReadWrite.setBackground(Color.white);
                    btnAsyncReadWrite.repaint();
                    panel_AsyncReadWrite.setVisible(true);
                    panel_AsyncReadWrite.repaint();

                    chooseAsyncReadWrite = 0;
                }

                if (chooseSyncReadWrite == 1) {
                    scrollPane_2.setBounds(10, 50, 670, 189);
                    btnSubscribeChoose.setBackground(new Color(45, 170, 240));
                    btnSubscribeChoose.repaint();
                    panel_subscribe.setVisible(false);
                    panel_subscribe.repaint();

                    btnAsyncReadWrite.setBackground(new Color(45, 170, 240));
                    btnAsyncReadWrite.repaint();
                    panel_AsyncReadWrite.setVisible(false);
                    panel_AsyncReadWrite.repaint();

                    btnSyncReadWrite.setBackground(Color.white);
                    btnSyncReadWrite.repaint();
                    panel_SyncReadWrite.setVisible(true);
                    panel_SyncReadWrite.repaint();

                    chooseSyncReadWrite = 0;
                }

                if (chooseSubscribe == 1) {
                    scrollPane.setBounds(10, 50, 670, 189);
                    btnAsyncReadWrite.setBackground(new Color(45, 170, 240));
                    btnAsyncReadWrite.repaint();
                    panel_AsyncReadWrite.setVisible(false);
                    panel_AsyncReadWrite.repaint();

                    btnSyncReadWrite.setBackground(new Color(45, 170, 240));
                    btnSyncReadWrite.repaint();
                    panel_SyncReadWrite.setVisible(false);
                    panel_SyncReadWrite.repaint();

                    btnSubscribeChoose.setBackground(Color.white);
                    btnSubscribeChoose.repaint();
                    panel_subscribe.setVisible(true);
                    panel_subscribe.repaint();

                    chooseSubscribe = 0;
                }

                //以下代码表示选择一个写值的类型后改变选择状态
                if (btnAsyncReadWrite.getBackground() == Color.WHITE) {
                    if (chosseAsyncRdBtn == 1) {
                        rdbtnAsyncFloat.setSelected(false);
                        rdbtnAsyncString.setSelected(false);
                        rdbtnAsyncInt.setSelected(true);
                    }
                    if (chosseAsyncRdBtn == 2) {
                        rdbtnAsyncFloat.setSelected(true);
                        rdbtnAsyncString.setSelected(false);
                        rdbtnAsyncInt.setSelected(false);
                    }
                    if (chosseAsyncRdBtn == 3) {
                        rdbtnAsyncFloat.setSelected(false);
                        rdbtnAsyncString.setSelected(true);
                        rdbtnAsyncInt.setSelected(false);
                    }
                }

                if (btnSyncReadWrite.getBackground() == Color.WHITE) {
                    if (chooseSyncRdBtn == 1) {
                        rdbtnSyncFloat.setSelected(false);
                        rdbtnSyncString.setSelected(false);
                        rdbtnSyncShort.setSelected(true);
                    }
                    if (chooseSyncRdBtn == 2) {
                        rdbtnSyncFloat.setSelected(true);
                        rdbtnSyncString.setSelected(false);
                        rdbtnSyncShort.setSelected(false);
                    }
                    if (chooseSyncRdBtn == 3) {
                        rdbtnSyncFloat.setSelected(false);
                        rdbtnSyncString.setSelected(true);
                        rdbtnSyncShort.setSelected(false);
                    }

                }

            }
        }, 800, 800);

        /******************** < 界面定时器END> ***********************/

        /************************* 数据读取定时器 *************************/
        Timer timer_subscribe = new Timer();
        timer_subscribe.schedule(new TimerTask() {
            public void run() {
                //同步读刷新
                if ((flagSyncReadComplete == 1) && (lamp.getBackground() == Color.GREEN)) {
                    flagSyncReadComplete = 0;//读一次
                    String[] tagNames = {};
                    String strAllTagName = null;
                    switch (flagSyncRead) {
                        case 1: {
                            strAllTagName = text_syncReadTagName.getText();
                            tagNames = strAllTagName.split(",");
                            break;
                        }
                        case 2: {
                            strAllTagName = synclist.get(0).toString();//拼接字符串
                            for (int i = 1; i < synclist.size(); i++) {
                                strAllTagName = strAllTagName + "," + synclist.get(i).toString();
                            }
                            tagNames = strAllTagName.split(",");
                            break;
                        }
                        case 3: {

                        }
                    }
                    Struct_TagInfo_AddName[] structTagValue = client.funcSyncRead(tagNames);
                    for (int i = 0; i < structTagValue.length; i++) {
                        Struct_TagInfo_AddName value = structTagValue[i];

                        if (value != null) {
                            Vector row = new Vector();
                            row.add(client.funcGetTagNameById((int) value.TagID));

                            switch ((int) value.TagValue.ValueType) {
                                case 1:
                                    row.add(value.TagValue.TagValue.bitVal);
                                    break;
                                case 2:
                                    row.add(value.TagValue.TagValue.i1Val);
                                    break;
                                case 3:
                                    row.add(value.TagValue.TagValue.i1Val);
                                    break;
                                case 4:
                                    row.add(value.TagValue.TagValue.i2Val);
                                    break;
                                case 5:
                                    row.add(value.TagValue.TagValue.i2Val);
                                    break;
                                case 6:
                                    row.add(value.TagValue.TagValue.i4Val);
                                    break;
                                case 7:
                                    row.add(value.TagValue.TagValue.i4Val);
                                    break;
                                case 8:
                                    row.add(value.TagValue.TagValue.i8Val);
                                    break;
                                case 9:
                                    row.add(value.TagValue.TagValue.r4Val);
                                    break;
                                case 10:
                                    row.add(value.TagValue.TagValue.r8Val);
                                    break;
                                case 11:
                                    row.add(value.TagValue.TagValue.wstrVal);
                                    break;
                                default:
                                    row.add("不支持类型");
                                    break;
                            }

                            Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                            //tolocaleString过时方法替代
                            DateFormat ddtf = DateFormat.getDateTimeInstance();
                            row.add("" + ddtf.format(TimeStamp));
                            row.add(value.QualityStamp);
                            row.add(value.TagID);
                            model_syncReadWrite.addRow(row);
                            table_syncReadWrite.repaint();
                        }
                    }

                }

                //订阅下同步读刷新
                if ((flagSyncReadComplete == 1) && flagSubscribeAll==1) {
                    flagSyncReadComplete = 0;//读一次
                    String[] tagNames = {};
                    String strAllTagName = null;
                    switch (flagSyncRead) {
                        case 1: {
                            strAllTagName = text_ReadTagName.getText();
                            tagNames = strAllTagName.split(",");
                            break;
                        }
                        case 2: {
                            strAllTagName = subscrilist.get(0).toString();//拼接字符串
                            for (int i = 1; i < subscrilist.size(); i++) {
                                strAllTagName = strAllTagName + "," + subscrilist.get(i).toString();
                            }
                            tagNames = strAllTagName.split(",");
                            break;
                        }
                        case 3: {
                            //todo 1111
                        }
                    }


                    Map<String,WString> tag_devicename=client.getDevicebyTagName(tagNames);// //获取设备名和tag的组合
                    Struct_TagInfo_AddName[] structTagValue = client.funcSyncRead(tagNames);
                    for (int i = 0; i < structTagValue.length; i++) {
                        Struct_TagInfo_AddName value = structTagValue[i];

                        if (value != null) {
                            Vector row = new Vector();
                            WString tagn=client.funcGetTagNameById((int) value.TagID);
                            row.add(tag_devicename.get(tagn.toString()));//添加设备名
                            row.add(tagn);//添加tagname

                            switch ((int) value.TagValue.ValueType) {
                                case 1:
                                    row.add(value.TagValue.TagValue.bitVal);
                                    break;
                                case 2:
                                    row.add(value.TagValue.TagValue.i1Val);
                                    break;
                                case 3:
                                    row.add(value.TagValue.TagValue.i1Val);
                                    break;
                                case 4:
                                    row.add(value.TagValue.TagValue.i2Val);
                                    break;
                                case 5:
                                    row.add(value.TagValue.TagValue.i2Val);
                                    break;
                                case 6:
                                    row.add(value.TagValue.TagValue.i4Val);
                                    break;
                                case 7:
                                    row.add(value.TagValue.TagValue.i4Val);
                                    break;
                                case 8:
                                    row.add(value.TagValue.TagValue.i8Val);
                                    break;
                                case 9:
                                    row.add(value.TagValue.TagValue.r4Val);
                                    break;
                                case 10:
                                    row.add(value.TagValue.TagValue.r8Val);
                                    break;
                                case 11:
                                    row.add(value.TagValue.TagValue.wstrVal);
                                    break;
                                default:
                                    row.add("不支持类型");
                                    break;
                            }

                            Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                            //tolocaleString过时方法替代
                            DateFormat ddtf = DateFormat.getDateTimeInstance();
                            row.add("" + ddtf.format(TimeStamp));
                            row.add(value.QualityStamp);
                            row.add(value.TagID);
                            model_subscribe.addRow(row);
                            table_subscribe.repaint();
                        }
                    }
                    flagSubscribeAll=3;//订阅同步下暂停
                }
                //异步读刷新
                if ((flagAsyncReadComplete == 1) && (lamp.getBackground() == Color.GREEN)) {
                    flagAsyncReadComplete = 0;
                    Struct_TagInfo[] structTagValue = client.getAsyncReadValue();

                    for (int i = 0; i < structTagValue.length; i++) {
                        if (structTagValue[i] != null) {

                            Struct_TagInfo value = structTagValue[i];

                            if (value != null) {
                                Vector row = new Vector();
                                row.add(client.funcGetTagNameById(value.TagID));

                                switch ((int) value.TagValue.ValueType) {
                                    case 1:
                                        row.add(value.TagValue.TagValue.bitVal);
                                        break;
                                    case 2:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        break;
                                    case 3:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        break;
                                    case 4:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        break;
                                    case 5:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        break;
                                    case 6:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        break;
                                    case 7:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        break;
                                    case 8:
                                        row.add(value.TagValue.TagValue.i8Val);
                                        break;
                                    case 9:
                                        row.add(value.TagValue.TagValue.r4Val);
                                        break;
                                    case 10:
                                        row.add(value.TagValue.TagValue.r8Val);
                                        break;
                                    case 11:
                                        row.add(value.TagValue.TagValue.wstrVal);
                                        break;
                                    default:
                                        row.add("不支持类型");
                                        break;
                                }

                                Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                                //tolocaleString过时方法替代
                                DateFormat ddtf = DateFormat.getDateTimeInstance();
                                row.add("" + ddtf.format(TimeStamp));
                                row.add(value.QualityStamp);
                                row.add(value.TagID);
                                model_asyncReadWrite.addRow(row);
                                table_asyncReadWrite.repaint();
                            }
                        }

                    }
                }

                // 订阅变量刷新
                if ((btnSubscribeChoose.getBackground() == Color.WHITE)) {
                    if (vecAllTagName.size() > 0 || false) {
                        model_subscribe.setRowCount(0);//清零之前的变量值
                        for (int i = 1; i < vecAllTagName.size(); i++) {
                            WString tagName = vecAllTagName.get(i);
                            Struct_TagInfo value = client.funcGetTagValue(tagName);//订阅发送的tagname
                            Vector row = new Vector();
                            row.add(vecAllTagName.get(0)); //设备名字添加
                            if (value != null) {
                                row.add(tagName);
                                switch ((int) value.TagValue.ValueType) {
                                    case 1:
                                        row.add(value.TagValue.TagValue.bitVal);
                                        break;
                                    case 2:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        break;
                                    case 3:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        break;
                                    case 4:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        break;
                                    case 5:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        break;
                                    case 6:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        break;
                                    case 7:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        break;
                                    case 8:
                                        row.add(value.TagValue.TagValue.i8Val);
                                        break;
                                    case 9:
                                        row.add(value.TagValue.TagValue.r4Val);
                                        break;
                                    case 10:
                                        row.add(value.TagValue.TagValue.r8Val);
                                        break;
                                    case 11:
                                        row.add(value.TagValue.TagValue.wstrVal);
                                        break;
                                    default:
                                        row.add("不支持类型");
                                        break;
                                }

                                Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                                //tolocaleString过时方法替代
                                DateFormat ddtf = DateFormat.getDateTimeInstance();
                                row.add("" + ddtf.format(TimeStamp));
                                row.add(value.QualityStamp);
                                row.add(value.TagID);
                                model_subscribe.addRow(row);
                                table_subscribe.repaint();
                            }
                        }
                    }
                }
                //Todo:导入的数据订阅刷新
                if (flagSubscribeAll == 1 && (btnSubscribeChoose.getBackground() == Color.WHITE)) {
                    if (MapvecSubscribeTagsName.size() > 0) {
                        model_subscribe.setRowCount(0);//清零之前的变量值
                        Iterator<Map.Entry<WString, Vector<WString>>> entries = MapvecSubscribeTagsName.entrySet().iterator();
                        while (entries.hasNext()) {
                            Map.Entry<WString, Vector<WString>> entry = entries.next();
                            //选择的设备不为空就显示所有订阅
                            if (subscri_device != entry.getKey()) {
                                //do something
                            } else {
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    WString tagName = entry.getValue().get(i);
                                    Struct_TagInfo value = client.funcGetTagValue(tagName);//订阅发送的tagname
                                    //设备名字添加
                                    Vector row = new Vector();
                                    row.add(entry.getKey());
                                    if (value != null) {
                                        row.add(tagName);
                                        switch ((int) value.TagValue.ValueType) {
                                            case 1:
                                                row.add(value.TagValue.TagValue.bitVal);
                                                break;
                                            case 2:
                                                row.add(value.TagValue.TagValue.i1Val);
                                                break;
                                            case 3:
                                                row.add(value.TagValue.TagValue.i1Val);
                                                break;
                                            case 4:
                                                row.add(value.TagValue.TagValue.i2Val);
                                                break;
                                            case 5:
                                                row.add(value.TagValue.TagValue.i2Val);
                                                break;
                                            case 6:
                                                row.add(value.TagValue.TagValue.i4Val);
                                                break;
                                            case 7:
                                                row.add(value.TagValue.TagValue.i4Val);
                                                break;
                                            case 8:
                                                row.add(value.TagValue.TagValue.i8Val);
                                                break;
                                            case 9:
                                                row.add(value.TagValue.TagValue.r4Val);
                                                break;
                                            case 10:
                                                row.add(value.TagValue.TagValue.r8Val);
                                                break;
                                            case 11:
                                                row.add(value.TagValue.TagValue.wstrVal);
                                                break;
                                            default:
                                                row.add("不支持类型");
                                                break;
                                        }

                                        Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                                        //tolocaleString过时方法替代
                                        DateFormat ddtf = DateFormat.getDateTimeInstance();
                                        row.add("" + ddtf.format(TimeStamp));
                                        row.add(value.QualityStamp);
                                        row.add(value.TagID);
                                        model_subscribe.addRow(row);
                                        table_subscribe.repaint();
                                    }
                                }
                            }


                        }
                    }
                }

//                if (flagSubscribeAll == 1) {
//                    flagSubscribeAll = 0;
//                    model_subscribe.setRowCount(0);
//                }
            }
        }, 1000, 1500);
        /********************* < 订阅定时器END> ************************/

        frame.getContentPane().add(btnSyncReadWrite);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().add(lblNewLabel);
        frame.getContentPane().add(lblNewLabel_1);
        frame.getContentPane().add(text_ip);
        frame.getContentPane().add(text_port);
        frame.getContentPane().add(label);
        frame.getContentPane().add(lamp);
        frame.getContentPane().add(btn_connect);
        frame.getContentPane().add(btn_disconnect);

    }
}
