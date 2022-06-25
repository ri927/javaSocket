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
    static Container c;
    static PrintWriter out;//出力用のライター
    MyCanvas mc;
    int x, y;   // mouse pointer position
    int px, py; // preliminary position

    int width = 700;
    int height = 700;


    ArrayList<String> nameList = new ArrayList<>();

    JPanel mainPanel = new JPanel();
    JPanel namePanel = new JPanel();
    JPanel answerPanel = new JPanel();

    JPanel rightPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel leftPanel = new JPanel();


    JPanel centerSouthPanel = new JPanel();
    JPanel centerNorthPanel = new JPanel();

    JTextArea logList = new JTextArea();
    JScrollPane logScr = new JScrollPane( logList ); // スクロールバーを付ける場合

    JTextArea userList = new JTextArea();

    JTextField answerText = new JTextField();
    JTextField questionField = new JTextField();

    JButton submitButton = new JButton(new submitAction());
    JButton resetButton = new JButton(new resetAction());
    JButton startButton = new JButton( new startAction());
    JButton endButton = new JButton( new endAction());




    public MyClient() {
        //名前の入力ダイアログを開く
        String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);

        if(myName.equals("")){
            myName = "No name";//名前がないときは，"No name"とする
        }

        //ウィンドウを作成する
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
        setTitle("DrawingApli");//ウィンドウのタイトルを設定する
        setSize(width,height);//ウィンドウのサイズを設定する
        c = getContentPane();//フレームのペインを取得する
        mc = new MyCanvas(this); // mc のオブジェクト（実体）を作成
        LineBorder border = new LineBorder(Color.BLACK, 1, true);//枠線の設定

        namePanel.setLayout(new BorderLayout());
        namePanel.add(new JLabel("あなたの名前 : " + myName) , BorderLayout.WEST) ;

        answerPanel.setLayout(new BorderLayout());
        answerPanel.setPreferredSize(new Dimension(150, 25));//パネルサイズを広げる
        answerPanel.add(answerText , BorderLayout.CENTER);
        answerPanel.add(submitButton , BorderLayout.EAST);

        centerSouthPanel.setLayout((new GridLayout(1,2)));
        centerSouthPanel.setPreferredSize(new Dimension(400, 25));//パネルサイズを広げる
        centerSouthPanel.add(endButton );
        centerSouthPanel.add(startButton );

        centerNorthPanel.setLayout(new GridLayout(2,1));
        centerNorthPanel.setPreferredSize(new Dimension(400, 50));//パネルサイズを広げる
        centerNorthPanel.add(questionField);
        centerNorthPanel.add(resetButton);

        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(200, 500));//パネルサイズを広げる
        rightPanel.setBorder(border);
        rightPanel.add(new JLabel("ログ" ) , BorderLayout.NORTH) ;
        rightPanel.add(logScr , BorderLayout.CENTER);
        rightPanel.add(answerPanel , BorderLayout.SOUTH);

        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(border);
        centerPanel.add(mc , BorderLayout.CENTER);
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
            InetSocketAddress endpoint= new InetSocketAddress("192.168.10.114",  10000);
            socket = new Socket();
            socket.connect(endpoint, 1000);

        } catch (UnknownHostException e) {
            System.err.println("ホストの IP アドレスが判定できません: " + e);
        } catch (IOException e) {
            System.err.println("エラーが発生しました: " + e);
        }

        MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
        mrt.start();//スレッドを動かす（Runが動く）
    }

    //メッセージ受信のためのスレッド
    public class MesgRecvThread extends Thread {

        Socket socket;
        static String myName;
        static boolean isGameStart = false; //ゲームが開始されているかどうか
        static boolean isQuestioner = false; //出題者かどうか

        public MesgRecvThread(Socket s, String n){
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

            /*        //userコマンドならnameListに追加
                    if(command.equals("user")){
                        String[] user = recvStr.split(",");
                        System.out.println(user);
                        userList.setText("");
                        for(String name : user){
                            userList.append(name + "\n");
                        }
                    }
                    //msgコマンドならログリストに追加
                    else if(command.equals("server")){
                        logList.append( recvStr + "\n");
                    }*/
                    if(!command.equals("user") && !command.equals("server")){
                        mc.myPaint(px, py, x, y);
                        mc.repaint();
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
                                mc.myPaint(px, py, x, y);
                                mc.repaint();


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

                        //gameコマンドならフラグを反転
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
                            mc.gc.setColor(Color.WHITE);
                            mc.gc.fillRect(0, 0, width, height);
                            mc.repaint();
                            mc.gc.setColor(Color.BLACK);
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

                if(MesgRecvThread.isQuestioner){
                    Object[] msg = { "あなたは出題者です" };
                    JOptionPane.showMessageDialog( MyClient.c, msg, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                }

                else if(!isSafety){
                    Object[] msg = { "使用できない文字が含まれています" };
                    JOptionPane.showMessageDialog( MyClient.c, msg, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                }
                else {
                    //サーバに情報を送る
                    //コマンド msg 名前とメッセージを , 区切りで挿入し送信
                    MyClient.out.println("msg:" + MesgRecvThread.myName + "." + inputMessage);//送信データをバッファに書き出す
                    MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

                    answerText.setText("");
                }
            }

        }
    }

    //リセットボタンを押したとき
     class resetAction extends AbstractAction {
        resetAction() {
            putValue(Action.NAME, "画面リセット");
            putValue(Action.SHORT_DESCRIPTION, "画面リセット");
        }

        public void actionPerformed(ActionEvent e) {

            if(!MesgRecvThread.isQuestioner && MesgRecvThread.isGameStart){
                Object[] msg = { "出題者でないためキャンバスクリアできません" };
                JOptionPane.showMessageDialog( MyClient.c, msg, "Warning",
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
            if(!MesgRecvThread.isGameStart){
                //サーバに情報を送る
                //コマンド game isGameStartフラグをTrueにするようにサーバーに要求
                MyClient.out.println("game:startGame");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else{
                Object[] msg = { "ゲームは開始されています" };
                JOptionPane.showMessageDialog( MyClient.c, msg, "Warning",
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
            if(MesgRecvThread.isGameStart){
                //サーバに情報を送る
                //コマンド game isGameStartフラグをFalseにするようにサーバーに要求
                MyClient.out.println("game:endGame");//送信データをバッファに書き出す
                MyClient.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
            }
            else{
                Object[] msg = { "ゲームは開始されていません" };
                JOptionPane.showMessageDialog( MyClient.c, msg, "Warning",
                        JOptionPane.WARNING_MESSAGE );
            }
        }
    }
}


class MyCanvas extends Canvas implements MouseListener, MouseMotionListener {
    // ■ フィールド変数
    int x, y;   // mouse pointer position
    int px, py; // preliminary position
    Image img = null;   // 仮の画用紙
    Graphics gc = null; // 仮の画用紙用のペン
    Dimension d; // キャンバスの大きさ取得用
    PrintWriter out;//出力用のライター
    MyClient client;

    // ■ コンストラクタ
    MyCanvas(MyClient obj){
        this.setSize(500,500);        // キャンバスのサイズを指定
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

        myPaint(px,py,x,y);

        g.drawImage(img, 0, 0, this); // 仮の画用紙の内容を MyCanvas に描画
    }

    public void myPaint(int px, int py, int x, int y){
        gc.drawLine(px, py, x, y);
    }

    public void mouseClicked(MouseEvent e){}// 使わないけど、無いとコンパイルエラー
    public void mouseEntered(MouseEvent e){}// 使わないけど無いとコンパイルエラー
    public void mouseExited(MouseEvent e){} // 使わないけど無いとコンパイルエラー

    public void mousePressed(MouseEvent e){ // マウスボタンが押された時
        System.out.println("マウスを押した");
        x = e.getX();
        y = e.getY();
        System.out.println(x+", "+y);

    }
    public void mouseReleased(MouseEvent e){ // マウスボタンが離された時
        System.out.println("マウスを放した");
    }

    public void mouseDragged(MouseEvent e){ // マウスがドラッグされた時の処理
        if(MyClient.MesgRecvThread.isQuestioner || !MyClient.MesgRecvThread.isGameStart) {
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
        }
        else{
            Object[] msg = { "出題者ではないためキャンバスに書き込めません" };
            JOptionPane.showMessageDialog( MyClient.c, msg, "Warning",
                    JOptionPane.WARNING_MESSAGE );
        }
    }
    public void mouseMoved(MouseEvent e){} // 使わないけど無いとコンパイルエラー

}