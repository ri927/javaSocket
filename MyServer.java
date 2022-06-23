package finalEx.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//スレッド部（各クライアントに応じて）
class ClientProcThread extends Thread {
    private int number;//自分の番号
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;//接続者の名前

    MyServer server = new MyServer();


    public ClientProcThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;
    }

    public void run() {
        try {

            myName = myIn.readLine();//初めて接続したときの一行目は名前

            MyServer.addUser((myName));


            //誰かが接続したら全員に名前のリストを送る
            MyServer.SendAll(MyServer.createUserList(MyServer.getUserName() ), myName);


            while (true) {//無限ループで，ソケットへの入力を監視する

                String str = myIn.readLine();

                int cmdIndex = str.indexOf(":");
                int endIndex = str.length();
                String cmd = str.substring(0,cmdIndex);//入力内容の分類
                String sendStr = str.substring(cmdIndex + 1 , endIndex);

                //描画している時
                if(cmd.equals("point")){
                   System.out.println( +number+"("+myName+"), Point: "+sendStr);
                   MyServer.SendAll("point:"+sendStr , myName);//サーバに来たメッセージは接続しているクライアント全員に配る
                }
                else if(cmd.equals("msg")){
                    MyServer.addMsg((sendStr));
                    MyServer.SendAll(MyServer.createMsgList(MyServer.getMessageList()) , myName);//サーバに来たメッセージは接続しているクライアント全員に配る
                }

                System.out.println("Received from client No."+number+"("+myName+"), Messages: "+str + "(" + cmd + ")");

                if (str != null) {//このソケット（バッファ）に入力があるかをチェック
                  if (str.toUpperCase().equals("BYE")) {
                        myOut.println("Good bye!");
                        break;

                }}
            }
        } catch (Exception e) {
            //ここにプログラムが到達するときは，接続が切れたとき
            System.out.println("Disconnect from client No."+number+"("+myName+")");
            MyServer.removeUser(myName);
            MyServer.SetFlag(number, false);//接続が切れたのでフラグを下げる
            //誰かが切れたらユーザリストを更新するために全員に新しいリストを送信
            MyServer.SendAll(MyServer.createUserList(MyServer.getUserName() ), myName);
        }
    }
}

class MyServer{

    private static int maxConnection=10;//最大接続数
    private static Socket[] incoming;//受付用のソケット
    private static boolean[] flag;//接続中かどうかのフラグ
    private static InputStreamReader[] isr;//入力ストリーム用の配列
    private static BufferedReader[] in;//バッファリングをによりテキスト読み込み用の配列
    private static PrintWriter[] out;//出力ストリーム用の配列
    private static ClientProcThread[] myClientProcThread;//スレッド用の配列
    private static int member;//接続しているメンバーの数

    //参加中のユーザを管理するリスト
    private static ArrayList<String> userName = new ArrayList<>();
    //送信されたメッセージを管理するリスト
    private static ArrayList<String> msgList = new ArrayList<>();

    //全員にメッセージを送る
    public static void SendAll(String str, String myName){
        //送られた来たメッセージを接続している全員に配る
        for(int i=1;i<=member;i++){
            if(flag[i] == true){
                out[i].println(str);
                out[i].flush();//バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
                System.out.println("Send messages to client No."+i);
            }
        }
    }

    //フラグの設定を行う
    public static void SetFlag(int n, boolean value){
        flag[n] = value;
    }

    //ユーザーを管理するリストにユーザーを追加
    public static void addUser(String user){
        userName.add(user);
    }
    //ユーザを削除
    public static void removeUser(String user){
        userName.remove(user);
    }

    public static ArrayList<String> getUserName(){return userName;}

    //メッセージを管理するリストにユーザーを追加
    public static void addMsg(String msg){
        msgList.add(msg);
    }


    public static ArrayList<String> getMessageList(){return msgList;}


    public static String createUserList(ArrayList<String> user){
        String userList = "user:";
        for(String name : user){
            userList += name + ",";
        }
        return userList;
    }

    public static String createMsgList(ArrayList<String> msg){
        String msgList = "msg:";
        for(String m : msg){
            msgList += m + ",";
        }
        return msgList;
    }

    //mainプログラム
    public static void main(String[] args) {
        //必要な配列を確保する
        incoming = new Socket[maxConnection];
        flag = new boolean[maxConnection];
        isr = new InputStreamReader[maxConnection];
        in = new BufferedReader[maxConnection];
        out = new PrintWriter[maxConnection];


        myClientProcThread = new ClientProcThread[maxConnection];

        int n = 1;
        member = 0;//誰も接続していないのでメンバー数は０

        try {
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("The server has launched!");
            System.out.println("IP Address     : " + addr.getHostAddress());
            ServerSocket server = new ServerSocket(10000);//10000番ポートを利用する


            while (true) {
                incoming[n] = server.accept();
                flag[n] = true;
                System.out.println("Accept client No." + n);
                //必要な入出力ストリームを作成する
                isr[n] = new InputStreamReader(incoming[n].getInputStream());
                in[n] = new BufferedReader(isr[n]);
                out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

                myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);//必要なパラメータを渡しスレッドを作成
                myClientProcThread[n] .start();//スレッドを開始する
                member = n;//メンバーの数を更新する
                n++;
            }

        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}


