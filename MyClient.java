package finalEx.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyClient extends JFrame {
    private Container c;
    PrintWriter out;//出力用のライター
    MyCanvas mc;
    int x, y;   // mouse pointer position
    int px, py; // preliminary position

    public MyClient() {
        //名前の入力ダイアログを開く
        String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
        if(myName.equals("")){
            myName = "No name";//名前がないときは，"No name"とする
        }

        //ウィンドウを作成する
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
        setTitle("DrawingApli");//ウィンドウのタイトルを設定する
        setSize(500,500);//ウィンドウのサイズを設定する
        c = getContentPane();//フレームのペインを取得する
        mc = new MyCanvas(this); // mc のオブジェクト（実体）を作成
        this.setLayout(new BorderLayout(10, 10)); // レイアウト方法の指定
        this.add(mc,  BorderLayout.CENTER);       // 左側に mc （キャンバス）を配置
        this.setVisible(true); //可視化

        //サーバに接続する
        Socket socket = null;
        try {
            //"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
            //10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
            socket = new Socket("localhost", 10000);
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
        String myName;

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
                    mc.paint2(px, py, x, y);
                    mc.repaint(); // これ
                    if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
                        System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
                        String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
                        String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
                        int px = Integer.parseInt(inputTokens[0]);//数値に変換する   L75
                        int py = Integer.parseInt(inputTokens[1]);//数値に変換する
                        int x = Integer.parseInt(inputTokens[2]);//数値に変換する
                        int y = Integer.parseInt(inputTokens[3]);//数値に変換する
                        mc.paint2(px, py, x, y);
                        mc.repaint();

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
}


class MyCanvas extends Canvas implements MouseListener, MouseMotionListener {
    // ■ フィールド変数
    int x, y;   // mouse pointer position
    int px, py; // preliminary position
    int ow, oh; // width and height of the object
    int mode;   // drawing mode associated as below
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

    // ■ メソッド（オーバーライド）
    // フレームに何らかの更新が行われた時の処理
    public void update(Graphics g) {
        paint(g); // 下記の paint を呼び出す
    }

    // ■ メソッド（オーバーライド）
    public void paint(Graphics g) {
        d = getSize();   // キャンバスのサイズを取得
        if (img == null) // もし仮の画用紙の実体がまだ存在しなければ
            img = createImage(d.width, d.height); // 作成
        if (gc == null)  // もし仮の画用紙用のペン (GC) がまだ存在しなければ
            gc = img.getGraphics(); // 作成

        paint2(px,py,x,y);
        g.drawImage(img, 0, 0, this); // 仮の画用紙の内容を MyCanvas に描画
    }

    public void paint2(int px, int py, int x, int y){
        gc.drawLine(px, py, x, y);
    }


    // ■ メソッド
    // 下記のマウス関連のメソッドは，MouseListener をインターフェースとして実装しているため
    // 例え使わなくても必ず実装しなければならない
    public void mouseClicked(MouseEvent e){}// 今回は使わないが、無いとコンパイルエラー
    public void mouseEntered(MouseEvent e){
        //System.out.println("マウスが入った");
    }// 今回は使わないが、無いとコンパイルエラー
    public void mouseExited(MouseEvent e){
        //System.out.println("マウス脱出");
    } // 今回は使わないが、無いとコンパイルエラー
    public void mousePressed(MouseEvent e){ // マウスボタンが押された時の処理
        System.out.println("マウスを押した");
        x = e.getX();
        y = e.getY();
        System.out.println(x+", "+y);

    }
    public void mouseReleased(MouseEvent e){ // マウスボタンが離された時の処理
        System.out.println("マウスを放した");
    }

    // ■ メソッド
    // 下記のマウス関連のメソッドは，MouseMotionListener をインターフェースとして実装しているため
    // 例え使わなくても必ず実装しなければならない
    public void mouseDragged(MouseEvent e){ // マウスがドラッグされた時の処理
        System.out.println("マウスをドラッグ");
        px = x;
        py = y;
        x = e.getX();
        y = e.getY();
        System.out.println(px+", "+py+", "+x+", "+y);

        //送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
        String msg = px+" "+py+" "+x+" "+y;

        //サーバに情報を送る
        client.out.println(msg);//送信データをバッファに書き出す
        client.out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
        repaint(); // 再描画
    }
    public void mouseMoved(MouseEvent e){
        //System.out.println("マウスをドラッグ");
    } // 今回は使わないが、無いとコンパイルエラー
}