import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MyClient extends JFrame {
    static Container container;
    static PrintWriter out;//出力用のライター
    ChatCanvas chatCanvas;
    int x, y;   // mouse pointer position
    int px, py; // preliminary position

    int width = 700;
    int height = 700;

    ArrayList<String> nameList = new ArrayList<>();

    static Color color = Color.black;
    JLabel label;

    JPanel mainPanel = new JPanel();
    JPanel namePanel = new JPanel();
    JPanel answerPanel = new JPanel();

    JPanel rightPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel leftPanel = new JPanel();


    JPanel centerSouthPanel = new JPanel();
    JPanel centerNorthPanel = new JPanel();
    JPanel toolPanel = new JPanel();
    JPanel endstartPanel = new JPanel();

    JTextArea logList = new JTextArea();
    JScrollPane logScr = new JScrollPane( logList ); // スクロールバーを付ける場合

    JTextArea userList = new JTextArea();

    JTextField answerText = new JTextField();
    JTextField questionField = new JTextField();
    JTextField strokeWidth = new JTextField();


    JButton submitButton = new JButton(new submitAction());
    JButton resetButton = new JButton(new resetAction());
    JButton startButton = new JButton( new startAction());
    JButton endButton = new JButton( new endAction());

    JButton plusButton = new JButton (new plusAction());
    JButton minusButton = new JButton(new minusAction());
    JButton colorItem;

    JToolBar colorToolbar = new JToolBar();




    public MyClient() {
        //名前の入力ダイアログを開く
        String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);

        //接続先ipアドレスの入力ダイアログを開く
        String ipAddress = JOptionPane.showInputDialog(null,"ポケモンペイントクイズへようこそ!\n接続先のIPアドレスを入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);

        if(myName.equals("")){
            myName = "No name";//名前がないときは，"No name"とする
        }

        //ウィンドウを作成する
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
        setTitle("ポケモンペイントクイズ");//ウィンドウのタイトルを設定する
        setSize(width,height);//ウィンドウのサイズを設定する
        container = getContentPane();//フレームのペインを取得する
        chatCanvas = new ChatCanvas(this); // mc のオブジェクト（実体）を作成
        LineBorder border = new LineBorder(Color.BLACK, 1, true);//枠線の設定

        chatCanvas.setStroke(5);
        strokeWidth.setText(String.valueOf(3));
        strokeWidth.setEditable(false);
        logList.setEditable(false);
        userList.setEditable(false);
        questionField.setEditable(false);

        colorItem = new JButton(new BlackAction());
        colorItem.setIcon( new BlackIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new RedAction());
        colorItem.setIcon( new RedIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new GreenAction());
        colorItem.setIcon( new GreenIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new BlueAction());
        colorItem.setIcon( new BlueIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new CyanAction());
        colorItem.setIcon( new CyanIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new MagentaAction());
        colorItem.setIcon( new MagentaIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new YellowAction());
        colorItem.setIcon( new YellowIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);

        colorItem = new JButton(new OrangeAction());
        colorItem.setIcon( new OrangeIcon()  ); // デフォルトアイコン
        colorToolbar.add(colorItem);




        namePanel.setLayout(new BorderLayout());
        namePanel.add(new JLabel("あなたの名前 : " + myName) , BorderLayout.WEST) ;

        answerPanel.setLayout(new BorderLayout());
        answerPanel.setPreferredSize(new Dimension(150, 25));//パネルサイズを広げる
        answerPanel.add(answerText , BorderLayout.CENTER);
        answerPanel.add(submitButton , BorderLayout.EAST);

        endstartPanel.setLayout((new GridLayout(1,2)));
        endstartPanel.setPreferredSize(new Dimension(400, 25));//パネルサイズを広げる
        endstartPanel.add(endButton );
        endstartPanel.add(startButton );

        toolPanel.setLayout(new GridLayout(1,4));
        toolPanel.setPreferredSize(new Dimension(400, 25));//パネルサイズを広げる
        toolPanel.add(minusButton);
        toolPanel.add(strokeWidth );
        toolPanel.add(plusButton);
        toolPanel.add(resetButton);

        colorToolbar.setFloatable(true);
        colorToolbar.setLayout(new GridLayout( 1, 8));

        centerNorthPanel.setLayout(new GridLayout(2,1));
        centerNorthPanel.setPreferredSize(new Dimension(400, 50));//パネルサイズを広げる
        centerNorthPanel.add(questionField);
        centerNorthPanel.add(colorToolbar);

        centerSouthPanel.setLayout(new GridLayout(2,1));
        centerSouthPanel.setPreferredSize(new Dimension(400, 50));//パネルサイズを広げる
        centerSouthPanel.add(toolPanel);
        centerSouthPanel.add(endstartPanel);

        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(200, 500));//パネルサイズを広げる
        rightPanel.setBorder(border);
        rightPanel.add(new JLabel("ログ" ) , BorderLayout.NORTH) ;
        rightPanel.add(logScr , BorderLayout.CENTER);
        rightPanel.add(answerPanel , BorderLayout.SOUTH);

        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(border);
        centerPanel.add(chatCanvas, BorderLayout.CENTER);
        centerPanel.add(centerSouthPanel , BorderLayout.SOUTH);
        centerPanel.add(centerNorthPanel, BorderLayout.NORTH);

        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(100, 500));//パネルサイズを広げる
        leftPanel.setBorder(border);
        leftPanel.add(new JLabel("参加中のユーザ" ) , BorderLayout.NORTH) ;
        leftPanel.add(userList , BorderLayout.CENTER);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(centerPanel , BorderLayout.CENTER);
        mainPanel.add(namePanel , BorderLayout.NORTH);
        mainPanel.add(rightPanel , BorderLayout.EAST);
        mainPanel.add(leftPanel , BorderLayout.WEST);
        this.add(mainPanel , BorderLayout.CENTER);

        //サーバに接続する
        Socket socket = null;
        try {
            //"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
            //10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
            InetSocketAddress endpoint= new InetSocketAddress(ipAddress ,   10000);
            socket = new Socket();
            socket.connect(endpoint, 1000);

        } catch (UnknownHostException e) {
            System.err.println("ホストの IP アドレスが判定できません: " + e);
            Object[] msg = { "サーバーが見つかりませんでした", "システムを終了します" };
            JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                    JOptionPane.WARNING_MESSAGE );
            System.exit(0);
        } catch (IOException e) {
            System.err.println("エラーが発生しました: " + e);
            Object[] msg = { "サーバーに接続できませんでした" };
            JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                    JOptionPane.WARNING_MESSAGE );
            System.exit(0);
        }

        ResvClientThread mrt = new ResvClientThread(socket, myName);//受信用のスレッドを作成する
        mrt.start();//スレッドを動かす（Runが動く）
    }

    //メッセージ受信のためのスレッド
    public class ResvClientThread extends Thread {

        Socket socket;
        static String myName;
        static boolean isGameStart = false; //ゲームが開始されているかどうか
        static boolean isQuestioner = false; //出題者かどうか

        public ResvClientThread(Socket s, String n){
            socket = s;
            myName = n;
        }

        //通信状況を監視し，受信データによって動作する
        public void run() {
            try{

                InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(sisr);
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println(myName);//接続の最初に名前を送る   L68

                while(true) {

                    String inputLine = br.readLine();//データを一行分だけ読み込んでみる

                    int cmdIndex = inputLine.indexOf(":");
                    int endIndex = inputLine.length();
                    String command = inputLine.substring(0,cmdIndex);//入力内容の分類
                    String recvStr = inputLine.substring(cmdIndex + 1 , endIndex);


                    if(!command.equals("user") && !command.equals("server")){
                        chatCanvas.myPaint(px, py, x, y);
                        chatCanvas.repaint();
                    }

                    if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
                        System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する

                        //pointコマンドなら描画
                        if(command.equals("point")){

                                System.out.println(command);
                                String[] inputTokens = recvStr.split(" ");    //入力データを解析するために、スペースで切り分ける

                                int px = Integer.parseInt(inputTokens[0]);//数値に変換する
                                int py = Integer.parseInt(inputTokens[1]);//数値に変換する
                                int x = Integer.parseInt(inputTokens[2]);//数値に変換する
                                int y = Integer.parseInt(inputTokens[3]);//数値に変換する
                                chatCanvas.myPaint(px, py, x, y );
                                chatCanvas.repaint();

                        }

                        //msgコマンドならログリストにメッセージを表示
                        else if(command.equals(("msg"))){
                            String[] splitMessage = recvStr.split("\\.");
                            logList.append( splitMessage[0] + ":" + splitMessage[1] + "\n");

                        }

                        else if(command.equals("server")){
                            logList.append(recvStr + "\n");
                        }
                        //userコマンドならnameListに追加
                        else if(command.equals("user")){
                            String[] user = recvStr.split(",");
                            userList.setText("");
                            for(String name : user){
                                userList.append(name + "\n");
                            }
                        }

                        else if(command.equals("game")){
                            if(recvStr.equals("startGame")){
                                this.isGameStart= true;
                                logList.setText("");
                            }
                            else if(recvStr.equals("endGame")){
                                this.isGameStart = false;
                            }

                        }

                        //questionerコマンドで名前が自分と一致した場合
                        else if(command.equals(("questioner"))){
                            if(recvStr.equals(myName)){
                                this.isQuestioner = true;
                            }
                        }

                        else if(command.equals("question")) {
                            if (this.isQuestioner) {
                                questionField.setText("");
                                questionField.setText("お題 : " + recvStr);
                            }
                            else{
                                questionField.setText("");
                                questionField.setText("お題 : ???");
                            }
                        }


                        else if(command.equals("clear")){
                            chatCanvas.px = -width;
                            chatCanvas.py = -height;
                            chatCanvas.x = -width;
                            chatCanvas.y = -height;

                            chatCanvas.gc.setColor(Color.WHITE);
                            chatCanvas.gc.fillRect(0, 0, width, height);
                            chatCanvas.gc.setColor(Color.BLACK);
                        }

                        else if(command.equals("stroke")){
                            chatCanvas.setStroke(Integer.parseInt(recvStr));
                            strokeWidth.setText(recvStr);
                        }

                        else if(command.equals("color")){
                            if(recvStr.equals("red")){
                                chatCanvas.setColor(Color.red);
                            }
                            else if(recvStr.equals("green")){
                                chatCanvas.setColor(Color.green);
                            }
                            else if(recvStr.equals("blue")){
                                chatCanvas.setColor(Color.blue);
                            }
                            else if(recvStr.equals("cyan")){
                                chatCanvas.setColor(Color.cyan);
                            }
                            else if(recvStr.equals("magenta")){
                                chatCanvas.setColor(Color.magenta);
                            }
                            else if(recvStr.equals("yellow")){
                                chatCanvas.setColor(Color.yellow);
                            }else if(recvStr.equals("orange")){
                                chatCanvas.setColor(Color.orange);
                            }
                            else if(recvStr.equals("black")){
                                chatCanvas.setColor(Color.black);
                            }


                        }

                        if(!this.isGameStart){
                            isQuestioner = false;
                            questionField.setText("");

                        }
                    }else{
                        break;
                    }
                }
                socket.close();

            } catch (IOException e) {
                System.err.println("エラーが発生しました: " + e);
            }
        }
    }


    public static void main(String[] args) {
        MyClient net = new MyClient();
        net.setVisible(true);
    }

    class BlackIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor(Color.black);
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class RedIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.red );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class GreenIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.green );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class BlueIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.blue );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class CyanIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.cyan );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class MagentaIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.magenta );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class YellowIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.yellow );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class OrangeIcon   implements Icon{
        static final int width  = 20;
        static final int height = 20;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.orange );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class BlackAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {

            color = Color.black;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart){
                MyClient.out.println("color:black");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
        }
    }

    class RedAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.red;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:red");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    class GreenAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {

            color = Color.green;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:green");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    class BlueAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.blue;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:blue");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    class CyanAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.cyan;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:cyan");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    class MagentaAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {

            color = Color.magenta;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:magenta");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    class YellowAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.yellow;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:yellow");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    class OrangeAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.orange;
            chatCanvas.setColor(color);

            if(ResvClientThread.isGameStart && ResvClientThread.isQuestioner){
                MyClient.out.println("color:orange");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため色の変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }

        }
    }

    //送信ボタンを押したとき
    class submitAction extends AbstractAction{
        submitAction() {
            putValue( Action.NAME, "送信" );
            putValue( Action.SHORT_DESCRIPTION, "送信" );
        }
        public void actionPerformed(ActionEvent e) {
            String inputMessage = answerText.getText();

            if(!inputMessage.equals((""))){

                boolean isSafety = true;

                //文字列に禁止文字がふくまれていないか
                String[] inputString = inputMessage.split("");

                String[] badString = {"," , "." , ":" };
                for(String s : inputString){
                    for(int i = 0 ; i < badString.length ; i++) {
                        if (s.contains(badString[i])){
                            isSafety = false;
                        }
                    }
                }

                if(ResvClientThread.isQuestioner){
                    Object[] msg = { "あなたは出題者です" };
                    JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                }

                else if(!isSafety){
                    Object[] msg = { "使用できない文字が含まれています" };
                    JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                }
                else {
                    //サーバに情報を送る
                    //コマンド msg 名前とメッセージを , 区切りで挿入し送信
                    MyClient.out.println("msg:" + ResvClientThread.myName + "." + inputMessage);//送信データをバッファに書き出す
                    MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

                    answerText.setText("");
                }
            }

        }
    }

    //リセットボタンを押したとき
     class resetAction extends AbstractAction {
        resetAction() {
            putValue(Action.NAME, "CLEAR");
            putValue(Action.SHORT_DESCRIPTION, "CLEAR");
        }

        public void actionPerformed(ActionEvent e) {

            if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないためキャンバスクリアできません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
            else {
                //サーバに情報を送る
                //コマンド clear キャンバス画面をリセットするようサーバーに要求
                MyClient.out.println("clear:canavsClear");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
        }
    }

    //ゲームスタートボタンを押したとき
    class startAction extends AbstractAction {
        startAction() {
                putValue(Action.NAME, "ゲームスタート");
                putValue(Action.SHORT_DESCRIPTION, "ゲームスタート");
        }

        public void actionPerformed(ActionEvent e) {

            //ゲームが開始されていないとき
            if(!ResvClientThread.isGameStart){
                //サーバに情報を送る
                //コマンド game isGameStartフラグをTrueにするようにサーバーに要求
                MyClient.out.println("game:startGame");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else{
                Object[] msg = { "ゲームは開始されています" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    //ゲーム終了ボタンを押したとき
    class endAction extends AbstractAction {
        endAction() {
            putValue(Action.NAME, "ゲーム終了");
            putValue(Action.SHORT_DESCRIPTION, "ゲーム終了");
        }

        public void actionPerformed(ActionEvent e) {
            //ゲームが開始されていないとき
            if(ResvClientThread.isGameStart){
                //サーバに情報を送る
                //コマンド game isGameStartフラグをFalseにするようにサーバーに要求
                MyClient.out.println("game:endGame");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else{
                Object[] msg = { "ゲームは開始されていません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    //+ボタンを押したとき
    class plusAction extends AbstractAction {
        plusAction() {
            putValue(Action.NAME, "+");
            putValue(Action.SHORT_DESCRIPTION, "+");
        }

        public void actionPerformed(ActionEvent e) {
            int fps = Integer.parseInt(strokeWidth.getText());
            if(fps < 15) {
                fps += 1;
            }
            chatCanvas.setStroke(fps);
            strokeWidth.setText(String.valueOf(fps));

            if (ResvClientThread.isQuestioner && ResvClientThread.isGameStart) {
                MyClient.out.println("stroke:" + fps);//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため変更ができません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    //-ボタンを押したとき
    class minusAction extends AbstractAction {
        minusAction() {
            putValue(Action.NAME, "-");
            putValue(Action.SHORT_DESCRIPTION, "-");
        }

        public void actionPerformed(ActionEvent e) {
            int fps = Integer.parseInt(strokeWidth.getText());
            if(fps > 1) {
                fps -= 1;
            }
            chatCanvas.setStroke(fps);
            strokeWidth.setText(String.valueOf(fps));

            if (ResvClientThread.isQuestioner && ResvClientThread.isGameStart) {
                MyClient.out.println("stroke:" + fps);//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }

            else  if(!ResvClientThread.isQuestioner && ResvClientThread.isGameStart){
                Object[] msg = { "出題者でないため変更できません" };
                JOptionPane.showMessageDialog( MyClient.container, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }
}


class ChatCanvas extends Canvas implements MouseListener, MouseMotionListener {
    // ■ フィールド変数
    int x, y ;   // mouse pointer position
    int px, py ; // preliminary position
    Image img = null;   // 仮の画用紙
    Graphics gc = null; // 仮の画用紙用のペン
    Dimension d; // キャンバスの大きさ取得用
    PrintWriter out;//出力用のライター
    MyClient client;
    Color color = Color.BLACK;

    int fps = 3;

    // ■ コンストラクタ
    ChatCanvas(MyClient obj) {
        this.setSize(500, 500);        // キャンバスのサイズを指定
        setBackground(new Color(255, 255, 255));
        addMouseListener(this);       // マウスのボタンクリックなどを監視するよう指定
        addMouseMotionListener(this); // マウスの動きを監視するよう指定
        client = obj;
    }

    @Override
    // フレームに何らかの更新が行われた時の処理
    public void update(Graphics g) {
        paint(g); // paint を呼び出す
    }

    @Override
    public void paint(Graphics g) {
        d = getSize();   // キャンバスのサイズを取得
        if (img == null) // もし仮の画用紙の実体がまだ存在しなければ
            img = createImage(d.width, d.height); // 作成
        if (gc == null)  // もし仮の画用紙用のペン (gc) がまだ存在しなければ
            gc = img.getGraphics(); // 作成

        myPaint(px, py, x, y);

        g.drawImage(img,0, 0, this); // 仮の画用紙の内容を MyCanvas に描画
    }

    public void myPaint(int px, int py, int x, int y) {
        BasicStroke bs = new BasicStroke(fps, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        ((Graphics2D) gc).setStroke(bs);
        gc.setColor(color);
        gc.drawLine(px, py, x, y);
    }

    public void mouseClicked(MouseEvent e) {
    }// 使わないけど、無いとコンパイルエラー

    public void mouseEntered(MouseEvent e) {
    }// 使わないけど無いとコンパイルエラー

    public void mouseExited(MouseEvent e) {
    } // 使わないけど無いとコンパイルエラー

    public void mousePressed(MouseEvent e) { // マウスボタンが押された時
        System.out.println("マウスを押した");
        x = e.getX();
        y = e.getY();
        System.out.println(x + ", " + y);

    }

    public void mouseReleased(MouseEvent e) { // マウスボタンが離された時
        System.out.println("マウスを放した");
    }

    public void mouseDragged(MouseEvent e) { // マウスがドラッグされた時の処理
        if (MyClient.ResvClientThread.isQuestioner || !MyClient.ResvClientThread.isGameStart) {
            System.out.println("マウスをドラッグ");
            px = x;
            py = y;
            x = e.getX();
            y = e.getY();
            System.out.println(px + ", " + py + ", " + x + ", " + y);

            //送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
            String msg = "point:" + px + " " + py + " " + x + " " + y;

            //サーバに情報を送る
            client.out.println(msg);//送信データをバッファに書き出す
            client.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            repaint(); // 再描画
        } else {
            Object[] msg = {"出題者ではないためキャンバスに書き込めません"};
            JOptionPane.showMessageDialog(MyClient.container, msg, "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void setStroke(int fps) {
        this.fps = fps;
    }

    public void setColor(Color color){this.color = color; }

    public void mouseMoved(MouseEvent e){} // 使わないけど無いとコンパイルエラー

}